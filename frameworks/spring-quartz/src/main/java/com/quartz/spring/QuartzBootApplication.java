package com.quartz.spring;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuartzBootApplication {

    private static Logger logger = LoggerFactory.getLogger(QuartzBootApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(QuartzBootApplication.class, args);

//        run();
    }

    public static void run() {
        try {
            // 获取调度器
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // 注册一个示例任务和触发器
            JobDetail jobDetail = JobBuilder.newJob(MySimpleJob.class)
                    .withIdentity("mySimpleJob", "simpleGroup")
                    .build();

            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(10)
                    .repeatForever();

            Trigger trigger = org.quartz.TriggerBuilder.newTrigger()
                    .withIdentity("simpleTrigger", "simpleGroup")
                    .startNow()
                    .withSchedule(scheduleBuilder)
                    .build();

            try {
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                logger.error("注册任务和触发器失败", e);
            }

            // 开启调度器
            scheduler.start();

            // scheduler.shutdown();

        } catch (SchedulerException se) {
            logger.error("调度器初始化异常", se);
        }
    }


    /**
     * 简单的任务
     */
    public static class MySimpleJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            logger.info("哇真的执行了");
        }
    }

}
