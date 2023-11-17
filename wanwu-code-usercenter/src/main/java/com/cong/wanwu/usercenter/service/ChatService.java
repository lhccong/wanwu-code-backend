package com.cong.wanwu.usercenter.service;



import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import com.cong.wanwu.common.model.vo.response.CursorPageBaseResp;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessageMarkReq;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessagePageReq;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessageReq;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMemberResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMemberStatisticResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMessageResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatRoomResp;
import com.cong.wanwu.usercenter.model.entity.chat.Message;
import com.cong.wanwu.usercenter.model.vo.message.RoomFriendVo;

import javax.annotation.Nullable;

/**
 * 聊天服务
 * Description: 消息处理类
 * @author liuhuaicong
 * @date 2023/10/31
 */
public interface ChatService {

    /**
     * 发送消息
     *
     * @param request
     */
    Long sendMsg(ChatMessageReq request, Long uid);

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param message
     * @param receiveUid 接受消息的uid，可null
     * @return
     */
    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param msgId
     * @param receiveUid 接受消息的uid，可null
     * @return
     */
    ChatMessageResp getMsgResp(Long msgId, Long receiveUid);

    /**
     * 获取群成员列表
     *
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMemberResp> getMemberPage(CursorPageBaseReq request);

    /**
     * 获取消息列表
     *
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, @Nullable Long receiveUid);

    /**
     * 获取会话列表
     *
     * @param request
     * @param uid
     * @return
     */
    CursorPageBaseResp<ChatRoomResp> getRoomPage(CursorPageBaseReq request, Long uid);

    /**
     * 获取私聊列表
     *
     * @param request

     * @return
     */
    CursorPageBaseResp<RoomFriendVo> getRoomFriendPage(CursorPageBaseReq request);
    /**
     * 获取成员统计信息
     *
     * @return {@link ChatMemberStatisticResp}
     */
    ChatMemberStatisticResp getMemberStatistic();

    /**
     * 设置消息标记
     *
     * @param uid     uid
     * @param request 请求
     */
    void setMsgMark(Long uid, ChatMessageMarkReq request);


    /**
     * @param uid uid
     * @return {@link ChatRoomResp}
     */
    RoomFriendVo getRoomByTargetUid(Long uid);
}
