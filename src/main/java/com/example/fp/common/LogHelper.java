package com.example.fp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogHelper {
    public static <T> Logger logger(final Class<T> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}