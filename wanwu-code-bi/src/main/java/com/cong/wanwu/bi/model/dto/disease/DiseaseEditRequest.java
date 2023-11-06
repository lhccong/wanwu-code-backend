package com.cong.wanwu.bi.model.dto.disease;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 编辑请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class DiseaseEditRequest implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}