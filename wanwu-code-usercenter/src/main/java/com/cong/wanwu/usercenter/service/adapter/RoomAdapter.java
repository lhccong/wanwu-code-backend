package com.cong.wanwu.usercenter.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessageReq;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatRoomResp;
import com.cong.wanwu.usercenter.model.entity.chat.Room;
import com.cong.wanwu.usercenter.model.entity.chat.RoomGroup;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房间适配器
 * Description: 消息适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 *
 * @author liuhuaicong
 * @date 2023/11/01
 */
public class RoomAdapter {


    public static List<ChatRoomResp> buildResp(List<Room> list) {
        return list.stream()
                .map(a -> {
                    ChatRoomResp resp = new ChatRoomResp();
                    BeanUtil.copyProperties(a, resp);
                    resp.setLastActiveTime(a.getActiveTime());
                    return resp;
                }).collect(Collectors.toList());
    }
}
