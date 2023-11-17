package com.cong.wanwu.usercenter.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.common.model.entity.User;
import com.cong.wanwu.usercenter.common.event.UserOnlineEvent;
import com.cong.wanwu.usercenter.config.ThreadPoolConfig;
import com.cong.wanwu.usercenter.model.dto.chat.response.ChatMessageResp;
import com.cong.wanwu.usercenter.model.dto.ws.WSChannelExtraDTO;
import com.cong.wanwu.usercenter.model.entity.chat.Message;
import com.cong.wanwu.usercenter.model.entity.chat.Room;
import com.cong.wanwu.usercenter.model.entity.chat.RoomFriend;
import com.cong.wanwu.usercenter.model.enums.chat.MessageTypeEnum;
import com.cong.wanwu.usercenter.model.enums.ws.WSReqTypeEnum;
import com.cong.wanwu.usercenter.model.vo.message.ChatMessageVo;
import com.cong.wanwu.usercenter.model.vo.message.MessageVo;
import com.cong.wanwu.usercenter.model.vo.ws.request.WSAuthorize;
import com.cong.wanwu.usercenter.model.vo.ws.request.WSBaseReq;
import com.cong.wanwu.usercenter.model.vo.ws.response.WSBaseResp;
import com.cong.wanwu.usercenter.service.*;
import com.cong.wanwu.usercenter.service.adapter.WSAdapter;
import com.cong.wanwu.usercenter.service.cache.UserCache;
import com.cong.wanwu.usercenter.websocket.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Description: websocket处理类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19 16:21
 */
@Component
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {


    /**
     * 所有请求登录的code与channel关系
     * todo 有可能有人请求了二维码，就是不登录，留个坑，之后处理
     */
    private static final ConcurrentHashMap<Integer, Channel> WAIT_LOGIN_MAP = new ConcurrentHashMap<>();
    /**
     * 所有已连接的websocket连接列表和一些额外参数
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Channel, WSChannelExtraDTO> getOnlineMap() {
        return ONLINE_WS_MAP;
    }

    public static final int EXPIRE_SECONDS = 60 * 60;
    @Resource
    private WxMpService wxMpService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserService userService;

    @Resource
    private RoomFriendService roomFriendService;
    @Resource
    private RoomService roomService;
    @Resource
    private MessageService messageService;

    @Autowired
    private UserCache userCache;
    @Resource
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 处理用户登录请求，需要返回一张带code的二维码
     *
     * @param channel
     */
    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        //生成随机不重复的登录码
        Integer code = generateLoginCode(channel);
        //请求微信接口，获取登录码地址
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, EXPIRE_SECONDS);
        //返回给前端
