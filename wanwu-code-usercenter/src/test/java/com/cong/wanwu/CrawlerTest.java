package com.cong.wanwu;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cong.wanwu.usercenter.model.entity.Picture;
import com.cong.wanwu.usercenter.model.entity.Post;
import com.cong.wanwu.usercenter.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 爬虫测试
 *
 * @author 86188
 * @date 2023/03/13
 */
@SpringBootTest
public class CrawlerTest {
    @Resource
    private PostService postService;


    @Test
    void testFetchPicture() throws IOException {
        int current = 1;
        String url = "https://cn.bing.com/images/search?q=哆啦&first="+current;
        Document doc = Jsoup.connect(url).get();
//        System.out.println(doc);
        Elements newsHeadlines = doc.select(".iuscp.isv");
        List<Picture> pictureList = newsHeadlines.stream().map(headline -> {
            //取图片地址（murl）
            String m = headline.select(".iusc").get(0).attr("m");
            Map map = JSONUtil.toBean(m, Map.class);
            String mUrl = (String) map.get("murl");
            //取标题
            String title = headline.select(".inflnk").get(0).attr("aria-label");
            Picture picture = new Picture();
            picture.setUrl(mUrl);
            picture.setTitle(title);
            return picture;
        }).collect(Collectors.toList());


        for (Element headline : newsHeadlines) {
            //取图片地址（murl）
            String m = headline.select(".iusc").get(0).attr("m");
            Map map = JSONUtil.toBean(m,Map.class);
            String mUrl = (String) map.get("murl");
            System.out.println("mUrl = " + mUrl);
            //取标题
            String title = headline.select(".inflnk").get(0).attr("aria-label");
            System.out.println("title = " + title);


//            System.out.println(headline);
        }
    }
    @Test
    void testFetchPassage(){
        //1.获取数据
        String json = "{\n" +
                "  \"current\": 1,\n" +
                "  \"pageSize\": 8,\n" +
                "  \"sortField\": \"createTime\",\n" +
                "  \"sortOrder\": \"descend\",\n" +
                "  \"category\": \"文章\",\n" +
                "  \"reviewStatus\": 1\n" +
                "}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest.post(url).body(json).execute().body();
        //2.json转字符串
        Map map = JSONUtil.toBean(result,Map.class);
        JSONObject data= (JSONObject) map.get("data");
        JSONArray records= (JSONArray) data.get("records");
        List<Post> postList = records.stream().map(record -> {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle((String) tempRecord.get("title"));
            post.setContent((String) tempRecord.get("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagsList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagsList));
            post.setUserId(1L);
            return post;
        }).collect(Collectors.toList());
        System.out.println(postList);
        //3.数据入库
        boolean b = postService.saveBatch(postList);

        Assertions.assertTrue(b);

    }
}
