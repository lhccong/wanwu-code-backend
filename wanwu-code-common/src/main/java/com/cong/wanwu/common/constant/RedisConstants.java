package com.cong.wanwu.common.constant;

/**
 * Redis
 * @author liuhuaicong
 * @date 2023/09/14
 */
public interface RedisConstants {
    /**
     * 通知
     */
    public static final String NOTIFICATION_PREFIX = "notification:uid:";
    //点赞
    public static final String LIKE_NOTIFICATION_SUFFIX = ":like";
    //评论
    public static final String COMMENT_NOTIFICATION_SUFFIX = ":comment";
    //消息
    public static final String MSG_NOTIFICATION_SUFFIX = ":msg";
    //系统通知
    public static final String NOTICE_NOTIFICATION_SUFFIX = ":notice";


}
