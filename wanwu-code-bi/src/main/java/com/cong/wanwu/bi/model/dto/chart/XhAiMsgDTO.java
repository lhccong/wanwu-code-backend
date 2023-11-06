package com.cong.wanwu.bi.model.dto.chart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息对象
 *
 * @author 86188
 * @date 2023/10/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XhAiMsgDTO {
    /**
     * 角色
     */
    private String role;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 响应结果字段：结果序号，取值为[0,10]; 当前为保留字段，开发者可忽略
     */
    private Integer index;

    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    public static XhAiMsgDTO createUserMsg(String content) {
        return new XhAiMsgDTO(ROLE_USER, content, null);
    }

    public static XhAiMsgDTO createAssistantMsg(String content) {
        return new XhAiMsgDTO(ROLE_ASSISTANT, content, null);
    }

}
