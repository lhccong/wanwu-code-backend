package com.cong.wanwu.usercenter.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.usercenter.datasource.DataSource;
import com.cong.wanwu.usercenter.datasource.PictureDataSource;
import com.cong.wanwu.usercenter.datasource.PostDataSource;
import com.cong.wanwu.usercenter.datasource.UserDataSource;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.common.exception.ThrowUtils;
import com.cong.wanwu.usercenter.model.dto.search.SearchQueryRequest;
import com.cong.wanwu.usercenter.model.enums.SearchTypeEnum;
import com.cong.wanwu.usercenter.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 搜索门面
 *
 * @author 86188
 * @date 2023/03/20
 */
@Component
@Slf4j
public class SearchFacade {
    @Resource
    private PostDataSource postDataSource;
    @Resource
    private UserDataSource userDataSource;
    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public SearchVO searchAll(SearchQueryRequest searchQueryRequest) {
        String type = searchQueryRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        String searchText = searchQueryRequest.getSearchText();
        long current = searchQueryRequest.getCurrent();
        long size = searchQueryRequest.getPageSize();
        //搜索出所有数据
        if (searchTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询类型");
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page<?> page = dataSource.doSearch(searchText, current, size);
            searchVO.setPageData(page);
            return searchVO;
        }


    }
}
