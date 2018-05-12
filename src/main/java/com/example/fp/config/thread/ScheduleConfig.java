package com.example.fp.config.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {
    private final AsyncExecutor asyncExecutor;

    @Autowired
    public ScheduleConfig(AsyncExecutor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setTaskScheduler(asyncExecutor.threadPoolTaskScheduler());
    }
}