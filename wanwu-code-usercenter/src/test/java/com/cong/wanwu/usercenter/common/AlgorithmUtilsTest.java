package com.cong.wanwu.usercenter.common;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmUtilsTest {

    @Test
    void minDistance() {
        List<String> TagList1 = Arrays.asList("Java", "大一", "男");
        List<String> TagList2 = Arrays.asList("Java", "大一", "女");
        List<String> TagList3 = Arrays.asList("Python", "大二", "男");

        int i = AlgorithmUtils.minDistance(TagList1, TagList2);
        int i1 = AlgorithmUtils.minDistance(TagList1, TagList3);

        System.out.println(i);
        System.out.println(i1);
    }
}