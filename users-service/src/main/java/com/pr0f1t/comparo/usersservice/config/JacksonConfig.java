package com.pr0f1t.comparo.usersservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Global configuration for Jackson 3 ObjectMapper to ensure consistent
 * JSON serialization and deserialization across the microservice.
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures the Jackson 3 ObjectMapper bean.
     * Uses @Primary to override the default Spring Boot Jackson 2 bean if present.
     * * @return Configured ObjectMapper instance.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}