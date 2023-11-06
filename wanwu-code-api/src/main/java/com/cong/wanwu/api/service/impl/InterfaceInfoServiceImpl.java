package com.cong.wanwu.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.api.common.model.entity.InterfaceInfo;
import com.cong.wanwu.api.mapper.InterfaceInfoMapper;
import com.cong.wanwu.api.service.InterfaceInfoService;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author 86188
* @description 针对表【interface_info(接口信息表)】的数据库操作Service实现
* @createDate 2022-10-30 17:11:45
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {

        String name = interfaceInfo.getName();
        String method = interfaceInfo.getMethod();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        if (add){
//            if (StringUtils.isAnyBlank(name,method,description,url,requestHeader,responseHeader)){
//                throw new BusinessException(ErrorCode.PARAMS_ERROR);
//            }
        }
        if (StringUtils.isNotBlank(name)&&name.length()>50){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"名称过长");
        }

    }
}




