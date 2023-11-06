package com.cong.wanwu.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cong.wanwu.bi.constant.CommonConstant;
import com.cong.wanwu.bi.service.DiseaseService;
import com.cong.wanwu.bi.model.dto.disease.DiseaseQueryRequest;
import com.cong.wanwu.bi.model.entity.Disease;
import com.cong.wanwu.bi.mapper.DiseaseMapper;
import com.cong.wanwu.bi.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
* @author 86188
* @description 针对表【disease(病症信息表)】的数据库操作Service实现
* @createDate 2023-06-01 16:20:46
*/
@Service
public class DiseaseServiceImpl extends ServiceImpl<DiseaseMapper, Disease>
    implements DiseaseService {

    @Override
    public QueryWrapper<Disease> getQueryWrapper(DiseaseQueryRequest diseaseQueryRequest) {
        QueryWrapper<Disease> queryWrapper = new QueryWrapper<>();
        if (diseaseQueryRequest == null) {
            return queryWrapper;
        }


        Long id = diseaseQueryRequest.getId();
        Long userId = diseaseQueryRequest.getUserId();
        String name = diseaseQueryRequest.getName();
        Integer sex = diseaseQueryRequest.getSex();
        String diseaseType = diseaseQueryRequest.getDiseaseType();
        String userDesc = diseaseQueryRequest.getUserDesc();
        long current = diseaseQueryRequest.getCurrent();
        long pageSize = diseaseQueryRequest.getPageSize();
        String sortField = diseaseQueryRequest.getSortField();
        String sortOrder = diseaseQueryRequest.getSortOrder();
                // 拼接查询条件

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(sex), "sex", sex);
        queryWrapper.like(ObjectUtils.isNotEmpty(name), "name", name);
        queryWrapper.eq(ObjectUtils.isNotEmpty(diseaseType), "diseaseType", diseaseType);
        queryWrapper.like(ObjectUtils.isNotEmpty(userDesc), "userDesc", userDesc);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




