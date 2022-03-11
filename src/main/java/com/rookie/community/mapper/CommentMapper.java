package com.rookie.community.mapper;

import com.rookie.community.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {

    //查询评论
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);
    //查询评论的数量
    int selectCountsByEntity(int entityType,int entityId);
    //添加评论
    int insertComment(Comment comment);
    //通过id查询评论
    Comment selectCommentById(int id);

}
