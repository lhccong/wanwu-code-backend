package com.cong.wanwu.usercenter.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.common.utils.BeanCopyUtils;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.usercenter.model.dto.userTag.UserTagAddRequest;
import com.cong.wanwu.usercenter.model.entity.UserTag;
import com.cong.wanwu.usercenter.model.vo.UserTagCategoryVo;
import com.cong.wanwu.usercenter.model.vo.UserTagVo;
import com.cong.wanwu.usercenter.service.UserTagService;
import java.util.Collections;
import com.cong.wanwu.usercenter.mapper.UserTagMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.cong.wanwu.common.constant.SystemConstants.TAG_COLORS;

/**
* @author liuhuaicong
* @description 针对表【user_tag】的数据库操作Service实现
* @createDate 2023-09-08 16:41:55
*/
@Service
public class UserTagServiceImpl extends ServiceImpl<UserTagMapper, UserTag>
    implements UserTagService{

    @Override
    public List<UserTagCategoryVo> getAllTags() {
        //TODO 获取大类 后面改成Redis获取
        List<UserTag> parentTagList = this.list(new LambdaQueryWrapper<UserTag>().eq(UserTag::getParentId, 0));
        //获取标签
        return parentTagList.stream().map(item -> {
            UserTagCategoryVo userTagCategoryVo = BeanCopyUtils.copyBean(item, UserTagCategoryVo.class);
            List<UserTag> tags = this.list(new LambdaQueryWrapper<UserTag>().eq(UserTag::getParentId, item.getId()));
            List<UserTagVo> userTagVoList = tags.stream().map(item1 -> BeanCopyUtils.copyBean(item1, UserTagVo.class)).collect(Collectors.toList());
            userTagCategoryVo.setTags(userTagVoList);
            return userTagCategoryVo;
        }).collect(Collectors.toList());
    }

    @Override
    public UserTagVo addTag(UserTagAddRequest userTagAddRequest) {

        //获取这个分类下的所有标签
        List<UserTag> tags = this.list(new LambdaQueryWrapper<UserTag>().eq(UserTag::getParentId, userTagAddRequest.getParentId()));

        //判断这个标签是否出现过
        for (UserTag tag : tags) {
            if(tag.getName().equals(userTagAddRequest.getName().toLowerCase())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"标签已存在");
            }
        }

        //标签不存在则将其插入到mysql和redis中（随机生成一个颜色）
        UserTag userTag = BeanCopyUtils.copyBean(userTagAddRequest, UserTag.class);
        userTag.setCreatedBy(Long.valueOf((String) StpUtil.getLoginId()));
        userTag.setColor(TAG_COLORS[new Random().nextInt(TAG_COLORS.length)]);
        save(userTag);
//
//        //存入redis并返回
//        UserTagVo userTagVo = BeanCopyUtils.copyBean(userTag, UserTagVo.class);
//        stringRedisTemplate.opsForList().rightPush(USER_TAGS_PREFIX + userTag.getParentId(), ObjectMapperUtils.writeValueAsString(userTagVo));
        return BeanCopyUtils.copyBean(userTag, UserTagVo.class);
    }

    @Override
    public List<UserTagVo> getUserTagsVo(List<Long> tagIdsList) {
        if (CollectionUtils.isEmpty(tagIdsList)){
            return Collections.emptyList();
        }
        List<UserTag> userTagList = this.list(new LambdaQueryWrapper<UserTag>().in(UserTag::getId, tagIdsList));
        return userTagList.stream().map(item -> BeanCopyUtils.copyBean(item, UserTagVo.class)).collect(Collectors.toList());
    }
}




