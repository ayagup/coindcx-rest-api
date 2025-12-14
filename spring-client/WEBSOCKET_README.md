# CoinDCX WebSocket Client

This Spring Boot application implements WebSocket clients for the CoinDCX cryptocurrency exchange API.

## Features

### WebSocket Support
- **Real-time Market Data**: Candlestick charts, orderbook updates, trade streams
- **Price Information**: Live price changes, current prices, 24h statistics
- **Private Channels**: Balance updates, order updates, trade updates (requires API credentials)

### Available WebSocket Channels

#### Public Channels (No Authentication Required)

1. **Candlestick Data**
   - Channel: `{pair}_{interval}` (e.g., `B-BTC_USDT_1m`)
   - Event: `candlestick`
   - Intervals: 1m, 5m, 15m, 30m, 1h, 4h, 8h, 1d, 3d, 1w, 1M

2. **Orderbook (Depth)**
   - Channel: `{pair}@orderbook@{depth}` (e.g., `B-BTC_USDT@orderbook@20`)
   - Events: `depth-snapshot`, `depth-update`
   - Depths: 10, 20, 50

3. **New Trades**
   - Channel: `{pair}@trades` (e.g., `B-BTC_USDT@trades`)
   - Event: `new-trade`

4. **Price Changes**
   - Channel: `{pair}@prices` (e.g., `B-BTC_USDT@prices`)
   - Event: `price-change`

5. **Current Prices (All Markets)**
   - Channel: `currentPrices@spot@{interval}` (e.g., `currentPrices@spot@10s`)
   - Event: `currentPrices@spot#update`
   - Intervals: 1s, 10s

6. **Price Statistics (24h)**
   - Channel: `priceStats@spot@60s`
   - Event: `priceStats@spot#update`

#### Private Channels (Authentication Required)

1. **Balance Updates**
   - Channel: `coindcx`
   - Event: `balance-update`

2. **Order Updates**
   - Channel: `coindcx`
   - Event: `order-update`

3. **Trade Updates**
   - Channel: `coindcx`
   - Event: `trade-update`

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# WebSocket Configuration
coindcx.websocket.endpoint=wss://stream.coindcx.com

# API Credentials (for private channels only)
coindcx.api.key=your_api_key_here
coindcx.api.secret=your_api_secret_here
```

## Building the Application

```bash
cd spring-client
mvn clean package
```

## Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/spring-client-1.0.0.jar
```

The application starts on port 8080 by default.

## API Endpoints

### Check WebSocket Status
```bash
GET http://localhost:8080/api/websocket/status
```

### Subscribe to Candlestick Data
```bash
POST http://localhost:8080/api/websocket/candlestick/subscribe?pair=B-BTC_USDT&interval=1m
```

### Subscribe to Orderbook
```bash
POST http://localhost:8080/api/websocket/orderbook/subscribe?pair=B-BTC_USDT&depth=20
```

### Subscribe to New Trades
```bash
POST http://localhost:8080/api/websocket/trades/subscribe?pair=B-BTC_USDT
```

### Subscribe to Price Changes
```bash
POST http://localhost:8080/api/websocket/prices/subscribe?pair=B-BTC_USDT
```

### Subscribe to Current Prices (All Markets)
```bash
POST http://localhost:8080/api/websocket/current-prices/subscribe?interval=10s
```

### Subscribe to Price Statistics
```bash
POST http://localhost:8080/api/websocket/price-stats/subscribe?interval=60s
```

### Subscribe to Balance Updates (Private - Requires API Credentials)
```bash
POST http://localhost:8080/api/websocket/balance/subscribe
```

### Subscribe to Order Updates (Private - Requires API Credentials)
```bash
POST http://localhost:8080/api/websocket/orders/subscribe
```

### Subscribe to User Trade Updates (Private - Requires API Credentials)
```bash
POST http://localhost:8080/api/websocket/user-trades/subscribe
```

### Get Received Messages
```bash
GET http://localhost:8080/api/websocket/messages?event=candlestick
```

### Unsubscribe from Channel
```bash
POST http://localhost:8080/api/websocket/unsubscribe?channel=B-BTC_USDT_1m
```

