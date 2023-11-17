package com.cong.wanwu.usercenter.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import com.cong.wanwu.common.model.vo.response.CursorPageBaseResp;
import com.cong.wanwu.common.utils.AssertUtil;
import com.cong.wanwu.common.utils.BeanCopyUtils;
import com.cong.wanwu.usercenter.common.event.MessageSendEvent;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessageMarkReq;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessagePageReq;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessageReq;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMemberResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMemberStatisticResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMessageResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatRoomResp;
import com.cong.wanwu.usercenter.model.entity.chat.Message;
import com.cong.wanwu.usercenter.model.entity.chat.Room;
import com.cong.wanwu.usercenter.model.entity.chat.RoomFriend;
import com.cong.wanwu.usercenter.model.vo.message.RoomFriendVo;
import com.cong.wanwu.usercenter.service.*;
import com.cong.wanwu.usercenter.service.adapter.MessageAdapter;
import com.cong.wanwu.usercenter.service.adapter.RoomAdapter;
import com.cong.wanwu.usercenter.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

/**
 * 聊天服务实现
 * Description: 消息处理类
 * @author liuhuaicong
 * @date 2023/10/31
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    public static final long ROOM_GROUP_ID = 1L;
    @Resource
    private MessageService messageService;
    @Resource
    private RoomService roomService;
    @Resource
    private UserService userService;
    @Resource
    private RoomFriendService roomFriendService;
    @Resource
    private UserCache userCache;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发送消息
     */
    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        //校验下回复消息
        Message replyMsg = null;
        if (Objects.nonNull(request.getReplyMsgId())) {
            replyMsg = messageService.getById(request.getReplyMsgId());
            AssertUtil.isNotEmpty(replyMsg, "回复消息不存在");
            AssertUtil.equal(replyMsg.getRoomId(), request.getRoomId(), "只能回复相同会话内的消息");

        }
        //同步获取消息的跳转链接标题
        Message insert = MessageAdapter.buildMsgSave(request, uid);
        messageService.save(insert);
        //如果有回复消息
        if (Objects.nonNull(replyMsg)) {
            Integer gapCount = messageService.getGapCount(request.getRoomId(), replyMsg.getId(), insert.getId());
            messageService.updateGapCount(insert.getId(), gapCount);
        }
        //发布消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, insert.getId()));
        return insert.getId();
    }

    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return null;
    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
        return null;
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(CursorPageBaseReq request) {
        return null;
    }


    @Override
    public void setMsgMark(Long uid, ChatMessageMarkReq request) {

    }

    @Override
    public RoomFriendVo getRoomByTargetUid(Long uid) {
        Long loginUserId = Long.valueOf(StpUtil.getLoginId().toString());
        RoomFriend roomFriend = roomFriendService.getOne(new LambdaQueryWrapper<RoomFriend>().eq(RoomFriend::getUid1,uid).eq(RoomFriend::getUid2,loginUserId)
                .or().eq(RoomFriend::getUid2,uid).eq(RoomFriend::getUid1,loginUserId));
        User user = userService.getById(uid);
        if (roomFriend==null){
            RoomFriendVo roomFriendVo = new RoomFriendVo();
            roomFriendVo.setId(-1L);
            roomFriendVo.setRoomId(-1L);
            roomFriendVo.setFromUid(loginUserId);
            roomFriendVo.setFromUsername(user.getUserName());
            roomFriendVo.setAvatar(user.getUserAvatar());
            roomFriendVo.setUnread(1);
            roomFriendVo.setLastMessage("");
            roomFriendVo.setUpdateTime(new Date());
            return roomFriendVo;
        }
        RoomFriendVo roomFriendVo = BeanCopyUtils.copyBean(roomFriend, RoomFriendVo.class);
        roomFriendVo.setFromUid(loginUserId);
        roomFriendVo.setFromUsername(user.getUserName());
        roomFriendVo.setAvatar(user.getUserAvatar());
        roomFriendVo.setUnread(1);
        roomFriendVo.setLastMessage("");
        roomFriendVo.setUpdateTime(new Date());
        return roomFriendVo;

    }

