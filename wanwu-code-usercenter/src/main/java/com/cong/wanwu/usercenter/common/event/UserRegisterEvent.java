package com.cong.wanwu.usercenter.common.event;

import com.cong.wanwu.common.model.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private User user;

    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
