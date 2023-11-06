package com.cong.wanwu.usercenter.service;

import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import com.cong.wanwu.common.model.vo.response.CursorPageBaseResp;
import com.cong.wanwu.usercenter.model.entity.chat.RoomFriend;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liuhuaicong
* @description 针对表【room_friend(单聊房间表)】的数据库操作Service
* @createDate 2023-10-31 10:35:04
*/
public interface RoomFriendService extends IService<RoomFriend> {

    /**
     * 获取光标页面
     *
     * @param request 请求
     * @param uid     uid
     * @return {@link CursorPageBaseResp}<{@link RoomFriend}>
     */
    CursorPageBaseResp<RoomFriend> getCursorPage(CursorPageBaseReq request,Long uid);
}
