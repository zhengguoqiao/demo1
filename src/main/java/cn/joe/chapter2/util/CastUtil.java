package cn.joe.chapter2.util;

import com.sun.org.apache.xml.internal.utils.StringToStringTable;
import org.apache.commons.lang3.StringUtils;

public class CastUtil {
    /**
     * 转换为String对象
     * @param obj 转String对象
     * @return
     */
    public static String castString(Object obj){
        return castString(obj,"");
    }

    /**
     * 转换位String对象
     * @param obj 转换对象
     * @param defaultValue 默认值
     * @return
     */
    public static String castString(Object obj,String defaultValue){
        return obj != null ? String.valueOf(obj) : "";
    }

    public double castDouble(Object obj){
        return castDouble(obj,0);
    }

    public static double castDouble(Object obj,double defaultValue){
        double doubleValue = defaultValue;
        if(obj != null){
            String str = castString(obj);
            if(StringUtil.isNotEmpty(str)){
                try {
                    doubleValue = Double.parseDouble(str);
                }catch(NumberFormatException e){
                }
            }
        }
        return doubleValue;
    }

    public static int castInt(Object obj){
        return castInt(obj,0);
    }

    public static int castInt(Object obj,int defaultvalue){
        int intValue = defaultvalue;
        if(obj != null){
            String str = castString(obj);
            if(StringUtil.isNotEmpty(str)){
                try {
                    intValue = Integer.parseInt(str);
                }catch (NumberFormatException e){
                }
            }
        }
        return intValue;
    }

    public static long castLong(Object obj){
        return castLong(obj,0l);
    }

    public static long castLong(Object obj,long defaultValue){
        long longValue = defaultValue;
        if(obj != null){
            String str = castString(obj);
            if(StringUtil.isNotEmpty(str)){
                try{
                    longValue = Long.parseLong(str);
                }catch (NumberFormatException e){
                }
            }
        }
        return longValue;
    }

    public static boolean castBoolean(Object obj){
        return castBoolean(obj,false);
    }

    public static boolean castBoolean(Object obj,boolean defaultValue){
        boolean booleanValue = defaultValue;
        if(obj != null){
              booleanValue =  Boolean.parseBoolean(castString(obj));
        }
        return booleanValue;
    }
}
