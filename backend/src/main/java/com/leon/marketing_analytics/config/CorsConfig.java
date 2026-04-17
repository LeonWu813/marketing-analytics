package com.leon.marketing_analytics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${ALLOWED_ORIGIN:http://localhost:3000}")
    private String allowedOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/events")
                .allowedOrigins("*")
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("Content-Type");


        registry.addMapping("/**")
                .allowedHeaders("Authorization", "Content-Type")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