//    @Override
//    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
//        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message), receiveUid));
//    }
//
//    @Override
//    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
//        Message msg = messageService.getById(msgId);
//        return getMsgResp(msg, receiveUid);
//    }
//
//    @Override
//    public CursorPageBaseResp<ChatMemberResp> getMemberPage(CursorPageBaseReq request) {
//        Pair<ChatActiveStatusEnum, String> pair = ChatMemberHelper.getCursorPair(request.getCursor());
//        ChatActiveStatusEnum activeStatusEnum = pair.getKey();
//        String timeCursor = pair.getValue();
//        List<ChatMemberResp> resultList = new ArrayList<>();//最终列表
//        Boolean isLast = Boolean.FALSE;
//        if (activeStatusEnum == ChatActiveStatusEnum.ONLINE) {//在线列表
//            CursorPageBaseResp<Pair<Long, Double>> cursorPage = userCache.getOnlineCursorPage(new CursorPageBaseReq(request.getPageSize(), timeCursor));
//            resultList.addAll(memberAdapter.buildMember(cursorPage.getList(), ChatActiveStatusEnum.ONLINE));//添加在线列表
//            if (cursorPage.getIsLast()) {//如果是最后一页,从离线列表再补点数据
//                Integer leftSize = request.getPageSize() - cursorPage.getList().size();
//                cursorPage = userCache.getOfflineCursorPage(new CursorPageBaseReq(leftSize, null));
//                resultList.addAll(memberAdapter.buildMember(cursorPage.getList(), ChatActiveStatusEnum.OFFLINE));//添加离线线列表
//                activeStatusEnum = ChatActiveStatusEnum.OFFLINE;
//            }
//            timeCursor = cursorPage.getCursor();
//            isLast = cursorPage.getIsLast();
//        } else if (activeStatusEnum == ChatActiveStatusEnum.OFFLINE) {//离线列表
//            CursorPageBaseResp<Pair<Long, Double>> cursorPage = userCache.getOfflineCursorPage(new CursorPageBaseReq(request.getPageSize(), timeCursor));
//            resultList.addAll(memberAdapter.buildMember(cursorPage.getList(), ChatActiveStatusEnum.OFFLINE));//添加离线线列表
//            timeCursor = cursorPage.getCursor();
//            isLast = cursorPage.getIsLast();
//        }
//        //组装结果
//        return new CursorPageBaseResp<>(ChatMemberHelper.generateCursor(activeStatusEnum, timeCursor), isLast, resultList);
//    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long receiveUid) {
        CursorPageBaseResp<Message> cursorPage = messageService.getCursorPage(request.getRoomId(), request);
        if (cursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(cursorPage, getMsgRespBatch(cursorPage.getList(), receiveUid));
    }
//
    @Override
    public CursorPageBaseResp<ChatRoomResp> getRoomPage(CursorPageBaseReq request, Long uid) {
        CursorPageBaseResp<Room> cursorPage = roomService.getCursorPage(request);
        ArrayList<Room> rooms = new ArrayList<>(cursorPage.getList());
        if (request.isFirstPage()) {
            //第一页插入置顶的大群聊
            Room group = roomService.getById(ROOM_GROUP_ID);
            rooms.add(0, group);
        }
        return CursorPageBaseResp.init(cursorPage, RoomAdapter.buildResp(rooms));
    }

    @Override
    public CursorPageBaseResp<RoomFriendVo> getRoomFriendPage(CursorPageBaseReq request) {
        Long loginUserId = Long.valueOf(StpUtil.getLoginId().toString());
        CursorPageBaseResp<RoomFriend>  cursorPage = roomFriendService.getCursorPage(request,loginUserId );
        List<RoomFriendVo> collect = cursorPage.getList().stream().map(item -> {
            RoomFriendVo roomFriendVo = BeanCopyUtils.copyBean(item, RoomFriendVo.class);
            Long fromUserId = Objects.equals(item.getUid1(), loginUserId) ? item.getUid2() : item.getUid1();
            User user = userService.getById(fromUserId);
            if (user != null) {
                roomFriendVo.setAvatar(user.getUserAvatar());
                roomFriendVo.setFromUid(fromUserId);
                roomFriendVo.setFromUsername(user.getUserName());
                roomFriendVo.setUnread(99);
            }
            return roomFriendVo;
        }).collect(Collectors.toList());
        CursorPageBaseResp<RoomFriendVo> resp = new CursorPageBaseResp<>();
        resp.setList(collect);
        resp.setCursor(cursorPage.getCursor());
        resp.setIsLast(cursorPage.getIsLast());
        return resp;
    }

    //
    @Override
    public ChatMemberStatisticResp getMemberStatistic() {
        System.out.println(Thread.currentThread().getName());
        Long onlineNum = userCache.getOnlineNum();
//        Long offlineNum = userCache.getOfflineNum();不展示总人数
        ChatMemberStatisticResp resp = new ChatMemberStatisticResp();
        resp.setOnlineNum(onlineNum);
//        resp.setTotalNum(onlineNum + offlineNum);
        return resp;
    }
//
//    @Override
//    @RedissonLock(key = "#uid")
//    public void setMsgMark(Long uid, ChatMessageMarkReq request) {
//        AbstractMsgMarkStrategy strategy = MsgMarkFactory.getStrategyNoNull(request.getMarkType());
//        switch (MessageMarkActTypeEnum.of(request.getActType())) {
//            case MARK:
//                strategy.mark(uid, request.getMsgId());
//                break;
//            case UN_MARK:
//                strategy.unMark(uid, request.getMsgId());
//                break;
//        }
//    }
//
//    private Integer transformAct(Integer actType) {
//        if (actType == 1) {
//            return YesOrNoEnum.NO.getStatus();
//        } else if (actType == 2) {
//            return YesOrNoEnum.YES.getStatus();
//        }
//        throw new BusinessException("动作类型 1确认 2取消");
//    }
//
public List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
    if (CollUtil.isEmpty(messages)) {
        return new ArrayList<>();
    }
    List<ChatMessageResp> chatMessageResps = MessageAdapter.buildMsgResp(messages, receiveUid);
    chatMessageResps.forEach(item ->{
        ChatMessageResp.UserInfo fromUser = item.getFromUser();
        Long uid = fromUser.getUid();
        User user = userService.getById(uid);
        fromUser.setUsername(user.getUserName());
        fromUser.setAvatar(user.getUserAvatar());
    });
    return chatMessageResps;
}

}
