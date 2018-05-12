package com.example.fp.config.resttemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;

@Configuration
public class AsyncRestTemplateConfig {
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        /*
         * In the case of SimpleAsyncTaskExecutor, setConcurrencyLimit refers how many parallel tasks will run through the executor.
         * AsyncRestTemplate should run without limitation in order to execute every api call
         */
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setTaskExecutor(taskExecutor);
        requestFactory.setConnectTimeout(5 * 1000);
        requestFactory.setReadTimeout(30 * 1000);
        return new AsyncRestTemplate(requestFactory);
    }
}