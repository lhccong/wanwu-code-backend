package com.cong.wanwu.bi.model.dto.chart;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 请求参数
 * 对应生成的 JSON 结构参考 resources/demo-json/request.json
 *
 * @author 86188
 * @date 2023/10/22
 */
@NoArgsConstructor
@Data
public class XhAiRequestDTO {

    @JsonProperty("header")
    private HeaderDTO header;
    @JsonProperty("parameter")
    private ParameterDTO parameter;
    @JsonProperty("payload")
    private PayloadDTO payload;

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class HeaderDTO {
        /**
         * 应用appid，从开放平台控制台创建的应用中获取
         */
        @JSONField(name = "app_id")
        private String appId;
        /**
         * 每个用户的id，用于区分不同用户，最大长度32
         */
        @JSONField(name = "uid")
        private String uid;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class ParameterDTO {
        private ChatDTO chat;

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        public static class ChatDTO {
            /**
             * 指定访问的领域,general指向V1.5版本 generalv2指向V2版本。注意：不同的取值对应的url也不一样！
             */
            @JsonProperty("domain")
            private String domain;
            /**
             * 核采样阈值。用于决定结果随机性，取值越高随机性越强即相同的问题得到的不同答案的可能性越高
             */
            @JsonProperty("temperature")
            private Float temperature;
            /**
             * 模型回答的tokens的最大长度
             */
            @JSONField(name = "max_tokens")
            private Integer maxTokens;
        }
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class PayloadDTO {
        @JsonProperty("message")
        private MessageDTO message;

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        public static class MessageDTO {
            @JsonProperty("text")
            private List<XhAiMsgDTO> text;
        }
    }
}
