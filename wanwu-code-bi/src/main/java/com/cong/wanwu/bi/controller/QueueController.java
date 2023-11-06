package com.cong.wanwu.bi.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cong.wanwu.bi.config.XfXhConfig;
import com.cong.wanwu.bi.listener.XfXhWebSocketListener;
import com.cong.wanwu.bi.model.dto.chart.*;
import com.cong.wanwu.bi.ws.XfXhStreamClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 队列测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev","local"})
public class QueueController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private XfXhStreamClient xfXhStreamClient;

    @Resource
    private XfXhConfig xfXhConfig;
    @GetMapping("/add")
    public void add(String name) {
        CompletableFuture.runAsync(() -> {
            System.out.println("任务执行中：" + name+"执行人："+Thread.currentThread().getName());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },threadPoolExecutor);
    }
    @GetMapping("/get")
    public String get() {
        Map<String, Object> map = new HashMap<>(16);
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度",size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务总数",taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成任务数目",completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("当前活动线程数",activeCount);
        return JSONUtil.toJsonStr(map);
    }
    /**
     * 发送问题
     *
     * @param question 问题
     * @return 星火大模型的回答
     */
    @GetMapping("/sendQuestion")
    public String sendQuestion(@RequestParam("question") String question) {
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
            log.error("错误：" + e.getMessage());
            return "系统内部错误，请联系管理员";
        } finally {
            // 关闭 websocket 连接
            webSocket.close(1000, "");
            // 归还令牌
            xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
        }
    }

}