### Clear Messages
```bash
DELETE http://localhost:8080/api/websocket/messages/clear?event=candlestick
DELETE http://localhost:8080/api/websocket/messages/clear-all
```

## Usage Examples

### Example 1: Subscribe to Bitcoin Candlestick Data (1-minute interval)

1. Start the application
2. Subscribe to candlestick channel:
   ```bash
   curl -X POST "http://localhost:8080/api/websocket/candlestick/subscribe?pair=B-BTC_USDT&interval=1m"
   ```
3. Wait a few seconds for data to arrive
4. Retrieve received messages:
   ```bash
   curl "http://localhost:8080/api/websocket/messages?event=candlestick"
   ```

### Example 2: Monitor Real-time Orderbook Updates

1. Subscribe to orderbook:
   ```bash
   curl -X POST "http://localhost:8080/api/websocket/orderbook/subscribe?pair=B-BTC_USDT&depth=20"
   ```
2. Get depth snapshots:
   ```bash
   curl "http://localhost:8080/api/websocket/messages?event=depth-snapshot"
   ```
3. Get depth updates:
   ```bash
   curl "http://localhost:8080/api/websocket/messages?event=depth-update"
   ```

### Example 3: Track All Market Prices

1. Subscribe to current prices (10-second updates):
   ```bash
   curl -X POST "http://localhost:8080/api/websocket/current-prices/subscribe?interval=10s"
   ```
2. View price updates:
   ```bash
   curl "http://localhost:8080/api/websocket/messages?event=currentPrices@spot#update"
   ```

## Project Structure

```
spring-client/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── coindcx/
│   │   │           └── springclient/
│   │   │               ├── SpringClientApplication.java
│   │   │               ├── config/
│   │   │               │   └── WebSocketConfig.java
│   │   │               ├── constants/
│   │   │               │   └── WebSocketChannels.java
│   │   │               ├── controller/
│   │   │               │   ├── HelloController.java
│   │   │               │   └── WebSocketController.java
│   │   │               ├── model/
│   │   │               │   └── websocket/
│   │   │               │       ├── CandlestickData.java
│   │   │               │       ├── NewTradeData.java
│   │   │               │       └── DepthSnapshotData.java
│   │   │               └── service/
│   │   │                   └── CoinDCXWebSocketService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── coindcx/
│                   └── springclient/
│                       └── SpringClientApplicationTests.java
├── pom.xml
└── README.md
```

## Dependencies

- **Spring Boot 3.2.0**: Web framework
- **Socket.IO Client 2.1.0**: WebSocket client library
- **Gson**: JSON processing

## WebSocket Connection Details

- **Endpoint**: `wss://stream.coindcx.com`
- **Transport**: WebSocket only
- **Reconnection**: Automatic (5 attempts, 1-second delay)
- **Message Storage**: Last 100 messages per event type

## Authentication for Private Channels

Private channels require HMAC-SHA256 authentication:

1. Configure `coindcx.api.key` and `coindcx.api.secret` in `application.properties`
2. The service automatically generates authentication signatures
3. Subscribe to private events using the dedicated endpoints

Example signature generation:
```java
String body = {"channel": "coindcx"}
String signature = HMAC_SHA256(secret, body)
```

## Troubleshooting

### WebSocket Not Connecting
- Check if the endpoint `wss://stream.coindcx.com` is accessible
- Verify network/firewall settings
- Check application logs for connection errors

### No Messages Received
- Ensure you've subscribed to the correct channel and event
- Wait a few seconds for data to arrive (some events may be infrequent)
- Check the connection status: `GET /api/websocket/status`

### Private Channels Not Working
- Verify API credentials are correctly configured
- Ensure the API key has the necessary permissions
- Check logs for authentication errors

## Notes

- WebSocket connection is initialized automatically when the application starts
- Messages are stored in memory (last 100 per event type)
- Use the clear endpoints to prevent memory buildup
- The application automatically disconnects when shutting down

## Next Steps

- Implement custom message handlers for specific use cases
- Add database persistence for historical data
- Create WebSocket push notifications to clients
- Build a real-time trading dashboard UI
