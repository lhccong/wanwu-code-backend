package com.cong.wanwu.api.model.vo;


import com.cong.wanwu.api.common.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口信息封装类
 *
 * @author cong
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoVO extends InterfaceInfo {

    /**
     * 统计次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}