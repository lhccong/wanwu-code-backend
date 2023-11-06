package com.cong.wanwu.bi.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * excel相关工具类
 *
 * @author 86188
 * @date 2023/05/10
 */
@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile)  {

        //读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误",e);
         }
        if (CollUtil.isEmpty(list)){
            return "";
        }
        //转换为csv
        StringBuffer stringBuffer = new StringBuffer();
        //读取表头
        LinkedHashMap<Integer, String> header = (LinkedHashMap<Integer, String>) list.get(0);
        List<String> collect = header.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuffer.append(StringUtils.join(collect,",")).append("\n");
        //读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuffer.append(StringUtils.join(dataList,",")).append("\n");

        }
        return stringBuffer.toString();
    }


}
