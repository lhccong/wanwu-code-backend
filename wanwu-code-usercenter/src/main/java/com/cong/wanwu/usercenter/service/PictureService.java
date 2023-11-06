package com.cong.wanwu.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.usercenter.model.entity.Picture;

import java.util.List;

/**
 * 图片服务
 *
 *  @author 聪
 *  
 */
public interface PictureService  {


    /**
     * 搜索图片
     *
     * @param searchText 搜索文本
     * @param pageNum    页面num
     * @param pageSize   页面大小
     * @return {@link List}<{@link Picture}>
     */
    Page<Picture> searchPicture(String searchText,long pageNum,long pageSize);
}
