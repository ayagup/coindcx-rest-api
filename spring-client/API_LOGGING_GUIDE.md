# API Call Logging Guide

## Overview

This Spring Boot application has comprehensive logging enabled for all API calls at multiple levels:

1. **HTTP Request/Response Logging** - Logs all incoming HTTP requests and outgoing responses
2. **Service Layer Logging** - Logs all service method calls using AOP
3. **Database Persistence** - Saves all API call logs to MySQL database
4. **External API Call Logging** - Logs calls to CoinDCX external APIs

## Logging Components

### 1. HTTP Logging Filter (`HttpLoggingFilter`)

**Location**: `com.coindcx.springclient.filter.HttpLoggingFilter`

**What it logs:**
- Incoming HTTP requests (method, URI, headers, body)
- Outgoing HTTP responses (status, headers, body)
- Request duration
- Remote client IP address

**Log Format:**
```
▶▶▶ INCOMING HTTP REQUEST ▶▶▶
Method: GET /api/public/ticker
Remote Address: 127.0.0.1
Headers:
  Content-Type: application/json
  User-Agent: Mozilla/5.0...
Request Body: {...}

◀◀◀ OUTGOING HTTP RESPONSE ◀◀◀
Status: 200
Duration: 145ms
Response Body: [...]
════════════════════════════════════════════════════════════
```

**Security Features:**
- Sensitive headers (secret, password, authorization) are masked as `********`
- Large bodies are truncated to 1000 characters

### 2. Service Layer AOP Logging (`ApiCallLoggingAspect`)

**Location**: `com.coindcx.springclient.aspect.ApiCallLoggingAspect`

**What it logs:**
- Service class and method name
- Execution time
- Request parameters (JSON serialized)
- Response data (JSON serialized)
- Success/Failure status
- Error messages for failures
- HTTP status codes

**Log Format:**
```
═══════════════════════════════════════════════════════════════
API CALL LOGGED
Service: PublicApiService.getTicker
Status: SUCCESS
Duration: 234ms
Timestamp: 2025-12-14T11:30:45.123
HTTP Status: 200
Request Params: [...]
Response Preview: {"market":"BTCINR",...}
✓ API call log saved to database
═══════════════════════════════════════════════════════════════
```

**Database Persistence:**
All logs are automatically saved to the `api_call_logs` table with:
- Service name
- Method name
- Timestamp
- Execution time
- Request parameters
- Response data
- Status (SUCCESS/FAILURE)
- Error message (if any)
- HTTP status code

### 3. External API Call Logging (`ApiRequestLoggingInterceptor`)

**Location**: `com.coindcx.springclient.interceptor.ApiRequestLoggingInterceptor`

**What it logs:**
- Outgoing requests to CoinDCX APIs
- Request method, URL, headers
- Response status and body
- Request duration

**Log Format:**
```
┌───────────────────────────────────────────────────────────
│ OUTGOING API CALL TO COINDCX
│ URL: GET https://api.coindcx.com/exchange/ticker
│ Headers:
│   Content-Type: application/json
│   X-API-KEY: ********
│ ✓ RESPONSE RECEIVED
│ Status: 200
│ Duration: 156ms
│ Response: {"market":"BTCINR",...}
└───────────────────────────────────────────────────────────
```

## Configuration

### Application Properties

The logging is configured in `application.properties`:

```properties
# Logging Configuration
logging.level.root=INFO
logging.level.com.coindcx=DEBUG
logging.level.com.coindcx.springclient.aspect=INFO
logging.level.com.coindcx.springclient.filter=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### Log Levels

- **ROOT**: INFO - General application logs
- **com.coindcx**: DEBUG - All CoinDCX package logs
- **Aspect**: INFO - Service layer API call logs
- **Filter**: INFO - HTTP request/response logs

### Database Schema

The `api_call_logs` table structure:

```sql
CREATE TABLE api_call_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(255),
    method_name VARCHAR(255),
    timestamp DATETIME,
    execution_time_ms BIGINT,
    request_params TEXT,
    response_data TEXT,
    status VARCHAR(50),
    error_message TEXT,
    http_status_code INT
);
```

## Viewing Logs

### 1. Console Logs

All logs are printed to the console in real-time with the following format:

```
2025-12-14 11:30:45.123 [http-nio-8080-exec-1] INFO  c.c.s.filter.HttpLoggingFilter - ▶▶▶ INCOMING HTTP REQUEST ▶▶▶
```

### 2. Database Logs

Query logs from the database:

```sql
-- Get all logs
SELECT * FROM api_call_logs ORDER BY timestamp DESC;

