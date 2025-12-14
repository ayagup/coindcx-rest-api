# Quick Start Guide - CoinDCX WebSocket

## Prerequisites
- Java 8 or higher
- Maven 3.6+
- Internet connection
- (Optional) CoinDCX API credentials for private channels

## Step 1: Build the Project

```bash
cd my-server-code
mvn clean install
```

## Step 2: Configure API Credentials (Optional)

For private channels only, edit `src/main/resources/application.properties`:

```properties
coindcx.api.key=YOUR_API_KEY_HERE
coindcx.api.secret=YOUR_API_SECRET_HERE
```

## Step 3: Run the Application

```bash
mvn spring-boot:run
```

The server will start on port 8080.

## Step 4: Test Public Channels

### Check Connection Status
```bash
curl http://localhost:8080/api/websocket/status
```

Expected response:
```json
{
  "publicConnected": true,
  "privateConnected": false,
  "subscribedChannels": []
}
```

### Subscribe to BTC/USDT 1-minute Candlesticks
```bash
curl -X POST "http://localhost:8080/api/websocket/subscribe/candlestick?pair=BTC_USDT&interval=1m"
```

Expected response:
```json
{
  "status": "success",
  "message": "Subscribed to candlestick channel: BTC_USDT_1m"
}
```

### Wait a few seconds and retrieve candlestick data
```bash
curl http://localhost:8080/api/websocket/data/candlesticks?limit=5
```

You should see candlestick data like:
```json
[
  {
    "startTimestamp": 1717075560000,
    "closeTimestamp": 1717075619999,
    "symbol": "BTCUSDT",
    "interval": "1m",
    "open": "68292.00000000",
    "close": "68273.44000000",
    "high": "68292.00000000",
    "low": "68273.43000000",
    "baseAssetVolume": "10.37494000",
    ...
  }
]
```

### Subscribe to Order Book
```bash
curl -X POST "http://localhost:8080/api/websocket/subscribe/orderbook?pair=BTC_USDT&depth=20"
```

### Retrieve Order Book Data
```bash
curl http://localhost:8080/api/websocket/data/orderbook?limit=1
```

## Step 5: Test Private Channels (Optional)

### Prerequisites
- API credentials must be configured in application.properties
- API key must have necessary permissions

### Connect to Private Channels
```bash
curl -X POST http://localhost:8080/api/websocket/subscribe/private
```

### Retrieve Balance Updates
```bash
curl http://localhost:8080/api/websocket/data/balances
```

### Retrieve Order Updates
```bash
curl http://localhost:8080/api/websocket/data/orders
```

### Retrieve Trade Updates
```bash
curl http://localhost:8080/api/websocket/data/trades
```

## Available Candlestick Intervals

- `1m` - 1 minute
- `5m` - 5 minutes
- `15m` - 15 minutes
- `30m` - 30 minutes
- `1h` - 1 hour
- `4h` - 4 hours
- `8h` - 8 hours
- `1d` - 1 day
- `3d` - 3 days
- `1w` - 1 week
- `1M` - 1 month

## Available Trading Pairs

Common pairs include:
- BTC_USDT
- ETH_USDT
- BTC_INR
- ETH_INR
- And many more...

Check the CoinDCX API for a complete list of available trading pairs.

## Unsubscribe from Channels

```bash
curl -X POST "http://localhost:8080/api/websocket/unsubscribe?channelName=B-BTC_USDT_1m"
```

## Using in Your Application

### Java Example

```java
@Autowired
private CoinDCXPublicWebSocketClient publicWebSocketClient;

public void startListening() {
    // Subscribe to candlesticks
    publicWebSocketClient.subscribeToCandlestick("BTC_USDT", "1m", candlestick -> {
        System.out.println("Received candlestick: " + candlestick);
        // Your custom logic here
    });
    
    // Subscribe to order book
    publicWebSocketClient.subscribeToDepthSnapshot("BTC_USDT", "20", depthSnapshot -> {
        System.out.println("Received order book: " + depthSnapshot);
        // Your custom logic here
    });
}
```

### JavaScript/Web Example

```javascript
// Subscribe to candlesticks
fetch('http://localhost:8080/api/websocket/subscribe/candlestick?pair=BTC_USDT&interval=1m', {
  method: 'POST'
})
.then(response => response.json())
.then(data => console.log('Subscribed:', data));

// Poll for latest data
setInterval(() => {
  fetch('http://localhost:8080/api/websocket/data/candlesticks?limit=1')
    .then(response => response.json())
    .then(data => console.log('Latest candlestick:', data));
}, 1000);
```

## Troubleshooting

### Issue: Connection refused
**Solution**: Make sure the application is running on port 8080

### Issue: No data received
**Solution**: 
1. Check if subscription was successful
2. Wait a few seconds for data to arrive
3. Check logs for errors
4. Verify trading pair exists

### Issue: Authentication failed (private channels)
**Solution**:
1. Verify API key and secret are correct
2. Ensure API key has necessary permissions
3. Check that credentials are properly set in application.properties

### Issue: WebSocket disconnects frequently
**Solution**:
1. Check internet connection stability
2. Review firewall settings
3. Check application logs for errors

## Logs

View application logs for WebSocket events:
```bash
tail -f logs/spring.log
```

Or check console output when running with `mvn spring-boot:run`

## Production Deployment

For production use:
1. Use environment variables for API credentials instead of application.properties
2. Configure proper logging
3. Set up monitoring and alerts
4. Consider using a message broker for scalability
5. Implement proper error handling
6. Add health check endpoints
7. Use reverse proxy (nginx) if needed

## Additional Resources

- Full documentation: See WEBSOCKET_README.md
- Implementation details: See IMPLEMENTATION_SUMMARY.md
- CoinDCX API docs: https://docs.coindcx.com/
- Spring WebSocket docs: https://spring.io/guides/gs/messaging-stomp-websocket/
