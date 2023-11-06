package com.cong.wanwu.usercenter.model.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.cong.wanwu.usercenter.model.entity.UserTag;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * accessKey
     */
    private String accessKey;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * 用户标签
     */
    private List<UserTagVo> tags;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 是否关注
     */
    private Boolean hasConcern;

    /**
     * 关注人数
     */
    private Integer concernNum;

    /**
     * 相似度
     */
    private Double similarity;

    /**
     * 粉丝人数
     */
    private Integer fansNum;

    /**
     * 帖子数量
     */
    private Integer postNum;

    /**
     * 创建时间
     */
    private Date createTime;


    private static final long serialVersionUID = 1L;
}