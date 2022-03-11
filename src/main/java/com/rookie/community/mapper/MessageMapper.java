package com.rookie.community.mapper;

import com.rookie.community.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper {
    //查询当前用户会话列表，针对每个会话只返回一条最新的sixin
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前会话的数量
    int selectConversationCount(int userId);

    //查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话包含的私信的数量
    int selectLetterCount(String conversationId);

    //查询未读的私信数量
    int selectLetterUnreadCount(int userId,String conversationId);

    //新增消息
    int insetMessage(Message message);

    //修改消息状态
    int updateStatus(List<Integer> ids, int status);

    //某个主题下最新的通知
    Message selectLatestNotice(int userId,String topic);
    //某个主题所包含的通知数量
    int selectNoticeCount(int userId,String topic);
    //某个主题未读
    int selectNoticeUnreadCount(int userId,String topic);

    //某个主题下分页查询通知列表
    List<Message> selectNotices(int userId,String topic, int offset,int limit);

}
