# MySQL Database Setup Guide

## Prerequisites

### 1. Install MySQL Server
Download and install MySQL Server 8.0 or higher from:
- **Windows**: https://dev.mysql.com/downloads/installer/
- **macOS**: `brew install mysql`
- **Linux**: `sudo apt-get install mysql-server` (Ubuntu/Debian) or `sudo yum install mysql-server` (RHEL/CentOS)

### 2. Start MySQL Service

**Windows:**
```powershell
# Start MySQL service
net start MySQL80

# Or using Services app (services.msc)
```

**macOS:**
```bash
brew services start mysql
```

**Linux:**
```bash
sudo systemctl start mysql
sudo systemctl enable mysql  # Enable auto-start on boot
```

## Database Configuration

### Option 1: Automatic Database Creation (Recommended)
The application will automatically create the `coindcx` database on first startup. Just ensure:
1. MySQL is running
2. Credentials in `application.properties` are correct

### Option 2: Manual Database Creation

1. **Login to MySQL:**
```bash
mysql -u root -p
```

2. **Create the database:**
```sql
CREATE DATABASE coindcx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Create a dedicated user (optional but recommended):**
```sql
CREATE USER 'coindcx_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON coindcx.* TO 'coindcx_user'@'localhost';
FLUSH PRIVILEGES;
```

4. **Exit MySQL:**
```sql
EXIT;
```

## Application Configuration

Update `src/main/resources/application.properties`:

### Default Configuration (root user):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coindcx?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_root_password
```

### Recommended Configuration (dedicated user):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coindcx?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=coindcx_user
spring.datasource.password=your_secure_password
```

### Production Configuration:
```properties
spring.datasource.url=jdbc:mysql://your-server:3306/coindcx?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.username=coindcx_user
spring.datasource.password=${DB_PASSWORD}  # Use environment variable
spring.jpa.hibernate.ddl-auto=validate  # Don't auto-update schema in production
```

## Verify Setup

### 1. Check MySQL Connection
```bash
mysql -u root -p -e "SHOW DATABASES;"
```

You should see `coindcx` database listed (after first app startup).

### 2. View Tables
```bash
mysql -u root -p coindcx -e "SHOW TABLES;"
```

Expected table: `api_call_logs`

### 3. Check Table Structure
```bash
mysql -u root -p coindcx -e "DESCRIBE api_call_logs;"
```

### 4. View Logs
```bash
mysql -u root -p coindcx -e "SELECT * FROM api_call_logs ORDER BY timestamp DESC LIMIT 10;"
```

## Running the Application

```bash
cd spring-client
mvn spring-boot:run
```

Or run the packaged JAR:
```bash
java -jar target/spring-client-1.0.0.jar
```

## Troubleshooting

### Connection Refused
**Issue:** `java.sql.SQLException: Connection refused`

**Solution:**
1. Ensure MySQL is running: `systemctl status mysql` (Linux) or check Services (Windows)
2. Check MySQL port (default 3306): `netstat -an | grep 3306`

### Access Denied
**Issue:** `Access denied for user 'root'@'localhost'`

**Solution:**
1. Reset MySQL root password:
   ```bash
   sudo mysql
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
   FLUSH PRIVILEGES;
   EXIT;
   ```
2. Update `application.properties` with correct password

### Public Key Retrieval Error
**Issue:** `Public Key Retrieval is not allowed`

**Solution:**
Already handled in JDBC URL with `allowPublicKeyRetrieval=true`

### Time Zone Issues
**Issue:** `The server time zone value is unrecognized`

**Solution:**
Already handled with `serverTimezone=UTC` in JDBC URL

### Database Not Created
**Issue:** `Unknown database 'coindcx'`

**Solution:**
1. Check JDBC URL includes `createDatabaseIfNotExist=true`
2. Create manually: `CREATE DATABASE coindcx;`

## Connection Pool Configuration (Optional)

Add to `application.properties` for better performance:

```properties
# HikariCP settings (Spring Boot's default connection pool)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## Monitoring Database

### Using MySQL Workbench
1. Download: https://dev.mysql.com/downloads/workbench/
2. Connect to `localhost:3306`
3. Browse `coindcx` database

### Using Command Line
```bash
# Watch live logs
watch -n 2 'mysql -u root -p<password> coindcx -e "SELECT COUNT(*) as total_logs FROM api_call_logs;"'

# Check failed calls
mysql -u root -p coindcx -e "SELECT * FROM api_call_logs WHERE status = 'FAILURE';"

# Performance stats
mysql -u root -p coindcx -e "SELECT service_name, COUNT(*) as calls, AVG(execution_time_ms) as avg_time FROM api_call_logs GROUP BY service_name;"
```

## Security Best Practices

1. **Never commit passwords** to version control
2. **Use environment variables** for sensitive data:
   ```bash
   export DB_PASSWORD='your_password'
   ```
3. **Enable SSL** in production
4. **Use dedicated database user** with minimal privileges
5. **Regular backups**:
   ```bash
   mysqldump -u root -p coindcx > backup_$(date +%Y%m%d).sql
   ```

## Performance Optimization

### Add Indexes (if needed)
```sql
USE coindcx;

-- Already created by Hibernate:
-- CREATE INDEX idx_service_name ON api_call_logs(service_name);
-- CREATE INDEX idx_timestamp ON api_call_logs(timestamp);
-- CREATE INDEX idx_status ON api_call_logs(status);

-- Additional indexes for custom queries:
CREATE INDEX idx_method_name ON api_call_logs(method_name);
CREATE INDEX idx_execution_time ON api_call_logs(execution_time_ms);
```

### Partitioning (for large datasets)
```sql
-- Partition by timestamp (monthly)
ALTER TABLE api_call_logs PARTITION BY RANGE (YEAR(timestamp) * 100 + MONTH(timestamp)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    -- Add more partitions as needed
    PARTITION pmax VALUES LESS THAN MAXVALUE
);
```

## Next Steps

1. ✅ MySQL installed and running
2. ✅ Database created (automatically or manually)
3. ✅ Application configured
4. ✅ Start the application
5. 📊 Access logs via REST API: `http://localhost:8080/api/logs`
6. 📈 View statistics: `http://localhost:8080/api/logs/statistics`

For more details, see `DAO_LAYER_README.md`
