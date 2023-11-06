package com.cong.wanwu.usercenter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.wanwu.usercenter.model.dto.user.UserQueryRequest;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.usercenter.model.vo.LoginUserVO;
import com.cong.wanwu.usercenter.model.vo.TokenLoginUserVo;
import com.cong.wanwu.usercenter.model.vo.UserVO;
import java.util.List;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * 用户服务
 *
 *  @author 聪
 *  
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    TokenLoginUserVo userLogin(String userAccount, String userPassword);

    /**
     * 用户登录（微信开放平台）
     *
     * @param wxOAuth2UserInfo 从微信获取的用户信息
 
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo);

    /**
     * 获取当前登录用户
     *
 
     * @return
     */
    User getLoginUser();

    /**
     * 获取当前登录用户
     *
     * @param token 令 牌
     * @return {@link User}
     */
    User getLoginUser(String token);

    /**
     * 获取当前登录用户（允许未登录）
     *
 
     * @return
     */
    User getLoginUserPermitNull();

    /**
     * 是否为管理员
     *
 
     * @return
     */
    boolean isAdmin();

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
 
     * @return
     */
    boolean userLogout();

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的已登录用户信息(Token)
     *
     * @return
     */
    LoginUserVO getTokenLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户voby页面列表
     *
     * @param userQueryRequest 用户查询请求
     * @return {@link Page}<{@link UserVO}>
     */
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    /**
     * 用户公众号登录
     *
     * @param mpCode  议员代码
  请求
     * @return {@link LoginUserVO}
     */
    LoginUserVO userMpLogin(String mpCode);

    /**
     * 获取推荐用户
     * @param num  推荐用户数量
     * @return {@link List}<{@link UserVO}>
     */
    List<UserVO> recommendUser(Long num);
}
