package com.cong.wanwu.usercenter.model.vo;

import com.cong.wanwu.usercenter.model.entity.UserTag;
import lombok.Data;

import java.util.List;

/**
 * @author liuhuaicong
 * @date 2023/09/08
 */
@Data
public class UserTagCategoryVo {
    private Long id;

    private String name;

    private List<UserTagVo> tags;
}