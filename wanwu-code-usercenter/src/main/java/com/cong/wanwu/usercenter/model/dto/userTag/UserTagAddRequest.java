package com.cong.wanwu.usercenter.model.dto.userTag;

import lombok.Data;

/**
 * @author liuhuaicong
 * @date 2023/09/08
 */
@Data
public class UserTagAddRequest {
    /**
     *父级id
     */
    private Long parentId;
    /**
     *名称
     */
    private String name;
}
