# WebSocket Channel and Event Name Corrections

## Summary

Updated all WebSocket channel names in `WebSocketAutoSubscriptionConfig.java` to match the official CoinDCX API specification. The previous channel names were using incorrect formats (`@rt`, wrong separators) that prevented data from being collected.

## Changes Made

### 1. Spot Candlestick
- ❌ **Before**: `B-BTC_USDT@candles_1m@rt`
- ✅ **After**: `B-BTC_USDT_1m`
- **Event**: `candlestick` (already correct)
- **API Format**: `{pair}_{interval}` (underscore separator, no @rt suffix)

### 2. Spot Orderbook Depth Update
- ❌ **Before**: `B-BTC_USDT@depth@rt`
- ✅ **After**: `B-BTC_USDT@orderbook@20`
- **Event**: `depth-update` (already correct)
- **API Format**: `{pair}@orderbook@{depth}` where depth can be 10, 20, or 50

### 3. Spot Current Prices
- ❌ **Before**: `currentPrices@rt`
- ✅ **After**: `currentPrices@spot@10s`
- **Event**: `currentPrices@spot#update` (already correct)
- **API Format**: `currentPrices@spot@{interval}` where interval can be 1s or 10s

### 4. Spot New Trade
- ❌ **Before**: `B-BTC_USDT` (missing @trades)
- ✅ **After**: `B-BTC_USDT@trades`
- **Event**: `new-trade` (already correct)
- **API Format**: `{pair}@trades`

### 5. Futures Candlestick
- ❌ **Before**: `B-BTC_USDT@candles_1m@rt`
- ✅ **After**: `B-BTC_USDT_1m`
- **Event**: `candlestick` (already correct)
- **Note**: Futures use same format as spot for candlesticks

### 6. Futures Orderbook Depth
- ⚠️ **Current**: `B-BTC_USDT@depth-futures@rt`
- **Event**: `depth-update` (already correct)
- **Status**: VERIFY - May need adjustment based on testing (futures depth channel format not fully documented)

### 7. Futures Current Prices
- ❌ **Before**: `currentPrices@futures@rt`
- ✅ **After**: `currentPrices@futures@10s`
- **Event**: `currentPrices@futures#update` (already correct)
- **API Format**: `currentPrices@futures@{interval}` where interval can be 1s or 10s

### 8. Futures New Trade
- ✅ **Already Correct**: `B-BTC_USDT@trades-futures`
- **Event**: `new-trade` (already correct)
- **API Format**: `{pair}@trades-futures`

## Event Names Status

All event names were already correct after previous manual fixes:

✅ **Correct Event Names**:
1. `candlestick` - Used for both spot and futures candlesticks
2. `depth-update` - Used for both spot and futures orderbook updates
3. `depth-snapshot` - Initial orderbook snapshot (handler exists but not subscribed)
4. `currentPrices@spot#update` - Spot current prices
5. `currentPrices@futures#update` - Futures current prices
6. `new-trade` - Used for both spot and futures trades
7. `balance-update` - Private channel (correct)
8. `order-update` - Private channel (correct)
9. `trade-update` - Private channel (correct)

## Root Cause Analysis

**Why Data Collection Failed:**
1. WebSocket connection was successful ✅
2. Channel subscriptions were attempted ✅
3. **BUT**: The channel names didn't exist on the server ❌
4. **Result**: Server never sent data to non-existent channels

**The Problem:**
- The channel names used generic `@rt` (real-time) suffixes
- API requires specific formats like `@orderbook@20`, `@trades`, etc.
- Incorrect separators (@ vs _)

## Next Steps

1. **Restart Application**: Kill the current process and restart Spring Boot
   ```powershell
   # Kill current instance (PID 15992)
   taskkill /F /PID 15992
   
   # Restart application
   cd spring-client
   mvn spring-boot:run
   ```

2. **Verify Subscriptions**: Check that channels are subscribed with new names
   - Look for log messages: "Subscribing to spot candlestick: B-BTC_USDT_1m"

3. **Wait for Data Collection**: Give it 2-3 minutes for data to accumulate

4. **Test Endpoints**: Verify data is being collected
   ```powershell
   # Test spot endpoints
   Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/spot-candlestick/recent/5"
   Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/spot-depth-update?limit=5"
   Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/spot-current-prices?limit=5"
   Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/spot-new-trades?limit=5"
   
   # Test futures endpoints
   Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/futures-candlestick/recent/5"
   Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/futures-orderbook?limit=5"
   Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/futures-current-prices?limit=5"
   Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/futures-new-trades?limit=5"
   ```

5. **Monitor Console**: Watch for incoming data in Spring Boot console logs

## Expected Outcome

After restarting the application:
- ✅ WebSocket connection established
- ✅ 8 channels subscribed (4 spot + 4 futures)
- ✅ Data begins flowing within 1-2 minutes
- ✅ Database tables begin populating:
  - `spot_candlestick`
  - `spot_depth_update`
  - `spot_current_prices`
  - `spot_new_trades`
  - `futures_candlestick`
  - `futures_orderbook`
  - `futures_current_prices`
  - `futures_new_trades`
- ✅ REST API endpoints return actual data (not empty arrays)

## Additional Notes

### Depth Snapshot vs Depth Update
The API provides TWO events for orderbook data:
- `depth-snapshot`: Initial full orderbook (not currently subscribed)
- `depth-update`: Incremental changes (currently subscribed)

Both use the same channel: `B-BTC_USDT@orderbook@20`

Consider adding depth-snapshot subscription if you need initial orderbook state.

### Futures Depth Channel
The futures depth channel format (`B-BTC_USDT@depth-futures@rt`) may need adjustment if data collection fails. Monitor the futures orderbook endpoint and check Spring Boot logs for any subscription errors.

### Private Channels
Private channels (balance-update, order-update, trade-update) use the correct format and should continue working as before.

## Reference

See `WEBSOCKET_EVENT_NAMES_REFERENCE.md` for complete API documentation of all channel and event names.
