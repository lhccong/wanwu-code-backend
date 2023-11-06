package com.cong.wanwu.usercenter.model.vo.message;

import lombok.Data;

import java.util.Date;

/**
 * 室友VO
 *
 * @author liuhuaicong
 * @date 2023/11/01
 */
@Data
public class RoomFriendVo {

    private Long id;
    private Long roomId;
    private Long fromUid;
    private String fromUsername;
    private String avatar;

    private Integer unread;
    private String lastMessage;
    private Date updateTime;
}
