package com.cong.wanwu.bi.model.dto.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * XH AI 响应 DTO
 * 返回参数
 * 对应生成的 JSON 结构参考 resources/demo-json/response.json
 *
 * @author 86188
 * @date 2023/10/22
 */
@NoArgsConstructor
@Data
public class XhAiResponseDTO {

    @JsonProperty("header")
    private HeaderDTO header;
    @JsonProperty("payload")
    private PayloadDTO payload;

    @NoArgsConstructor
    @Data
    public static class HeaderDTO {
        /**
         * 错误码，0表示正常，非0表示出错
         */
        @JsonProperty("code")
        private Integer code;
        /**
         * 会话是否成功的描述信息
         */
        @JsonProperty("message")
        private String message;
        /**
         * 会话的唯一id，用于讯飞技术人员查询服务端会话日志使用,出现调用错误时建议留存该字段
         */
        @JsonProperty("sid")
        private String sid;
        /**
         * 会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果
         */
        @JsonProperty("status")
        private Integer status;
    }

    @NoArgsConstructor
    @Data
    public static class PayloadDTO {
        @JsonProperty("choices")
        private ChoicesDTO choices;
        /**
         * 在最后一次结果返回
         */
        @JsonProperty("usage")
        private UsageDTO usage;

        @NoArgsConstructor
        @Data
        public static class ChoicesDTO {
            /**
             * 文本响应状态，取值为[0,1,2]; 0代表首个文本结果；1代表中间文本结果；2代表最后一个文本结果
             */
            @JsonProperty("status")
            private Integer status;
            /**
             * 返回的数据序号，取值为[0,9999999]
             */
            @JsonProperty("seq")
            private Integer seq;
            /**
             * 响应文本
             */
            @JsonProperty("text")
            private List<XhAiMsgDTO> text;

        }

        @NoArgsConstructor
        @Data
        public static class UsageDTO {
            @JsonProperty("text")
            private TextDTO text;

            @NoArgsConstructor
            @Data
            public static class TextDTO {
                /**
                 * 保留字段，可忽略
                 */
                @JsonProperty("question_tokens")
                private Integer questionTokens;
                /**
                 * 包含历史问题的总tokens大小
                 */
                @JsonProperty("prompt_tokens")
                private Integer promptTokens;
                /**
                 * 回答的tokens大小
                 */
                @JsonProperty("completion_tokens")
                private Integer completionTokens;
                /**
                 * prompt_tokens和completion_tokens的和，也是本次交互计费的tokens大小
                 */
                @JsonProperty("total_tokens")
                private Integer totalTokens;
            }
        }
    }
}
