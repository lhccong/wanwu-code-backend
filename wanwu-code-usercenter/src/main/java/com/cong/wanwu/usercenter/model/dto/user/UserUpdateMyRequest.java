package com.cong.wanwu.usercenter.model.dto.user;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 用户更新个人信息请求
 *
 *  @author 聪
 *  
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户标签
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}