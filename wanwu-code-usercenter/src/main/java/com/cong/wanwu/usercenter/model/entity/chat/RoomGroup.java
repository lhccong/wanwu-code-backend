package com.cong.wanwu.usercenter.model.entity.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 会议室组
 * 群聊房间表
 *
 * @author liuhuaicong
 * @TableName room_group
 * @date 2023/10/31
 */
@TableName(value ="room_group")
@Data
public class RoomGroup implements Serializable {
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
     * 群名称
     */
    private String name;

    /**
     * 群头像
     */
    private String avatar;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    private Object extJson;

    /**
     * 逻辑删除(0-正常,1-删除)
     */
    private Integer deleteStatus;

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