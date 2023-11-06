package com.cong.wanwu.usercenter.model.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * WX爱心留言
 *
 * @author liuhuaicong
 * @date 2023/10/23
 */
@Data
public class WxLoveMessage {
    /**
     * 模板标识
     */
    String template_id;

    /**
     * 用户
     */
    String touser;

    /**
     * 网址
     */
    String url;

    /**
     * 数据
     */
    Map<String,WxLoveData> data = new HashMap<>();
}
