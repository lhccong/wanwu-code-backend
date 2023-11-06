package com.cong.wanwu.usercenter.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源接口（新接入数据源必须实现 ）
 *
 * @author 86188
 * @date 2023/03/20
 */
public interface DataSource<T> {
    /**
     * 做搜索
     *
     * @param searchText 搜索文本
     * @param pageNum    页面num
     * @param pageSize   页面大小
     * @return {@link Page}<{@link T}>
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
