package com.cong.wanwu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.usercenter.model.dto.post.PostQueryRequest;
import com.cong.wanwu.usercenter.model.entity.Post;
import javax.annotation.Resource;

import com.cong.wanwu.usercenter.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子服务测试
 *
 *  @author 聪
 *  
 */
@SpringBootTest
class PostServiceTest {

    @Resource
    private PostService postService;

    @Test
    void searchFromEs() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setUserId(1L);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        Assertions.assertNotNull(postPage);
    }

}