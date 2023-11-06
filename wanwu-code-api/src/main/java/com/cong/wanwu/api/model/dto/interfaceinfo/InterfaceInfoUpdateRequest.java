package com.cong.wanwu.api.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @author lhcong
 * @TableName InterfaceInfo
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;


    /**
     * 接口名称
     */
    private String name;

    /**
     * 请求类型
     */
    private String method;
    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 接口状态（0-关闭  1-开启）
     */
    private Integer status;

    /**
     * 响应头
     */
    private String responseHeader;
}