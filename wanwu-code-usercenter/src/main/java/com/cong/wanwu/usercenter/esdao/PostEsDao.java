package com.cong.wanwu.usercenter.esdao;

import com.cong.wanwu.usercenter.model.dto.post.PostEsDTO;
import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 帖子 ES 操作
 *
 *  @author 聪
 *  
 */
public interface  PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    /**
     * 根据用户id查找
     *
     * @param userId 用户id
     * @return {@link List}<{@link PostEsDTO}>
     */
    List<PostEsDTO> findByUserId(Long userId);

    /**
     * 根据title查找
     *
     * @param title 标题
     * @return {@link List}<{@link PostEsDTO}>
     */
    List<PostEsDTO> findByTitle(String title);
}