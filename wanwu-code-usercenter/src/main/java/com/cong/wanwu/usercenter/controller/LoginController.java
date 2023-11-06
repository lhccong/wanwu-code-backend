package com.cong.wanwu.usercenter.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.cong.wanwu.common.constant.SystemConstants;
import com.cong.wanwu.common.constant.UserConstant;
import com.cong.wanwu.common.model.entity.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录测试
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

    // 测试登录  ---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
    @RequestMapping("doLogin")
    public SaResult doLogin(String name, String pwd) {
        // 第1步，先登录上
        StpUtil.login(10003);
        User user = new User();
        user.setUserName("聪");
        user.setId(1L);
        user.setUserRole("admin");
        SaSession session = StpUtil.getTokenSession();
        // 3. 记录用户的登录态
        session.set(SystemConstants.USER_LOGIN_STATE, user);
        // 第2步，获取 Token  相关参数
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        // 第3步，返回给前端
        return SaResult.data(tokenInfo);
    }

    // 查询登录状态  ---- http://localhost:8081/acc/isLogin
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @RequestMapping("isLogin")
    public SaResult isLogin() {
        SaSession tokenSession = StpUtil.getTokenSession();
        return SaResult.ok("是否登录：" + StpUtil.getLoginId());
    }

    // 查询 Token 信息  ---- http://localhost:8081/acc/tokenInfo
    @SaCheckRole("admin")
    @RequestMapping("tokenInfo")
    public SaResult tokenInfo() {
        return SaResult.data(StpUtil.getTokenInfo());
    }

    // 测试注销  ---- http://localhost:8081/acc/logout
    @RequestMapping("logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

}
