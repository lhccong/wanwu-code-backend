package com.cong.wanwu.bi.model.dto.chart;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class ChartQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */

    private Long id;
    /**
     * 图标名称
     */
    private String name;
    /**
     * 创建用户
     */

    private Long userId;

    /**
     * 分析目标
     */
    private String goal;


    /**
     * 图表类型
     */
    private String chartType;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}