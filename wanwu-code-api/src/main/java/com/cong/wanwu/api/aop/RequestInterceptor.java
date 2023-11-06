package com.cong.wanwu.api.aop;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * 请求 AOP
 *
 * @author cong
 **/
@Aspect
@Component
@Slf4j
public class RequestInterceptor {

    /**
     * 请求响应日志
     */
    @Around("execution(* com.cong.wanwu.api.controller.*.*(..))")
    public Object logInterceptor(ProceedingJoinPoint point) throws Throwable {
        // count time
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // get request path
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        // traceId
        String requestId = UUID.randomUUID().toString();
        String url = httpServletRequest.getRequestURI();
        // get request params
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
                httpServletRequest.getRemoteHost(), reqParam);
        // get response
        Object result = point.proceed();
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        return result;
    }
}

