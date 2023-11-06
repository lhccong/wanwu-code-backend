package com.cong.wanwu.usercenter.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.usercenter.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子视图
 *
 */
@Data
public class SearchVO implements Serializable {


    private Page<?> pageData;
}
