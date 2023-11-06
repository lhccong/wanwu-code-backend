package com.cong.wanwu.usercenter.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.usercenter.model.entity.Picture;
import com.cong.wanwu.usercenter.service.PictureService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务impl
 *
 * @author 86188
 * @date 2023/03/13
 */
@Service
public class PictureServiceImpl implements PictureService {
    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1 ) * pageSize==0?1:(pageNum - 1 ) * pageSize;
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s",StringUtils.isBlank(searchText)?"哆啦":searchText,current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据获取异常");
        }
        Elements newsHeadlines = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element headline : newsHeadlines) {
            //取图片地址（murl）
            String m = headline.select(".iusc").get(0).attr("m");
            Map map = JSONUtil.toBean(m, Map.class);
            String mUrl = (String) map.get("murl");
            //取标题
            String title = headline.select(".inflnk").get(0).attr("aria-label");
            Picture picture = new Picture();
            picture.setUrl(mUrl);
            picture.setTitle(title);
            pictureList.add(picture);
            if (pictureList.size()>=pageSize){
                break;
            }
        }
        Page<Picture> page = new Page<>(pageNum, pageSize);
        page.setRecords(pictureList);
        return page;
    }
}
