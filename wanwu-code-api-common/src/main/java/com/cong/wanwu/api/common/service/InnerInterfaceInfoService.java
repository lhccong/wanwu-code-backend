package com.cong.wanwu.api.common.service;


import com.cong.wanwu.api.common.model.entity.InterfaceInfo;

/**
* @author 86188
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2022-10-30 17:11:45
*/
public interface InnerInterfaceInfoService  {
    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     * @param path   路径
     * @param method 方法
     * @return {@link InterfaceInfo}
     */
    InterfaceInfo getInterfaceInfo(String path,String method);

}
