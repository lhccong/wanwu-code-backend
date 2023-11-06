package com.cong.wanwu.bi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 病症信息表
 * @TableName disease
 */
@TableName(value ="disease")
@Data
public class Disease implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建用户
     */
    private Long userId;

    /**
     * 用户确诊症状
     */
    private String name;

    /**
     * 用户性别
     */
    private Integer sex;

    /**
     * 所属科
     */
    private String diseaseType;

    /**
     * 用户描述病症
     */
    private String userDesc;

    /**
     * 生成的治疗计划
     */
    private String genResult;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}