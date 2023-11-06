package com.cong.wanwu.usercenter.model.dto.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 贴子发布
 * @author liuhuaicong
 */
@Data
public class PostPublishRequest implements Serializable {
    private Long postId;
    private static final long serialVersionUID = 1L;
}
