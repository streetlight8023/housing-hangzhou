package com.fangtan.hourse.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
public class StringUtil extends StringUtils {

    public static boolean isNotBlank(Object object) {
        if (object == null) return false;
        return isNotBlank(String.valueOf(object));
    }

    public static String firstCharUpper(String s) {
        StringBuffer sb = new StringBuffer(s.substring(0, 1).toUpperCase());
        sb.append(s.substring(1, s.length()));
        return sb.toString();
    }

    public static String firstCharLower(String s) {
        StringBuffer sb = new StringBuffer(s.substring(0, 1).toLowerCase());
        sb.append(s.substring(1, s.length()));
        return sb.toString();
    }

    public static String classStyle(String s, String separator) {
        String[] sArray = split(s, separator);
        StringBuilder sb = new StringBuilder();
        for (String t : sArray) sb.append(firstCharUpper(t));
        return sb.toString();
    }

    public static String humpStyle(String s, String separator) {
        return firstCharLower(classStyle(s, separator));
    }

    public static boolean isNull(Object object) {
        return object == null ? true : false;
    }

    public static String cutOut(String str, int len) {
        return str.length() < len ? str : str.substring(0, len);
    }


    /**
     * 姓名脱敏 两个字保留后一个字  以上字数保留两端
     *
     * @param userName
     * @return
     */
    public static String hideName(String userName) {

        if (StringUtils.isBlank(userName)) {
            return "";
        }

        if (StringUtils.length(userName) == 2) {
            return StringUtils.overlay(userName, "*", 0, userName.length() - 1);
        } else {
            String leftName = StringUtils.left(userName, 1);
            String rightName = StringUtils.right(userName, 1);
            return StringUtils.join(leftName, StringUtils.leftPad(rightName, userName.length() - 1, "*"));
        }

    }


    /**
     * 身份证脱敏 默认保留前6后4
     *
     * @param cardNo
     * @return
     */
    public static String hideIdNumber(String cardNo) {
        if (StringUtils.isBlank(cardNo)) {
            return "";
        }
        if (cardNo.toCharArray().length<10){
            return "-";
        }
        return StringUtils.left(cardNo, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNo, 4), StringUtils.length(cardNo), "*"), "***"));
    }

    public static String hideMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return "";
        }
        String left = StringUtils.left(mobile, 3);
        String right = StringUtils.right(mobile, 4);
        return StringUtils.join(left, StringUtils.leftPad(right, mobile.length() - 3, "*"));
    }




    /**
     * 将脱敏数据转换成 sql LIKE查询占位符
     * 例子: 王*岳 176****9500 -> 王%岳 176%9550
     * @param markingString String
     * @return unmaskingString String
     */
    public static String unmaskingString(String markingString) {
        if (StringUtils.startsWith(markingString, "*")) {
           return StringUtils.join("%", StringUtils.difference("*", markingString).trim());
        } else {
            String leftString = StringUtils.left(markingString, StringUtils.indexOf(markingString, "*")).trim();
            String rightString = StringUtils.substring(markingString, StringUtils.lastIndexOf(markingString, "*")+1).trim();
            return StringUtils.join(leftString, "%", rightString);
        }
    }


}
