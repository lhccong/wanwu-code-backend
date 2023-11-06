package com.cong.wanwu.api.feign;

import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.model.dto.UserAccessKeyRequest;
import com.cong.wanwu.common.model.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author liuhuaicong
 * @date 2023/10/12
 */
@FeignClient(value = "wanwu-code-usercenter",url = "https://qingxin.store/wanwu/wanwu-usercenter")
public interface FeignUser {
    /**
     * 通过accessKey获取用户信息
     * @param userAccessKeyRequest 请求内容
     * @return {@link BaseResponse}<{@link User}>
     */
    @PostMapping(value = "/api/user/getInvokerUser")
    BaseResponse<User> getInvokerUser(UserAccessKeyRequest userAccessKeyRequest);
}
