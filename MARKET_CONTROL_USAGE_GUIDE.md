# WebSocket Market Control - Usage Guide

## Overview

The new Market Control system allows you to enable **only ONE market at a time** (either SPOT or FUTURES). This prevents:
1. **Duplicate channel subscriptions** - Spot and futures candlesticks use the same channel name
2. **Data confusion** - Clearer separation between spot and futures data
3. **Testing issues** - Easier to verify which market's data is being collected

## API Endpoints

### 1. Enable SPOT Market
```http
GET http://localhost:8080/api/websocket/market/enable/spot
```

**What it does:**
- Unsubscribes from futures channels (if active)
- Subscribes to 4 spot channels:
  - `B-BTC_USDT_1m` → candlestick
  - `B-BTC_USDT@orderbook@20` → depth-update
  - `currentPrices@spot@10s` → currentPrices@spot#update
  - `B-BTC_USDT@trades` → new-trade

**Response:**
```json
{
  "success": true,
  "message": "SPOT market enabled successfully",
  "enabledMarket": "spot",
  "subscribedChannels": 4
}
```

### 2. Enable FUTURES Market
```http
GET http://localhost:8080/api/websocket/market/enable/futures
```

**What it does:**
- Unsubscribes from spot channels (if active)
- Subscribes to 4 futures channels:
  - `BTCUSDT_1m` → candlestick
  - `B-BTC_USDT@depth-futures@rt` → depth-update
  - `currentPrices@futures@10s` → currentPrices@futures#update
  - `B-BTC_USDT@trades-futures` → new-trade

**Response:**
```json
{
  "success": true,
  "message": "FUTURES market enabled successfully",
  "enabledMarket": "futures",
  "subscribedChannels": 4
}
```

### 3. Disable All Markets
```http
GET http://localhost:8080/api/websocket/market/disable
```

**What it does:**
- Unsubscribes from all active channels
- Sets market to "none"

**Response:**
```json
{
  "success": true,
  "message": "All markets disabled successfully",
  "enabledMarket": "none"
}
```

### 4. Check Market Status
```http
GET http://localhost:8080/api/websocket/market/status
```

**Response:**
```json
{
  "enabledMarket": "spot",
  "spotEnabled": true,
  "futuresEnabled": false,
  "websocketConnected": true
}
```

## Usage Flow

### PowerShell Commands

```powershell
# 1. Start the application
cd C:\Users\Lenovo\Documents\sources\coindcx-rest-api\spring-client
mvn spring-boot:run

# 2. Wait for application to start (watch for "Started SpringClientApplication")

# 3. Check initial status
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/market/status" | Select-Object -ExpandProperty Content

# 4. Enable SPOT market
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/market/enable/spot" | Select-Object -ExpandProperty Content

# 5. Wait 2-3 minutes for data collection

# 6. Verify SPOT data is being collected
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/spot-candlestick/recent/5"
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/spot-depth-update?limit=5"
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/spot-current-prices?limit=5"
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/spot-new-trades?limit=5"

# 7. Switch to FUTURES market
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/market/enable/futures" | Select-Object -ExpandProperty Content

# 8. Wait 2-3 minutes for data collection

# 9. Verify FUTURES data is being collected
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/futures-candlestick/recent/5"
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/futures-orderbook?limit=5"
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/futures-current-prices?limit=5"
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/futures-new-trades?limit=5"

# 10. Disable all markets
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/market/disable" | Select-Object -ExpandProperty Content
```

### cURL Commands (Alternative)

```bash
# Enable SPOT
curl http://localhost:8080/api/websocket/market/enable/spot

# Enable FUTURES
curl http://localhost:8080/api/websocket/market/enable/futures

# Disable all
curl http://localhost:8080/api/websocket/market/disable

# Check status
curl http://localhost:8080/api/websocket/market/status
```

## Testing Strategy

### Phase 1: Test SPOT Market
1. Start application
2. Enable SPOT market
3. Wait 3 minutes
4. Check all spot endpoints for data
5. Verify database tables have records:
   - `spot_candlestick`
   - `spot_depth_update`
   - `spot_current_prices`
   - `spot_new_trades`

