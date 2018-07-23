package com.quartz.spring.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@DisallowConcurrentExecution // 保证上一次任务执行完毕再执行下一任务
public class SimpleJob2 extends AbstractJob {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.info("哇被触发了222222222222");
    }

}
