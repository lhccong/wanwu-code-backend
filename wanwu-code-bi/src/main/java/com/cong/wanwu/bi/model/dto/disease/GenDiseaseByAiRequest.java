package com.cong.wanwu.bi.model.dto.disease;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class GenDiseaseByAiRequest implements Serializable {


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


    private static final long serialVersionUID = 1L;
}