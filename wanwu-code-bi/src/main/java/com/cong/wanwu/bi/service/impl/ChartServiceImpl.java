package com.cong.wanwu.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.bi.constant.CommonConstant;
import com.cong.wanwu.bi.service.ChartService;
import com.cong.wanwu.bi.model.dto.chart.ChartQueryRequest;
import com.cong.wanwu.bi.model.entity.Chart;
import com.cong.wanwu.bi.mapper.ChartMapper;
import com.cong.wanwu.bi.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
* @author 86188
* @description 针对表【chart(图表信息)】的数据库操作Service实现
* @createDate 2023-04-29 09:57:48
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {
    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }

        Long id = chartQueryRequest.getId();
        Long userId = chartQueryRequest.getUserId();
        String goal = chartQueryRequest.getGoal();
        String name = chartQueryRequest.getName();
        String chartType = chartQueryRequest.getChartType();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();
                // 拼接查询条件

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(goal), "goal", goal);
        queryWrapper.like(ObjectUtils.isNotEmpty(name), "name", name);
        queryWrapper.eq(ObjectUtils.isNotEmpty(chartType), "chartType", chartType);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
    @Override
    public void handleChartUpdateError(long chartId, String execMessage) {
        Chart errorChart = new Chart();
        errorChart.setStatus("failed");
        errorChart.setId(chartId);
        errorChart.setExecMessage(execMessage);
        boolean b = this.updateById(errorChart);
        if (!b){
            log.error("更新图表失败状态错误"+"chartId:"+chartId+execMessage);
        }

    }
}




