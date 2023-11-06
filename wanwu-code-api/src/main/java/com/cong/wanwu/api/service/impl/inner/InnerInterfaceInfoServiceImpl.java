package com.cong.wanwu.api.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cong.wanwu.api.common.model.entity.InterfaceInfo;
import com.cong.wanwu.api.common.service.InnerInterfaceInfoService;
import com.cong.wanwu.api.mapper.InterfaceInfoMapper;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部接口信息服务
 *
 * @author 86188
 * @date 2023/01/13
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {

            if (StringUtils.isAnyBlank(url,method)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            return interfaceInfoMapper.selectOne(new LambdaQueryWrapper<InterfaceInfo>()
                    .eq(InterfaceInfo::getUrl,url).eq(InterfaceInfo::getMethod,method));
        }
}
