package com.jinhua.easyexcel.ext.util;

/**
 * @author Jinhua-Lee
 */
@SuppressWarnings(value = "unused")
public class StringUtil {

    /**
     * 判断一个字符串是否是纯数字
     *
     * @param str 给定字符串
     * @return 是否纯数字
     */
    public static boolean ofNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch < 48 || ch > 57) {
                return false;
            }
        }
        return true;
    }
}
