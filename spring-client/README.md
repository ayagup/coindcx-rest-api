# CoinDCX Spring Client - Hello World Application

A simple Spring Boot "Hello World" application.

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Build and Run

### Using Maven

```bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run
```

### Using Java

```bash
# Build first
mvn clean package

# Run the JAR
java -jar target/spring-client-1.0.0.jar
```

## Endpoints

Once the application is running, you can access:

- **Home**: http://localhost:8080/api/
- **Hello World**: http://localhost:8080/api/hello

## Testing

```bash
# Run tests
mvn test
```

## Project Structure

```
spring-client/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/coindcx/springclient/
│   │   │       ├── SpringClientApplication.java
│   │   │       └── controller/
│   │   │           └── HelloController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/coindcx/springclient/
│               └── SpringClientApplicationTests.java
└── pom.xml
```

## Configuration

The application runs on port 8080 by default. You can change this in `application.properties`:

```properties
server.port=8080
```
