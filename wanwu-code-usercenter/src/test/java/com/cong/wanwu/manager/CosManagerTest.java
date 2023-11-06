package com.cong.wanwu.manager;

import javax.annotation.Resource;

import com.cong.wanwu.usercenter.manager.CosManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Cos 操作测试
 *
 *  @author 聪
 *  
 */
@SpringBootTest
class CosManagerTest {

    @Resource
    private CosManager cosManager;

    @Test
    void putObject() {
        cosManager.putObject("test", "test.json");
    }
}