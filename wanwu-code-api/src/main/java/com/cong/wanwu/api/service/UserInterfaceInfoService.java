package com.cong.wanwu.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.wanwu.api.common.model.entity.UserInterfaceInfo;

/**
 * 用户接口信息服务
 *
 * @author 86188
 * @date 2023/01/13
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 有效用户界面信息
     *
     * @param userInterfaceInfo 用户界面信息
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用计数
     *
     * @param interfaceInfoId 接口信息id
     * @param userId          用户id
     * @return Boolean
     */
    Boolean invokeCount(Long interfaceInfoId,long userId);
}
