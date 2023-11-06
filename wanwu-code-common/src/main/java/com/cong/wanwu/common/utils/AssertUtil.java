package com.cong.wanwu.common.utils;

import cn.hutool.core.util.ObjectUtil;

import com.cong.wanwu.common.common.ErrorCode;
import com.cong.wanwu.common.exception.BusinessException;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

/**
 * 校验工具类
 *
 * @author liuhuaicong
 * @date 2023/10/31
 */
public class AssertUtil {

    //如果不是true，则抛异常
    public static void isTrue(boolean expression, String msg) {
        if (!expression) {
            throwException(msg);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorEnum, Object... args) {
        if (!expression) {
            throwException(errorEnum, args);
        }
    }

    //如果是true，则抛异常
    public static void isFalse(boolean expression, String msg) {
        if (expression) {
            throwException(msg);
        }
    }

    //如果是true，则抛异常
    public static void isFalse(boolean expression, ErrorCode errorEnum, Object... args) {
        if (expression) {
            throwException(errorEnum, args);
        }
    }

    //如果不是非空对象，则抛异常
    public static void isNotEmpty(Object obj, String msg) {
        if (isEmpty(obj)) {
            throwException(msg);
        }
    }

    //如果不是非空对象，则抛异常
    public static void isNotEmpty(Object obj, ErrorCode errorEnum, Object... args) {
        if (isEmpty(obj)) {
            throwException(errorEnum, args);
        }
    }

    //如果不是非空对象，则抛异常
    public static void isEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            throwException(msg);
        }
    }

    public static void equal(Object o1, Object o2, String msg) {
        if (!ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }

    private static boolean isEmpty(Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    private static void throwException(String msg) {
        throwException(null, msg);
    }

    private static void throwException(ErrorCode errorEnum, Object... arg) {
        if (Objects.isNull(errorEnum)) {
            errorEnum = ErrorCode.OPERATION_ERROR;
        }
        throw new BusinessException(errorEnum, Arrays.toString(arg));
    }


}
