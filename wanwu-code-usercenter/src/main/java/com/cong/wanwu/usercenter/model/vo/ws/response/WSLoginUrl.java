package com.cong.wanwu.usercenter.model.vo.ws.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WSLOGIN URL
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 *
 * @author liuhuaicong
 * @date 2023/10/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginUrl {
    private String loginUrl;
}
