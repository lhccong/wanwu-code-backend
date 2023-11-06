package com.cong.wanwu.usercenter.common.listener;


import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.usercenter.common.event.UserOnlineEvent;
import com.cong.wanwu.usercenter.service.UserService;
import com.cong.wanwu.usercenter.service.WebSocketService;
import com.cong.wanwu.usercenter.service.adapter.WSAdapter;
import com.cong.wanwu.usercenter.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户上线监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class UserOnlineListener {
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private UserService userService;
    @Resource
    private UserCache userCache;
    @Resource
    private WSAdapter wsAdapter;


    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveRedisAndPush(UserOnlineEvent event) {
        User user = event.getUser();
        userCache.online(user.getId(), user.getUpdateTime());
        //推送给所有在线用户，该用户登录成功
        webSocketService.sendToAllOnline(wsAdapter.buildOnlineNotifyResp(event.getUser()));
    }

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDb(UserOnlineEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setUpdateTime(user.getUpdateTime());
        userService.updateById(update);
    }

}
