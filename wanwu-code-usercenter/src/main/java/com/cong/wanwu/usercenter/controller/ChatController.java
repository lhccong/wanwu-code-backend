package com.cong.wanwu.usercenter.controller;



import cn.dev33.satoken.stp.StpUtil;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import com.cong.wanwu.common.model.vo.response.CursorPageBaseResp;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessageMarkReq;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessagePageReq;
import com.cong.wanwu.usercenter.model.dto.chat.request.ChatMessageReq;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMemberResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMemberStatisticResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMessageResp;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatRoomResp;
import com.cong.wanwu.usercenter.model.entity.chat.RoomFriend;
import com.cong.wanwu.usercenter.model.vo.message.RoomFriendVo;
import com.cong.wanwu.usercenter.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 群聊相关接口
 * </p>
 *
 * @author liuhuaicong
 * @date 2023/10/31
 * @since 2023-03-19
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天室相关接口")
public class ChatController {
    @Resource
    private ChatService chatService;


    @GetMapping("/public/room/page")
    @ApiOperation("会话列表")
    public BaseResponse<CursorPageBaseResp<ChatRoomResp>> getRoomPage(CursorPageBaseReq request) {
        return ResultUtils.success(chatService.getRoomPage(request, (Long) StpUtil.getLoginId()));
    }

    @GetMapping("/public/member/page")
    @ApiOperation("群成员列表")
    public BaseResponse<CursorPageBaseResp<ChatMemberResp>> getMemberPage(CursorPageBaseReq request) {
        CursorPageBaseResp<ChatMemberResp> memberPage = chatService.getMemberPage(request);
//        filterBlackMember(memberPage);
        return ResultUtils.success(memberPage);
    }

    @GetMapping("/public/private/room/page")
    @ApiOperation("会话私聊列表")
    public BaseResponse<CursorPageBaseResp<RoomFriendVo>> getRoomFriendVoPage(CursorPageBaseReq request) {
        return ResultUtils.success(chatService.getRoomFriendPage(request));
    }

    @GetMapping("public/member/statistic")
    @ApiOperation("群成员人数统计")
    public BaseResponse<ChatMemberStatisticResp> getMemberStatistic() {
        return ResultUtils.success(chatService.getMemberStatistic());
    }

    @GetMapping("/public/msg/page")
    @ApiOperation("消息列表")
    public BaseResponse<CursorPageBaseResp<ChatMessageResp>> getMsgPage(ChatMessagePageReq request) {
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(request, Long.valueOf(StpUtil.getLoginId().toString()));
//        filterBlackMsg(msgPage);
        return ResultUtils.success(msgPage);
    }

//    private void filterBlackMsg(CursorPageBaseResp<ChatMessageResp> memberPage) {
//        memberPage.getList().removeIf(a -> getBlackUidSet().contains(a.getFromUser().getUid().toString()));
//        System.out.println(1);
//    }

    @PostMapping("/msg")
    @ApiOperation("发送消息")
//    @FrequencyControl(time = 5, count = 2, target = FrequencyControl.Target.UID)
//    @FrequencyControl(time = 30, count = 5, target = FrequencyControl.Target.UID)
//    @FrequencyControl(time = 60, count = 10, target = FrequencyControl.Target.UID)
    public BaseResponse<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
        Long msgId = chatService.sendMsg(request, Long.valueOf(StpUtil.getLoginId().toString()));
        //返回完整消息格式，方便前端展示
        return ResultUtils.success(chatService.getMsgResp(msgId, Long.valueOf(StpUtil.getLoginId().toString())));
    }

    @PutMapping("/msg/mark")
    @ApiOperation("消息标记")
//    @FrequencyControl(time = 20, count = 3, target = FrequencyControl.Target.UID)
    public void setMsgMark(@Valid @RequestBody ChatMessageMarkReq request) {
        chatService.setMsgMark((Long) StpUtil.getLoginId(), request);
    }
}

