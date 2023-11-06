package com.cong.wanwu.bi.bizmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
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
public class MyMessageConsumer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 指定监听的消息队列和确认机制
     * @param message
     * @param channel
     * @param deliveryTags
     */
    @RabbitListener(queues = {"code-queue"},ackMode = "MANUAL")
    @SneakyThrows
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTags){
        log.info("收到消息啦->{}",message);
        channel.basicAck(deliveryTags,false);
    }
}
