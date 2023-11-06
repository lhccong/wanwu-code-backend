package com.cong.wanwu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.usercenter.mapper.PostThumbMapper;
import com.cong.wanwu.usercenter.model.entity.Post;
import com.cong.wanwu.usercenter.model.entity.PostThumb;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.usercenter.service.PostService;
import com.cong.wanwu.usercenter.service.PostThumbService;
import javax.annotation.Resource;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.cong.wanwu.common.constant.RedisConstants.LIKE_NOTIFICATION_SUFFIX;
import static com.cong.wanwu.common.constant.RedisConstants.NOTIFICATION_PREFIX;
import static com.cong.wanwu.common.constant.SystemConstants.WANWU_POST_LIKE;

/**
 * 帖子点赞服务实现
 *
 *  @author 聪
 *  
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    private PostService postService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostThumb(long postId, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        PostThumbService postThumbService = (PostThumbService) AopContext.currentProxy();
        RLock lock = redissonClient.getLock(WANWU_POST_LIKE+userId); // 指定锁的名称
        lock.lock(); // 获取锁
        try {
            return postThumbService.doPostThumbInner(userId, postId);
            // 在这里执行需要被锁保护的代码
        } finally {
            lock.unlock(); // 释放锁
        }

    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param postId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostThumbInner(long userId, long postId) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> thumbQueryWrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb = this.getOne(thumbQueryWrapper);
        Post postServiceById = postService.getById(postId);
        String likeMsgCacheKey = NOTIFICATION_PREFIX + postServiceById.getUserId() + LIKE_NOTIFICATION_SUFFIX;
        boolean result;
        // 已点赞
        if (oldPostThumb != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = postService.update()
                        .eq("id", postId)
                        .gt("thumbNum", 0)
                        .setSql("thumbNum = thumbNum - 1")
                        .update();
                //redis缓存用户点赞消息通知
                int num = Integer.parseInt(Objects.requireNonNull(valueOperations.get(likeMsgCacheKey)));
                if (result && num> 0){
                    valueOperations.decrement(likeMsgCacheKey);
                }
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(postThumb);
            if (result) {
                // 点赞数 + 1
                result = postService.update()
                        .eq("id", postId)
                        .setSql("thumbNum = thumbNum + 1")
                        .update();
                //redis缓存用户点赞消息通知
                if (result){
                    valueOperations.increment(likeMsgCacheKey);
                }

                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

}




