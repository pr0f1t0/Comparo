package com.pr0f1t.comparo.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyResolver userKeyResolver() {

        return exchange -> exchange.getPrincipal()
                .map(java.security.Principal::getName)
                .defaultIfEmpty(
                        Objects.requireNonNull(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress())
                );

    }

}
