package com.game.base.commons.utils.text;

import org.apache.commons.lang.StringUtils;

public class NeteaseEduStringUtils {

    public static String[] parseStandardStringArrayFromString(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        int left = text.indexOf("[");
        int right = text.lastIndexOf("]");

        if (left >= 0 && right > left) {
            String strNoThresh = text.substring(left + 1, right);
            return StringUtils.split(strNoThresh, ",");
        }

        return null;

    }

    public static String parseColumnNameToPropertyString(String text) {
        if (text == null || text.length() == 0) return text;
        String[] subStrings = text.split("_");
        StringBuilder sb = new StringBuilder();
        for (String subString : subStrings) {
            if (Character.isLowerCase(subString.charAt(0)) && subString.length() > 0) subString = Character.toUpperCase(subString.charAt(0))
                                                                                                  + subString.substring(1);
            sb.append(subString);
        }
        return sb.toString();
    }

    public static Long[] parseStandardLongArrayFromString(String text) {
        String[] strAry = parseStandardStringArrayFromString(text);
        if (strAry == null) {
            return null;
        }
        Long[] longAry = new Long[strAry.length];
        for (int i = 0; i < strAry.length; i++) {
            longAry[i] = Long.parseLong(strAry[i]);

        }
        return longAry;

    }

    public static String substring(String s, int maxLength) {
        if (StringUtils.isEmpty(s) || maxLength <= 0) return null;
        maxLength = maxLength > s.length() ? s.length() : maxLength;
        return s.substring(0, maxLength);
    }

    public static String upperFirstChar(String source) {
        if (StringUtils.isBlank(source)) {
            return "";
        }
        Character firstChar = source.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return Character.toUpperCase(firstChar) + source.substring(1);
        }
        return source;
    }

    public static String convertCamel2UnderlineStyle(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length() + 10);
        for (int i = 0; i < str.length(); i++) {
            char array_element = str.charAt(i);
            if (Character.isUpperCase(array_element)) {
                if (i != 0) {
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(array_element));
            } else {
                sb.append(array_element);
            }

        }
        return sb.toString();
    }

    public static void main(String[] args) {

        System.out.println(convertCamel2UnderlineStyle(NeteaseEduStringUtils.class.getSimpleName()));

        System.out.println(String.valueOf(new Integer(12345)));
        System.out.println(new Integer(12345));

        System.out.println(substring("1234", 3));

        System.out.println(upperFirstChar("TestGo"));
        System.out.println(upperFirstChar("testGo"));
        System.out.println(upperFirstChar("TTstGo"));
        System.out.println(upperFirstChar("ttstGo"));
        System.out.println(upperFirstChar(""));
        System.out.println(upperFirstChar(null));
        System.out.println(parseColumnNameToPropertyString("is_boolean"));
        System.out.println(parseColumnNameToPropertyString("follow_q_cancel_best_repl"));
        System.out.println(parseColumnNameToPropertyString("Follow_q_cancel_best_repl"));
        System.out.println(parseColumnNameToPropertyString(null));
        System.out.println(parseColumnNameToPropertyString("follow"));
    }
}
