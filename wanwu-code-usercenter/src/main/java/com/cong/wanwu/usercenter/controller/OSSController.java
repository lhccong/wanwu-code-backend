package com.cong.wanwu.usercenter.controller;

import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.usercenter.model.vo.QiNiuPolicyVo;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liuhuaicong
 */
@RestController
@Data
@RequestMapping(value = "/member/oss")
@ConfigurationProperties(prefix = "third-party.qiniuyun")
public class OSSController {
    // 访问授权码
    private String accessKey;
    // 秘密钥匙
    private String secretKey;
    // 空间名称
    private String bucket;
    // 外链域名
    private String domain;

    @GetMapping("/policy")
    public BaseResponse<QiNiuPolicyVo> policy() {
        //验证七牛云身份是否通过
        Auth auth = Auth.create(accessKey, secretKey);
        //生成凭证
        String upToken = auth.uploadToken(bucket);
        QiNiuPolicyVo qiNiuPolicyVo = new QiNiuPolicyVo();
        qiNiuPolicyVo.setToken(upToken);
        //存入外链默认域名，用于拼接完整的资源外链路径
        qiNiuPolicyVo.setDomain(domain);

        //生成文件夹名
        String dir = "wanwu/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        qiNiuPolicyVo.setDir(dir);
        return ResultUtils.success(qiNiuPolicyVo);
    }
}
