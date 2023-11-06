package com.cong.wanwu.api;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liuhuaicong
 * @date 2023/10/12
 */
@SpringBootApplication
@MapperScan("com.cong.wanwu.api.mapper")
@EnableDubbo
@EnableFeignClients
@EnableDiscoveryClient
public class WanwuApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(WanwuApiApplication.class, args);
    }

}
