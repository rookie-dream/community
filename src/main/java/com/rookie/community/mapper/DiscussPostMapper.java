package com.rookie.community.mapper;


import com.rookie.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit,int orderMode);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);
    //添加帖子
    int insertDiscussPost(DiscussPost discussPost);
    //通过id查找帖子
    DiscussPost selectDiscussPostById(int id);
    //更新帖子的回复数量
    int updateCommentCount(int id,int commentCount);
    //修改帖子的类型
    int updateType(int id,int type);
    //修改帖子的状态
    int updateStatus(int id,int status);
    //修改帖子的分数
    int updateScore(int id,double score);

}
