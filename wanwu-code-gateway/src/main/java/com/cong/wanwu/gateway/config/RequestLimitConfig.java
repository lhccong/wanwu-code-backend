package com.cong.wanwu.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 请求限流配置类
 * @author lhc
 * @date 2022-10-03 13:44
 */
@Configuration
public class RequestLimitConfig {
    /**
     *     针对某一个接口 ip来限流 /doLogin 每一个ip  10s访问3次  @Primary //主要的
     * @return
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver(){
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getHeaders().getHost()).getHostString());
    }

    /**
     * 针对路径限制  /doLogin
     * @return
     */
    @Bean
    public KeyResolver apiKeyResolver(){
        return exchange -> Mono.just(exchange.getRequest().getPath().value());

    }
}
