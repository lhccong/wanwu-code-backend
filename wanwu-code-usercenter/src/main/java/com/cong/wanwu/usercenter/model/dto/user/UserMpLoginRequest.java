package com.cong.wanwu.usercenter.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 *  @author 聪
 *  
 */
@Data
public class UserMpLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String mpCode;

}
