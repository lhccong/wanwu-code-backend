package com.cong.wanwu.api.service.impl.inner;


import com.cong.wanwu.api.common.service.InnerUserInterfaceInfoService;
import com.cong.wanwu.api.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部用户界面信息服务
 *
 * @author 86188
 * @date 2023/01/13
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public Boolean invokeCount(Long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }
}
