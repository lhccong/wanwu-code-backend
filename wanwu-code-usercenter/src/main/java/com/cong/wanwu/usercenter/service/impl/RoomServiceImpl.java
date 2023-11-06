package com.cong.wanwu.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import com.cong.wanwu.common.model.vo.response.CursorPageBaseResp;
import com.cong.wanwu.common.utils.CursorUtils;
import com.cong.wanwu.usercenter.model.entity.chat.Message;
import com.cong.wanwu.usercenter.model.entity.chat.Room;
import com.cong.wanwu.usercenter.model.enums.chat.MessageStatusEnum;
import com.cong.wanwu.usercenter.service.RoomService;
import com.cong.wanwu.usercenter.mapper.RoomMapper;
import org.springframework.stereotype.Service;

/**
* @author liuhuaicong
* @description 针对表【room(房间表)】的数据库操作Service实现
* @createDate 2023-10-31 10:35:04
*/
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room>
    implements RoomService{

    @Override
    public CursorPageBaseResp<Room> getCursorPage(CursorPageBaseReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
        }, Room::getId);
    }
}




