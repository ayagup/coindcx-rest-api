# REST API Controllers Documentation

## Overview
This document provides comprehensive information about all REST API controllers in the CoinDCX Spring Client application. Each controller corresponds to a specific service and provides HTTP endpoints for interacting with the CoinDCX trading platform.

## Base URL
```
http://localhost:8080
```

## Controllers

### 1. FuturesController
**Base Path:** `/api/futures`

REST controller for futures trading operations.

#### Endpoints:

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/instruments/active?marginCurrencyShortName={name}` | Get active futures instruments | N/A |
| POST | `/orders/create` | Create a new futures order | ExchangeV1DerivativesFuturesOrdersCreatePostRequest |
| POST | `/orders/cancel` | Cancel a futures order | ExchangeV1DerivativesFuturesOrdersCancelPostRequest |
| POST | `/orders` | Get futures orders | ExchangeV1DerivativesFuturesOrdersPostRequest |
| POST | `/positions` | Get futures positions | ExchangeV1DerivativesFuturesPositionsPostRequest |
| POST | `/positions/add-margin` | Add margin to position | ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest |
| POST | `/positions/remove-margin` | Remove margin from position | ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest |
| POST | `/positions/update-leverage` | Update leverage for position | ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest |
| POST | `/positions/exit` | Exit a futures position | ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest |
| POST | `/positions/create-tpsl` | Create TP/SL for position | ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest |
| POST | `/positions/cancel-all-orders` | Cancel all open orders for position | ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest |
| POST | `/orders/cancel-all` | Cancel all open orders | ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest |
| POST | `/positions/transactions` | Get position transactions | ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest |
| POST | `/trades` | Get futures trades | ExchangeV1DerivativesFuturesTradesPostRequest |
| GET | `/depth?instrument={instrument}&depth={depth}` | Get futures depth/orderbook | N/A |

---

### 2. LendController
**Base Path:** `/api/lending`

REST controller for lending operations.

#### Endpoints:

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/orders/fetch` | Fetch lending orders | ExchangeV1FundingFetchOrdersPostRequest |
| POST | `/orders/create` | Create a new lending order | ExchangeV1FundingLendPostRequest |

---

### 3. MarginController
**Base Path:** `/api/margin`

REST controller for margin trading operations.

#### Endpoints:

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/add-margin` | Add margin to position | ExchangeV1MarginAddMarginPostRequest |
| POST | `/cancel` | Cancel a margin order | ExchangeV1MarginCancelPostRequest |
| POST | `/create` | Create margin orders | ExchangeV1MarginCreatePostRequest |
| POST | `/edit-target-price` | Edit price of target order | ExchangeV1MarginEditPriceOfTargetOrderPostRequest |
| POST | `/edit-sl` | Edit stop loss | ExchangeV1MarginEditSlPostRequest |
| POST | `/edit-target` | Edit target | ExchangeV1MarginEditTargetPostRequest |
| POST | `/edit-trailing-sl` | Edit trailing stop loss | ExchangeV1MarginEditTrailingSlPostRequest |
| POST | `/exit` | Exit a margin position | ExchangeV1MarginExitPostRequest |
| POST | `/orders` | Fetch margin orders | ExchangeV1MarginFetchOrdersPostRequest |
| POST | `/order-status` | Get margin order status | ExchangeV1MarginOrderPostRequest |
| POST | `/remove-margin` | Remove margin from position | ExchangeV1MarginRemoveMarginPostRequest |

---

### 4. OrderController
**Base Path:** `/api/orders`

REST controller for order management operations.

#### Endpoints:

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/active/count` | Get count of active orders | ExchangeV1OrdersActiveOrdersPostRequest |
| POST | `/active` | Get active orders | ExchangeV1OrdersActiveOrdersPostRequest |
| POST | `/cancel-all` | Cancel all orders | ExchangeV1OrdersCancelAllPostRequest |
| POST | `/cancel-by-ids` | Cancel orders by IDs | ExchangeV1OrdersCancelByIdsPostRequest |
| POST | `/cancel` | Cancel a single order | ExchangeV1OrdersCancelPostRequest |
| POST | `/create-multiple` | Create multiple orders | ExchangeV1OrdersCreateMultiplePostRequest |
| POST | `/create` | Create a single order | ExchangeV1OrdersCreatePostRequest |
| POST | `/edit` | Edit an existing order | ExchangeV1OrdersEditPostRequest |
| POST | `/status/multiple` | Get status of multiple orders | ExchangeV1OrdersStatusMultiplePostRequest |
| POST | `/status` | Get status of a single order | ExchangeV1OrdersStatusPostRequest |
| POST | `/trade-history` | Get trade history | ExchangeV1OrdersTradeHistoryPostRequest |

