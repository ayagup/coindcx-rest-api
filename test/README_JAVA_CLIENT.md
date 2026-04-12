# CoinDCX WebSocket Java Client

Java implementation of the CoinDCX WebSocket client using [socket.io-client-java](https://github.com/socketio/socket.io-client-java).

## Features

- ✅ WebSocket connection to CoinDCX streaming API
- ✅ HMAC-SHA256 authentication for private channels
- ✅ Support for both public and private channel subscriptions
- ✅ Event handlers for all CoinDCX event types:
  - **Private**: balance-update, order-update, trade-update
  - **Public**: candlestick, depth-update, new-trade, price-change, currentPrices
- ✅ Graceful shutdown handling
- ✅ Pretty-printed JSON output with timestamps

## Requirements

- Java 11 or higher
- Maven 3.6+ (for building)

## Dependencies

The project uses Maven for dependency management:

- `io.socket:socket.io-client:2.1.0` - Socket.IO Java client
- `org.json:json:20231013` - JSON processing
- `org.slf4j:slf4j-api:2.0.9` - Logging API
- `org.slf4j:slf4j-simple:2.0.9` - Simple logging implementation

## Building the Project

### Using Maven

```bash
# Compile the project
mvn clean compile

# Create executable JAR with all dependencies
mvn clean package

# This creates:
# target/websocket-client-1.0.0-jar-with-dependencies.jar
```

### Manual Compilation (if not using Maven)

```bash
# Download dependencies first, then:
javac -cp "socket.io-client-2.1.0.jar:json-20231013.jar:slf4j-api-2.0.9.jar:slf4j-simple-2.0.9.jar" CoinDCXWebSocketClient.java
```

## Running the Client

### Using Maven

```bash
# Run directly with Maven
mvn exec:java -Dexec.mainClass="CoinDCXWebSocketClient"
```

### Using the JAR file

```bash
# Run the executable JAR
java -jar target/websocket-client-1.0.0-jar-with-dependencies.jar
```

### Manual execution

```bash
java -cp ".:socket.io-client-2.1.0.jar:json-20231013.jar:slf4j-api-2.0.9.jar:slf4j-simple-2.0.9.jar" CoinDCXWebSocketClient
```

## Configuration

### API Credentials

Update the credentials in `CoinDCXWebSocketClient.java`:

```java
private static final String API_KEY = "your_api_key_here";
private static final String API_SECRET = "your_api_secret_here";
```

### Subscribing to Channels

In the `EVENT_CONNECT` handler, uncomment the channels you want to subscribe to:

```java
// For authenticated private channel
joinAuthChannel("coindcx");

// For public channels (no auth needed)
joinPublicChannel("B-BTC_USDT_1m");
joinAuthChannel("B-BTC_USDT_1m-futures");  // For futures with auth

// Current prices with authentication
joinAuthChannel("currentPrices@futures@rt");
```

## Channel Types

### Private Channels (require authentication)

- `coindcx` - Balance, order, and trade updates
- `*-futures` - Futures market data (with authentication)

### Public Channels (no authentication)

- `B-{SYMBOL}_1m` - 1-minute candlestick data
- `B-{SYMBOL}@trades` - Real-time trade data
- `B-{SYMBOL}@orderbook@20` - Order book depth updates
- `currentPrices@spot@10s` - Current prices (10-second updates)

## Event Handlers

The client handles the following events:

| Event Name | Description | Channel Type |
|------------|-------------|--------------|
| `balance-update` | Account balance changes | Private |
| `order-update` | Order status changes | Private |
| `trade-update` | Trade execution updates | Private |
| `candlestick` | OHLCV candlestick data | Public |
| `depth-update` | Order book updates | Public |
| `new-trade` | New trade events | Public |
| `price-change` | Price change events | Public |
| `currentPrices@spot#update` | Current price updates | Public |

## Example Output

```
🔌 Connecting to wss://stream.coindcx.com...
✅ Connected to CoinDCX WebSocket!
   Time: 2026-02-08 14:30:15
================================================================================
🔐 Subscribing to PRIVATE channel: coindcx
📊 Subscribing to PUBLIC channels:
   1. B-BTC_USDT_1m (candlestick)
   2. B-BTC_USDT@trades (new-trade)
   3. B-BTC_USDT@orderbook@20 (depth-update)
   4. currentPrices@spot@10s (current prices)

⏳ Waiting for events...
================================================================================

💰 CURRENT PRICES - 14:30:16
Data: {
  "BTC_USDT": "45230.50",
  "ETH_USDT": "2890.75"
}
--------------------------------------------------------------------------------
```

## Stopping the Client

Press `Ctrl+C` to gracefully disconnect and exit.

## Comparison with Python Client

This Java implementation mirrors the functionality of the Python `test_sync.py` client:

| Feature | Python | Java |
|---------|--------|------|
| Socket.IO Connection | ✅ | ✅ |
| HMAC-SHA256 Auth | ✅ | ✅ |
| Event Handlers | ✅ | ✅ |
| JSON Formatting | ✅ | ✅ |
| Timestamps | ✅ | ✅ |
| Graceful Shutdown | ✅ | ✅ |
| Catch-all Handler | ✅ | ⚠️ (manual) |

**Note**: The Java Socket.IO client doesn't support a catch-all (`*`) event handler like Python. You need to add specific handlers for any new event types.

## Troubleshooting

### Connection Issues

```java
// Enable verbose logging
System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
```

### SSL/TLS Errors

If you encounter SSL certificate issues, you may need to import CoinDCX's certificate into your Java truststore.

### Event Not Firing

Make sure:
1. The channel name is correct
2. For private channels, authentication signature is valid
3. The event handler is registered before connecting

## Advanced Usage

### Custom Event Handlers

Add your own event handlers:

```java
socket.on("custom-event", new Emitter.Listener() {
    @Override
    public void call(Object... args) {
        // Handle custom event
        System.out.println("Custom event received: " + args[0]);
    }
});
```

### Multiple Channel Subscriptions

Subscribe to multiple channels dynamically:

```java
String[] symbols = {"BTC_USDT", "ETH_USDT", "XRP_USDT"};
for (String symbol : symbols) {
    joinAuthChannel("B-" + symbol + "_1m-futures");
}
```

## License

Same as parent project.

## References

- [CoinDCX WebSocket API Documentation](https://docs.coindcx.com/)
- [Socket.IO Java Client](https://github.com/socketio/socket.io-client-java)
- [Socket.IO Protocol](https://socket.io/docs/v4/)
