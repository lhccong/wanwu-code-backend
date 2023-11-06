package com.cong.wanwu.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.wanwu.api.common.model.entity.InterfaceInfo;

/**
* @author 86188
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2022-10-30 17:11:45
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {


    /**
     * 有效接口信息
     *
     * @param interfaceInfo 接口信息
     * @param add           添加
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
