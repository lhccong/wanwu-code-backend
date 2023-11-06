package com.cong.wanwu.usercenter.service.impl;

import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 锁具服务
 *
 * @author liuhuaicong
 * @date 2023/10/18
 */
@Service
@Slf4j
public class LockService {

    @Resource
    private RedissonClient redissonClient;

    public <T> T executeWithLockThrows(String key, int waitTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        boolean lockSuccess = lock.tryLock(waitTime, unit);
        if (!lockSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        try {
            return supplier.get();//执行锁内的代码逻辑
        } finally {
            lock.unlock();
        }
    }
    @SneakyThrows
    public <T> T executeWithLock(String key, int waitTime, TimeUnit unit, Supplier<T> supplier) {
        return executeWithLockThrows(key, waitTime, unit, supplier::get);
    }

    @FunctionalInterface
    public interface SupplierThrow<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }
}