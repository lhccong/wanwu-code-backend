package com.cong.wanwu.bi.model.dto.disease;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cong.wanwu.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DiseaseQueryRequest extends PageRequest implements Serializable {

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


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}