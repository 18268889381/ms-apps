package com.example.demo;

import com.alibaba.druid.pool.DruidDataSource;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@PropertySource({"database.properties"})
@Configuration
public class SchedulerConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            @Qualifier("quartzDataSource") DataSource quartzDataSource) {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setQuartzProperties(quartzProperties());
        // 使用应用的数据源
        bean.setDataSource(quartzDataSource);
        // 覆盖已存在的任务
        bean.setOverwriteExistingJobs(true);
        // 延时启动定时任务，避免系统未完全启动却开始执行定时任务的情况
        bean.setStartupDelay(15);
        return bean;
    }

    @Bean
    public Properties quartzProperties() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("/quartz.properties"));
        //在quartz.properties中的属性被读取并注入后再初始化对象
        try {
            bean.afterPropertiesSet();
            return bean.getObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("无法设置PropertiesFactoryBean对象的Properties");
    }

    /**
     * quartz初始化监听器
     */
    @Bean
    public QuartzInitializerListener quartzInitializerListener() {
        return new QuartzInitializerListener();
    }

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    @Bean(name = "primaryScheduler")
    public Scheduler primaryScheduler(SchedulerFactoryBean schedulerFactoryBean) {
        return schedulerFactoryBean.getScheduler();
    }

    @Qualifier("quartzDataSource")
    @Bean
    @ConditionalOnMissingBean(name = "quartzDataSource")
    public DataSource quartzDataSource(QuartzDataSourceProperty property) {
        DruidDataSource source = new DruidDataSource();
        /* 基础配置 */
        source.setUrl(property.getUrl());
        source.setUsername(property.getUsername());
        source.setPassword(property.getPassword());
        source.setDriverClassName(property.getDriverClassName());

        /* 其他配置 */
        source.setInitialSize(property.getInitialSize());
        source.setMinIdle(property.getMinIdle());
        source.setMaxActive(property.getMaxActive());
        source.setMaxWait(property.getMaxWait());
        source.setTimeBetweenEvictionRunsMillis(property.getTimeBetweenEvictionRunsMillis());
        source.setMinEvictableIdleTimeMillis(property.getMinEvictableIdleTimeMillis());
        source.setValidationQuery(property.getValidationQuery());
        source.setTestWhileIdle(property.getTestWhileIdle());
        source.setTestOnBorrow(property.getTestOnBorrow());
        source.setTestOnReturn(property.getTestOnReturn());

        return source;
    }
}
