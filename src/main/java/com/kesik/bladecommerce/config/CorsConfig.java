package com.kesik.bladecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed.origins:http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // Only allow specific origins (comma-separated)
                        .allowedOrigins(allowedOrigins.split(","))
                        // Only allow specific HTTP methods
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        // Only allow specific headers
                        .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")
                        // Allow credentials (cookies, authorization headers)
                        .allowCredentials(true)
                        // Expose specific headers to the client
                        .exposedHeaders("Authorization", "Content-Type")
                        // Cache preflight requests for 1 hour
                        .maxAge(3600);
                registry.addMapping("/v3/api-docs/**").allowedOrigins("*"); //TODO kaldır
                registry.addMapping("/swagger-ui/**").allowedOrigins("*"); //TODO kaldır

            }
        };
    }
}