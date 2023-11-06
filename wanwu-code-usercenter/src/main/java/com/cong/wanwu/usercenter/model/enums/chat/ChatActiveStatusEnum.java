package com.cong.wanwu.usercenter.model.enums.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 聊天活动状态枚举
 * Description: ws前端请求类型枚举
 * @author liuhuaicong
 * @date 2023/10/31
 */
@AllArgsConstructor
@Getter
public enum ChatActiveStatusEnum {
    ONLINE(1, "在线"),
    OFFLINE(2, "离线"),
    ;

    private final Integer status;
    private final String desc;

    private static Map<Integer, ChatActiveStatusEnum> cache;

    static {
        cache = Arrays.stream(ChatActiveStatusEnum.values()).collect(Collectors.toMap(ChatActiveStatusEnum::getStatus, Function.identity()));
    }

    public static ChatActiveStatusEnum of(Integer type) {
        return cache.get(type);
    }
}
