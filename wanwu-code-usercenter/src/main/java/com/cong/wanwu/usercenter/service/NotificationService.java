package com.cong.wanwu.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.usercenter.model.vo.NotificationCountVo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liuhuaicong
 * @description 针对表【notification】的数据库操作Service
 * @createDate 2023-05-18 16:36:19
 * @date 2023/09/14
 */
public interface NotificationService {

    /**
     * 获取信息数量
     * @return {@link NotificationCountVo}
     */
    NotificationCountVo count();

    /**
     * 清除指定消息
     * @param type
     */
    void clearNotification(String type);

//    Page<LikeNotificationVo> listLikeNotificationByPage(NotificationQueryRequest notificationQueryRequest);
//
//
//    Page<CommentNotificationVo> listCommentNotificationByPage(NotificationQueryRequest notificationQueryRequest);
}
