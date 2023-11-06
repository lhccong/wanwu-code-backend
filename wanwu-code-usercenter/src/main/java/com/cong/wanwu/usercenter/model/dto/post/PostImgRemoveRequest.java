package com.cong.wanwu.usercenter.model.dto.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 贴子附件图片移除
 * @author liuhuaicong
 */
@Data
public class PostImgRemoveRequest implements Serializable {
    private Long postId;
    private String imgUrl;
    private static final long serialVersionUID = 1L;
}
