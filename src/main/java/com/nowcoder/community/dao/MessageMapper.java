package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {


    /**
     * 查询当前用户会话列表,每个会话只返回最新的一条私信
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户会话数
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信的数量
    int selectLetterUnreadCount(int userId, String conversationId);

    int insertMessage(Message message);

    //修改消息状态
    int updateStatus(List<Integer> ids, int status);

    //查询某个主题下最新的那条通知
    Message selectLatestMotice(int userId, String topic);

    //查询某个主题所包含通知数
    int selectNoticeCount(int userId, String topic);



    /**
     *  查询topic下未读的通知数,当topic=null表示查询所有主题
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeUnReadCount(int userId, String topic);

    /**
     * 查询某个主题所包含的通知列表
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