//        sendMsg(channel, WSAdapter.buildLoginResp(wxMpQrCodeTicket));
    }

    /**
     * 获取不重复的登录的code，微信要求最大不超过int的存储极限
     * 防止并发，可以给方法加上synchronize，也可以使用cas乐观锁
     *
     * @return
     */
    private Integer generateLoginCode(Channel channel) {
        int code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (WAIT_LOGIN_MAP.contains(code)
                || Objects.nonNull(WAIT_LOGIN_MAP.putIfAbsent(code, channel)));
        return code;
    }

    /**
     * 处理所有ws连接的事件
     *
     * @param channel
     */
    @Override
    public void  connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 处理万物连接
     *
     * @param channel 渠道
     */
    @Override
    public void handleWanwuConnect(Channel channel) {
        String token = NettyUtil.getAttr(channel, NettyUtil.TOKEN);
        //更新上线列表
        online(channel, Long.valueOf(StpUtil.getLoginIdByToken(token).toString()));
        User loginUser = userService.getLoginUser(token);
        //返回给用户登录成功
        sendMsg(channel, WSAdapter.buildLoginSuccessResp(loginUser, token));
        //发送用户上线事件
        boolean online = userCache.isOnline(loginUser.getId());
        if (!online) {
            loginUser.setUpdateTime(new Date());
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, loginUser));
        }
    }

    @Override
    public void removed(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offlineAll = offline(channel, uidOptional);
        if (uidOptional.isPresent() && offlineAll) {//已登录用户断连,并且全下线成功
            User user = new User();
            user.setId(uidOptional.get());
//            user.setLastOptTime(new Date());
//            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    @Override
    public void authorize(Channel channel, WSAuthorize wsAuthorize) {
        //校验token
//        boolean verifySuccess = loginService.verify(wsAuthorize.getToken());
//        if (verifySuccess) {//用户校验成功给用户登录
//            User user = userDao.getById(loginService.getValidUid(wsAuthorize.getToken()));
//            loginSuccess(channel, user, wsAuthorize.getToken());
//        } else { //让前端的token失效
//            sendMsg(channel, WSAdapter.buildInvalidateTokenResp());
//        }
    }

    /**
     * 登录成功，并更新状态
     */
    private void loginSuccess(Channel channel, User user, String token) {
        //更新上线列表
        online(channel, user.getId());
//        //返回给用户登录成功
//        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user, token));
//        //发送用户上线事件
//        boolean online = userCache.isOnline(user.getId());
//        if (!online) {
//            user.setLastOptTime(new Date());
//            user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
//            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
//        }
    }

    /**
     * 用户上线
     */
    private void online(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
    }

    /**
     * 用户下线
     * return 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(channel1 -> channel1.equals(channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
    }

    @Override
    public Boolean scanLoginSuccess(Integer loginCode, User user, String token) {
        //发送消息
        Channel channel = WAIT_LOGIN_MAP.get(loginCode);
        if (Objects.isNull(channel)) {
            return Boolean.FALSE;
        }
        //移除code
        WAIT_LOGIN_MAP.remove(loginCode);
        //用户登录
        loginSuccess(channel, user, token);
        return true;
    }

    @Override
    public Boolean scanSuccess(Integer loginCode) {
        Channel channel = WAIT_LOGIN_MAP.get(loginCode);
        if (Objects.isNull(channel)) {
            return Boolean.FALSE;
        }
//        sendMsg(channel, WSAdapter.buildScanSuccessResp());
        return true;
    }


    /**
     * 如果在线列表不存在，就先把该channel放进在线列表
     *
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO =
                ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    //entrySet的值不是快照数据,但是它支持遍历，所以无所谓了，不用快照也行。
    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (ObjectUtil.equal(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {
        sendToAllOnline(wsBaseResp, null);
    }

    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    /**
     * 案例证明ConcurrentHashMap#entrySet的值不是快照数据
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition condition = reentrantLock.newCondition();
        ConcurrentHashMap<Integer, Integer> a = new ConcurrentHashMap<>();
        a.put(1, 1);
        a.put(2, 2);
        new Thread(() -> {
            reentrantLock.lock();
            Set<Map.Entry<Integer, Integer>> entries = a.entrySet();
            System.out.println(entries);
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(entries);
            reentrantLock.unlock();

        }).start();
        Thread.sleep(1000);
        reentrantLock.lock();
        a.put(3, 3);
        System.out.println("haha");
        condition.signalAll();
        reentrantLock.unlock();
        Thread.sleep(1000);
    }
    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendMessage(Channel channel, WSBaseReq req) {
        //发送数据
        String content = req.getData();
        ChatMessageVo chatMessageVo = JSONUtil.toBean(content, ChatMessageVo.class);
        //接收消息 用户id
        Long uid = req.getUid();
        String token = NettyUtil.getAttr(channel, NettyUtil.TOKEN);
        if (CharSequenceUtil.isEmpty(token)){
            channel.writeAndFlush(new BaseResponse<>(ErrorCode.FORBIDDEN_ERROR));
        }
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.of(chatMessageVo.getType());
        switch (messageTypeEnum) {
            case PRIVATE:
                long loginUserId = Long.parseLong(StpUtil.getLoginIdByToken(token).toString());
                long uid1 = Math.min(loginUserId, uid);
                long uid2 = Math.max(loginUserId, uid);
                //查询是否有聊天室了
                RoomFriend roomFriend = roomFriendService.getOne(new LambdaQueryWrapper<RoomFriend>()
                        .eq(RoomFriend::getUid1, uid1).eq(RoomFriend::getUid2, uid2));

                if (roomFriend==null){
                    Room room = Room.builder().type(MessageTypeEnum.PRIVATE.getType()).build();
                    roomService.save(room);
                    roomFriend = RoomFriend.builder().roomKey(uid1 + "_" + uid2).uid1(uid1).uid2(uid2).roomId(room.getId()).build();
                    roomFriendService.save(roomFriend);
                }

                CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
                //对方不在线，只需要保存消息就好了
                if(channels != null){
                    //TODO 后面整理逻辑
                    ChatMessageResp chatMessageResp = new ChatMessageResp();
                    User user = userService.getById(loginUserId);
                    ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
                    userInfo.setUsername(user.getUserName());
                    userInfo.setUid(user.getId());
                    userInfo.setAvatar(user.getUserAvatar());
                    chatMessageResp.setRoomId(roomFriend.getRoomId());
                    chatMessageResp.setFromUser(userInfo);
                    ChatMessageResp.Message message = new ChatMessageResp.Message();
                    message.setContent(chatMessageVo.getContent());
                    chatMessageResp.setMessage(message);


                    WSBaseResp<ChatMessageResp> messageVoWSBaseResp = new WSBaseResp<>();
                    messageVoWSBaseResp.setData(chatMessageResp);
                    messageVoWSBaseResp.setType(1);
                    channels.forEach(channelUser -> threadPoolTaskExecutor.execute(() -> sendMsg(channelUser, messageVoWSBaseResp)));
                }
                messageService.save(Message.builder().fromUid(loginUserId).content(chatMessageVo.getContent()).roomId(roomFriend.getRoomId()).build());
                break;
            case GROUP:
                break;
            case NORMAL:
                break;
            case LIKE:
                break;
            case DISLIKE:
                break;
            default:
                channel.writeAndFlush(new BaseResponse<>(ErrorCode.FORBIDDEN_ERROR));
                break;
        }
    }
}
