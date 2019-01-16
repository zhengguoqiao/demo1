package cn.joe.chapter2.util;


import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 加载属性文件
     */
    public static Properties loadProps(String fileName){
        Properties prop = null;
        InputStream in = null;
        try{
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if(in == null){
                throw new FileNotFoundException();
            }
            prop = new Properties();
            prop.load(in);
        }catch (IOException e){
            LOGGER.error("properties load() Exception!");
        }finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("InputStream close Exception!");
                }
            }
        }
        return prop;
    }

    /**
     * 获得字符型数据(默认值位空串)
     */
    public static String getString(Properties prop,String key){
        return getString(prop,key,"");
    }

    /**
     * 获取字符型数据(可设置默认值)
     */
    public static String getString(Properties prop,String key,String defaultValue){
        String stringValue = defaultValue;
        if(prop.containsKey(key)){
            return prop.getProperty(key);
        }
        return stringValue;
    }

    /**
     * 获取整数型数据(默认值位0)
     */
    public static int getInt(Properties prop,String key){
        return getInt(prop,key,0);
    }

    /**
     * 获得整数型数据（可设置默认值）
     */
    public static int getInt(Properties prop,String key,int defaultValue){
        int intValue = defaultValue;
        if(prop.contains(key)){
            intValue = CastUtil.castInt(prop.getProperty(key));
        }
        return intValue;
    }

    /**
     * 获得布尔型数据(可设置默认值)
     */
    public static boolean getBoolean(Properties prop,String key,boolean defaultValue){
        boolean booleanValue = defaultValue;
        if(prop.contains(key)){
            booleanValue = CastUtil.castBoolean(prop.getProperty(key));
        }
        return booleanValue;
    }
}
