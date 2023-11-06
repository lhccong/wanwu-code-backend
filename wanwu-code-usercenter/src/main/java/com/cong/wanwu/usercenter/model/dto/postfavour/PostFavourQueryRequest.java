package com.cong.wanwu.usercenter.model.dto.postfavour;

import com.cong.wanwu.common.common.PageRequest;
import com.cong.wanwu.usercenter.model.dto.post.PostQueryRequest;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子收藏查询请求
 *
 *  @author 聪
 *  
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostFavourQueryRequest extends PageRequest implements Serializable {

    /**
     * 帖子查询请求
     */
    private PostQueryRequest postQueryRequest;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}