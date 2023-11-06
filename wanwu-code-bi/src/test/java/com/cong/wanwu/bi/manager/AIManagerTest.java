package com.cong.wanwu.bi.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * AI 操作测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class AIManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    void putObject() {
        String message = aiManager.doChat(1659171950288818178L,"分析需求：\n" +
                "分析网站用户的增长情况\n"+
                "原始数据：\n"+
                "日期，用户数\n" +
                "1号，10\n" +
                "2号，20\n" +
                "3号，30\n");
        System.out.println("message = " + message);
    }
}