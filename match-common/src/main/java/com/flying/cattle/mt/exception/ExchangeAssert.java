/*
 * @project exchange-common  V1.0
 * @filename: ExchangeAssert 2020-07-10
 * Copyright(c) 2020 kinbug Co. Ltd.
 * All right reserved.
 */
package com.flying.cattle.mt.exception;

import java.util.Collection;
import java.util.Map;

/**
 * @author senkyouku
 * @ClassName: ExchangeAssert
 * @Description: 交易断言工具类
 * @date 2020-07-10
 */
public class ExchangeAssert {

    private static final String EMPTY_STR = "";

    public static void createException(ExchangeError err, Object... args) {
        String message = String.format(err.getMessage(), args);
        throw new ExchangeException(err.getCode(), message);
    }

    /**
     * 如果表达式结果为false，则抛出异常
     *
     * @param expression 传入的表达式
     * @param err        错误信息枚举对象
     */
    public static void isTrue(boolean expression, ExchangeError err) {
        if (!expression) {
            createException(err);
        }
    }

    /**
     * 如果表达式结果为false，则抛出异常
     *
     * @param expression 传入的表达式
     * @param err        错误信息枚举对象
     * @param args       消息参数
     */
    public static void isTrue(boolean expression, ExchangeError err, Object... args) {
        if (!expression) {
            createException(err, args);
        }
    }

    /**
     * 如果表达式结果为true，则抛出异常
     *
     * @param expression 传入的表达式
     * @param err        错误信息枚举对象
     */
    public static void notTrue(boolean expression, ExchangeError err) {
        if (expression) {
            createException(err);
        }
    }

    /**
     * 如果表达式结果为true，则抛出异常
     *
     * @param expression 传入的表达式
     * @param err        错误信息枚举对象
     * @param args       消息参数
     */
    public static void notTrue(boolean expression, ExchangeError err, Object... args) {
        if (expression) {
            createException(err, args);
        }
    }

    /**
     * 如果对象不为空，则抛出异常
     *
     * @param object 要检查的对象
     * @param err    错误信息枚举对象
     * @param args   消息参数
     */
    public static void isNull(Object object, ExchangeError err, Object... args) {
        if (object != null) {
            createException(err, args);
        }
    }

    /**
     * 如果对象不为空，则抛出异常
     *
     * @param object 要检查的对象
     * @param err    错误信息枚举对象
     */
    public static void isNull(Object object, ExchangeError err) {
        if (object != null) {
            createException(err);
        }
    }

    /**
     * 如果对象为空，则抛出异常
     *
     * @param object 要检查的对象
     * @param err    错误信息枚举对象
     * @param args   消息参数
     */
    public static void notNull(Object object, ExchangeError err, Object... args) {
        if (object == null) {
            createException(err, args);
        }
    }

    /**
     * 如果对象为空，则抛出异常
     *
     * @param object 要检查的对象
     * @param err    错误信息枚举对象
     */
    public static void notNull(Object object, ExchangeError err) {
        if (object == null) {
            createException(err);
        }
    }

    /**
     * 如果字符串为空字符串，则抛出异常
     *
     * @param text 要判断的字符串
     * @param err  错误信息枚举对象
     */
    public static void notEmpty(String text, ExchangeError err) {
        if (text == null || text.equals(EMPTY_STR)) {
            createException(err);
        }
    }

    /**
     * 如果字符串为空字符串，则抛出异常
     *
     * @param text 要判断的字符串
     * @param err  错误信息枚举对象
     * @param args 消息参数
     */
    public static void notEmpty(String text, ExchangeError err, Object... args) {
        if (text == null || text.equals(EMPTY_STR)) {
            createException(err, args);
        }
    }

    /**
     * 如果字符串为非空字符串，则抛出异常
     *
     * @param text 要判断的字符串
     * @param err  错误信息枚举对象
     */
    public static void isEmpty(String text, ExchangeError err) {
        if (!(text == null || text.equals(EMPTY_STR))) {
            createException(err);
        }
    }

    /**
     * 如果字符串为非空字符串，则抛出异常
     *
     * @param text 要判断的字符串
     * @param err  错误信息枚举对象
     * @param args 消息参数
     */
    public static void isEmpty(String text, ExchangeError err, Object... args) {
        if (text == null || text.equals(EMPTY_STR)) {
            createException(err, args);
        }
    }

