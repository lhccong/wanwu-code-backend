package com.cong.wanwu.bi.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息发送
 *
 * @author 86188
 * @date 2023/06/23
 */
@Component
public class MyMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange,String routingKey,String message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }
}
