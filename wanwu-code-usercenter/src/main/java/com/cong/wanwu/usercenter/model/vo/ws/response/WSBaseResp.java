package com.cong.wanwu.usercenter.model.vo.ws.response;

import com.cong.wanwu.usercenter.model.enums.ws.WSReqTypeEnum;
import lombok.Data;

/**
 * Description: ws的基本返回信息体
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
public class WSBaseResp<T> {
    /**
     * ws推送给前端的消息
     *
     * @see WSReqTypeEnum
     */
    private Integer type;
    private T data;
}
