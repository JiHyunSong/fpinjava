package com.example.fp.config.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class AsyncException implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(final Throwable throwable, final Method method, final Object... objects) {
        log.error("Uncaught async exception occurred.");
    }
}
