package com.cong.wanwu.usercenter.model.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WSSchannel 额外 DTO
 * Description: 记录和前端连接的一些映射信息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-21
 *
 * @author liuhuaicong
 * @date 2023/10/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSChannelExtraDTO {
    /**
     * 前端如果登录了，记录uid
     */
    private Long uid;
}
