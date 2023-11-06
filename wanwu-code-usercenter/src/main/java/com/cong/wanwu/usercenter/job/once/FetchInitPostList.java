package com.cong.wanwu.usercenter.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cong.wanwu.usercenter.model.entity.Post;
import com.cong.wanwu.usercenter.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 获取初始化列表后
 *
 * @author 86188
 * @date 2023/03/13
 */
/** todo 取消注释开启任务
 * @author 86188**/
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;
    @Override
    public void run(String... args) throws Exception {
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
        boolean resultSave = postService.saveBatch(postList);
        if (resultSave){
            log.info("获取初始化帖子列表成功，条数 = {}",resultSave);
        }else {
            log.error("获取初始化帖子列表失败");
        }
    }
}
