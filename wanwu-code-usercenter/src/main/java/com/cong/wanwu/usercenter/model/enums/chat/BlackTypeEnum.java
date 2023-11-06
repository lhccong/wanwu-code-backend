package com.cong.wanwu.usercenter.model.enums.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 黑色类型枚举
 * Description: 物品枚举
 * Date: 2023-03-19
 *
 * @author liuhuaicong
 * @date 2023/10/31
 */
@AllArgsConstructor
@Getter
public enum BlackTypeEnum {
    IP(1),
    UID(2),
    ;

    private final Integer type;

}
