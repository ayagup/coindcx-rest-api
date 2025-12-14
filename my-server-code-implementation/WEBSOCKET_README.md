# CoinDCX WebSocket Implementation

This implementation provides WebSocket connectivity to CoinDCX streaming APIs for real-time market data and account updates.

## Features

### Public Channels (No Authentication Required)
- **Candlestick Data**: Real-time candlestick/OHLCV data for trading pairs
- **Order Book (Depth Snapshot)**: Real-time order book updates

### Private Channels (Authentication Required)
- **Balance Updates**: Real-time wallet balance changes
- **Order Updates**: Real-time order status changes
- **Trade Updates**: Real-time trade execution notifications

## Architecture

### Components

1. **WebSocketConfig**: Spring configuration for WebSocket settings
2. **Model Classes**: 
   - `BalanceUpdate`: Balance update event model
   - `OrderUpdate`: Order update event model
   - `TradeUpdate`: Trade update event model
   - `Candlestick`: Candlestick/OHLCV data model
   - `DepthSnapshot`: Order book snapshot model

3. **Service Classes**:
   - `CoinDCXPublicWebSocketClient`: Manages public WebSocket connections
   - `CoinDCXPrivateWebSocketClient`: Manages authenticated WebSocket connections with HMAC signature

4. **WebSocketController**: REST endpoints to control WebSocket subscriptions

## Configuration

Add your API credentials in `application.properties`:

```properties
coindcx.api.key=YOUR_API_KEY_HERE
coindcx.api.secret=YOUR_API_SECRET_HERE
```

## API Endpoints

### Status
- **GET** `/api/websocket/status`
  - Returns WebSocket connection status and subscribed channels

### Public Channels

#### Subscribe to Candlestick Data
- **POST** `/api/websocket/subscribe/candlestick?pair=BTC_USDT&interval=1m`
  - Parameters:
    - `pair`: Trading pair (e.g., BTC_USDT, ETH_USDT)
    - `interval`: Candlestick interval (1m, 5m, 15m, 30m, 1h, 4h, 8h, 1d, 3d, 1w, 1M)

#### Subscribe to Order Book
- **POST** `/api/websocket/subscribe/orderbook?pair=BTC_USDT&depth=20`
  - Parameters:
    - `pair`: Trading pair
    - `depth`: Depth level (default: 20)

#### Unsubscribe from Channel
- **POST** `/api/websocket/unsubscribe?channelName=B-BTC_USDT_1m`
  - Parameters:
    - `channelName`: Full channel name to unsubscribe from

### Private Channels

#### Subscribe to Private Channels
- **POST** `/api/websocket/subscribe/private`
  - Connects to private WebSocket and subscribes to balance, order, and trade updates
  - Requires API credentials configured

#### Unsubscribe from Private Channels
- **POST** `/api/websocket/unsubscribe/private`
  - Disconnects from private WebSocket

### Data Retrieval

#### Get Latest Candlesticks
- **GET** `/api/websocket/data/candlesticks?limit=10`
  - Returns latest candlestick data received

#### Get Latest Order Book
- **GET** `/api/websocket/data/orderbook?limit=10`
  - Returns latest order book snapshots

#### Get Latest Balance Updates
- **GET** `/api/websocket/data/balances?limit=10`
  - Returns latest balance updates

#### Get Latest Order Updates
- **GET** `/api/websocket/data/orders?limit=10`
  - Returns latest order updates

#### Get Latest Trade Updates
- **GET** `/api/websocket/data/trades?limit=10`
  - Returns latest trade updates

## Usage Examples

### 1. Check Connection Status
```bash
curl http://localhost:8080/api/websocket/status
```

### 2. Subscribe to BTC/USDT 1-minute Candlesticks
```bash
curl -X POST "http://localhost:8080/api/websocket/subscribe/candlestick?pair=BTC_USDT&interval=1m"
```

### 3. Subscribe to BTC/USDT Order Book (20 levels)
```bash
curl -X POST "http://localhost:8080/api/websocket/subscribe/orderbook?pair=BTC_USDT&depth=20"
```

### 4. Get Latest Candlestick Data
```bash
curl http://localhost:8080/api/websocket/data/candlesticks?limit=5
```

### 5. Subscribe to Private Channels (requires API credentials)
```bash
curl -X POST http://localhost:8080/api/websocket/subscribe/private
```

### 6. Get Latest Balance Updates
```bash
curl http://localhost:8080/api/websocket/data/balances?limit=5
```

### 7. Unsubscribe from a Channel
```bash
curl -X POST "http://localhost:8080/api/websocket/unsubscribe?channelName=B-BTC_USDT_1m"
```

## WebSocket Event Details

### Candlestick Event
- **Channel**: `B-{PAIR}_{INTERVAL}` (e.g., `B-BTC_USDT_1m`)
- **Event**: `candlestick`
- **Intervals**: 1m, 5m, 15m, 30m, 1h, 4h, 8h, 1d, 3d, 1w, 1M

### Depth Snapshot Event
- **Channel**: `B-{PAIR}@orderbook@{DEPTH}` (e.g., `B-BTC_USDT@orderbook@20`)
- **Event**: `depth-snapshot`

### Balance Update Event (Private)
- **Channel**: `coindcx`
- **Event**: `balance-update`
- **Authentication**: Required

### Order Update Event (Private)
- **Channel**: `coindcx`
- **Event**: `order-update`
- **Authentication**: Required

### Trade Update Event (Private)
- **Channel**: `coindcx`
- **Event**: `trade-update`
- **Authentication**: Required

## Authentication

Private channels require HMAC SHA256 authentication:
1. Create a JSON body: `{"channel": "coindcx"}`
2. Generate HMAC SHA256 signature using your API secret
3. Send signature along with API key when joining the channel

The implementation handles this automatically when you call `/api/websocket/subscribe/private`.

## Dependencies

```xml
<!-- WebSocket Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- Socket.IO Java Client -->
<dependency>
    <groupId>io.socket</groupId>
    <artifactId>socket.io-client</artifactId>
    <version>2.0.1</version>
</dependency>

<!-- JSON Processing -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>
```

## Notes

- The WebSocket server endpoint is: `wss://stream.coindcx.com`
- Socket.IO version 2.4.0 is recommended by CoinDCX
- The implementation uses Socket.IO Java Client 2.0.1 for compatibility
- Auto-reconnection is enabled by default
- Data is stored in-memory for demonstration; consider using a message broker for production

## Testing

1. Start the application:
   ```bash
   mvn spring-boot:run
   ```

2. Test public channel subscription:
   ```bash
   curl -X POST "http://localhost:8080/api/websocket/subscribe/candlestick?pair=BTC_USDT&interval=1m"
   ```

3. Check received data:
   ```bash
   curl http://localhost:8080/api/websocket/data/candlesticks
   ```

## Troubleshooting

### Connection Issues
- Verify internet connectivity
- Check if firewall allows WebSocket connections
- Ensure correct endpoint URL: `wss://stream.coindcx.com`

### Authentication Errors (Private Channels)
- Verify API key and secret are correctly configured
- Check that API key has necessary permissions
- Ensure HMAC signature is generated correctly

### No Data Received
- Verify subscription was successful by checking `/api/websocket/status`
- Check if the trading pair exists and is active
- Review logs for any error messages

## References

- [CoinDCX API Documentation](https://docs.coindcx.com/)
- Socket.IO specification version 2.x
- WebSocket Protocol (RFC 6455)
