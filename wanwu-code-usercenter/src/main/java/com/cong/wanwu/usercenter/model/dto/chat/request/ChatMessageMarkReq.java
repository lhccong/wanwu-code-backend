package com.cong.wanwu.usercenter.model.dto.chat.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 聊天消息标记请求
 * Description: 消息标记请求
 * Date: 2023-03-29
 *
 * @author liuhuaicong
 * @date 2023/10/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkReq {
    @NotNull
    @ApiModelProperty("消息id")
    private Long msgId;

    @NotNull
    @ApiModelProperty("标记类型 1点赞 2举报")
    private Integer markType;

    @NotNull
    @ApiModelProperty("动作类型 1确认 2取消")
    private Integer actType;
}
