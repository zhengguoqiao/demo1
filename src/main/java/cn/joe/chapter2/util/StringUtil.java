package cn.joe.chapter2.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str){
        if(str != null){
            str.trim();
        }
        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串非空
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
}
