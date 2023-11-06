package com.cong.wanwu.gateway.filter;


import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.cong.wanwu.api.client.sdk.model.SdkUser;
import com.cong.wanwu.api.client.sdk.utils.SignUtils;
import com.cong.wanwu.api.common.service.InnerInterfaceInfoService;
import com.cong.wanwu.api.common.service.InnerUserInterfaceInfoService;
import com.cong.wanwu.api.common.service.InnerUserService;
import com.cong.wanwu.common.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 自定义全局过滤器
 *
 * @author 86188
 * @date 2022/12/05
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Collections.singletonList("127.0.0.1");
    private static final String INTERFACE_HOST  = "http://localhost:8123";

    private static final String REQUEST_FILTER_PREFIXX  = "apiRequest";
    /**
     * 全局过滤器
     *
     * @param exchange 交换
     * @param chain    链
     * @return {@link Mono}<{@link Void}>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.用户发送请求到API网关
        //2,请求日志
        ServerHttpRequest request = exchange.getRequest();
        //请求路径
//        String path = INTERFACE_HOST + request.getPath().value();
        String path = request.getPath().value();
        //请求路径
        String method = request.getMethod().toString();

        log.info("请求唯一标识{}", request.getId());
        log.info("请求路径{}", request.getPath().value());
        log.info("请求方法{}", request.getMethod());
        log.info("请求参数{}", request.getQueryParams());
        log.info("请求来源地址{}", request.getRemoteAddress());
        String sourceAddress = Objects.requireNonNull(request.getLocalAddress()).getHostString();
        log.info("请求来源地址{}", sourceAddress);
        ServerHttpResponse response = exchange.getResponse();
        //3.(黑白名单)
//        if (!IP_WHITE_LIST.contains(sourceAddress)) {
//            handleNoAuth(response);
//        }
        //4,用户鉴权（判断ak、sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String apiRequest = headers.getFirst("ApiRequest");
        if (CharSequenceUtil.isEmpty(apiRequest)){
           return chain.filter(exchange);
        }
        String accessKey = headers.getFirst("accessKey");
        String once = headers.getFirst("once");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // 实际情况应该是去数据库中查是否己分配给用户

        User invokerUser = null;
        try {
            invokerUser = innerUserService.getInvokerUser(accessKey);

        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokerUser == null) {
            handleNoAuth(response);
        }
//        if (!"cong".equals(accessKey)){
//            handleNoAuth(response);
//        }
        if (Long.parseLong(once) > 100000) {
            handleNoAuth(response);
        }
        // 时间和当前时间不能超过五分钟 [timestamp]
        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        assert timestamp != null;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);

        }
        // 实际情况从数据库中查出 secretKey
        assert body != null;
        assert invokerUser != null;
        String serverSign = SignUtils.getSign(body, invokerUser.getSecretKey());
        if (!serverSign.equals(sign)) {
            return handleNoAuth(response);
        }
        //调用成功后
        //5.请求的模拟接口是否存在☒ 从数据库中查询模拟接口是否存在，以及请求方法是否匹配，（还可以校验）
        com.cong.wanwu.api.common.model.entity.InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);

        } catch (Exception e) {
            log.error("getinterfaceInfo error", e);
        }

        if (interfaceInfo == null) {
            handleNoAuth(response);
        }
        //6.请求转发，调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);
        //todo 是否还有调用次数
        //7.响应日志
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokerUser.getId());
//        log.info("响应："+response.getStatusCode());

        //9。调用失败，返回一个规范的错误码
//        if (response.getStatusCode()!=HttpStatus.OK){
//           return handleInvokeError(response);
//        }
//        log.info("custom global filter");
//        return filter;
    }

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            String path = exchange.getRequest().getPath().pathWithinApplication().value();
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                //装饰增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    //等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //往返回值里写数据
                            //拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 8.调用成功，接口调用次数+1 invokeCount
                                try {
                                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                } catch (Exception e) {
                                    log.error("invokeCount error",e);
                                }
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                //打印日志
                                log.info("响应结果{}", data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置response对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            //降级处理返回数据
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("网关处理异常{}" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}
