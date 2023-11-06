package com.cong.wanwu.usercenter.model.vo.message;

import lombok.Data;

import java.util.Date;

/**
 * 消息 VO
 *
 * @author liuhuaicong
 * @date 2023/10/31
 */
@Data
public class MessageVo {
    private String avatar;

    /**
     * 会话表id
     */
    private Long roomId;

    /**
     * 消息发送者uid
     */
    private Long fromUid;

    /**
     * 消息内容
     */
    private String content;



    /**
     * 与回复的消息间隔多少条
     */
    private Integer gapCount;


    /**
     * 扩展信息
     */
    private String extra;

    /**
     * 创建时间
     */
    private Date createTime;
}
