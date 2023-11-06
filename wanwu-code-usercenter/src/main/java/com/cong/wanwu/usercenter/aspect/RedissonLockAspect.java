package com.cong.wanwu.usercenter.aspect;


import cn.hutool.core.util.StrUtil;
import com.cong.wanwu.usercenter.service.impl.LockService;
import com.cong.wanwu.common.utils.SpElUtils;
import com.cong.wanwu.usercenter.annotation.RedissonLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Description: 分布式锁切面
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-20
 *
 * @author liuhuaicong
 * @date 2023/10/18
 */
@Slf4j
@Aspect
@Component
@Order(0)//确保比事务注解先执行，分布式锁在事务外
public class RedissonLockAspect {
    @Resource
    private LockService lockService;

    @Around("@annotation(com.cong.wanwu.usercenter.annotation.RedissonLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
        String prefix = StrUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();//默认方法限定名+注解排名（可能多个）
        String key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        return lockService.executeWithLockThrows(prefix + ":" + key, redissonLock.waitTime(), redissonLock.unit(), joinPoint::proceed);
    }
}
