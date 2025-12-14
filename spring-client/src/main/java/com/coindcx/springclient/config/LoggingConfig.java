package com.coindcx.springclient.config;

import com.coindcx.springclient.filter.HttpLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for HTTP request/response logging
 */
@Configuration
public class LoggingConfig {

    @Bean
    public FilterRegistrationBean<HttpLoggingFilter> loggingFilter(HttpLoggingFilter httpLoggingFilter) {
        FilterRegistrationBean<HttpLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(httpLoggingFilter);
        registrationBean.addUrlPatterns("/api/*"); // Only log API endpoints
        registrationBean.setOrder(1); // Set filter order
        
        return registrationBean;
    }
}
