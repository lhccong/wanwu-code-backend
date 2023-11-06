package com.cong.wanwu.bi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cong.wanwu.bi.model.dto.chart.ChartQueryRequest;
import com.cong.wanwu.bi.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86188
* @description 针对表【chart(图表信息)】的数据库操作Service
* @createDate 2023-04-29 09:57:48
*/
public interface ChartService extends IService<Chart> {

    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);
    void handleChartUpdateError(long chartId,String execMessage);
}
