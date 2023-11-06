package com.cong.wanwu.usercenter.controller;

import com.cong.wanwu.common.common.BaseResponse;
import com.cong.wanwu.common.common.ResultUtils;
import com.cong.wanwu.usercenter.manager.SearchFacade;
import com.cong.wanwu.usercenter.model.dto.search.SearchQueryRequest;
import com.cong.wanwu.usercenter.model.vo.SearchVO;
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
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private SearchFacade searchFacade;


    /**
     * 分页获取列表（封装类）
     *
     * @param searchQueryRequest

     * @return
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest) {
        return ResultUtils.success(searchFacade.searchAll(searchQueryRequest));
    }


}