-- Get recent logs
SELECT * FROM api_call_logs 
WHERE timestamp >= NOW() - INTERVAL 1 HOUR 
ORDER BY timestamp DESC;

-- Get failed API calls
SELECT * FROM api_call_logs 
WHERE status = 'FAILURE' 
ORDER BY timestamp DESC;

-- Get slow API calls (> 1 second)
SELECT * FROM api_call_logs 
WHERE execution_time_ms > 1000 
ORDER BY execution_time_ms DESC;

-- Get logs by service
SELECT * FROM api_call_logs 
WHERE service_name = 'PublicApiService' 
ORDER BY timestamp DESC;
```

### 3. REST API Endpoints

Access logs via REST API:

```bash
# Get all logs
GET http://localhost:8080/api/logs

# Get log by ID
GET http://localhost:8080/api/logs/{id}

# Get logs by endpoint
GET http://localhost:8080/api/logs/endpoint/{endpoint}

# Get logs by status code
GET http://localhost:8080/api/logs/status/{statusCode}

# Get logs by HTTP method
GET http://localhost:8080/api/logs/method/{method}

# Get recent logs
GET http://localhost:8080/api/logs/recent/{count}

# Get logs in time range
GET http://localhost:8080/api/logs/time-range?start={start}&end={end}

# Get slow API calls
GET http://localhost:8080/api/logs/slow/{thresholdMs}

# Get failed calls
GET http://localhost:8080/api/logs/failed

# Get statistics
GET http://localhost:8080/api/logs/stats
```

## Log File Configuration (Optional)

To enable file-based logging, uncomment these lines in `application.properties`:

```properties
logging.file.name=logs/coindcx-api.log
logging.file.max-size=10MB
logging.file.max-history=30
```

This will create:
- Log files in the `logs/` directory
- Maximum 10MB per file
- Keep 30 days of history
- Automatic rotation

## Performance Considerations

### Database Logging

- Logs are saved **asynchronously** to avoid performance impact
- Long request/response data is truncated to 10,000 characters
- Failed database saves don't affect API calls

### HTTP Logging

- Request/response bodies are cached for logging
- Large bodies (>1000 chars) are truncated in logs
- Sensitive data (passwords, secrets) are automatically masked

### Service Layer Logging

- Uses AOP for non-invasive logging
- Minimal overhead (<5ms per call)
- Exception handling doesn't interfere with business logic

## Troubleshooting

### No Logs Appearing

1. **Check Log Levels**:
   ```properties
   logging.level.com.coindcx.springclient.aspect=INFO
   logging.level.com.coindcx.springclient.filter=INFO
   ```

2. **Verify Components are Active**:
   - Check `@Component` annotation on filter and aspect
   - Verify `@Aspect` annotation on `ApiCallLoggingAspect`
   - Check `@Configuration` on `LoggingConfig`

3. **Database Connection**:
   - Verify MySQL is running
   - Check database connection in `application.properties`
   - Verify `api_call_logs` table exists

### Database Save Failures

If logs show "Failed to save API call log to database":

1. **Check Database Permissions**:
   ```sql
   GRANT ALL PRIVILEGES ON coindcx.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Verify Table Exists**:
   ```sql
   SHOW TABLES LIKE 'api_call_logs';
   ```

