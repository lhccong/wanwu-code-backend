package com.cong.wanwu.usercenter.model.vo.ws.request;

import com.cong.wanwu.usercenter.model.enums.ws.WSReqTypeEnum;
import lombok.Data;

/**
 * Description: websocket前端请求体
 * Date: 2023-03-19
 */
@Data
public class WSBaseReq {
    /**
     * 请求类型 1.请求登录二维码，2心跳检测
     *
     * @see WSReqTypeEnum
     */
    private Integer type;

    /**
     * 接收的用户id
     */
    private Long uid;

    /**
     * 每个请求包具体的数据，类型不同结果不同
     */
    private String data;
}
