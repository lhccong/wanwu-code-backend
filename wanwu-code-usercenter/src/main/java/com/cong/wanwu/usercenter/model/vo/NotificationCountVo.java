package com.cong.wanwu.usercenter.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 消息通知数量
 * @author liuhuaicong
 * @date 2023/09/14
 */
@Data
@AllArgsConstructor
public class NotificationCountVo {
    private Integer likeCount;
    private Integer commentCount;
    private Integer msgCount;
    private Integer noticeCount;
}
