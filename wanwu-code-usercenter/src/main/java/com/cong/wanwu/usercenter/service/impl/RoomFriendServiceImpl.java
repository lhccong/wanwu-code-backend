package com.cong.wanwu.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import com.cong.wanwu.common.model.vo.response.CursorPageBaseResp;
import com.cong.wanwu.common.utils.CursorUtils;
import com.cong.wanwu.usercenter.model.entity.chat.Room;
import com.cong.wanwu.usercenter.model.entity.chat.RoomFriend;
import com.cong.wanwu.usercenter.service.RoomFriendService;
import com.cong.wanwu.usercenter.mapper.RoomFriendMapper;
import org.springframework.stereotype.Service;

/**
* @author liuhuaicong
* @description 针对表【room_friend(单聊房间表)】的数据库操作Service实现
* @createDate 2023-10-31 10:35:04
*/
@Service
public class RoomFriendServiceImpl extends ServiceImpl<RoomFriendMapper, RoomFriend>
    implements RoomFriendService{

    @Override
    public CursorPageBaseResp<RoomFriend> getCursorPage(CursorPageBaseReq request,Long uid) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(RoomFriend::getUid1,uid).or().eq(RoomFriend::getUid2,uid);
        }, RoomFriend::getId);
    }
}




