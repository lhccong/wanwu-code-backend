package com.cong.wanwu.common.utils;

import java.util.Random;

/**
 * 代码跑龙套
 *
 * @author 86188
 * @date 2023/04/25
 */
public class CodeUtils {
    public static int generateRandomNumber() {
        // 创建 Random 对象
        Random random = new Random();
        // 生成一个六位随机数（范围从 100000 到 999999）
        int randomNumber = random.nextInt(900000) + 100000;
        return randomNumber;
    }
}
