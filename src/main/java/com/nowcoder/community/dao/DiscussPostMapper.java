package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    int selectDiscussPostRows(@Param("userId") int userId);//id=0时表示所有

    DiscussPost selectDiscussPostById(int id);

    int insertDiscussPost(DiscussPost discussPost);

    int updateCommentCount(int id, int commentCount);
}
