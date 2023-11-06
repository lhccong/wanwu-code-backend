package com.cong.wanwu.api.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户模拟请求
 *
 * @author lhcong
 * @TableName InterfaceInfo
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;


    /**
     * 用户请求参数
     */
    private String userRequestParams;


}