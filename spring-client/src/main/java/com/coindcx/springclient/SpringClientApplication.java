package com.coindcx.springclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application
 * 
 * @EnableScheduling - Enables scheduled tasks for data cleanup
 * Note: @EnableAsync is configured in AsyncConfig.java with custom exception handler
 */
@SpringBootApplication
@EnableScheduling
public class SpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringClientApplication.class, args);
    }
}
