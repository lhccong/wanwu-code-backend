package com.cong.wanwu.bi.manager;

import cn.hutool.core.util.StrUtil;
import com.cong.wanwu.bi.config.XfXhConfig;
import com.cong.wanwu.bi.listener.XfXhWebSocketListener;
import com.cong.wanwu.bi.model.dto.chart.XhAiMsgDTO;
import com.cong.wanwu.bi.ws.XfXhStreamClient;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import okhttp3.WebSocket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;

/**
 * 人工智能管理器
 *
 * @author 86188
 * @date 2023/05/19
 */
@Service
public class AiManager {
    @Resource
    private YuCongMingClient yuCongMingClient;

    @Resource
    private XfXhStreamClient xfXhStreamClient;

    @Resource
    private XfXhConfig xfXhConfig;
    /**
     * 做聊天
     * AI绘画
     *
     * @param message 消息
     * @param modeId  模式标识
     * @return {@link String}
     */
    public String doChat(long modeId,String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modeId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI 响应错误");
        }
        return response.getData().getContent();
    }
    /**
     * 星火同步
     *
     * @param question 消息
     * @return {@link String}
     */
    public String doChat(String question){
        // 如果是无效字符串，则不对大模型进行请求
        if (StrUtil.isBlank(question)) {
            return "无效问题，请重新输入";
        }
        // 获取连接令牌
        if (!xfXhStreamClient.operateToken(XfXhStreamClient.GET_TOKEN_STATUS)) {
            return "当前大模型连接数过多，请稍后再试";
        }

        // 创建消息对象
        XhAiMsgDTO msgDTO = XhAiMsgDTO.createUserMsg(question);
        // 创建监听器
        XfXhWebSocketListener listener = new XfXhWebSocketListener();
        // 发送问题给大模型，生成 websocket 连接
        WebSocket webSocket = xfXhStreamClient.sendMsg(UUID.randomUUID().toString().substring(0, 10), Collections.singletonList(msgDTO), listener);
        if (webSocket == null) {
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
            return "系统内部错误，请联系管理员";
        }
        try {
            int count = 0;
            // 为了避免死循环，设置循环次数来定义超时时长
            int maxCount = xfXhConfig.getMaxResponseTime() * 5;
            while (count <= maxCount) {
                Thread.sleep(200);
                if (listener.isWsCloseFlag()) {
                    break;
                }
                count++;
            }
            if (count > maxCount) {
                return "大模型响应超时，请联系管理员";
            }
            // 响应大模型的答案
            return listener.getAnswer().toString();
        } catch (InterruptedException e) {
//            log.error("错误：" + e.getMessage());
            return "系统内部错误，请联系管理员";
        } finally {
            // 关闭 websocket 连接
            webSocket.close(1000, "");
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
        }
    }
}
