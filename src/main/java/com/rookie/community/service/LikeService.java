package com.rookie.community.service;

public interface LikeService {

    //点赞
    void like (int userId,int entityType,int entityId,int entityUserId);
    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId);
    //查询用户给该实体的点赞状态
    int findEntityLikeStatus(int userId,int entityType,int entityId);
    //查询用户获得赞的总数
    int findUserLikeCount(int userId);


}
