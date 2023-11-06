package com.cong.wanwu.usercenter.model.dto.post;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 创建请求
 *
 *  @author 聪
 *  
 */
@Data
public class PostAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String synopsis;

    /**
     * 图片缩略图
     */
    private List<String> imgList;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}