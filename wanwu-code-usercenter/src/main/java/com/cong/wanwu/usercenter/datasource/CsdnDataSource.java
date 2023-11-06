package com.cong.wanwu.usercenter.datasource;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.usercenter.model.vo.CsdnBlogVo;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

/**
 * 图片服务实现类
 */
@Service
public class CsdnDataSource implements DataSource<CsdnBlogVo> {
    public static final int CSDN_PAGE_SIZE = 30;

    /**
     * 根据关键词搜索博客园文章
     * @param searchText 关键词
     * @param pageNum 页码
     * @param pageSize 页大小，这里固定为10，暂不支持其他pageSize
     * @return
     */
    @Override
    public Page<CsdnBlogVo> doSearch(String searchText, long pageNum, long pageSize) {
        if(pageSize != 10){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求分页大小不合法");
        } else if (pageNum > 60) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求页码超出限制");
        }

        int p = (int) ((pageNum - 1) * pageSize / CSDN_PAGE_SIZE + 1);
        String url = "https://so.csdn.net/api/v3/search?q=" + searchText + "&t=blog&p=" + p;
        String result = HttpRequest.get(url).execute().body();

        ArrayList<CsdnBlogVo> csdnBlogVos = new ArrayList<>((int) pageSize);
        Map map = JSONUtil.toBean(result, Map.class);

        JSONArray records = (JSONArray) map.get("result_vos");
        int startIndex = (int) ((pageNum - 1) * pageSize % CSDN_PAGE_SIZE);
        for(int i = 0;i < pageSize;i++){
            JSONObject tempRecord = null;
            try {
                tempRecord = (JSONObject) records.get(startIndex + i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            CsdnBlogVo csdnBlogVo = new CsdnBlogVo();
            csdnBlogVo.setTitle(tempRecord.getStr("title"));
            csdnBlogVo.setArticleUrl(tempRecord.getStr("url"));
            String body = tempRecord.getStr("body");
            if(StringUtils.isNotEmpty(body)){
                if(body.length() < 800){
                    csdnBlogVo.setSummary(body);
                } else {
                    csdnBlogVo.setSummary(body.substring(0, 800));
                }
            } else {
                String description = tempRecord.getStr("description");
                if(description.length() < 800) {
                    csdnBlogVo.setSummary(description);
                } else {
                    csdnBlogVo.setSummary(description.substring(0, 800));
                }
            }
            csdnBlogVo.setViewCount(tempRecord.getStr("view"));
            csdnBlogVo.setLikeCount(tempRecord.getStr("digg"));
            csdnBlogVo.setCommentCount(tempRecord.getStr("comment"));
            csdnBlogVo.setAuthor(tempRecord.getStr("nickname"));
            csdnBlogVo.setAuthorUrl(tempRecord.getStr("author_space"));
            csdnBlogVo.setCreatedTime(tempRecord.getStr("create_time_str"));
            csdnBlogVo.setPicList(tempRecord.getBeanList("pic_list", String.class));
            csdnBlogVos.add(csdnBlogVo);
        }

        Page<CsdnBlogVo> csdnBlogPage = new Page<>(pageNum, pageSize);
        csdnBlogPage.setRecords(csdnBlogVos);
        return csdnBlogPage;
    }
}
