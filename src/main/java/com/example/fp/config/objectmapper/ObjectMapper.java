package com.example.fp.config.objectmapper;

import org.springframework.context.annotation.Bean;

public class ObjectMapper {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
