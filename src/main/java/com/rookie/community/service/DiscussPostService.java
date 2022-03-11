package com.rookie.community.service;


import com.rookie.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface DiscussPostService {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit,int orderMode);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);
    //添加帖子
    int addDiscussPost(DiscussPost post);
    //通过id查找帖子
    DiscussPost findDiscussPostById(int id);
    //更新帖子评论数量
    int updateCommentCount(int id,int commentCount);
    //修改帖子的类型
    int updateType(int id,int type);
    //修改帖子的状态
    int updateStatus(int id,int status);
    //修改帖子的分数
    int updateScore(int id,double score);

}
