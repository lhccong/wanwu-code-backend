package com.cong.wanwu.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.common.exception.ThrowUtils;
import com.cong.wanwu.usercenter.model.dto.picture.PictureQueryRequest;
import com.cong.wanwu.usercenter.model.entity.Picture;
import com.cong.wanwu.usercenter.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 图片接口
 *
 *  @author 聪
 *  
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;




    /**
     * 分页获取列表（封装类）
     *
     * @param pictureQueryRequest
 
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Picture> pictures = pictureService.searchPicture(pictureQueryRequest.getSearchText(), current, size);

        return ResultUtils.success(pictures);
    }


}
