package com.cong.wanwu.api.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求

 * @author lhcong
 * @TableName InterfaceInfo
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {


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
     * 响应头
     */
    private String responseHeader;



    private static final long serialVersionUID = 1L;
}