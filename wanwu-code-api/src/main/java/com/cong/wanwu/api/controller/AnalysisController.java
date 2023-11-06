package com.cong.wanwu.api.controller;


import com.cong.wanwu.api.mapper.UserInterfaceInfoMapper;
import com.cong.wanwu.api.model.vo.InterfaceInfoVO;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分析控制器
 *
 * @author 86188
 * @date 2023/01/27
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    @GetMapping("/top/interface/invoke")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo(){
        int limit = 3;
        List<InterfaceInfoVO> interfaceInfoVOList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(limit);
    return ResultUtils.success(interfaceInfoVOList);
    }
}
