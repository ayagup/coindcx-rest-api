package com.coindcx.springclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

/**
 * CORS (Cross-Origin Resource Sharing) Configuration
 * 
 * This configuration allows the React frontend (running on localhost:3000)
 * to make requests to the Spring Boot backend (running on localhost:8080).
 * 
 * In production, update the allowed origins to include your actual frontend domain.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // Allow requests from React frontend (development)
        corsConfiguration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001",  // Alternative port
            "http://localhost:5173",  // Vite default port
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3001",
            "http://127.0.0.1:5173"
        ));
        
        // Allow all HTTP methods
        corsConfiguration.setAllowedMethods(Arrays.asList(
            "GET", 
            "POST", 
            "PUT", 
            "DELETE", 
            "OPTIONS", 
            "PATCH"
        ));
        
        // Allow all headers
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        
        // Allow credentials (cookies, authorization headers)
        corsConfiguration.setAllowCredentials(true);
        
        // How long the response from a pre-flight request can be cached (in seconds)
        corsConfiguration.setMaxAge(3600L);
        
        // Expose these headers to the frontend
        corsConfiguration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count"
        ));
        
        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsFilter(source);
    }
}
