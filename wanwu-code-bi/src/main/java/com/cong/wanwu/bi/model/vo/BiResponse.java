package com.cong.wanwu.bi.model.vo;

import lombok.Data;

/**
 * bi响应
 *
 * @author 86188
 * @date 2023/05/19
 */
@Data
public class BiResponse {
    private String genChart;
    private String genResult;

    private Long chartId;
}
