package com.cong.wanwu.usercenter.model.entity.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 室友
 * 单聊房间
 *
 * @author liuhuaicong
 * @TableName room_friend
 * @date 2023/10/31
 */
@TableName(value ="room_friend")
@Data
@Builder
public class RoomFriend implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 房间id
     */
    private Long roomId;

    /**
     * uid1（更小的uid）
     */
    private Long uid1;

    /**
     * uid2（更大的uid）
     */
    private Long uid2;

    /**
     * 房间key由两个uid拼接，先做排序uid1_uid2
     */
    private String roomKey;

    /**
     * 房间状态 0正常 1禁用(删好友了禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}