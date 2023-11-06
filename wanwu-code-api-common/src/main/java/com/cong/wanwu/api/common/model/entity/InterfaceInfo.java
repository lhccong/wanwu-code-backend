package com.cong.wanwu.api.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息表
 *
 * @author liuhuaicong
 * @TableName interface_info
 * @date 2023/10/12
 */
@TableName(value ="interface_info")
@Data
public class InterfaceInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * 请求参数
     */
    private String requestParams;

    /**
     * 描述
     */
    private String description;
    /**
     * 接口示例
     */
    private String example;

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

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}