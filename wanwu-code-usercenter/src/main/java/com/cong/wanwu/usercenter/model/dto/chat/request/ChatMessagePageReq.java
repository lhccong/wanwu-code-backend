package com.cong.wanwu.usercenter.model.dto.chat.request;

import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 聊天消息页面请求
 * Description: 消息列表请求
 * @author liuhuaicong
 * @date 2023/10/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessagePageReq extends CursorPageBaseReq {
    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;
}
