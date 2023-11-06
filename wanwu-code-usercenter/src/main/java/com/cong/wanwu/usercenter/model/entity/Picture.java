package com.cong.wanwu.usercenter.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片
 *
 * @author 86188
 * @date 2023/03/13
 */
@Data
public class Picture  implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * url地址
     */
    private String url;
    private static final long serialVersionUID = 1L;
}
