# Swagger/OpenAPI Documentation Guide

## Overview
This application provides comprehensive Swagger/OpenAPI 3.0 documentation for all REST APIs. The interactive Swagger UI allows you to explore, test, and understand all available endpoints.

## Accessing Swagger UI

### Local Development
After starting the application, access Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

Or the alternative URL:
```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON Specification
The raw OpenAPI 3.0 specification (JSON format) is available at:
```
http://localhost:8080/api-docs
```

Download for use with tools like Postman, Insomnia, or API clients:
```bash
curl http://localhost:8080/api-docs > openapi.json
```

## Starting the Application

### Using Maven
```bash
cd spring-client
mvn spring-boot:run
```

### Using JAR
```bash
java -jar target/spring-client-1.0.0.jar
```

Once started, navigate to `http://localhost:8080/swagger-ui.html` in your browser.

## Swagger UI Features

### 1. Interactive API Explorer
- **Browse all endpoints** organized by tags (categories)
- **View request/response models** with detailed schemas
- **Try out APIs** directly from the browser
- **See example responses** for all endpoints

### 2. API Categories (Tags)

The APIs are organized into the following categories:

| Tag | Description | Endpoints | Authentication |
|-----|-------------|-----------|----------------|
| **Public Market Data** | Market data and ticker information | 8 endpoints | ❌ Not Required |
| **Order Management** | Create, cancel, and manage orders | 11 endpoints | ✅ Required |
| **Futures Trading** | Futures/derivatives trading | 15 endpoints | ✅ Required |
| **Margin Trading** | Margin trading operations | 11 endpoints | ✅ Required |
| **Lending** | Lending and funding | 2 endpoints | ✅ Required |
| **User Account** | Account info and balances | 2 endpoints | ✅ Required |
| **Wallet Management** | Wallet transfers | 2 endpoints | ✅ Required |
| **API Monitoring** | API logs and statistics | 12 endpoints | ❌ Not Required |

### 3. Security Schemes

The API uses two security headers for authentication:

- **X-API-KEY**: Your CoinDCX API key
- **X-API-SECRET**: Your CoinDCX API secret

To authenticate in Swagger UI:
1. Click the **"Authorize"** button (🔒 icon) at the top
2. Enter your API key and secret
3. Click **"Authorize"**
4. All subsequent API calls will include these credentials

### 4. Try It Out Feature

For each endpoint:
1. Click on the endpoint to expand it
2. Click **"Try it out"** button
3. Fill in required parameters/request body
4. Click **"Execute"**
5. View the response (status code, headers, body)

## API Documentation Structure

### Endpoint Documentation Includes:

1. **HTTP Method** (GET, POST, DELETE, etc.)
2. **Endpoint Path** (e.g., `/api/public/ticker`)
3. **Description** - What the endpoint does
4. **Parameters** - Query params, path params, request body
5. **Request Body Schema** - JSON structure with field descriptions
6. **Response Codes** - Possible HTTP status codes
7. **Response Schema** - Expected response structure
8. **Security Requirements** - Whether authentication is needed

### Example: Public Ticker Endpoint

**Endpoint:** `GET /api/public/ticker`

**Description:** Retrieves real-time ticker information for all available markets including last price, bid, ask, high, low, and volume

**Authentication:** Not required

**Response:** 200 OK
```json
[
  {
    "market": "BTCUSDT",
    "last_price": "50000",
    "bid": "49950",
    "ask": "50050",
    "high": "51000",
    "low": "49000",
    "volume": "1234.56"
  }
]
```

## Configuration

### Application Properties

Swagger is configured in `application.properties`:

```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.show-actuator=false
springdoc.packagesToScan=com.coindcx.springclient.controller
springdoc.pathsToMatch=/api/**
```

### Configuration Options

| Property | Value | Description |
|----------|-------|-------------|
| `springdoc.api-docs.path` | `/api-docs` | Path for OpenAPI JSON spec |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | Path for Swagger UI |
| `springdoc.swagger-ui.enabled` | `true` | Enable/disable Swagger UI |
| `springdoc.swagger-ui.operationsSorter` | `method` | Sort by HTTP method |
| `springdoc.swagger-ui.tagsSorter` | `alpha` | Sort tags alphabetically |
| `springdoc.swagger-ui.tryItOutEnabled` | `true` | Enable try-it-out feature |
| `springdoc.packagesToScan` | `com.coindcx.springclient.controller` | Scan controllers |
| `springdoc.pathsToMatch` | `/api/**` | Include paths matching pattern |

## OpenAPI Configuration

