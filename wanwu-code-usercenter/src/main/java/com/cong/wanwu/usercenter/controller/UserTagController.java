package com.cong.wanwu.usercenter.controller;

import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.usercenter.model.dto.userTag.UserTagAddRequest;
import com.cong.wanwu.usercenter.model.vo.UserTagCategoryVo;
import com.cong.wanwu.usercenter.model.vo.UserTagVo;
import com.cong.wanwu.usercenter.service.UserTagService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/member/tags")
public class UserTagController {
    @Resource
    private UserTagService userTagService;

    /**
     * 获取所有标签
     * @return
     */
    @GetMapping
    public BaseResponse<List<UserTagCategoryVo>> getAllTags(){
        List<UserTagCategoryVo> allTags = userTagService.getAllTags();
        return ResultUtils.success(allTags);
    }

    /**
     * 添加一个标签
     * @param userTagAddRequest
 
     * @return
     */
    @PostMapping
    public BaseResponse<UserTagVo> addTag(@RequestBody UserTagAddRequest userTagAddRequest){
        UserTagVo userTagVo = userTagService.addTag(userTagAddRequest);
        return ResultUtils.success(userTagVo);
    }
}
