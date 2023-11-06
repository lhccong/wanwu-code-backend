package com.cong.wanwu.bi.model.dto.disease;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class DiseaseAddRequest implements Serializable {

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