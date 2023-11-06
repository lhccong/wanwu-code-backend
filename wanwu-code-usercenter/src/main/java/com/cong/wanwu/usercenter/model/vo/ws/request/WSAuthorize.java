package com.cong.wanwu.usercenter.model.vo.ws.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * wsauthorize
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
public class WSAuthorize {
    private String token;
}
