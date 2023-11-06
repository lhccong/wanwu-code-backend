package com.cong.wanwu.usercenter.common.event;

import com.cong.wanwu.common.model.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserOfflineEvent extends ApplicationEvent {
    private User user;

    public UserOfflineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
