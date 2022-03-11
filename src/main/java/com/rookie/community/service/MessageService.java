package com.rookie.community.service;

import com.rookie.community.pojo.Message;

import java.util.List;

public interface MessageService {
    //查询当前用户会话列表，针对每个会话只返回一条最新的sixin
    List<Message> findConversations(int userId, int offset, int limit);

    //查询当前会话的数量
    int findConversationCount(int userId);

    //查询某个会话包含的私信列表
    List<Message> findLetters(String conversationId, int offset, int limit);

    //查询某个会话包含的私信的数量
    int findLetterCount(String conversationId);

    //查询未读的私信数量
    int findLetterUnreadCount(int userId,String conversationId);

    //添加一个消息
    int addMessage(Message message);

    //读取消息后，改变消息状态
    int readMessage(List<Integer> ids);

    //某个主题下最新的通知
    Message findLatestNotice(int userId,String topic);
    //某个主题所包含的通知数量
    int findNoticeCount(int userId,String topic);
    //某个主题未读
    int findNoticeUnreadCount(int userId,String topic);

    //某个主题下分页查询通知列表
    List<Message> findNotices(int userId,String topic, int offset,int limit);
}
