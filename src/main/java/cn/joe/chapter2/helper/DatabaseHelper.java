package cn.joe.chapter2.helper;

import cn.joe.chapter2.util.CollectionUtil;
import cn.joe.chapter2.util.PropsUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 数据库操作类
 */
public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<Connection>();
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    private static final BasicDataSource DATA_SOURCE = new BasicDataSource();
    //常量
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    //静态块初始化常量及加载mysql驱动
    static{
        //加载配置文件
        Properties props = PropsUtil.loadProps("config.properties");
        DRIVER = PropsUtil.getString(props, "jdbc.driver");
        URL = PropsUtil.getString(props, "jdbc.url");
        USERNAME = PropsUtil.getString(props, "jdbc.username");
        PASSWORD = PropsUtil.getString(props, "jdbc.password");

        //数据库连接池
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
    }

    /**
     * 获得链接
     */
    private static Connection getConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null){
            try {
                conn = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                LOGGER.error("get connection error",e);
            }finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }

    /**
     * 关闭连接
     */
    private static void closeConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection error",e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    /**
     * 获得实体列表,dbutil操作数据库
     */
    public static<T> List<T> queryEntityList(Class<T> clazz,String sql,Object... params){
        Connection conn;
        List<T> resultList = null;
        try {
            conn = getConnection();
            resultList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(clazz), params);
        }catch (SQLException e){
            LOGGER.error("query entity list fail",e);
        }finally {
            closeConnection();
        }
        return resultList;
    }

    /**
     * 获得单个实体
     * @param clazz 实体类对象
     * @param sql 执行的sql语句
     * @param params 参数
     */
    public static<T> T queryEntity(Class<T> clazz,String sql,Object... params){
        Connection conn;
        T t = null;
        try {
            conn = getConnection();
            t = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(clazz), params);
        } catch (SQLException e) {
            LOGGER.error("query entity error",e);
        }finally {
            closeConnection();
        }
        return t;
    }

    /**
     * 自定义查询
     * 返回列名与值得映射得List<Map<String,Object>>
     */
    public List<Map<String,Object>> executeQuery(Class clazz,String sql,Object... params){
        Connection conn;
        List<Map<String,Object>> result = null;
        try{
            conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        }catch (SQLException e){
            LOGGER.error("execute sql error!",e);
        }finally {
            closeConnection();
        }
        return result;
    }

    /**
     * 执行更新语句(包括新增、更新、删除)
     */
    public static int executeUpdate(String sql,Object... params){
        Connection conn;
        int result = 0;
        try {
            conn = getConnection();
            result = QUERY_RUNNER.update(conn,sql,params);
        } catch (SQLException e) {
            LOGGER.error("execute update error",e);
        }finally {
            closeConnection();
        }
        return result;
    }

    /**
     * 获得实体类映射表名
     */
    private static String getTableName(Class clazz){
        String clazzName = clazz.getSimpleName();
        char[] cs = clazzName.toCharArray();
        StringBuilder tableName = new StringBuilder();
        tableName.append(cs[0]);
        for(int i=1;i<cs.length;i++){
            if(cs[i] >= 'A' && cs[i]<='Z'){
                tableName.append("_"+cs[i]);
            }else {
                tableName.append(cs[i]);
            }
        }
        return tableName.toString().toLowerCase();
    }

    /**
     * 插入实体
     */
    public static<T> boolean insertEntity(Class<T> entityClass,Map<String,Object> fieldMap){
        int result = 0;
        if(CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("fieldMap is null!");
            return false;
        }

        //拼装插入语句
        String sql =  "insert into "+getTableName(entityClass);
        StringBuilder colums = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for(String colum : fieldMap.keySet()){
            colums.append(colum).append(",");
            values.append("?,");
        }
        colums.replace(colums.lastIndexOf(","),colums.length(),")");
        values.replace(values.lastIndexOf(","),values.length(),")");
        sql += colums.toString() + "values" + values.toString();

        Object[] objects = fieldMap.values().toArray();
        return executeUpdate(sql,objects) == 1;
    }

    /**
     * 更新实体
     */
    public static<T> boolean updateEntity(Class<T> entityClass,Map<String,Object> fieldMap,Long id){
        if(fieldMap == null){
            LOGGER.error("fieldMap is null!");
            return false;
        }
        String sql = "update "+getTableName(entityClass);
        StringBuilder colums = new StringBuilder(" set ");
        for(String key : fieldMap.keySet()){
            colums.append(key + "=?,");
        }
        colums.replace(colums.lastIndexOf(","),colums.length()," ");

        sql += colums.toString()+"where id = ?";
        List<Object> paramsList = new ArrayList<Object>();
        paramsList.addAll(fieldMap.values());
        paramsList.add(id);
        Object[] params = paramsList.toArray();
        return executeUpdate(sql,params) == 1;
    }

    /**
     * 删除实体
     */
    public static<T> boolean deleteEntity(Class<T> entityClass,long id){
        String sql = "delete from "+getTableName(entityClass)+" where id = ?";
        return executeUpdate(sql,id) == 1;
    }

    /**
     * 执行sql文件
     */
    public static void executeSqlFile(String filePath){
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        InputStreamReader isr = new InputStreamReader(is);
        try{
            BufferedReader br = new BufferedReader(isr);
            String sql;
            while((sql = br.readLine()) != null){
                executeUpdate(sql);
            }
        }catch (IOException e){
            LOGGER.error("execute sql file error!",e);
        }
    }
}
