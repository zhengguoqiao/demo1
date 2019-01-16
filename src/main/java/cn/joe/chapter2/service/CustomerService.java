package cn.joe.chapter2.service;

import cn.joe.chapter2.helper.DatabaseHelper;
import cn.joe.chapter2.model.Customer;
import cn.joe.chapter2.util.PropsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 提供客户数据服务
 */
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList(){
        String sql = "select * from customer";
        return DatabaseHelper.queryEntityList(Customer.class,sql);
    }

    /**
     * 获取客户信息
     */
    public Customer getCustomer(Long id){
        String sql = "select * from customer where id = ?";
        return DatabaseHelper.queryEntity(Customer.class,sql,id);
    }

    /**
     * 创建客户信息
     */
    public boolean createCustomer(Map<String,Object> fieldMap){
        return DatabaseHelper.insertEntity(Customer.class,fieldMap);
    }

    /**
     * 更新客户信息
     */
    public boolean updateCustomer(Long id,Map<String,Object> fieldMap){
        return DatabaseHelper.updateEntity(Customer.class,fieldMap,id);
    }

    /**
     *删除客户信息
     */
    public boolean deleteCustomer(Long id){
        return DatabaseHelper.deleteEntity(Customer.class,id);
    }
}


