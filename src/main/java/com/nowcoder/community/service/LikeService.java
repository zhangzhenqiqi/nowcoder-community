package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;


    //点赞

    /**
     * @param userId       当前点赞用户的id
     * @param entityType
     * @param entityId
     * @param entityUserId ：这种做法不用从数据库再次查询userId，否则本来内存高效的redis又会加入dao，浪费时间性能
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                /*
                需要将查询放在事务之外，redis事务并非立即执行，遇到一句将其放入队列，当执行到exec时才开始运行，
                如果将查询放事务里，那么得不到查询的值，判断状态会出错；在java源码中也可以看到 ：
                null when used in pipeline / transaction.
                * */
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                operations.multi();
                if (isMember) {//已经点过赞，再点表示取消
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {//点赞
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }


    /**
     * //查询某实体点赞数量
     *
     * @param entityType 1-帖子 2-评论/回复
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }


    /**
     * //查询某人对某实体的点赞状态
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return 1-已点赞 0-未点赞
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        return isMember ? 1 : 0;
    }

    /**
     * 查询某个用户得到的赞的数量
     *
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer result = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return result == null ? 0 : result;
    }
}
