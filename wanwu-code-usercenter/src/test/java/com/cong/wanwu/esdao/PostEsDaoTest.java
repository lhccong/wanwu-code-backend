package com.cong.wanwu.esdao;

import com.cong.wanwu.usercenter.esdao.PostEsDao;
import com.cong.wanwu.usercenter.model.dto.post.PostEsDTO;
import com.cong.wanwu.usercenter.model.dto.post.PostQueryRequest;
import com.cong.wanwu.usercenter.model.entity.Post;
import com.cong.wanwu.usercenter.service.PostService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * 帖子 ES 操作测试
 *
 *  @author 聪
 *  
 */
@SpringBootTest
public class PostEsDaoTest {

    @Resource
    private PostEsDao postEsDao;

    @Resource
    private PostService postService;

    @Test
    void test() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Post> page =
                postService.searchFromEs(postQueryRequest);
        System.out.println(page);
    }

    @Test
    void testSelect() {
        System.out.println(postEsDao.count());
        Page<PostEsDTO> PostPage = postEsDao.findAll(
                PageRequest.of(0, 5, Sort.by("createTime")));
        List<PostEsDTO> postList = PostPage.getContent();
        System.out.println(postList);
    }

    @Test
    void testAdd() {
        PostEsDTO postEsDTO = new PostEsDTO();
        postEsDTO.setId(1632938674335064065L);
        postEsDTO.setTitle("聪程序员");
        postEsDTO.setContent("作为一名程序员，应该注重自身的专业技能和贡献，而不是外表。");
        postEsDTO.setTags(Arrays.asList("java", "Python"));
        postEsDTO.setUserId(1L);
        postEsDTO.setCreateTime(new Date());
        postEsDTO.setUpdateTime(new Date());
        postEsDTO.setIsDelete(0);
        postEsDao.save(postEsDTO);
        System.out.println(postEsDTO.getId());
    }

    @Test
    void testFindById() {
        Optional<PostEsDTO> postEsDTO = postEsDao.findById(1L);
        System.out.println(postEsDTO);
    }

    @Test
    void testCount() {
        System.out.println(postEsDao.count());
    }

    @Test
    void testFindByCategory() {
        List<PostEsDTO> postEsDaoTestList = postEsDao.findByUserId(1L);
        System.out.println(postEsDaoTestList);
    }
    @Test
    void testFindByTitle(){
        List<PostEsDTO> postEsDTOS = postEsDao.findByTitle("聪");
        System.out.println(postEsDTOS);
    }
}
