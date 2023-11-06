package com.cong.wanwu.usercenter.model.vo;

import lombok.Data;

/**
 * 七牛云签名任认证
 * @author liuhuaicong
 * @date 2023/09/11
 */
@Data
public class QiNiuPolicyVo {

    /**
     *凭证
     */
    String token;

    /**
     *存入外链默认域名，用于拼接完整的资源外链路径
     */
    String domain;

    /**
     * 存放文件夹
     */
    String dir;
}
