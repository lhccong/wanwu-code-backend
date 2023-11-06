package com.cong.wanwu.usercenter.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.common.utils.discover.PrioritizedUrlTitleDiscover;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessageReq;

import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMessageResp;
import com.cong.wanwu.usercenter.model.entity.chat.Message;
import com.cong.wanwu.usercenter.model.enums.chat.MessageStatusEnum;
import com.cong.wanwu.usercenter.model.vo.message.MessageExtra;
import com.cong.wanwu.usercenter.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 消息适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-26
 */
public class MessageAdapter {

    public static final int CAN_CALLBACK_GAP_COUNT = 50;
    private static final PrioritizedUrlTitleDiscover URL_TITLE_DISCOVER = new PrioritizedUrlTitleDiscover();

    public static Message buildMsgSave(ChatMessageReq request, Long uid) {

        return Message.builder()
                .replyMsgId(request.getReplyMsgId())
                .content(request.getContent())
                .fromUid(uid)
                .roomId(request.getRoomId())
                .status(MessageStatusEnum.NORMAL.getStatus())
                .extra(JSONUtil.toJsonStr(buildExtra(request)))
                .build();

    }

    private static MessageExtra buildExtra(ChatMessageReq request) {
        Map<String, String> contentTitleMap = URL_TITLE_DISCOVER.getContentTitleMap(request.getContent());
        return MessageExtra.builder().urlTitleMap(contentTitleMap).build();
    }

    public static List<ChatMessageResp> buildMsgResp(List<Message> messages,Long receiveUid) {
        return messages.stream().map(a -> {
                    ChatMessageResp resp = new ChatMessageResp();
                    resp.setFromUser(buildFromUser(a.getFromUid()));
                    resp.setMessage(buildMessage(a, receiveUid));
                    return resp;
                })
                .sorted(Comparator.comparing(a -> a.getMessage().getSendTime()))//帮前端排好序，更方便它展示
                .collect(Collectors.toList());
    }
    private static ChatMessageResp.Message buildMessage(Message message , Long receiveUid) {
        ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message, messageVO);
        messageVO.setSendTime(message.getCreateTime());
        return messageVO;
    }
//
//    private static ChatMessageResp.MessageMark buildMsgMark(List<MessageMark> marks, Long receiveUid) {
//        Map<Integer, List<MessageMark>> typeMap = marks.stream().collect(Collectors.groupingBy(MessageMark::getType));
//        List<MessageMark> likeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.LIKE.getType(), new ArrayList<>());
//        List<MessageMark> dislikeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.DISLIKE.getType(), new ArrayList<>());
//        ChatMessageResp.MessageMark mark = new ChatMessageResp.MessageMark();
//        mark.setLikeCount(likeMarks.size());
//        mark.setUserLike(Optional.ofNullable(receiveUid).filter(uid -> likeMarks.stream().anyMatch(a -> a.getUid().equals(uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
//        mark.setDislikeCount(dislikeMarks.size());
//        mark.setUserDislike(Optional.ofNullable(receiveUid).filter(uid -> dislikeMarks.stream().anyMatch(a -> a.getUid().equals(uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
//        return mark;
//    }
//
private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
    ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
    userInfo.setUid(fromUid);
    return userInfo;
}


}
