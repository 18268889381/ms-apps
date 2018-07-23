package com.quartz.spring;

import com.alibaba.druid.pool.DruidDataSource;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Quartz配置
 */
@PropertySource({
        "database.properties"
})
@Configuration
public class QuartzConfig {

    private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);

    @Bean
    @ConditionalOnMissingBean(name = "quartzDataSource")
    public DataSource quartzDataSource(
            @Autowired QuartzDataSourceProperty property) {
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

    /**
     * 调度器工厂Bean
     */
    @Bean(name = "schedulerFactory")
    @ConditionalOnMissingBean(SchedulerFactoryBean.class)
    public SchedulerFactoryBean schedulerFactory(
            @Autowired @Qualifier("quartzDataSource") DataSource quartzDataSource,
            @Autowired List<Trigger> triggers) {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();

        Properties prop = new Properties();
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream stream = classLoader.getResourceAsStream("quartz.properties");
            prop.load(stream);
        } catch (IOException e) {
            logger.error("加载quartz.properties失败", e);
            throw new Error(e);
        }
        bean.setQuartzProperties(prop);

        // 使用应用的数据源
        bean.setDataSource(quartzDataSource);

        // 覆盖已存在的任务
        bean.setOverwriteExistingJobs(true);
        // 延时启动定时任务，避免系统未完全启动却开始执行定时任务的情况
        bean.setStartupDelay(15);
        // 注册触发器
        if (triggers != null && !triggers.isEmpty()) {
            bean.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
        }
        return bean;
    }

}
