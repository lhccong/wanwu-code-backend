package com.cong.wanwu.usercenter.service;

import com.cong.wanwu.usercenter.model.dto.userTag.UserTagAddRequest;
import com.cong.wanwu.usercenter.model.entity.UserTag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.wanwu.usercenter.model.vo.UserTagCategoryVo;
import com.cong.wanwu.usercenter.model.vo.UserTagVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author liuhuaicong
* @description 针对表【user_tag】的数据库操作Service
* @createDate 2023-09-08 16:41:55
*/
public interface UserTagService extends IService<UserTag> {

    /**
     * 获取所有标签
     * @return {@link List}<{@link UserTagCategoryVo}>
     */
    List<UserTagCategoryVo> getAllTags();

    /**
     * 增加标签
     * @param userTagAddRequest
 
     * @return {@link UserTagVo}
     */
    UserTagVo addTag(UserTagAddRequest userTagAddRequest);

    /**
     * 获取tagsVo列表
     * @param tagIdsList tagsId
     * @return {@link List}<{@link UserTagVo}>
     */
    List<UserTagVo> getUserTagsVo(List<Long> tagIdsList);
}
