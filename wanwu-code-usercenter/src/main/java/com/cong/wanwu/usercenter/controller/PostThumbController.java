package com.cong.wanwu.usercenter.controller;

import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.usercenter.model.dto.postthumb.PostThumbAddRequest;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.usercenter.service.PostThumbService;
import com.cong.wanwu.usercenter.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子点赞接口
 *
 *  @author 聪
 *  
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
 
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
                                         HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser();
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

}