---

### 5. PublicController
**Base Path:** `/api/public`

REST controller for public market data operations (no authentication required).

#### Endpoints:

| Method | Endpoint | Description | Request Parameters |
|--------|----------|-------------|-------------------|
| GET | `/ticker` | Get ticker information for all markets | N/A |
| GET | `/markets/details` | Get market details | N/A |
| GET | `/markets` | Get markets information | N/A |
| GET | `/candles?pair={pair}&interval={interval}&startTime={start}&endTime={end}&limit={limit}` | Get candlestick data | pair, interval, startTime (opt), endTime (opt), limit (opt) |
| GET | `/orderbook?pair={pair}` | Get orderbook for a market | pair |
| GET | `/trade-history?pair={pair}&limit={limit}` | Get trade history for a market | pair, limit (opt) |
| GET | `/futures/depth?instrument={instrument}&depth={depth}` | Get futures depth/orderbook | instrument, depth |

---

### 6. UserController
**Base Path:** `/api/user`

REST controller for user account operations.

#### Endpoints:

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/balances` | Get account balances | ExchangeV1UsersBalancesPostRequest |
| POST | `/info` | Get user account information | ExchangeV1UsersBalancesPostRequest |

---

### 7. WalletController
**Base Path:** `/api/wallet`

REST controller for wallet operations.

#### Endpoints:

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/sub-account/transfer` | Transfer funds between sub-accounts | ExchangeV1WalletsSubAccountTransferPostRequest |
| POST | `/transfer` | Transfer funds between wallets | ExchangeV1WalletsTransferPostRequest |

---

### 8. ApiCallLogController (Existing)
**Base Path:** `/api/logs`

REST controller for API call logging and monitoring.

#### Endpoints:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all API call logs |
| GET | `/service/{serviceName}` | Get logs by service name |
| GET | `/service/{serviceName}/method/{methodName}` | Get logs by service and method |
| GET | `/range?start={start}&end={end}` | Get logs in time range |
| GET | `/failed` | Get failed API calls |
| GET | `/slow?threshold={ms}` | Get slow calls over threshold |
| GET | `/statistics` | Get statistics for all services |
| GET | `/statistics/{serviceName}` | Get statistics for specific service |
| GET | `/recent?limit={n}` | Get recent N logs |
| GET | `/count/status` | Get count by status |
| GET | `/count` | Get total count |
| DELETE | `/cleanup?days={n}` | Delete logs older than N days |

---

### 9. WebSocketController (Existing)
**Base Path:** `/api/websocket`

REST controller for WebSocket operations.

---

### 10. HelloController (Existing)
**Base Path:** `/`

Simple health check controller.

---

## Usage Examples

### Example 1: Get Public Market Ticker
```bash
curl http://localhost:8080/api/public/ticker
```

### Example 2: Get Candlestick Data
```bash
curl "http://localhost:8080/api/public/candles?pair=B-BTC_USDT&interval=1h&limit=100"
```

