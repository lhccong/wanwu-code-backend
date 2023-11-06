package com.cong.wanwu.api.common.service;

/**
* @author 86188
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2022-11-21 21:34:18
*/
public interface InnerUserInterfaceInfoService {



    /**
     * 调用计数
     *
     * @param interfaceInfoId 接口信息id
     * @param userId          用户id
     * @return Boolean
     */
    Boolean invokeCount(Long interfaceInfoId,long userId);




}
