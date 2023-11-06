package com.cong.wanwu.usercenter.manager;

import com.cong.wanwu.usercenter.datasource.*;
import com.cong.wanwu.usercenter.model.enums.SearchTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源注册表
 *
 * @author 86188
 * @date 2023/03/20
 */
@Component
public class DataSourceRegistry {

    @Resource
    private PostDataSource postDataSource;
    @Resource
    private UserDataSource userDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
    private Map<String, DataSource> typeDataSourceMap;

    @Resource
    private CnBlogDataSource cnBlogDataSource;
    @Resource
    private CsdnDataSource csdnDataSource;

    @PostConstruct
    public void doInit() {
        typeDataSourceMap = new HashMap() {{
            put(SearchTypeEnum.POST.getValue(), postDataSource);
            put(SearchTypeEnum.USER.getValue(), userDataSource);
            put(SearchTypeEnum.USER.getValue(), userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
            put(SearchTypeEnum.CNBLOG.getValue(), cnBlogDataSource);
            put(SearchTypeEnum.CSDN.getValue(), csdnDataSource);
        }};
    }


    public DataSource getDataSourceByType(String type) {
        if (typeDataSourceMap==null){
            return null;
        }
        return typeDataSourceMap.get(type);
    }
}
