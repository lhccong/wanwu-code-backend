package com.cong.wanwu.usercenter.service;

import com.cong.wanwu.common.model.vo.request.CursorPageBaseReq;
import com.cong.wanwu.common.model.vo.response.CursorPageBaseResp;
import com.cong.wanwu.usercenter.model.entity.chat.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liuhuaicong
* @description 针对表【message(消息表)】的数据库操作Service
* @createDate 2023-10-31 10:35:04
*/
public interface MessageService extends IService<Message> {

    /**
     * 获取间隙计数
     *
     * @param roomId 房间 ID
     * @param fromId 从 ID
     * @param toId   到 ID
     * @return {@link Integer}
     */
    Integer getGapCount(Long roomId, Long fromId, Long toId);

    /**
     * 更新间隔计数
     *
     * @param id       编号
     * @param gapCount 间隙计数
     */
    void updateGapCount(Long id, Integer gapCount);

    /**
     * 获取光标页面
     *
     * @param roomId    房间 ID
     * @param request   请求
     * @return {@link CursorPageBaseResp}<{@link Message}>
     */
    CursorPageBaseResp<Message> getCursorPage(Long roomId, CursorPageBaseReq request);
}
