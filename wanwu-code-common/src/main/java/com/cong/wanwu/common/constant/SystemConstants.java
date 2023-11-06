package com.cong.wanwu.common.constant;

/**
 * 系统常量
 * @author liuhuaicong
 * @date 2023/09/11
 */
public interface SystemConstants {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";
    //标签颜色
    public static final String[] TAG_COLORS = new String[]{"pink", "red", "orange", "green", "cyan", "blue", "purple"};


    //推荐用户的相似度阈值
    public static final double RECOMMEND_THRESHOLD = 10;
    public static final int RANDOM_RECOMMEND_BATCH_SIZE = 50;
    public static final int RECOMMEND_SIZE = 8;

    //文章的一些属性
    public static final int ARTICLE_STATUS_PUBLISHED = 1;
    public static final int ARTICLE_STATUS_DRAFT = 0;
    public static final int ARTICLE_PRIME = 1;
    public static final int ARTICLE_COMMON = 0;
    public static final int ARTICLE_GLOBAL_TOP = 1;
    /**
     * 文章点赞锁
     */
    String WANWU_POST_LIKE = "wanwu_post_like_lock_";
    /**
     * 文章收藏锁
     */
    String WANWU_POST_FAVOUR = "wanwu_post_favour_lock_";

    /**
     * 全局盐值
     */
    String SALT = "cong";

    //消息的一些属性（类型）
    public static final int NOTIFICATION_LIKE = 1;
    public static final int NOTIFICATION_COMMENT = 2;
    public static final int NOTIFICATION_MSG = 3;
    public static final int NOTIFICATION_NOTICE = 4;

}