### Example 3: Create an Order (with Authentication)
```bash
curl -X POST http://localhost:8080/api/orders/create \
  -H "Content-Type: application/json" \
  -d '{
    "side": "buy",
    "order_type": "limit",
    "market": "BTCUSDT",
    "price_per_unit": 50000,
    "total_quantity": 0.01,
    "timestamp": 1638360000000
  }'
```

### Example 4: Get User Balances
```bash
curl -X POST http://localhost:8080/api/user/balances \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": 1638360000000
  }'
```

### Example 5: Get Active Futures Instruments
```bash
curl "http://localhost:8080/api/futures/instruments/active?marginCurrencyShortName=USDT"
```

### Example 6: Fetch Lending Orders
```bash
curl -X POST http://localhost:8080/api/lending/orders/fetch \
  -H "Content-Type: application/json" \
  -d '{
    "page": 1,
    "limit": 50
  }'
```

### Example 7: Create Margin Order
```bash
curl -X POST http://localhost:8080/api/margin/create \
  -H "Content-Type: application/json" \
  -d '{
    "side": "buy",
    "order_type": "limit",
    "market": "BTCUSDT",
    "price_per_unit": 50000,
    "total_quantity": 0.01,
    "target_price": 55000,
    "stop_loss_price": 48000
  }'
```

### Example 8: Transfer Between Wallets
```bash
curl -X POST http://localhost:8080/api/wallet/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "from_wallet": "spot",
    "to_wallet": "margin",
    "currency": "BTC",
    "amount": 0.1
  }'
```

## Error Handling

All controllers return appropriate HTTP status codes:
- **200 OK**: Successful request
- **400 Bad Request**: Invalid request parameters
- **401 Unauthorized**: Authentication required or failed
- **403 Forbidden**: Access denied
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

Error responses include a message describing the issue.

## Authentication

Most endpoints (except PublicController) require authentication via API keys. Configure your API keys in `application.properties`:

```properties
coindcx.api.key=your_api_key
coindcx.api.secret=your_api_secret
```

## Automatic API Call Logging

All service method calls are automatically logged by the `ApiCallLoggingAspect`. You can view logs and statistics via the `/api/logs/*` endpoints.

## Testing Controllers

Start the application:
```bash
mvn spring-boot:run
```

Or run the packaged JAR:
```bash
java -jar target/spring-client-1.0.0.jar
```

Access Swagger/OpenAPI documentation (if configured):
```
http://localhost:8080/swagger-ui.html
```

## Project Structure

```
src/main/java/com/coindcx/springclient/
├── controller/
│   ├── ApiCallLogController.java    (Logging & Monitoring)
│   ├── FuturesController.java       (Futures Trading - 15 endpoints)
│   ├── HelloController.java         (Health Check)
│   ├── LendController.java          (Lending - 2 endpoints)
│   ├── MarginController.java        (Margin Trading - 11 endpoints)
│   ├── OrderController.java         (Order Management - 11 endpoints)
│   ├── PublicController.java        (Public Data - 8 endpoints)
│   ├── UserController.java          (User Account - 2 endpoints)
│   ├── WalletController.java        (Wallet Operations - 2 endpoints)
│   └── WebSocketController.java     (WebSocket Management)
├── service/
│   ├── ApiCallLogService.java
│   ├── CoinDCXWebSocketService.java
│   ├── FuturesService.java
│   ├── LendService.java
│   ├── MarginService.java
│   ├── OrderService.java
│   ├── PublicService.java
│   ├── UserService.java
│   └── WalletService.java
└── ... (other packages)
```

## Summary

- **Total Controllers:** 10
- **Total REST Endpoints:** 60+ endpoints
- **Public Endpoints:** 8 (no auth required)
- **Private Endpoints:** 52+ (auth required)
- **Monitoring Endpoints:** 12 (API logs & statistics)

All controllers follow RESTful principles and return JSON responses. The API is production-ready with comprehensive error handling, automatic logging, and MySQL persistence.
