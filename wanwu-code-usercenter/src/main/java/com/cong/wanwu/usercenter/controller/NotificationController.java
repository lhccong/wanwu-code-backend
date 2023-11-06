package com.cong.wanwu.usercenter.controller;

import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.usercenter.model.vo.NotificationCountVo;
import com.cong.wanwu.usercenter.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 消息通知
 * @author liuhuaicong
 * @date 2023/09/14
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Resource
    private NotificationService notificationService;

    /**
     * 用户登录后查询自己的通知数量，count保存在redis中，redis中没有了再去数据库查询
     * @return
     */
    @GetMapping("/count")
    public BaseResponse<NotificationCountVo> getNoticeCount(){
        NotificationCountVo notificationCountVo = notificationService.count();
        return ResultUtils.success(notificationCountVo);
    }

    @PostMapping("/clear")
    public BaseResponse<Boolean> clearNotification(@RequestParam("type") String type){
        notificationService.clearNotification(type);
        return ResultUtils.success(true);
    }

//    /**
//     * 分页查询点赞消息记录
//     * @param notificationQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/like")
//    public BaseResponse<Boolean> listLikeNotificationByPage(@RequestBody NotificationQueryRequest notificationQueryRequest){
//        Page<LikeNotificationVo> page = notificationService.listLikeNotificationByPage(notificationQueryRequest);
//        return ResultUtils.success(true);
//    }
//
//    /**
//     * 分页查询评论消息记录
//     * @param notificationQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/comment")
//    public R listCommentNotificationByPage(@RequestBody NotificationQueryRequest notificationQueryRequest,
//                                    HttpServletRequest request){
//        Page<CommentNotificationVo> vos = notificationService.listCommentNotificationByPage(notificationQueryRequest, request);
//        return R.ok(vos);
//    }
}
