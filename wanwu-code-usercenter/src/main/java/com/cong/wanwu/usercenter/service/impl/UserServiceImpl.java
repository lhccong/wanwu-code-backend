package com.cong.wanwu.usercenter.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.common.constant.SystemConstants;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.common.utils.BeanCopyUtils;
import com.cong.wanwu.usercenter.common.AlgorithmUtils;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.constant.CommonConstant;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.usercenter.mapper.UserMapper;
import com.cong.wanwu.usercenter.model.dto.user.UserQueryRequest;
import com.cong.wanwu.usercenter.model.entity.*;
import com.cong.wanwu.usercenter.model.enums.UserRoleEnum;
import com.cong.wanwu.usercenter.model.vo.*;
import com.cong.wanwu.usercenter.service.*;
import com.cong.wanwu.common.utils.SqlUtils;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.cong.wanwu.common.constant.SystemConstants.RECOMMEND_THRESHOLD;
import static com.cong.wanwu.common.constant.SystemConstants.SALT;
import static com.cong.wanwu.common.constant.UserConstant.NICK_NAME_PREFIX;
import static com.cong.wanwu.common.constant.UserConstant.USER_AVATAR_DEFALUT;

/**
 * 用户服务实现
 *
 * @author 聪
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserTagService userTagService;
    @Resource
    private UserAttentionService userAttentionService;
    @Resource
    @Lazy
    private PostService postService;




    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            //3. 分配 accessKey secretKey
            String accessKey = DigestUtils.md5DigestAsHex((SALT+userAccount+ RandomUtil.randomNumbers(5)).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT+userAccount+ RandomUtil.randomNumbers(8)).getBytes());
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserAvatar(USER_AVATAR_DEFALUT);
            user.setUserName(NICK_NAME_PREFIX+System.currentTimeMillis());
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public TokenLoginUserVo userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        StpUtil.login(user.getId());
        StpUtil.getTokenSession().set(SystemConstants.USER_LOGIN_STATE, user);
        return this.getTokenLoginUserVO(user);
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxoauth2Userinfo) {
        String unionId = wxoauth2Userinfo.getUnionId();
        String mpOpenId = wxoauth2Userinfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxoauth2Userinfo.getHeadImgUrl());
                user.setUserName(wxoauth2Userinfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            // 第2步，获取 Token  相关参数
            StpUtil.getTokenSession().set(SystemConstants.USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }
    }

    /**
     * 获取当前登录用户
     */
    @Override
    public User getLoginUser() {
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
    /**
     * 获取当前登录用户
     */
    @Override
    public User getLoginUser(String token) {
        if (StrUtil.isEmpty(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSessionByToken(token).get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     */
    @Override
    public User getLoginUserPermitNull() {
        // 先判断是否已登录
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }

    /**
     * 是否为管理员
     */
    @Override
    public boolean isAdmin() {
        // 仅管理员可查询
        Object userObj = StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     */
    @Override
    public boolean userLogout() {
        if (!StpUtil.isLogin() || StpUtil.getTokenSession().get(SystemConstants.USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        StpUtil.logout();
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        List<Long> tagIdsList = JSONUtil.toList(user.getTags(), Long.class);
        loginUserVO.setTags(userTagService.getUserTagsVo(tagIdsList));
        return loginUserVO;
    }


    @Override
    public TokenLoginUserVo getTokenLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        TokenLoginUserVo loginUserVO = new TokenLoginUserVo();
        BeanUtils.copyProperties(user, loginUserVO);
        List<Long> tagIdsList = JSONUtil.toList(user.getTags(), Long.class);
        loginUserVO.setTags(userTagService.getUserTagsVo(tagIdsList));
        //获取 Token  相关参数
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        loginUserVO.setSaTokenInfo(tokenInfo);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        List<Long> tagIdsList = JSONUtil.toList(user.getTags(), Long.class);
        userVO.setTags(userTagService.getUserTagsVo(tagIdsList));
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {

        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(userQueryRequest));
        Page<UserVO> uservopage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = this.getUserVO(userPage.getRecords());
        uservopage.setRecords(userVO);
        return uservopage;
    }

    @Override
    public LoginUserVO userMpLogin(String mpCode) {

        String mpOpenId = stringRedisTemplate.opsForValue().get(mpCode);

        if (StringUtils.isBlank(mpOpenId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "验证码错误");
        }
        // 单机锁
        synchronized (mpOpenId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mpOpenId", mpOpenId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUserName("小哆啦");
                user.setUserAvatar("https://img0.baidu.com/it/u=1688188257,3971328910&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1682528400&t=e11461d06fc474c507583241b74eef08");
                user.setMpOpenId(mpOpenId);
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            StpUtil.getTokenSession().set(SystemConstants.USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }
    }

    @Override
    public List<UserVO> recommendUser(Long num) {
        //当前登录用户
        User loginUser = this.getLoginUser();
        //登录用户的标签ID
        List<String> loginUserTagList = JSONUtil.toList(loginUser.getTags(), String.class);
        List<User> userList = this.list(new LambdaQueryWrapper<User>().select(User::getId, User::getTags));
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();

        }


        // 用户列表的下标 => 相似度
        List<Pair<UserVO, Long>> list = new ArrayList<>();

        // 依次计算所有用户和当前用户的相似度
        for (User user : userList) {
            List<String> userTagList = JSONUtil.toList(user.getTags(), String.class);
            // 无标签或者为当前用户自己
            if (CollectionUtils.isEmpty(userTagList) || user.getId().equals(loginUser.getId())) {
                continue;
            }

            // 计算分数
            long distance = AlgorithmUtils.minDistance(loginUserTagList, userTagList);
            UserVO userVO = BeanCopyUtils.copyBean(user, UserVO.class);
            double similarity = 0D;
            int loginUserTagSize = loginUserTagList.size();
            //计算相似度
            if (loginUserTagSize != 0&&loginUserTagSize-distance>0) {
                similarity = (double) (loginUserTagSize - distance) / loginUserTagSize;
            }
            //设置相似度
            userVO.setSimilarity(similarity * 100);
            list.add(new Pair<>(userVO, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<UserVO, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<UserVO> userVOList = topUserPairList.stream().map(Pair::getKey).collect(Collectors.toList());
        List<Long> userIdList = userVOList.stream().map(UserVO::getId).collect(Collectors.toList());
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<UserVO>> userIdUserListMap = this.list(new LambdaQueryWrapper<User>().in(User::getId, userIdList))
                .stream()
                .map(this::getUserVO)
                .collect(Collectors.groupingBy(UserVO::getId));
        List<UserVO> finalUserList = new ArrayList<>();
        for (UserVO userVO : userVOList) {
            //如果相似度小于60就不显示
            if (userVO.getSimilarity()<RECOMMEND_THRESHOLD){
                continue;
            }
            UserVO userVo = userIdUserListMap.get(userVO.getId()).get(0);
            userVo.setSimilarity(userVO.getSimilarity());
            finalUserList.add(userVo);
        }
        //没有推荐用户，直接返回
        if (CollectionUtils.isEmpty(finalUserList)){
            return Collections.emptyList();
        }
        // 2. 已登录，获取用户关注状态
        Map<Long, Boolean> userIdConcernSumMap = new HashMap<>();
        Set<Long> userIdSet = finalUserList.stream().map(UserVO::getId).collect(Collectors.toSet());
        // 获取关注
        List<UserAttention> userAttentionList = userAttentionService.list(new LambdaQueryWrapper<UserAttention>().in(UserAttention::getFollowedUserId, userIdSet).eq(UserAttention::getUserId, loginUser.getId()));
        userAttentionList.forEach(userConcernSum -> userIdConcernSumMap.put(userConcernSum.getFollowedUserId(), true));
        // 填充信息
        finalUserList.forEach(userVO -> userVO.setHasConcern(userIdConcernSumMap.getOrDefault(userVO.getId(), false)));
        return finalUserList;
    }

}
