package com.cong.wanwu.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.wanwu.common.common.DeleteRequest;
import com.cong.wanwu.usercenter.model.dto.post.*;
import com.cong.wanwu.usercenter.model.entity.Post;
import com.cong.wanwu.usercenter.model.vo.PostVO;

/**
 * 帖子服务
 *
 *  @author 聪
 *  
 */
public interface PostService extends IService<Post> {

    /**
     * 校验
     *
     * @param post
     * @param add
     */
    void validPost(Post post, boolean add);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param postQueryRequest
     * @return
     */
    Page<Post> searchFromEs(PostQueryRequest postQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param post
 
     * @return
     */
    PostVO getPostVO(Post post);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
 
     * @return
     */
    Page<PostVO> getPostVOPage(Page<Post> postPage);

    /**
     * 文章列表voby页面
     *
     * @param postQueryRequest 查询请求后
           请求
     * @return {@link Page}<{@link Post}>
     */
    Page<PostVO>  listPostVOByPage(PostQueryRequest postQueryRequest);

    /**
     * 添加文章
     * @param postAddRequest
     * @return {@link Long}
     */
    Long addPost(PostAddRequest postAddRequest);

    /**
     * 删除文章
     * @param deleteRequest
     * @return {@link Boolean}
     */
    Boolean deletePost(DeleteRequest deleteRequest);

    /**
     * 发布帖子
     * @param postPublishRequest
     * @return {@link Boolean}
     */
    Boolean publishMyPost(PostPublishRequest postPublishRequest);

    /**
     * 编辑个人文章
     * @param postEditRequest
     * @return boolean
     */
    Boolean edit(PostEditRequest postEditRequest);

    /**
     * 移除附件图片
     * @param postImgRemoveRequest
     * @return {@link Boolean}
     */
    Boolean removePostImg(PostImgRemoveRequest postImgRemoveRequest);
}
