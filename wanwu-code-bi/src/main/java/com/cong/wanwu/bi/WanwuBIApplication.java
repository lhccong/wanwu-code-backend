package com.cong.wanwu.bi;

import com.cong.wanwu.bi.bizmq.BiMqConstant;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
// todo 如需开启 Redis，须移除 exclude 中的内容
@SpringBootApplication()
@MapperScan("com.cong.wanwu.bi.mapper")
@EnableScheduling
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class WanwuBIApplication {
    @Value("${spring.rabbitmq.host}")
    private static String host;

    @Value("${spring.rabbitmq.username}")
    private static String username;

    @Value("${spring.rabbitmq.password}")
    private static String password;

    @Value("${spring.rabbitmq.port}")
    private static Integer port;

    public static void main(String[] args) {
        initMq();
        SpringApplication.run(WanwuBIApplication.class, args);
    }
    public static void initMq() {

        try {
            String EXCHANGE_NAME = BiMqConstant.BI_EXCHANGE_NAME;
            ConnectionFactory factory = new ConnectionFactory();
            //服务器地址
            factory.setHost(host);
            //账号
            factory.setUsername(username);
            //密码
            factory.setPassword(password);
            //端口号
            factory.setPort(port);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            //创建分配一个队列
            String queueName = BiMqConstant.BI_QUEUE_NAME;
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY);
            System.out.println("创建交换机队列成功");
        } catch (Exception e) {

        }

    }


}
