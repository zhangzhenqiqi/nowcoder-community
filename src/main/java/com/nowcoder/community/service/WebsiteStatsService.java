package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 统计网站相关的数据：日活，独立访问量等
 */
@Service
public class WebsiteStatsService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    //将指定的ip计入uv
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    /**
     * 统计区间UV
     *
     * @param start
     * @param end
     * @return
     */
    public long calculateUV(Date start, Date end) {
        if (start != null && end != null) {
            List<String> keyList = new ArrayList<>();
            Calendar calender = Calendar.getInstance();
            calender.setTime(start);
            while (!calender.getTime().after(end)) {
                String key = RedisKeyUtil.getUVKey(df.format(calender.getTime()));
                keyList.add(key);
                calender.add(Calendar.DATE, 1);
            }
            String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
            return redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());
        } else {
            throw new IllegalArgumentException("参数不能为空！");
        }
    }

    //将指定用户计入dau
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    /**
     * 区间dau统计
     *
     * @param start
     * @param end
     * @return
     */
    public long calculateDAU(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        List<byte[]> keyList = new ArrayList<>();
        Calendar calender = Calendar.getInstance();
        calender.setTime(start);
        while (!calender.getTime().after(end)) {
            String key = RedisKeyUtil.getDAUKey(df.format(calender.getTime()));
            keyList.add(key.getBytes());
            calender.add(Calendar.DATE, 1);
        }
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                return connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),
                        keyList.toArray(new byte[0][0]));
            }
        });
    }
}
