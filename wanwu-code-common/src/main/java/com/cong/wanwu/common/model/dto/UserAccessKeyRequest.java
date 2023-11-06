package com.cong.wanwu.common.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author liuhuaicong
 * @date 2023/10/12
 */
@Data
@Builder
public class UserAccessKeyRequest {
    /**
     * accessKey
     */
    private String accessKey;
    /**
     * 加密判断标识
     */
    private String salt;
}
