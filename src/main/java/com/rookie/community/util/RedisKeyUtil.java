package com.rookie.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    //关注目标
    private static final String PREFIX_FOLLOWEE = "followee";
    //粉丝
    private static final String PREFIX_FOLLOWER = "follower";
    //验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";
    //登录凭证
    private static final String PREFIX_TICKET = "ticket";
    //缓存用户
    private static final String PREFIX_USER = "user";
    //统计独立访客（UV）
    private static final String PREFIX_UV = "uv";
    //统计日活跃用户（DAU）
    private static final String PREFIX_DAU = "dau";
    //需要重新评分的帖子
    private static final String PREFIX_POST = "post";


    //某个实体的赞
    //key=====>like:entity:entityType:entityId ->存set集合（userId）
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某一个用户的赞
    public static String getUSerLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体
    //followee:userId:entityType   ->set(entityId,Date)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个用户拥有的分数
    //follower:entityType:entityId   ->set(userId,Date)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //验证码key
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录凭证key
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //缓存user key
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    //单日UV
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT +date;
    }

    //区间UV
    public static String getUVKey(String startDate,String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //单日活跃用户
    public static String getDAUKey(String date){
        return PREFIX_DAU + SPLIT +date;
    }

    //区间DAU
    public static String getDAUKey(String startDate,String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    //需要重新评分的帖子的key
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }

}