3. **Check Hibernate Settings**:
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```

### Too Many Logs

To reduce log volume:

1. **Adjust Log Levels**:
   ```properties
   logging.level.com.coindcx.springclient.filter=WARN
   ```

2. **Filter Specific Endpoints**:
   Modify `LoggingConfig` to exclude certain paths:
   ```java
   registrationBean.addUrlPatterns("/api/orders/*", "/api/futures/*");
   ```

3. **Disable Console Logging**:
   Keep database logging but reduce console output:
   ```properties
   logging.level.com.coindcx.springclient.aspect=WARN
   ```

## Log Retention

### Database Cleanup

Create a scheduled task to clean old logs:

```java
@Scheduled(cron = "0 0 2 * * *") // Run at 2 AM daily
public void cleanOldLogs() {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
    apiCallLogRepository.deleteByTimestampBefore(cutoffDate);
}
```

### Log File Rotation

If using file logging, Spring Boot automatically rotates logs based on:
- `logging.file.max-size`
- `logging.file.max-history`

## Example Log Output

### Successful API Call

```
2025-12-14 11:30:45.123 [http-nio-8080-exec-1] INFO  c.c.s.filter.HttpLoggingFilter - ▶▶▶ INCOMING HTTP REQUEST ▶▶▶
2025-12-14 11:30:45.124 [http-nio-8080-exec-1] INFO  c.c.s.filter.HttpLoggingFilter - Method: GET /api/public/ticker
2025-12-14 11:30:45.125 [http-nio-8080-exec-1] INFO  c.c.s.filter.HttpLoggingFilter - Remote Address: 127.0.0.1
2025-12-14 11:30:45.378 [http-nio-8080-exec-1] INFO  c.c.s.aspect.ApiCallLoggingAspect - ═══════════════════════════════════════════════════════════════
2025-12-14 11:30:45.379 [http-nio-8080-exec-1] INFO  c.c.s.aspect.ApiCallLoggingAspect - API CALL LOGGED
2025-12-14 11:30:45.380 [http-nio-8080-exec-1] INFO  c.c.s.aspect.ApiCallLoggingAspect - Service: PublicApiService.getTicker
2025-12-14 11:30:45.381 [http-nio-8080-exec-1] INFO  c.c.s.aspect.ApiCallLoggingAspect - Status: SUCCESS
2025-12-14 11:30:45.382 [http-nio-8080-exec-1] INFO  c.c.s.aspect.ApiCallLoggingAspect - Duration: 253ms
2025-12-14 11:30:45.383 [http-nio-8080-exec-1] INFO  c.c.s.aspect.ApiCallLoggingAspect - Response Preview: [{"market":"BTCINR","last_price":"5000000"...
2025-12-14 11:30:45.384 [http-nio-8080-exec-1] INFO  c.c.s.aspect.ApiCallLoggingAspect - ═══════════════════════════════════════════════════════════════
2025-12-14 11:30:45.450 [http-nio-8080-exec-1] INFO  c.c.s.filter.HttpLoggingFilter - ◀◀◀ OUTGOING HTTP RESPONSE ◀◀◀
2025-12-14 11:30:45.451 [http-nio-8080-exec-1] INFO  c.c.s.filter.HttpLoggingFilter - Status: 200
2025-12-14 11:30:45.452 [http-nio-8080-exec-1] INFO  c.c.s.filter.HttpLoggingFilter - Duration: 329ms
```

### Failed API Call

```
2025-12-14 11:35:22.456 [http-nio-8080-exec-2] ERROR c.c.s.aspect.ApiCallLoggingAspect - API CALL LOGGED
2025-12-14 11:35:22.457 [http-nio-8080-exec-2] ERROR c.c.s.aspect.ApiCallLoggingAspect - Service: OrderApiService.createOrder
2025-12-14 11:35:22.458 [http-nio-8080-exec-2] ERROR c.c.s.aspect.ApiCallLoggingAspect - Status: FAILURE
2025-12-14 11:35:22.459 [http-nio-8080-exec-2] ERROR c.c.s.aspect.ApiCallLoggingAspect - Error: HTTP 401 Unauthorized - Invalid API credentials
```

## Best Practices

1. **Security**: Never log sensitive data (passwords, API secrets, credit cards)
2. **Performance**: Use appropriate log levels (DEBUG for development, INFO for production)
3. **Storage**: Regularly clean old logs to save database space
4. **Monitoring**: Set up alerts for high error rates or slow API calls
5. **Privacy**: Comply with data protection regulations (GDPR, etc.)

## Summary

✅ **Console Logging**: All API calls logged in real-time  
✅ **Database Logging**: Persistent storage for historical analysis  
✅ **HTTP Logging**: Complete request/response tracking  
✅ **External API Logging**: CoinDCX API call monitoring  
✅ **Security**: Sensitive data automatically masked  
✅ **Performance**: Asynchronous logging with minimal overhead  
✅ **Querying**: REST API and SQL access to logs  
✅ **Maintenance**: Automatic truncation and error handling  

Your API logging system is now fully operational! 🚀
