package com.coindcx.springclient.config;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * OpenFeign configuration for the MetaTrader 5 REST Gateway client.
 * Configures logging level, timeouts, and error decoding.
 */
@Configuration
public class MetaTraderFeignConfig {

    /**
     * Log full request/response details for debugging.
     * Change to Logger.Level.BASIC or NONE in production.
     */
    @Bean
    public Logger.Level mt5FeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * Connection and read timeouts for MT5 gateway calls.
     */
    @Bean
    public Request.Options mt5RequestOptions() {
        return new Request.Options(
                5, TimeUnit.SECONDS,   // connect timeout
                30, TimeUnit.SECONDS,  // read timeout
                true                   // follow redirects
        );
    }

    /**
     * Custom error decoder that propagates HTTP errors as runtime exceptions.
     */
    @Bean
    public ErrorDecoder mt5ErrorDecoder() {
        return new ErrorDecoder.Default();
    }
}
