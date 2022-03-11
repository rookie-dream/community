package com.rookie.community.service;

import com.rookie.community.pojo.Comment;

import java.util.List;

public interface CommentService {
    //查询评论
    List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit);
    //查询评论的数量
    int findCommentCount(int entityType,int entityId);
    //增加评论
    int addComment(Comment comment);
    //通过id查询评论
    Comment findCommentById(int id);
}
