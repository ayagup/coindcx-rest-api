package com.coindcx.springclient.aspect;

import com.coindcx.springclient.dao.ApiCallLogDao;
import com.coindcx.springclient.entity.ApiCallLog;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Aspect for logging all API calls made through service layer
 */
@Aspect
@Component
public class ApiCallLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiCallLoggingAspect.class);

    private final ApiCallLogDao apiCallLogDao;
    private final Gson gson;

    @Autowired
    public ApiCallLoggingAspect(ApiCallLogDao apiCallLogDao) {
        this.apiCallLogDao = apiCallLogDao;
        this.gson = new Gson();
    }

    /**
     * Log all service method calls
     */
    @Around("execution(* com.coindcx.springclient.service.*Service.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        
        ApiCallLog log = new ApiCallLog();
        log.setServiceName(serviceName);
        log.setMethodName(methodName);
        log.setTimestamp(LocalDateTime.now());
        
        // Serialize request parameters
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                String requestParams = gson.toJson(args);
                // Truncate if too long to avoid database issues
                if (requestParams.length() > 10000) {
                    requestParams = requestParams.substring(0, 10000) + "... (truncated)";
                }
                log.setRequestParams(requestParams);
            }
        } catch (Exception e) {
            logger.warn("Failed to serialize request parameters for {}.{}", serviceName, methodName, e);
            log.setRequestParams("(serialization failed)");
        }
        
        Object result = null;
        try {
            // Execute the actual method
            result = joinPoint.proceed();
            
            // Log successful execution
            log.setStatus("SUCCESS");
            
            // Serialize response data
            try {
                if (result != null) {
                    String responseData = gson.toJson(result);
                    // Truncate if too long
                    if (responseData.length() > 10000) {
                        responseData = responseData.substring(0, 10000) + "... (truncated)";
                    }
                    log.setResponseData(responseData);
                }
            } catch (Exception e) {
                logger.warn("Failed to serialize response data for {}.{}", serviceName, methodName, e);
                log.setResponseData("(serialization failed)");
            }
            
        } catch (Exception e) {
            // Log failed execution
            log.setStatus("FAILURE");
            log.setErrorMessage(e.getMessage());
            
            // Extract HTTP status code if available
            if (e.getMessage() != null && e.getMessage().contains("HTTP")) {
                try {
                    String[] parts = e.getMessage().split("HTTP");
                    if (parts.length > 1) {
                        String statusPart = parts[1].trim();
                        String statusCode = statusPart.split("\\s+")[0];
                        log.setHttpStatusCode(Integer.parseInt(statusCode));
                    }
                } catch (Exception parseEx) {
                    logger.debug("Could not parse HTTP status code from error message", parseEx);
                }
            }
            
            throw e; // Re-throw the exception
            
        } finally {
            long endTime = System.currentTimeMillis();
            log.setExecutionTimeMs(endTime - startTime);
            
            // Enhanced console logging
            logger.debug("═══════════════════════════════════════════════════════════════");
            logger.debug("API CALL LOGGED");
            logger.debug("Service: {}.{}", serviceName, methodName);
            logger.debug("Status: {}", log.getStatus());
            logger.debug("Duration: {}ms", log.getExecutionTimeMs());
            logger.debug("Timestamp: {}", log.getTimestamp());
            if (log.getHttpStatusCode() != null) {
                logger.debug("HTTP Status: {}", log.getHttpStatusCode());
            }
            if (log.getRequestParams() != null && !log.getRequestParams().isEmpty()) {
                logger.debug("Request Params: {}", log.getRequestParams());
            }
            if ("FAILURE".equals(log.getStatus()) && log.getErrorMessage() != null) {
                logger.error("Error: {}", log.getErrorMessage());
            }
            if (log.getResponseData() != null && !log.getResponseData().isEmpty()) {
                logger.debug("Response Preview: {}",
                    log.getResponseData().length() > 200 
                        ? log.getResponseData().substring(0, 200) + "..." 
                        : log.getResponseData());
            }
            logger.debug("═══════════════════════════════════════════════════════════════");
            
            // Save log to database
            try {
                apiCallLogDao.save(log);
                logger.debug("✓ API call log saved to database");
            } catch (Exception e) {
                logger.error("✗ Failed to save API call log to database for {}.{}", serviceName, methodName, e);
            }
        }
        
        return result;
    }
}
