package com.cong.wanwu.usercenter.common.listener;


import com.cong.wanwu.usercenter.common.event.MessageSendEvent;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMessageResp;
import com.cong.wanwu.usercenter.model.entity.chat.Message;
import com.cong.wanwu.usercenter.service.ChatService;
import com.cong.wanwu.usercenter.service.MessageService;
import com.cong.wanwu.usercenter.service.WebSocketService;
import com.cong.wanwu.usercenter.service.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 消息发送监听器
 *
 * @author zhongzb create on 2022/08/26
 */
@Slf4j
@Component
public class MessageSendListener {
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageService messageService;

    @Async
    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void notifyAllOnline(MessageSendEvent event) {
        Message message = messageService.getById(event.getMsgId());
        ChatMessageResp msgResp = chatService.getMsgResp(message, null);
        webSocketService.sendToAllOnline(WSAdapter.buildMsgSend(msgResp), message.getFromUid());
    }

}
