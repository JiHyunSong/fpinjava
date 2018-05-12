package com.example.fp.config.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class AsyncExecutor {
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        log.info("Setting up thread pool task scheduler");
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        taskScheduler.setPoolSize(10);
        return taskScheduler;
    }

    @Bean
    public ExecutorService executorService() {
        log.info("Setting up async task executor service");
        return Executors.newFixedThreadPool(10);
    }
}