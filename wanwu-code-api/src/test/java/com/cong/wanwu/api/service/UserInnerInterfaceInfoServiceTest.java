package com.cong.wanwu.api.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserInnerInterfaceInfoServiceTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public void invokeCount() {
        Boolean aBoolean = userInterfaceInfoService.invokeCount(1L, 1L);
        Assert.assertTrue(aBoolean);
    }
}