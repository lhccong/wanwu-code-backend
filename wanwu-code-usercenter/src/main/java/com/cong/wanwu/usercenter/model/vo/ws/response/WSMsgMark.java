package com.cong.wanwu.usercenter.model.vo.ws.response;

import com.cong.wanwu.usercenter.model.enums.chat.MessageMarkActTypeEnum;
import com.cong.wanwu.usercenter.model.enums.chat.MessageMarkTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMsgMark {
    private List<WSMsgMarkItem> markList;

    @Data
    public static class WSMsgMarkItem {
        @ApiModelProperty("操作者")
        private Long uid;
        @ApiModelProperty("消息id")
        private Long msgId;
        /**
         * @see MessageMarkTypeEnum
         */
        @ApiModelProperty("标记类型 1点赞 2举报")
        private Integer markType;
        @ApiModelProperty("被标记的数量")
        private Integer markCount;
        /**
         * @see MessageMarkActTypeEnum
         */
        @ApiModelProperty("动作类型 1确认 2取消")
        private Integer actType;
    }
}
