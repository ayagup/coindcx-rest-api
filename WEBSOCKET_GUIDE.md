# 📡 WebSocket Data Collection Guide

## 🎯 How to Enable and Start Receiving Data

Your CoinDCX WebSocket application has **13 complete systems** ready to collect and store data. Here's how to enable data collection:

---

## ✅ Quick Start (3 Methods)

### Method 1: Auto-Subscribe on Application Restart (Recommended)

The application now includes **automatic subscription** that will activate when you restart:

1. **Stop the current application** (Ctrl+C in the terminal)
2. **Recompile**: 
   ```powershell
   mvn clean compile
   ```
3. **Restart the application**:
   ```powershell
   mvn spring-boot:run
   ```

The `WebSocketAutoSubscriptionConfig` class will automatically subscribe to:
- ✅ BTC/USDT Spot Candlestick (1h)
- ✅ BTC/USDT Spot Orderbook
- ✅ Spot Current Prices
- ✅ BTC/USDT Spot Trades
- ✅ BTC/USDT Futures Candlestick (1h)
- ✅ BTC/USDT Futures Orderbook
- ✅ Futures Current Prices
- ✅ BTC/USDT Futures Trades

---

### Method 2: Use Quick Subscribe Endpoints (No Restart Needed)

While the application is running, use these **one-click** endpoints:

#### Spot Market Subscriptions:
```bash
# Subscribe to BTC/USDT Spot Candlestick
curl -X POST http://localhost:8080/api/websocket/subscriptions/quick/spot-btc-candlestick

# Subscribe to BTC/USDT Spot Orderbook
curl -X POST http://localhost:8080/api/websocket/subscriptions/quick/spot-btc-orderbook

# Subscribe to Spot Current Prices
curl -X POST http://localhost:8080/api/websocket/subscriptions/quick/spot-current-prices

# Subscribe to BTC/USDT Spot Trades
curl -X POST http://localhost:8080/api/websocket/subscriptions/quick/spot-btc-trades
```

#### Futures Market Subscriptions:
```bash
# Subscribe to BTC/USDT Futures Candlestick
curl -X POST http://localhost:8080/api/websocket/subscriptions/quick/futures-btc-candlestick

# Subscribe to BTC/USDT Futures Orderbook
curl -X POST http://localhost:8080/api/websocket/subscriptions/quick/futures-btc-orderbook

# Subscribe to Futures Current Prices
curl -X POST http://localhost:8080/api/websocket/subscriptions/quick/futures-current-prices

# Subscribe to BTC/USDT Futures Trades
curl -X POST http://localhost:8080/api/websocket/subscriptions/quick/futures-btc-trades
```

---

### Method 3: Custom Channel Subscription

Subscribe to any channel using the flexible API:

```bash
# Custom subscription example
curl -X POST http://localhost:8080/api/websocket/subscriptions/public \
  -H "Content-Type: application/json" \
  -d '{
    "channelName": "B-ETH_USDT@candles_1h@rt",
    "eventName": "candlestick"
  }'
```

---

## 📊 Verify Data Collection

### 1. Check Subscription Status
```bash
curl http://localhost:8080/api/websocket/status
```

**Expected Response:**
```json
{
  "connected": true,
  "status": "CONNECTED",
  "subscribedChannels": [
    "B-BTC_USDT@candles_1h@rt",
    "B-BTC_USDT@depth@rt",
    "currentPrices@rt",
    ...
  ],
  "storageStats": {
    "candlestickCount": 150,
    "depthUpdateCount": 2341,
    "currentPricesCount": 523,
    ...
  }
}
```

### 2. Check Database Records

Query the REST APIs to see stored data:

#### Spot Candlestick Data:
```bash
curl http://localhost:8080/api/websocket/spot-candlesticks?limit=10
curl http://localhost:8080/api/websocket/spot-candlesticks/symbol/B-BTC_USDT
```

#### Futures New Trade Data:
```bash
curl http://localhost:8080/api/websocket/futures-new-trades?limit=10
curl http://localhost:8080/api/websocket/futures-new-trades/symbol/B-BTC_USDT
```

#### Spot Orderbook Data:
```bash
curl http://localhost:8080/api/websocket/spot-orderbook?limit=10
```

#### Current Prices:
```bash
curl http://localhost:8080/api/websocket/spot-current-prices?limit=10
curl http://localhost:8080/api/websocket/futures-current-prices?limit=10
```

---

## 🔍 Monitor Real-Time Data Collection

### Watch Application Logs

The application logs will show data being received and saved:

```
[INFO] Saved spot candlestick data for channel B-BTC_USDT@candles_1h@rt
[INFO] Saved futures new trade data for channel B-BTC_USDT@trades-futures
[INFO] Saved spot depth update data for channel B-BTC_USDT@depth@rt
```

### Check Storage Statistics (Hourly)

The system logs storage statistics every hour:
```
[INFO] WebSocket Storage Statistics:
[INFO] - Spot Candlesticks: 150 records
[INFO] - Futures Candlesticks: 143 records
[INFO] - Spot Orderbook: 2341 records
[INFO] - Futures New Trades: 1829 records
```

