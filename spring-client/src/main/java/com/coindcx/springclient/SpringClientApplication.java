package com.coindcx.springclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application
 *
 * @EnableAsync - Enables asynchronous method execution for WebSocket data persistence
 * @EnableScheduling - Enables scheduled tasks for data cleanup
 * @EnableFeignClients - Enables OpenFeign declarative REST clients
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableFeignClients
public class SpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringClientApplication.class, args);
    }
}
