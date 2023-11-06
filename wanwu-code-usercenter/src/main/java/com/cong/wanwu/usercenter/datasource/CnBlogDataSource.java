package com.cong.wanwu.usercenter.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;
import com.cong.wanwu.usercenter.model.vo.CnBlogVo;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

/**
 根据关键词搜索博客园文章
 *
 * @author liuhuaicong
 * @date 2023/10/19
 */
@Service
public class CnBlogDataSource implements DataSource<CnBlogVo> {
    @Value("${cnblog.NotRobot}")
    private String notRobot;

    /**
     * 根据关键词搜索博客园文章
     * @param searchText 关键词
     * @param pageNum 页码
     * @param pageSize 页大小，这里固定为10，暂不支持其他pageSize
     * @return
     */
    @Override
    public Page<CnBlogVo> doSearch(String searchText, long pageNum, long pageSize) {
        if(pageSize != 10){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请求分页大小不合法");
        }

        String url = "https://zzk.cnblogs.com/s/blogpost?Keywords=" + searchText + "&pageindex=" + pageNum;
        Document doc;
        try {
            doc = Jsoup.connect(url).cookie("NotRobot", notRobot).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "爬取信息失败！");
        }

        //获取总页数
        String totalPageStr = doc.select(".last").text();
        if(StringUtils.isEmpty(totalPageStr)){
            totalPageStr = doc.select(".current").text();
        }
        long totalPage = StringUtils.isNotEmpty(totalPageStr) ? Long.valueOf(totalPageStr) : 1L;

        //页数超出限制
        if(pageNum > totalPage){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请求页码超出限制！");
        }

        Page<CnBlogVo> cnBlogPage = new Page<>(pageNum, pageSize);
        cnBlogPage.setTotal(totalPage * pageSize);

        Elements elements = doc.select(".searchItem");
        ArrayList<CnBlogVo> cnBlogVos = new ArrayList<>((int) pageSize);
        for (Element element : elements) {
            CnBlogVo cnBlogVo = new CnBlogVo();

            Elements titleElement = element.select(".searchItemTitle");
            cnBlogVo.setTitle(titleElement.text());
            cnBlogVo.setArticleUrl(titleElement.select("a").attr("href"));

            cnBlogVo.setSummary(element.select(".searchCon").text());

            Elements searchItemInfo = element.select(".searchItemInfo");

            Elements authorElement = searchItemInfo.select(".searchItemInfo-userName");
            cnBlogVo.setAuthor(authorElement.text());
            cnBlogVo.setAuthorUrl(authorElement.select("a").attr("href"));

            cnBlogVo.setViewCount(searchItemInfo.select(".searchItemInfo-views").text());
            cnBlogVo.setLikeCount(searchItemInfo.select(".searchItemInfo-good").text());
            cnBlogVo.setCommentCount(searchItemInfo.select(".searchItemInfo-comments").text());
            cnBlogVo.setCreatedTime(searchItemInfo.select(".searchItemInfo-publishDate").text());

            cnBlogVos.add(cnBlogVo);
        }

        cnBlogPage.setRecords(cnBlogVos);
        return cnBlogPage;
    }

}
