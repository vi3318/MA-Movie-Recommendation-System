package com.recommendationservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // You would typically get this token from a security context or a token service
            // For testing purposes, you can hardcode a token obtained after login
            // In a production environment, you would implement a proper token management strategy
            String bearerToken = "YOUR_JWT_TOKEN"; // Replace with an actual token for testing
            
            // Only add the token if it's not null or empty
            if (!bearerToken.isEmpty()) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
            }
        };
    }
} 