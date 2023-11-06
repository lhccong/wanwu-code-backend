package com.cong.wanwu.api.model.dto.interfaceinfo;

import com.cong.wanwu.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 *
 * @author lhcong
 * @TableName InterfaceInfo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;
    /**
     * 创建人
     */
    private Long userId;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 请求类型
     */
    private String method;

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