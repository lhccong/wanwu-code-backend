package com.cong.wanwu.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import com.cong.wanwu.common.model.vo.response.CursorPageBaseResp;
import com.cong.wanwu.common.utils.CursorUtils;
import com.cong.wanwu.usercenter.model.entity.chat.Message;
import com.cong.wanwu.usercenter.model.enums.chat.MessageStatusEnum;
import com.cong.wanwu.usercenter.service.MessageService;
import com.cong.wanwu.usercenter.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
* @author liuhuaicong
* @description 针对表【message(消息表)】的数据库操作Service实现
* @createDate 2023-10-31 10:35:04
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService{


    @Autowired
    private CursorUtils cursorUtils;

    /**
     * 获取间隙计数
     *
     * @param roomId 房间 ID
     * @param fromId 从 ID
     * @param toId   到 ID
     * @return {@link Integer}
     */
    @Override
    public Integer getGapCount(Long roomId, Long fromId, Long toId) {
        return Math.toIntExact(lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Message::getId, fromId)
                .le(Message::getId, toId)
                .count());
    }
    @Override
    public void updateGapCount(Long id, Integer gapCount) {
        lambdaUpdate()
                .eq(Message::getId, id)
                .set(Message::getGapCount, gapCount)
                .update();
    }
    @Override
    public CursorPageBaseResp<Message> getCursorPage(Long roomId, CursorPageBaseReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> {
            wrapper.eq(Message::getRoomId, roomId);
            wrapper.eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus());
        }, Message::getId);
    }
}




