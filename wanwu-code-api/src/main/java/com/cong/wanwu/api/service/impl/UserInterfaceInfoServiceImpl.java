package com.cong.wanwu.api.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.api.common.model.entity.UserInterfaceInfo;
import com.cong.wanwu.api.mapper.UserInterfaceInfoMapper;
import com.cong.wanwu.api.service.UserInterfaceInfoService;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import org.springframework.stereotype.Service;

/**
* @author 86188
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2022-11-21 21:34:18
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo ==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //创建时所有参数不为空
        if (add){
            if (userInterfaceInfo.getInterfaceInfoId()<=0 || userInterfaceInfo.getUserId()<=0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum()<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"剩余次数不能小于0");
        }
    }

    @Override
    public Boolean invokeCount(Long interfaceInfoId, long userId) {
        if (interfaceInfoId<=0||userId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return this.update(new UpdateWrapper<UserInterfaceInfo>().eq("interfaceInfoId", interfaceInfoId)
                .eq("userId", userId)
                        .gt("leftNum",0)
                .setSql("leftNum = leftNum - 1,totalNum = totalNum + 1"));
    }
}




