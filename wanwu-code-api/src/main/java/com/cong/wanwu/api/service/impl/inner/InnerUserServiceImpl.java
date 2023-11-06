package com.cong.wanwu.api.service.impl.inner;

import com.cong.wanwu.api.common.service.InnerUserService;
import com.cong.wanwu.api.feign.FeignUser;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.common.model.dto.UserAccessKeyRequest;
import com.cong.wanwu.common.model.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

import static com.cong.wanwu.common.constant.SystemConstants.SALT;

/**
 * @author 86188
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private FeignUser feignUser;
    @Override
    public User getInvokerUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //feign调用获取用户信息
        UserAccessKeyRequest userAccessKeyRequest = UserAccessKeyRequest.builder().accessKey(accessKey).salt(SALT).build();
        BaseResponse<User> invokerUser = feignUser.getInvokerUser(userAccessKeyRequest);
        return invokerUser.getData();
    }
}
