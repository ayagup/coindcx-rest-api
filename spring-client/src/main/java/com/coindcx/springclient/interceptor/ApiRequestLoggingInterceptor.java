package com.coindcx.springclient.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Interceptor to log outgoing HTTP requests to external APIs (CoinDCX)
 */
@Component
public class ApiRequestLoggingInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestLoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        long startTime = System.currentTimeMillis();
        
        // Log outgoing request
        logger.info("┌───────────────────────────────────────────────────────────");
        logger.info("│ OUTGOING API CALL TO COINDCX");
        logger.info("│ URL: {} {}", request.method(), request.url());
        logger.info("│ Headers:");
        request.headers().names().forEach(name -> {
            // Don't log sensitive headers
            if (!name.toLowerCase().contains("secret") && 
                !name.toLowerCase().contains("key") &&
                !name.toLowerCase().contains("authorization")) {
                logger.info("│   {}: {}", name, request.header(name));
            } else {
                logger.info("│   {}: ********", name);
            }
        });
        
        // Execute request
        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("│ ✗ REQUEST FAILED");
            logger.error("│ Error: {}", e.getMessage());
            logger.error("│ Duration: {}ms", duration);
            logger.info("└───────────────────────────────────────────────────────────");
            throw e;
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Log response
        logger.info("│ ✓ RESPONSE RECEIVED");
        logger.info("│ Status: {}", response.code());
        logger.info("│ Duration: {}ms", duration);
        
        if (response.body() != null) {
            // Read and log response body (carefully to not consume it)
            ResponseBody responseBody = response.body();
            String responseBodyString = responseBody.string();
            
            // Log response body preview
            if (responseBodyString.length() > 500) {
                logger.info("│ Response Preview: {}...", responseBodyString.substring(0, 500));
            } else {
                logger.info("│ Response: {}", responseBodyString);
            }
            
            // Create new response with the consumed body
            response = response.newBuilder()
                    .body(ResponseBody.create(responseBodyString, responseBody.contentType()))
                    .build();
        }
        
        logger.info("└───────────────────────────────────────────────────────────");
        
        return response;
    }
}
