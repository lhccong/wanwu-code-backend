package com.cong.wanwu.bi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cong.wanwu.bi.model.dto.disease.DiseaseQueryRequest;
import com.cong.wanwu.bi.model.entity.Disease;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86188
* @description 针对表【disease(病症信息表)】的数据库操作Service
* @createDate 2023-06-01 16:20:46
*/
public interface DiseaseService extends IService<Disease> {

    /**
     * 得到查询包装
     *
     * @param diseaseQueryRequest 疾病查询请求
     * @return {@link Object}
     */
    QueryWrapper<Disease> getQueryWrapper(DiseaseQueryRequest diseaseQueryRequest);
}
