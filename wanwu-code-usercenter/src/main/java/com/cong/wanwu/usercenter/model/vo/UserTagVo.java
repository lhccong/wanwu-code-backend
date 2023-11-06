package com.cong.wanwu.usercenter.model.vo;

import lombok.Data;

/**
 * @author liuhuaicong
 * @date 2023/09/08
 */
@Data
public class UserTagVo {
    private Long id;
    private Long parentId;
    /**
     *名称
     */
    private String name;
    /**
     *颜色
     */
    private String color;
}