---

## 🎨 Available Channel Types

### All 13 Implemented Systems:

| # | System | Channel Example | Event Name | Table |
|---|--------|----------------|------------|-------|
| 1 | Spot Candlestick | `B-BTC_USDT@candles_1h@rt` | `candlestick` | `websocket_spot_candlestick_data` |
| 2 | Spot Orderbook | `B-BTC_USDT@depth@rt` | `depth-update` | `websocket_spot_orderbook_data` |
| 3 | Spot Current Prices | `currentPrices@rt` | `currentPrices#update` | `websocket_spot_current_prices_data` |
| 4 | Spot New Trade | `B-BTC_USDT` | `new-trade` | `websocket_spot_new_trade_data` |
| 5 | Futures Candlestick | `B-BTC_USDT@candles_1h@rt` | `futures-candlestick` | `websocket_futures_candlestick_data` |
| 6 | Futures Orderbook | `B-BTC_USDT@depth-futures@rt` | `futures-depth-update` | `websocket_futures_orderbook_data` |
| 7 | Futures Current Prices | `currentPrices@futures@rt` | `currentPrices@futures#update` | `websocket_futures_current_prices_data` |
| 8 | Futures New Trade | `B-BTC_USDT@trades-futures` | `new-trade` | `websocket_futures_new_trade_data` |
| 9 | Spot Balance Update | `coindcx` | `balance-update` | `websocket_spot_balance_update_data` |
| 10 | Spot Order Update | `coindcx` | `order-update` | `websocket_spot_order_update_data` |
| 11 | Spot Trade Update | `coindcx` | `trade-update` | `websocket_spot_trade_update_data` |
| 12 | Futures Position | `coindcx` | `futures-position-update` | `websocket_futures_position_data` |
| 13 | Futures Order | `coindcx` | `futures-order-update` | `websocket_futures_order_data` |

**Note:** Systems 9-13 (private channels) are **already auto-subscribed** if API credentials are configured.

---

## 🛠️ Manage Subscriptions

### View Current Subscriptions
```bash
curl http://localhost:8080/api/websocket/subscriptions
```

### Unsubscribe from a Channel
```bash
curl -X DELETE http://localhost:8080/api/websocket/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "channelName": "B-BTC_USDT@candles_1h@rt",
    "eventName": "candlestick"
  }'
```

---

## 📈 Advanced Usage

### Subscribe to Multiple Symbols

Edit `WebSocketAutoSubscriptionConfig.java` to add more symbols:

```java
// Add ETH/USDT spot candlestick
webSocketService.subscribeToPublicChannel(
    "B-ETH_USDT@candles_1h@rt",
    "candlestick",
    true
);

// Add SOL/USDT futures trades
webSocketService.subscribeToPublicChannel(
    "B-SOL_USDT@trades-futures",
    "new-trade",
    true
);
```

### Change Candlestick Intervals

Available intervals: `1m`, `5m`, `15m`, `30m`, `1h`, `4h`, `1d`, `1w`

```java
// 5-minute candlesticks
webSocketService.subscribeToPublicChannel(
    "B-BTC_USDT@candles_5m@rt",
    "candlestick",
    true
);
```

---

## 🗑️ Data Cleanup

The system automatically:
- ✅ Deletes records older than **7 days** (configurable)
- ✅ Runs cleanup daily at **2:00 AM**
- ✅ Logs cleanup results

To change retention period, edit `application.properties`:
```properties
websocket.data.retention.days=30
```

---

## 🔧 Troubleshooting

### Issue: No data being received

**Check 1: WebSocket Connection**
```bash
curl http://localhost:8080/api/websocket/status
```
Ensure `"connected": true`

**Check 2: Subscriptions Active**
```bash
curl http://localhost:8080/api/websocket/subscriptions
```
Ensure channels are listed

**Check 3: Application Logs**
Look for errors in the terminal output

### Issue: Private channels not working

**Solution:** Verify API credentials in `application.properties`:
```properties
coindcx.api.key=your_api_key
coindcx.api.secret=your_api_secret
```

---

## 📚 API Documentation

Full API documentation available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

---

## 🎯 Recommended Next Steps

1. ✅ **Restart the application** to enable auto-subscription
2. ✅ **Wait 2-3 minutes** for data to accumulate
3. ✅ **Check storage stats** using the `/status` endpoint
4. ✅ **Query the REST APIs** to view collected data
5. ✅ **Monitor logs** to see real-time data collection
6. ✅ **Add more symbols** by editing the auto-subscription config

---

## 🚀 Summary

You now have **three ways** to enable data collection:

1. **Auto-Subscribe** (restart required) - subscribes to all major channels automatically
2. **Quick Endpoints** (no restart) - one-click subscription for common channels
3. **Custom API** (no restart) - flexible subscription to any channel

All data is automatically:
- ✅ Stored in MySQL database
- ✅ Accessible via REST APIs
- ✅ Cleaned up after 7 days
- ✅ Logged for monitoring

**Ready to collect data!** 🎉
