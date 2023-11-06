package com.cong.wanwu.usercenter.websocket;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.cong.wanwu.usercenter.model.enums.ws.WSReqTypeEnum;
import com.cong.wanwu.usercenter.model.vo.ws.request.WSAuthorize;
import com.cong.wanwu.usercenter.model.vo.ws.request.WSBaseReq;
import com.cong.wanwu.usercenter.service.WebSocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * Netty Web 套接字服务器处理程序
 *
 * @author liuhuaicong
 * @date 2023/10/27
 */
@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        getService().connect(ctx.channel());
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    private void userOffLine(ChannelHandlerContext ctx) {
        getService().removed(ctx.channel());
        ctx.channel().close();
    }

    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffLine(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息 ={}", cause.getMessage());
        ctx.channel().close();
    }

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case LOGIN:
                getService().handleLoginReq(ctx.channel());
                log.info("请求二维码 = " + msg.text());
                break;
            case HEARTBEAT:
                break;
            case WANWU_LOGIN:
                getService().handleWanwuConnect(ctx.channel());
                break;
            case CHAT:
                getService().sendMessage(ctx.channel(),wsBaseReq);
                break;
            case AUTHORIZE:
                getService().authorize(ctx.channel(), JSONUtil.toBean(wsBaseReq.getData(), WSAuthorize.class));
                log.info("主动认证 = " + msg.text());
                break;
            default:
                log.info("未知类型");
        }
    }
}
