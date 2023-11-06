package com.cong.wanwu.usercenter.model.dto.picture;

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
public class PictureQueryRequest extends PageRequest implements Serializable {


    /**
     * 搜索词
     */
    private String searchText;



    private static final long serialVersionUID = 1L;
}