    /**
     * 如果字符串中包含子字符串，则抛出异常
     *
     * @param textToSearch
     * @param substring
     * @param err
     */
    public static void doesNotContain(String textToSearch, String substring, ExchangeError err) {
        if (textToSearch != null && !textToSearch.equals(EMPTY_STR) && substring != null && !substring.equals(EMPTY_STR) && textToSearch.indexOf(substring) != -1) {
            createException(err);
        }
    }

    /**
     * 如果字符串中包含子字符串，则抛出异常
     *
     * @param textToSearch
     * @param substring
     * @param err
     * @param args
     */
    public static void doesNotContain(String textToSearch, String substring, ExchangeError err, Object... args) {
        if (textToSearch != null && !textToSearch.equals(EMPTY_STR) && substring != null && !substring.equals(EMPTY_STR) && textToSearch.indexOf(substring) != -1) {
            createException(err, args);
        }
    }

    /**
     * 如果数组为空，则抛出异常
     *
     * @param array
     * @param err
     */
    public static void notEmpty(Object[] array, ExchangeError err) {
        if (array == null || array.length == 0) {
            createException(err);
        }
    }

    /**
     * 如果数组为空，则抛出异常
     *
     * @param array
     * @param err
     * @param args
     */
    public static void notEmpty(Object[] array, ExchangeError err, Object... args) {
        if (array == null || array.length == 0) {
            createException(err, args);
        }
    }

    /**
     * 如果对象数组中存在空对象，则抛出异常
     *
     * @param array
     * @param err
     */
    public static void noNullElements(Object[] array, ExchangeError err) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    createException(err);
                }
            }
        }
    }

    /**
     * 如果对象数组中存在空对象，则抛出异常
     *
     * @param array
     * @param err
     * @param args
     */
    public static void noNullElements(Object[] array, ExchangeError err, Object... args) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    createException(err, args);
                }
            }
        }
    }

    /**
     * 如果集合为空，则抛出异常
     *
     * @param collection
     * @param err
     */
    public static void notEmpty(Collection<?> collection, ExchangeError err) {
        if (collection == null || collection.size() == 0) {
            createException(err);
        }
    }

    /**
     * 如果集合为空，则抛出异常
     *
     * @param collection
     * @param err
     * @param args
     */
    public static void notEmpty(Collection<?> collection, ExchangeError err, Object... args) {
        if (collection == null || collection.size() == 0) {
            createException(err, args);
        }
    }

    /**
     * 如果集合不为空，则抛出异常
     *
     * @param collection
     * @param err
     */
    public static void isEmpty(Collection<?> collection, ExchangeError err) {
        if (!(collection == null || collection.size() == 0)) {
            createException(err);
        }
    }

    /**
     * 如果集合不为空，则抛出异常
     *
     * @param collection
     * @param err
     * @param args
     */
    public static void isEmpty(Collection<?> collection, ExchangeError err, Object... args) {
        if (!(collection == null || collection.size() == 0)) {
            createException(err, args);
        }
    }

    /**
     * 如果map为空，则抛出异常
     *
     * @param map
     * @param err
     */
    public static void notEmpty(Map<?, ?> map, ExchangeError err) {
        if (map == null || map.size() == 0) {
            createException(err);
        }
    }

    /**
     * 如果map为空，则抛出异常
     *
     * @param map
     * @param err
     * @param args
     */
    public static void notEmpty(Map<?, ?> map, ExchangeError err, Object... args) {
        if (map == null || map.size() == 0) {
            createException(err, args);
        }
    }

    /**
     * 如果对象不是指定class的实例，则抛出异常
     *
     * @param clazz
     * @param obj
     * @param err
     */
    public static void isInstanceOf(Class<?> clazz, Object obj, ExchangeError err) {
        notNull(clazz, err);
        if (!clazz.isInstance(obj)) {
            createException(err);
        }
    }

    /**
     * 如果对象不是指定class的实例，则抛出异常
     *
     * @param clazz
     * @param obj
     * @param err
     * @param args
     */
    public static void isInstanceOf(Class<?> clazz, Object obj, ExchangeError err, Object... args) {
        notNull(clazz, err, args);
        if (!clazz.isInstance(obj)) {
            createException(err, args);
        }
    }

    /**
     * 如果不全是数字，则抛出异常
     *
     * @param text
     * @param err
     */
    public static void isDigit(String text, ExchangeError err) {
        notEmpty(text, err);
        String reg_digit = "[0-9]*";
        if (!text.matches(reg_digit)) {
            createException(err);
        }
    }
}
