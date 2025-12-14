# API Call Logging DAO Layer

## Overview
This DAO (Data Access Object) layer provides comprehensive persistence and querying capabilities for tracking all API calls made through the CoinDCX Spring Client services.

## Components

### 1. Entity Layer

#### `ApiCallLog.java`
JPA entity that represents an API call log entry with the following fields:
- **id**: Auto-generated primary key
- **serviceName**: Name of the service (e.g., OrderService, FuturesService)
- **methodName**: Name of the method called (e.g., createOrder, getBalances)
- **endpoint**: API endpoint (if applicable)
- **requestParams**: JSON serialized request parameters
- **responseData**: JSON serialized response data
- **status**: Call status (SUCCESS, FAILURE, ERROR)
- **errorMessage**: Error details if call failed
- **executionTimeMs**: Execution time in milliseconds
- **timestamp**: When the call was made
- **userId**: User identifier (optional)
- **ipAddress**: IP address (optional)
- **httpStatusCode**: HTTP status code (if applicable)

### 2. Repository Layer

#### `ApiCallLogRepository.java`
Spring Data JPA repository providing:
- Standard CRUD operations
- Custom query methods:
  - `findByServiceName()`: Get logs for a specific service
  - `findByServiceNameAndMethodName()`: Get logs for specific service method
  - `findByStatus()`: Filter by status
  - `findByTimestampBetween()`: Get logs in time range
  - `findFailedCalls()`: Get all failed API calls
  - `getStatisticsByService()`: Get aggregated statistics
  - `findSlowCalls()`: Find calls exceeding time threshold
  - `deleteByTimestampBefore()`: Clean up old logs

### 3. DAO Layer

#### `ApiCallLogDao.java`
Data Access Object providing transactional operations:
- `save()`: Persist a single log entry
- `saveAll()`: Bulk save operations
- `findById()`: Retrieve by ID
- `findByServiceName()`: Query by service
- `findByServiceAndMethod()`: Query by service and method
- `findByTimeRange()`: Query by date range
- `findFailedCalls()`: Get failed operations
- `findSlowCalls()`: Get slow operations
- `deleteOldLogs()`: Cleanup utility

### 4. Aspect Layer

#### `ApiCallLoggingAspect.java`
AOP aspect that automatically intercepts and logs all service method calls:
- **Pointcut**: `execution(* com.coindcx.springclient.service.*Service.*(..))`
- **Features**:
  - Automatic request/response serialization
  - Execution time tracking
  - Exception handling and logging
  - Status determination (SUCCESS/FAILURE/ERROR)
  - HTTP status code extraction
  - Automatic truncation of large payloads (10,000 chars)

### 5. Service Layer

#### `ApiCallLogService.java`
Business logic layer providing:
- Query operations for logs
- Statistical analysis:
  - Total calls by service
  - Success/failure rates
  - Average execution times
  - Min/max execution times
- Cleanup operations
- Recent logs retrieval
- Count aggregations

### 6. Controller Layer

#### `ApiCallLogController.java`
REST API endpoints for accessing log data:

**GET Endpoints:**
- `GET /api/logs` - Get all logs
- `GET /api/logs/service/{serviceName}` - Logs by service
- `GET /api/logs/service/{serviceName}/method/{methodName}` - Logs by service and method
- `GET /api/logs/range?start={start}&end={end}` - Logs in time range
- `GET /api/logs/failed` - Failed API calls
- `GET /api/logs/slow?threshold={ms}` - Slow calls over threshold
- `GET /api/logs/statistics` - Statistics for all services
- `GET /api/logs/statistics/{serviceName}` - Statistics for specific service
- `GET /api/logs/recent?limit={n}` - Recent N logs
- `GET /api/logs/count/status` - Count by status
- `GET /api/logs/count` - Total count

**DELETE Endpoints:**
- `DELETE /api/logs/cleanup?days={n}` - Delete logs older than N days

### 7. DTO Layer

#### `ApiCallStatistics.java`
Data Transfer Object for statistics:
- serviceName
- totalCalls
- successfulCalls
- failedCalls
- averageExecutionTimeMs
- minExecutionTimeMs
- maxExecutionTimeMs

## Database Configuration

### MySQL Database (Production-Ready)
The application uses MySQL as the database with the following configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coindcx?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

### MySQL Setup

#### Prerequisites
1. Install MySQL Server 8.0 or higher
2. Start MySQL service

#### Database Creation
The database `coindcx` will be automatically created when you first run the application (thanks to `createDatabaseIfNotExist=true` in the JDBC URL).

