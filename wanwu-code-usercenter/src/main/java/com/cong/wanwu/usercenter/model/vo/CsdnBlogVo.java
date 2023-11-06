package com.cong.wanwu.usercenter.model.vo;

import lombok.Data;

import java.util.List;

/**
 * CSDN 博客 VO
 *
 * @author liuhuaicong
 * @date 2023/10/19
 */
@Data
public class CsdnBlogVo {
    /**
     * 标题
     */
    private String title;
    /**
     * 文章网址
     */
    private String articleUrl;
    /**
     * 总结
     */
    private String summary;
    /**
     * 观看次数
     */
    private String viewCount;
    /**
     * 喜欢计数
     */
    private String likeCount;
    /**
     * 评论计数
     */
    private String commentCount;
    /**
     * 作者
     */
    private String author;
    /**
     * 作者网址
     */
    private String authorUrl;
    /**
     * 创建时间
     */
    private String createdTime;
    /**
     * 图片列表
     */
    private List<String> picList;
}
