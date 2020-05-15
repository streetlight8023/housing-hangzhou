package com.fangtan.hourse.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 *@desc:  数据源配置
 *@EnableTransactionManagement  // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
 *@author:  weiqingeng
 *@date:  2018/7/14 17:28
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.fangtan.hourse"})
public class DataSourceConfig {

    private Logger bizlogger = LoggerFactory.getLogger("biz");

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment env;

    /**
     * 配置数据源,为了尊重老的加密方式，就直接使用老的方式进行解密
     * @return
     */
    @Bean
    public DataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl( "jdbc:mysql://127.0.0.1:13306/ald?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&testOnBorrow=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setMaxActive(2);
        dataSource.setInitialSize(1);
        dataSource.setMaxWait(15000);
        dataSource.setMinIdle(1);
        List<Filter> filters = new ArrayList<>();
        StatFilter filter = new StatFilter();
        //慢sql，默认3s
        filter.setSlowSqlMillis(3);
        //增加sql统计的merge功能,默认是false
        filter.setMergeSql(true);
        //慢SQL日志记录
        filter.setLogSlowSql(true);
        filters.add(filter);
        dataSource.setProxyFilters(filters);
        return dataSource;
    }


    /**
     * 配置事务
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

}