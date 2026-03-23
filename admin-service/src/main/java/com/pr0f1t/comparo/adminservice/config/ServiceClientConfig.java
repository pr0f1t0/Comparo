package com.pr0f1t.comparo.adminservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ServiceClientConfig {

    @Bean
    public RestClient catalogRestClient(@Value("${app.services.catalog-url}") String catalogUrl) {
        return RestClient.builder()
                .baseUrl(catalogUrl)
                .build();
    }

    @Bean
    public RestClient reviewRestClient(@Value("${app.services.review-url}") String reviewUrl) {
        return RestClient.builder()
                .baseUrl(reviewUrl)
                .build();
    }
}
