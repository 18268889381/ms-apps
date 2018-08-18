//package com.quartz.spring;
//
//import com.quartz.spring.job.SimpleJob;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
//import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
//
//@Configuration
//public class QuartzJobConfig {
//
//    /**
//     * 方法调用任务明细工厂Bean
//     */
//    @Bean(name = "jobDetailFactoryBean")
//    public MethodInvokingJobDetailFactoryBean jobDetailFactoryBean(SimpleJob simpleJob) {
//        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
//        jobDetail.setConcurrent(false); // 是否并发
//        jobDetail.setName("general-simpleJob"); // 任务的名字
//        jobDetail.setGroup("general"); // 任务的分组
//        jobDetail.setTargetObject(simpleJob); // 被执行的对象
//        jobDetail.setTargetMethod("execute"); // 被执行的方法
//        return jobDetail;
//    }
//
//    /**
//     * 表达式触发器工厂Bean
//     */
//    @Bean(name = "myFirstExerciseJobTrigger")
//    public CronTriggerFactoryBean myFirstExerciseJobTrigger(
//            @Qualifier("jobDetailFactoryBean") MethodInvokingJobDetailFactoryBean jobDetailFactoryBean) {
//        CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
//        tigger.setJobDetail(jobDetailFactoryBean.getObject());
//        // 什么是否触发，Spring Scheduler Cron表达式
//        tigger.setCronExpression("0/10 * * * * ?");
//        tigger.setName("general-simpleJob");
//        return tigger;
//    }
//
//}
