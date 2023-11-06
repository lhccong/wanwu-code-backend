package com.cong.wanwu.usercenter.model.dto.search;

import com.cong.wanwu.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 *  @author 聪
 *  
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchQueryRequest extends PageRequest implements Serializable {


    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 类型
     */
    private String type;


    private static final long serialVersionUID = 1L;
}