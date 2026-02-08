package com.coindcx.springclient.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configuration for async method execution
 * Provides custom exception handler to catch and log exceptions in @Async methods
 * 
 * Thread Pool Configuration:
 * - Core Pool Size: 5 threads (always active)
 * - Max Pool Size: 10 threads (scales up under load)
 * - Queue Capacity: 100 tasks (buffers work when all threads are busy)
 * - Rejection Policy: CallerRunsPolicy (caller thread executes task if queue is full)
 * 
 * Expected Load:
 * - WebSocket events: ~10-20/second during normal market hours
 * - Each persistence task: ~10-50ms (database write)
 * - Current config handles ~200 tasks/second with buffering
 * 
 * Backpressure Handling:
 * If queue is full + all threads busy, the caller thread will execute the task.
 * This provides natural backpressure instead of rejecting tasks.
 * Monitor logs for "Async method execution failed" to detect execution issues.
 * Increase maxPoolSize and queueCapacity if sustained high throughput is needed.
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-websocket-");
        // Use CallerRunsPolicy: if queue is full, caller thread executes the task
        // This provides backpressure instead of rejecting tasks
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                logger.error("❌ Async method execution failed: {}.{}()",
                        method.getDeclaringClass().getSimpleName(),
                        method.getName());
                logger.error("❌ Exception: {}", ex.getMessage(), ex);
                logger.error("❌ Method parameters: {}", Arrays.toString(params));
            }
        };
    }
}
