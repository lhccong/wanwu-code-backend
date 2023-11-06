package com.cong.wanwu.usercenter.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author liuhuaicong
 * @date 2023/10/18
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinIoClientConfig {
 
    /**
     * 访问密钥
     */
    private String accessKey;
 
    /**
     * 密钥
     */
    private String secretKey;
 
    /**
     * 访问api Url
     */
    private String endpoint;
 
    /**
     * 捅名称
     */
    private String bucketName;
 
    /**
     * 创建MinIo客户端
     *
     * @return
     */
    @Bean
    public MinioClient minioClient() {
 
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
 
}