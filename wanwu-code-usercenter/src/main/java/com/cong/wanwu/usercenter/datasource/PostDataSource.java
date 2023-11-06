package com.cong.wanwu.usercenter.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.cong.wanwu.usercenter.model.dto.post.PostQueryRequest;
import com.cong.wanwu.usercenter.model.entity.Post;
import com.cong.wanwu.usercenter.model.vo.PostVO;
import com.cong.wanwu.usercenter.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务实现
 *
 *  @author 聪
 *  
 */
@Service
@Slf4j
public class PostDataSource implements DataSource<PostVO> {

    private final static Gson GSON = new Gson();

    @Resource
    private PostService postService;


    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return postService.getPostVOPage(postPage);
    }
}