The OpenAPI spec is configured in `OpenApiConfig.java`:

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI coinDCXOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("CoinDCX Spring Client API")
                .description("REST API for CoinDCX trading platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("CoinDCX API Support")
                    .url("https://coindcx.com")
                    .email("support@coindcx.com")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development"),
                new Server()
                    .url("https://api.coindcx.com")
                    .description("Production")))
            .components(new Components()
                .addSecuritySchemes("apiKey", ...)
                .addSecuritySchemes("apiSecret", ...));
    }
}
```

## Using Swagger with Tools

### 1. Import into Postman

1. Download OpenAPI spec:
   ```bash
   curl http://localhost:8080/api-docs > openapi.json
   ```
2. Open Postman
3. Click **Import** → **Upload Files**
4. Select `openapi.json`
5. All endpoints will be imported as a collection

### 2. Generate Client SDKs

Using Swagger Codegen or OpenAPI Generator:

```bash
# Install OpenAPI Generator
npm install @openapitools/openapi-generator-cli -g

# Download spec
curl http://localhost:8080/api-docs > openapi.json

# Generate Python client
openapi-generator-cli generate -i openapi.json -g python -o ./python-client

# Generate JavaScript client
openapi-generator-cli generate -i openapi.json -g javascript -o ./js-client

# Generate Java client
openapi-generator-cli generate -i openapi.json -g java -o ./java-client
```

### 3. API Testing with curl

Get the exact curl command from Swagger UI:
1. Expand an endpoint
2. Click "Try it out"
3. Fill in parameters
4. Click "Execute"
5. Copy the generated curl command

Example:
```bash
curl -X GET "http://localhost:8080/api/public/ticker" -H "accept: */*"
```

## Advanced Features

### 1. Filtering by Tags

Use the search box to filter endpoints by name or tag.

### 2. Models Schema

Click on **"Schemas"** at the bottom to see all request/response models with:
- Field names and types
- Required vs optional fields
- Field descriptions
- Nested object structures

### 3. Download Specification

Click **"Download"** in Swagger UI to get:
- JSON format: OpenAPI 3.0 spec
- YAML format: OpenAPI 3.0 spec

### 4. Request/Response Examples

Each endpoint shows:
- Example request body (pre-filled)
- Example response (successful)
- Error response examples

## Production Deployment

### Disable Swagger in Production (Optional)

Add to `application-prod.properties`:

```properties
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false
```

Or use environment variable:
```bash
export SPRINGDOC_SWAGGER_UI_ENABLED=false
export SPRINGDOC_API_DOCS_ENABLED=false
```

### Secure Swagger UI (Optional)

Add Spring Security to protect Swagger endpoints:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/api-docs/**").authenticated()
                .anyRequest().permitAll())
            .httpBasic();
        return http.build();
    }
}
```

## Troubleshooting

### Issue: Swagger UI not loading

**Solution:**
1. Check application is running: `curl http://localhost:8080`
2. Verify port 8080 is not in use
3. Check console for errors
4. Clear browser cache

### Issue: Endpoints not showing

**Solution:**
1. Verify controllers are in `com.coindcx.springclient.controller` package
2. Check `@RestController` annotation is present
3. Rebuild project: `mvn clean compile`

### Issue: Authentication not working

**Solution:**
1. Click "Authorize" button in Swagger UI
2. Enter valid API key and secret
3. Check API credentials in `application.properties`

### Issue: 404 on /swagger-ui.html

**Solution:**
Try alternative URLs:
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/swagger-ui/`

## API Summary

### Total Endpoints: 61+

| Category | Count | Auth Required |
|----------|-------|---------------|
| Public Market Data | 8 | No |
| Order Management | 11 | Yes |
| Futures Trading | 15 | Yes |
| Margin Trading | 11 | Yes |
| Lending | 2 | Yes |
| User Account | 2 | Yes |
| Wallet Management | 2 | Yes |
| API Monitoring | 12 | No |

## Screenshots Guide

### Main Swagger UI
- Tag-based organization
- Expandable sections
- Search functionality
- Authorize button

### Endpoint Details
- HTTP method badge
- Request parameters
- Request body schema
- Response codes
- Response schema
- Try it out button

### Try It Out
- Parameter input fields
- Execute button
- Response viewer
- Curl command generator

### Models/Schemas
- Complete data models
- Field types
- Required fields
- Nested structures

## Next Steps

1. **Start Application**: `mvn spring-boot:run`
2. **Open Swagger UI**: `http://localhost:8080/swagger-ui.html`
3. **Authorize**: Click 🔒 and enter API credentials
4. **Explore APIs**: Browse tags and endpoints
5. **Test APIs**: Use "Try it out" feature
6. **Generate Clients**: Export OpenAPI spec for code generation

## Resources

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec (JSON)**: http://localhost:8080/api-docs
- **SpringDoc Documentation**: https://springdoc.org/
- **OpenAPI Specification**: https://swagger.io/specification/

Enjoy exploring and testing your CoinDCX APIs with Swagger! 🚀
