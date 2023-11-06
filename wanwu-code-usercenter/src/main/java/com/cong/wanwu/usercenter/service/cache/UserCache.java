package com.cong.wanwu.usercenter.service.cache;


import com.cong.wanwu.common.utils.RedisUtils;
import com.cong.wanwu.usercenter.constant.RedisKey;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户缓存
 * Description: 用户相关缓存
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-27
 *
 * @author liuhuaicong
 * @date 2023/10/31
 */
@Component
public class UserCache {

    public boolean isOnline(Long uid) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zIsMember(onlineKey, uid);
    }
    //用户上线
    public void online(Long uid, Date optTime) {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        String offlineKey = RedisKey.getKey(RedisKey.OFFLINE_UID_ZET);
        //移除离线表
        RedisUtils.zRemove(offlineKey, uid);
        //更新上线表
        RedisUtils.zAdd(onlineKey, uid, optTime.getTime());
    }
    public Long getOnlineNum() {
        String onlineKey = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zCard(onlineKey);
    }
}
