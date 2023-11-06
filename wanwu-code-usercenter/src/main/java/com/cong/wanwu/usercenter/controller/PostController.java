package com.cong.wanwu.usercenter.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.common.constant.UserConstant;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.common.exception.ThrowUtils;
import com.cong.wanwu.usercenter.model.dto.post.*;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.usercenter.model.vo.PostVO;
import com.cong.wanwu.usercenter.service.PostService;
import com.cong.wanwu.usercenter.service.UserService;
import com.google.gson.Gson;
import com.cong.wanwu.common.common.DeleteRequest;
import com.cong.wanwu.usercenter.model.entity.Post;

import java.util.List;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子接口
 *
 * @author 聪
 */
@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;


    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param postAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long newPostId = postService.addPost(postAddRequest);
        return ResultUtils.success(newPostId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = postService.deletePost(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 更新（仅管理员）
     *
     * @param postUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest) {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            post.setTags(GSON.toJson(tags));
        }
        // 参数校验
        postService.validPost(post, false);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = postService.updateById(post);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PostVO> getPostVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(postService.getPostVO(post));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostVO>> listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest) {
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<PostVO> postPage = postService.listPostVOByPage(postQueryRequest);
        return ResultUtils.success(postPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<PostVO>> listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser();
        postQueryRequest.setUserId(loginUser.getId());
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postService.getPostVOPage(postPage));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param postQueryRequest
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<PostVO>> searchPostVOByPage(@RequestBody PostQueryRequest postQueryRequest) {
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return ResultUtils.success(postService.getPostVOPage(postPage));
    }

    /**
     * 编辑（用户）
     *
     * @param postEditRequest
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPost(@RequestBody PostEditRequest postEditRequest) {
        boolean result =postService.edit(postEditRequest);
        return ResultUtils.success(result);
    }
    /**
     * 发布帖子（用户）
     *
     * @param postPublishRequest
     */
    @PostMapping("/publish")
    public BaseResponse<Boolean> publishMyPost(@RequestBody PostPublishRequest postPublishRequest) {
        Boolean result = postService.publishMyPost(postPublishRequest);
        return ResultUtils.success(result);
    }
    /**
     * 删除帖子图片（用户）
     *
     * @param postImgRemoveRequest
     */
    @PostMapping("/remove/img")
    public BaseResponse<Boolean> removePostImg(@RequestBody PostImgRemoveRequest postImgRemoveRequest) {
        Boolean result = postService.removePostImg(postImgRemoveRequest);
        return ResultUtils.success(result);
    }
}