### Phase 2: Test FUTURES Market
1. Disable all markets (clean slate)
2. Enable FUTURES market
3. Wait 3 minutes
4. Check all futures endpoints for data
5. Verify database tables have records:
   - `futures_candlestick`
   - `futures_orderbook`
   - `futures_current_prices`
   - `futures_new_trades`

### Phase 3: Test Market Switching
1. Enable SPOT
2. Wait 1 minute
3. Switch to FUTURES
4. Verify SPOT channels unsubscribed
5. Verify FUTURES channels subscribed
6. Check data collection continues

## Troubleshooting

### No Data After Enabling Market

**Check 1: WebSocket Connection**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/status" | Select-Object -ExpandProperty Content
```
Look for `"connected": true`

**Check 2: Channel Subscriptions**
In the status response, check `subscribedChannels` object to see which channels are active.

**Check 3: Application Logs**
Look for these log messages:
- `📊 Enabling SPOT market...`
- `✓ Subscribed to spot candlestick`
- `✓ Subscribed to spot depth update`
- `✓ Subscribed to spot current prices`
- `✓ Subscribed to spot new trade`
- `✅ SPOT market enabled successfully`

### Error Enabling Market

**Symptom:** HTTP 500 or error response

**Solution:**
1. Check WebSocket is connected: `/api/websocket/status`
2. Restart application if WebSocket disconnected
3. Check application logs for error details

### Data Collection Stops

**Symptom:** Was collecting data, then stopped

**Possible Causes:**
1. **WebSocket disconnected** - Check `/api/websocket/status`
2. **Channel unsubscribed** - Re-enable market
3. **Network issue** - Restart application

**Solution:**
```powershell
# Restart application
# Then re-enable desired market
Invoke-WebRequest -Uri "http://localhost:8080/api/websocket/market/enable/spot"
```

## Key Differences from Auto-Subscription

| Feature | Old (Auto-Subscription) | New (Market Control) |
|---------|------------------------|---------------------|
| **Startup** | All channels subscribed automatically | No channels subscribed |
| **Control** | Manual code changes required | API endpoints for control |
| **Spot/Futures** | Both active simultaneously | Only one at a time |
| **Testing** | Difficult to test separately | Easy to test each market |
| **Duplicates** | Possible with same channel names | Prevented by mutex |
| **Flexibility** | Restart required for changes | No restart needed |

## Advanced Usage

### Enable Only Specific Channels

To enable only candlestick data for spot:

```java
// Add custom endpoint in WebSocketMarketController.java
@GetMapping("/enable/spot/candlestick")
public ResponseEntity<Map<String, Object>> enableSpotCandlestick() {
    webSocketService.subscribeToPublicChannel(
        "B-BTC_USDT_1m",
        "candlestick",
        true
    );
    return ResponseEntity.ok(Map.of("success", true));
}
```

### Monitor Real-Time Data Flow

Watch the application logs while markets are enabled to see data flowing in real-time.

### Database Query to Verify Data

```sql
-- Check spot data counts
SELECT 
    'spot_candlestick' as table_name, COUNT(*) as records FROM spot_candlestick
UNION ALL
SELECT 'spot_depth_update', COUNT(*) FROM spot_depth_update
UNION ALL
SELECT 'spot_current_prices', COUNT(*) FROM spot_current_prices
UNION ALL
SELECT 'spot_new_trades', COUNT(*) FROM spot_new_trades;

-- Check futures data counts
SELECT 
    'futures_candlestick' as table_name, COUNT(*) as records FROM futures_candlestick
UNION ALL
SELECT 'futures_orderbook', COUNT(*) FROM futures_orderbook
UNION ALL
SELECT 'futures_current_prices', COUNT(*) FROM futures_current_prices
UNION ALL
SELECT 'futures_new_trades', COUNT(*) FROM futures_new_trades;
```

## Next Steps

1. **Start the application**
2. **Enable SPOT market first** (better documented in API)
3. **Wait 3 minutes** and check for data
4. **If no data**, check application logs and WebSocket status
5. **Try FUTURES market** after confirming SPOT works
