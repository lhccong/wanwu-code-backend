package com.cong.wanwu.bi.bizmq;

import com.cong.wanwu.bi.constant.CommonConstant;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.bi.manager.AiManager;
import com.cong.wanwu.bi.model.entity.Chart;
import com.cong.wanwu.bi.service.ChartService;
import com.cong.wanwu.common.exception.BusinessException;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 信息消费
 *
 * @author 86188
 * @date 2023/06/23
 */
@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;

    /**
     * 指定监听的消息队列和确认机制
     * @param message
     * @param channel
     * @param deliveryTags
     */
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME},ackMode = "MANUAL")
    @SneakyThrows
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTags){
        log.info("Bi收到消息Id{}",message);
        if (StringUtils.isBlank(message)){
            //如果失败消息拒绝
            channel.basicNack(deliveryTags,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart==null){
            channel.basicNack(deliveryTags,false,false);

            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
        }
        //先将任务修改为执行中，防止重复执行
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if(!b){
            channel.basicNack(deliveryTags,false,false);

            chartService.handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            return;
        }
        String response = aiManager.doChat(buildInput(chart));

//        String[] split = response.split("【【【【【");
        String[] split = response.split("```json");
//        if (split.length<3){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI生成错误");
//        }
        split = split[1].split("```");
//        if (split.length<2){
//            channel.basicNack(deliveryTags,false,false);
//
//            chartService.handleChartUpdateError(chart.getId(), "AI生成错误");
//            return;
//        }
        String genChart = split[0].trim();
        String genResult = "AI处理繁忙";
        if (split.length>=2){
            genResult= split[1].trim();
        }

        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        //todo 定义状态枚举
        updateChartResult.setStatus("succeed");
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        boolean updateResult = chartService.updateById(updateChartResult);
        if(!updateResult){
            channel.basicNack(deliveryTags,false,false);

            chartService.handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
        }
        //消息确认
        channel.basicAck(deliveryTags,false);
    }

    /**
     * 构建用户输入
     * @param chart
     * @return
     */
    private String buildInput(Chart chart){
        //用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        //拼接分析目标
        String userGoal = goal;
        if(StringUtils.isNotBlank(chartType)){
            userGoal +=",请使用"+chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }
}
