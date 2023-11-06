package com.cong.wanwu.usercenter.service.impl;

import com.cong.wanwu.common.constant.RedisConstants;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.usercenter.model.vo.NotificationCountVo;
import com.cong.wanwu.usercenter.service.NotificationService;
import com.cong.wanwu.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Antares
 * @description 针对表【notification】的数据库操作Service实现
 * @createDate 2023-05-18 16:36:19
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserService userService;

    @Override
    public NotificationCountVo count() {
        User loginUser = userService.getLoginUser();
        String cacheKeyPrefix = RedisConstants.NOTIFICATION_PREFIX + loginUser.getId();
        List<String> keys = Arrays.asList(
                cacheKeyPrefix + RedisConstants.LIKE_NOTIFICATION_SUFFIX,
                cacheKeyPrefix + RedisConstants.COMMENT_NOTIFICATION_SUFFIX,
                cacheKeyPrefix + RedisConstants.MSG_NOTIFICATION_SUFFIX,
                cacheKeyPrefix + RedisConstants.NOTICE_NOTIFICATION_SUFFIX
        );

        List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<List<Object>>) connection -> {
            for (String key : keys) {
                connection.get(key.getBytes());
            }
            return null;
        });

        Integer likeCount = results.get(0) == null ? 0 : Integer.parseInt(results.get(0).toString());
        Integer commentCount = results.get(1) == null ? 0 : Integer.parseInt(results.get(1).toString());
        Integer msgCount = results.get(2) == null ? 0 : Integer.parseInt(results.get(2).toString());
        Integer noticeCount = results.get(3) == null ? 0 : Integer.parseInt(results.get(3).toString());

        return new NotificationCountVo(likeCount, commentCount, msgCount, noticeCount);
    }


    @Override
    public void clearNotification(String type) {
        User loginUser = userService.getLoginUser();
        String cacheKeyPrefix = RedisConstants.NOTIFICATION_PREFIX + loginUser.getId();
        if("all".equals(type)){
            String[] keys = {cacheKeyPrefix + RedisConstants.LIKE_NOTIFICATION_SUFFIX,
                    cacheKeyPrefix + RedisConstants.COMMENT_NOTIFICATION_SUFFIX};
            stringRedisTemplate.delete(Arrays.asList(keys));
        }
        switch (type) {
            case "like": stringRedisTemplate.delete(cacheKeyPrefix + RedisConstants.LIKE_NOTIFICATION_SUFFIX);break;
            case "comment": stringRedisTemplate.delete(cacheKeyPrefix + RedisConstants.COMMENT_NOTIFICATION_SUFFIX);break;
            case "chat":
                stringRedisTemplate.delete(cacheKeyPrefix + RedisConstants.MSG_NOTIFICATION_SUFFIX);
                //TODO 移除消息未读
                break;
            case "notice": stringRedisTemplate.delete(cacheKeyPrefix + RedisConstants.NOTICE_NOTIFICATION_SUFFIX);break;
        }
    }
}