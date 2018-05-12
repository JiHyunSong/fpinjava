package com.example.fp.config.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    private final AsyncExecutor asyncExecutor;
    private final AsyncException asyncException;

    @Autowired
    public AsyncConfig(final AsyncExecutor asyncExecutor, final AsyncException asyncException) {
        this.asyncExecutor = asyncExecutor;
        this.asyncException = asyncException;
    }

    @Override
    public ExecutorService getAsyncExecutor() {
        return asyncExecutor.executorService();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncException;
    }
}