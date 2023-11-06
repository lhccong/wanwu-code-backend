package com.cong.wanwu.usercenter.model.dto.post;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 编辑请求
 *
 *  @author 聪
 *  
 */
@Data
public class PostEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;
    /**
     * 图片缩略图
     */
    private List<String> imgList;
    /**
     * 描述
     */
    private String synopsis;

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