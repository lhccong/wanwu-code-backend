package com.cong.wanwu.bi.manager;

import com.cong.wanwu.common.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import com.cong.wanwu.common.common.ErrorCode;
import javax.annotation.Resource;

/**
 * 提供RedisLimiter限流基础服务
 *
 * @author 86188
 * @date 2023/05/26
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     * @param key 区分不同的限流器，比如不同的用户id应该分别统计
     */
    public void doRateLimit(String key){
        //创建一个限流器，每秒最多访问2次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);
        //每当操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }

}