Alternatively, you can create it manually:
```sql
CREATE DATABASE coindcx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Connection Configuration
Update `application.properties` with your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coindcx?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

#### For Production
For production environments, consider:
- Using SSL: Change `useSSL=false` to `useSSL=true`
- Configuring connection pooling (HikariCP is already included)
- Setting `spring.jpa.hibernate.ddl-auto=validate` instead of `update`
- Using environment variables for sensitive credentials

### Schema Auto-Generation
The database schema is automatically created/updated on application startup using Hibernate DDL auto-update.

## Usage Examples

### Automatic Logging
All service method calls are automatically logged. No code changes needed:

```java
@Autowired
private OrderService orderService;

// This call is automatically logged
orderService.createOrder(request);
```

### Query Logs Programmatically

```java
@Autowired
private ApiCallLogService logService;

// Get all logs
List<ApiCallLog> allLogs = logService.getAllLogs();

// Get logs for specific service
List<ApiCallLog> orderLogs = logService.getLogsByService("OrderService");

// Get failed calls
List<ApiCallLog> failed = logService.getFailedCalls();

// Get statistics
ApiCallStatistics stats = logService.getStatisticsForService("OrderService");
```

### Query Logs via REST API

```bash
# Get all logs
curl http://localhost:8080/api/logs

# Get logs for OrderService
curl http://localhost:8080/api/logs/service/OrderService

# Get failed calls
curl http://localhost:8080/api/logs/failed

# Get slow calls (over 5 seconds)
curl http://localhost:8080/api/logs/slow?threshold=5000

# Get statistics
curl http://localhost:8080/api/logs/statistics

# Get logs in time range
curl "http://localhost:8080/api/logs/range?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59"

# Clean up old logs (older than 30 days)
curl -X DELETE http://localhost:8080/api/logs/cleanup?days=30
```

## Performance Considerations

1. **Asynchronous Logging**: Logs are saved asynchronously to minimize impact on API call performance
2. **Payload Truncation**: Large request/response payloads are automatically truncated to 10,000 characters
3. **Indexing**: Database indexes on `service_name`, `timestamp`, and `status` for faster queries
4. **Cleanup**: Regular cleanup of old logs recommended to prevent database bloat

## Monitoring & Analytics

### Key Metrics
- Total API calls by service
- Success rate per service
- Average response time per service
- Failed API call patterns
- Slow API call identification

### Example Queries

**Most Used Services:**
```java
List<ApiCallStatistics> stats = logService.getStatistics();
stats.sort(Comparator.comparing(ApiCallStatistics::getTotalCalls).reversed());
```

**Services with High Failure Rate:**
```java
List<ApiCallStatistics> stats = logService.getStatistics();
stats.stream()
    .filter(s -> s.getFailedCalls() > s.getSuccessfulCalls() * 0.1) // >10% failure
    .collect(Collectors.toList());
```

**Slowest Operations:**
```java
List<ApiCallLog> slowCalls = logService.getSlowCalls(5000L); // > 5 seconds
```

## Maintenance

### Cleanup Strategy
Recommended cleanup intervals:
- **Development**: Keep 7 days of logs
- **Staging**: Keep 30 days of logs
- **Production**: Keep 90 days of logs

Schedule cleanup using:
```java
@Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
public void scheduledCleanup() {
    apiCallLogService.cleanupOldLogs(30);
}
```

## Extension Points

### Custom Fields
Add custom fields to `ApiCallLog` entity:
```java
@Column(name = "custom_field")
private String customField;
```

### Custom Queries
Add to `ApiCallLogRepository`:
```java
@Query("SELECT a FROM ApiCallLog a WHERE a.customField = :value")
List<ApiCallLog> findByCustomField(@Param("value") String value);
```

### Custom Statistics
Extend `ApiCallStatistics` DTO with additional metrics.

## Dependencies

Required Maven dependencies (automatically included):
- `spring-boot-starter-data-jpa` - JPA support
- `spring-boot-starter-aop` - AOP support for intercepting calls
- `mysql-connector-j` - MySQL JDBC driver

### Alternative Databases
To use a different database, replace the MySQL dependency with:

**PostgreSQL:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```
Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/coindcx
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

**H2 (Embedded - Development Only):**
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
Update `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:./data/coindcx
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

## Migration to Production Database

To use PostgreSQL or MySQL instead of H2, update `application.properties`:

### PostgreSQL
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/coindcx
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### MySQL
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coindcx
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

And add corresponding JDBC driver dependency to `pom.xml`.
