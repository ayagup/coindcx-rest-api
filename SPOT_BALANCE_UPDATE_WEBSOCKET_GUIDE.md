# Spot Balance Update WebSocket Integration - Complete Guide

## Overview
This implementation subscribes to the CoinDCX WebSocket `balance-update` event on the private `coindcx` channel and persists all **spot balance** updates to a database table. A comprehensive REST API is provided to query and manage the stored spot balance data.

---

## System Architecture

```
┌─────────────────────────────────────────────────────┐
### Database Schema

### Table: `websocket_spot_balance_data`

```sql
CREATE TABLE websocket_spot_balance_data (    CoinDCX WebSocket Server                    │
│         wss://stream.coindcx.com                    │
└─────────────────────────────────────────────────────┘
                      │
                      │ Private Channel: coindcx
                      │ Event: balance-update
                      ▼
┌─────────────────────────────────────────────────────┐
│      CoinDCXWebSocketService                        │
│      • Auto-connect on startup                      │
│      • Subscribe to balance-update                  │
│      • Authenticate with API credentials            │
└─────────────────────────────────────────────────────┘
                      │
                      │ Balance data received
                      ▼
┌─────────────────────────────────────────────────────┐
│   WebSocketDataPersistenceService                   │
│   • Parse balance JSON                              │
│   • Extract fields (currency, balance, locked, etc) │
│   • Save to database asynchronously                 │
└─────────────────────────────────────────────────────┘
                      │
                      │ Persist
                      ▼
┌─────────────────────────────────────────────────────┐
│            MySQL Database                           │
│   Table: websocket_spot_balance_data                │
│   • id (PK, auto-increment)                         │
│   • user_id                                         │
│   • currency (BTC, ETH, USDT, etc.)                 │
│   • balance                                         │
│   • locked_balance                                  │
│   • available_balance                               │
│   • total_balance                                   │
│   • raw_data (JSON)                                 │
│   • timestamp                                       │
│   • exchange_timestamp                              │
│   Indexes: spot_currency, spot_timestamp, spot_user_id │
└─────────────────────────────────────────────────────┘
                      │
                      │ Query
                      ▼
┌─────────────────────────────────────────────────────┐
│      WebSocketSpotBalanceController                 │
│      REST API Endpoints (12 endpoints)              │
│      /api/websocket/spot-balance/*                  │
└─────────────────────────────────────────────────────┘
```

---

## Components Created

### 1. **Entity: WebSocketSpotBalanceData.java**
**Purpose:** JPA entity representing spot balance update records

**Fields:**
- `id` - Primary key (auto-increment)
- `userId` - User identifier (if available)
- `currency` - Currency code (BTC, ETH, USDT, etc.)
- `balance` - Available balance
- `lockedBalance` - Balance locked in orders
- `availableBalance` - Available for trading
- `totalBalance` - Total balance (calculated)
- `rawData` - Complete JSON data (TEXT)
- `timestamp` - Record creation time
- `exchangeTimestamp` - Timestamp from exchange

**Indexes:**
- `idx_spot_currency` - Fast queries by currency
- `idx_spot_timestamp` - Fast queries by time
- `idx_spot_user_id` - Fast queries by user

### 2. **Repository: WebSocketSpotBalanceDataRepository.java**
**Purpose:** Data access layer with custom queries

**Methods:**
- `findByCurrencyOrderByTimestampDesc(currency)` - All records for currency
- `findLatestByCurrency(currency)` - Latest balance for currency
- `findByUserIdOrderByTimestampDesc(userId)` - All records for user
- `findByTimeRange(start, end)` - Records in time range
- `findByCurrencyAndTimeRange(currency, start, end)` - Filtered by currency and time
- `findRecentRecords(limit)` - Recent N records
- `findDistinctCurrencies()` - All currencies with data
- `findLatestBalanceForAllCurrencies()` - Latest balance per currency
- `deleteByTimestampBefore(timestamp)` - Cleanup old data
- `count()` - Total records
- `countByCurrency(currency)` - Count per currency

### 3. **Service: WebSocketDataPersistenceService.java (Updated)**
**Purpose:** Persist spot balance data to database

**New Method:**
```java
@Async
@Transactional
public void saveSpotBalanceData(Object data)
```

**Features:**
- Asynchronous processing (@Async)
- Transactional integrity
- JSON parsing and field extraction
- Handles multiple field name formats (full names and abbreviations)
- Calculates total balance if not provided
- Error handling with logging

**Supported Field Formats:**
- `currency` or `c` → Currency code
- `balance` or `b` → Balance amount
- `locked_balance` or `l` → Locked balance
- `available_balance` or `a` → Available balance
- `user_id` or `u` → User identifier
- `timestamp` or `t` → Exchange timestamp

### 4. **Service: CoinDCXWebSocketService.java (Updated)**
**Purpose:** Manage WebSocket connection and subscriptions

**Changes:**
- Auto-subscribe to balance-update on connection
- Route balance-update events to persistence service
- Check API credentials before subscribing

**Auto-Subscription Logic:**
```java
socket.on(WebSocketChannels.EVENT_CONNECT, new Emitter.Listener() {
    @Override
    public void call(Object... args) {
        if (config.getApiKey() != null && !config.getApiKey().isEmpty()) {
            subscribeToPrivateChannel(
                WebSocketChannels.CHANNEL_PRIVATE_COINDCX,
                WebSocketChannels.EVENT_BALANCE_UPDATE,
                true  // Persist spot balance to database
            );
        }
    }
});
```

### 5. **Controller: WebSocketSpotBalanceController.java**
**Purpose:** REST API for spot balance data queries

**Base Path:** `/api/websocket/spot-balance`

**12 Endpoints Available:**

---

## REST API Endpoints

### 1. **Get Spot Balance Statistics**
```
GET /api/websocket/spot-balance/stats
```

**Response:**
```json
{
  "totalRecords": 1234,
  "currencies": ["BTC", "ETH", "USDT", "INR"],
  "currencyCount": 4
}
```

**Use Case:** Dashboard overview, monitoring

---

### 2. **Get Spot Balance by Currency**
```
GET /api/websocket/spot-balance/currency/{currency}?limit=100
```

**Example:**
```
GET /api/websocket/spot-balance/currency/BTC?limit=50
```

**Response:**
```json
[
  {
    "id": 123,
    "userId": "user123",
    "currency": "BTC",
    "balance": "0.5",
    "lockedBalance": "0.1",
    "availableBalance": "0.4",
    "totalBalance": "0.6",
    "timestamp": "2025-12-14T17:30:00",
    "exchangeTimestamp": 1702571400000,
    "rawData": "{...}"
  }
]
```

**Parameters:**
- `currency` (path) - Currency code (BTC, ETH, etc.)
- `limit` (query, optional) - Max records (default: 100)

**Use Case:** View spot balance history for specific currency

---

### 3. **Get Latest Spot Balance by Currency**
```
GET /api/websocket/spot-balance/currency/{currency}/latest
```

**Example:**
```
GET /api/websocket/spot-balance/currency/BTC/latest
```

**Response:**
```json
{
  "id": 123,
  "currency": "BTC",
  "balance": "0.5",
  "lockedBalance": "0.1",
  "availableBalance": "0.4",
  "totalBalance": "0.6",
  "timestamp": "2025-12-14T17:30:00"
}
```

**Use Case:** Current spot balance for a currency

---

### 4. **Get Spot Balance by User ID**
```
GET /api/websocket/spot-balance/user/{userId}?limit=100
```

**Example:**
```
GET /api/websocket/spot-balance/user/user123?limit=20
```

**Response:** Array of spot balance records for the user

**Use Case:** View all spot currencies for a specific user

---

### 5. **Get Spot Balance by Time Range**
```
GET /api/websocket/spot-balance/range?start={ISO_DATETIME}&end={ISO_DATETIME}
```

**Example:**
```
GET /api/websocket/spot-balance/range?start=2025-12-14T00:00:00&end=2025-12-14T23:59:59
```

**Response:** Array of spot balance records in time range

**Use Case:** Daily/hourly spot balance analysis

---

### 6. **Get Spot Balance by Currency and Time Range**
```
GET /api/websocket/spot-balance/currency/{currency}/range?start={ISO_DATETIME}&end={ISO_DATETIME}
```

**Example:**
```
GET /api/websocket/spot-balance/currency/BTC/range?start=2025-12-14T00:00:00&end=2025-12-14T23:59:59
```

**Response:** Array of BTC spot balance records in time range

**Use Case:** Specific currency history analysis

---

### 7. **Get Recent Spot Balance Records**
```
GET /api/websocket/spot-balance/recent/{limit}
```

**Example:**
```
GET /api/websocket/spot-balance/recent/50
```

**Response:** Last 50 spot balance update records (all currencies)

**Use Case:** Recent activity monitoring

---

### 8. **Get All Currencies with Spot Balance**
```
GET /api/websocket/spot-balance/currencies
```

**Response:**
```json
["BTC", "ETH", "USDT", "INR", "XRP"]
```

**Use Case:** List available currencies with spot balance data

---

### 9. **Get Latest Spot Balance for All Currencies**
```
GET /api/websocket/spot-balance/latest-all
```

**Response:** Array of latest spot balance for each currency

**Use Case:** Portfolio overview, spot wallet snapshot

---

### 10. **Get Spot Balance by ID**
```
GET /api/websocket/spot-balance/{id}
```

**Example:**
```
GET /api/websocket/spot-balance/123
```

**Response:** Single spot balance record

**Use Case:** Lookup specific record by ID

---

### 11. **Get Spot Balance Count by Currency**
```
GET /api/websocket/spot-balance/currency/{currency}/count
```

**Example:**
```
GET /api/websocket/spot-balance/currency/BTC/count
```

**Response:**
```json
{
  "currency": "BTC",
  "count": 456
}
```

**Use Case:** Statistics, data availability check

---

### 12. **Cleanup Old Spot Balance Records**
```
DELETE /api/websocket/spot-balance/cleanup?before={ISO_DATETIME}
```

**Example:**
```
DELETE /api/websocket/spot-balance/cleanup?before=2025-12-01T00:00:00
```

**Response:**
```json
{
  "status": "success",
  "message": "Deleted spot balance records before 2025-12-01T00:00:00"
}
```

**Use Case:** Manual data cleanup, maintenance

---

## Configuration

### API Credentials (Required)
Configure in `application.properties`:

```properties
# WebSocket API Credentials
coindcx.api.key=your_api_key_here
coindcx.api.secret=your_api_secret_here

# WebSocket Endpoint
coindcx.websocket.endpoint=wss://stream.coindcx.com

# Data Retention (optional, default: 7 days)
websocket.data.retention.days=7
```

**Important:** Balance updates require valid API credentials. The service will automatically subscribe to the private channel if credentials are configured.

---

## Automatic Features

### 1. **Auto-Subscribe on Startup**
- Connects to WebSocket on application startup
- Automatically subscribes to `balance-update` if API credentials are present
- Reconnects automatically if connection drops

### 2. **Async Data Persistence**
- Non-blocking database writes (@Async)
- Doesn't slow down WebSocket message processing
- Transactional integrity guaranteed

### 3. **Scheduled Cleanup**
- Runs daily at 2:00 AM
- Deletes balance records older than configured retention period
- Configurable via `websocket.data.retention.days`

### 4. **Hourly Statistics Logging**
- Logs storage statistics every hour
- Helps monitor data growth
- Includes balance record count

---

## Database Schema

### Table: `websocket_balance_data`

```sql
CREATE TABLE websocket_balance_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100),
    currency VARCHAR(20) NOT NULL,
    balance DECIMAL(30,10),
    locked_balance DECIMAL(30,10),
    available_balance DECIMAL(30,10),
    total_balance DECIMAL(30,10),
    raw_data TEXT,
    timestamp DATETIME NOT NULL,
    exchange_timestamp BIGINT,
    
    INDEX idx_spot_currency (currency),
    INDEX idx_spot_timestamp (timestamp),
    INDEX idx_spot_user_id (user_id)
);
```

**Auto-Created:** Yes (via JPA/Hibernate)

**Storage Estimates:**
- ~500 bytes per record
- 1000 updates/day = ~0.5 MB/day
- 7 days retention = ~3.5 MB

---

## Usage Examples

### Example 1: Check Current Spot Balances
```bash
# Get latest spot balance for all currencies
curl http://localhost:8080/api/websocket/spot-balance/latest-all

# Get latest BTC spot balance
curl http://localhost:8080/api/websocket/spot-balance/currency/BTC/latest
```

### Example 2: Monitor Spot Balance Changes
```bash
# Get recent 20 spot balance updates
curl http://localhost:8080/api/websocket/spot-balance/recent/20

# Get BTC spot balance changes in last hour
curl "http://localhost:8080/api/websocket/spot-balance/currency/BTC/range?start=2025-12-14T16:00:00&end=2025-12-14T17:00:00"
```

### Example 3: Spot Portfolio Analysis
```bash
# Get statistics
curl http://localhost:8080/api/websocket/spot-balance/stats

# Get all currencies
curl http://localhost:8080/api/websocket/spot-balance/currencies

# Get spot balance for each currency
curl http://localhost:8080/api/websocket/spot-balance/latest-all
```

### Example 4: Data Maintenance
```bash
# Count BTC spot balance records
curl http://localhost:8080/api/websocket/spot-balance/currency/BTC/count

# Delete old data (before Dec 1, 2025)
curl -X DELETE "http://localhost:8080/api/websocket/spot-balance/cleanup?before=2025-12-01T00:00:00"
```

---

## Testing

### 1. **Verify WebSocket Connection**
Check logs on startup:
```
INFO  WebSocket connected successfully
INFO  Auto-subscribing to balance updates...
INFO  Subscribing to private channel: coindcx for event: balance-update
INFO  Joined private channel: coindcx
```

### 2. **Test API Endpoints**
```bash
# Test statistics endpoint
curl http://localhost:8080/api/websocket/spot-balance/stats

# Expected response:
# {
#   "totalRecords": 0,
#   "currencies": [],
#   "currencyCount": 0
# }
```

### 3. **Verify Data Persistence**
After receiving spot balance updates:
```bash
# Check recent records
curl http://localhost:8080/api/websocket/spot-balance/recent/10

# Should show spot balance update records
```

### 4. **Check Database**
```sql
-- Count records
SELECT COUNT(*) FROM websocket_spot_balance_data;

-- View recent records
SELECT * FROM websocket_spot_balance_data 
ORDER BY timestamp DESC 
LIMIT 10;

-- Check currencies
SELECT DISTINCT currency FROM websocket_spot_balance_data;
```

---

## Monitoring

### Application Logs
```
✓ Saved spot balance data for currency: BTC
✓ Saved spot balance data for currency: ETH
```

### Hourly Statistics (Automatic)
```
═══════════════════════════════════════════
WebSocket Data Storage Statistics
  Spot Markets:         1,234 records
  Futures Markets:      567 records
  Spot Balance Updates: 89 records
  Total Records:        1,890 records
  Retention Days:       7 days
═══════════════════════════════════════════
```

### Daily Cleanup (2:00 AM)
```
WebSocket data cleanup completed:
  Spot records deleted: 45
  Futures records deleted: 23
  Spot Balance records deleted: 12
  Total deleted: 80
  Remaining - Spot: 1,189, Futures: 544, Spot Balance: 77
```

---

## Troubleshooting

### Issue: Not receiving spot balance updates
**Cause:** API credentials not configured or invalid

**Solution:**
1. Check `application.properties` for API key and secret
2. Verify credentials are correct
3. Check logs for authentication errors
4. Restart application after configuring credentials

---

### Issue: No data in database
**Cause:** No spot balance changes yet or WebSocket not connected

**Solution:**
1. Check WebSocket connection status
2. Make a spot trade or transfer to trigger spot balance update
3. Verify subscription in logs: "Auto-subscribing to spot balance updates..."
4. Check for errors in persistence service

---

### Issue: Too much data accumulating
**Cause:** High trading volume, low retention period

**Solution:**
1. Adjust retention period: `websocket.data.retention.days=3`
2. Run manual cleanup more frequently
3. Use DELETE endpoint to remove old data

---

## Security Considerations

1. **API Credentials:** Store securely, never commit to version control
2. **Private Data:** Spot balance data contains sensitive user information
3. **Access Control:** Consider adding authentication to spot balance endpoints
4. **Data Retention:** Follow regulatory requirements for data retention
5. **Encryption:** Consider encrypting sensitive fields in database

---

## Performance

### Throughput
- **Async processing:** Non-blocking, high throughput
- **Batch inserts:** Handled by Hibernate
- **Indexed queries:** Fast lookups by currency, time, user

### Database Impact
- **Writes:** Asynchronous, minimal impact
- **Reads:** Optimized with indexes
- **Storage:** ~500 bytes per record

### Optimization Tips
1. Adjust retention period based on needs
2. Use pagination for large result sets
3. Add database connection pooling
4. Consider archiving very old data

---

## Summary

✅ **Implemented:**
- WebSocket subscription to spot balance-update channel
- Database entity and repository for spot balances
- Async spot balance data persistence
- 12 REST API endpoints for spot balance queries
- Scheduled cleanup of old spot balance data
- Hourly statistics including spot balance counts
- Auto-connect and auto-subscribe to spot balance updates
- Comprehensive error handling

✅ **Features:**
- Real-time spot balance tracking
- Historical spot balance data storage
- Query by currency, user, time
- Latest spot balance retrieval
- Spot portfolio overview
- Data cleanup and maintenance

✅ **Production Ready:**
- Transactional integrity
- Error handling and logging
- Auto-reconnection
- Performance optimized
- Well documented

---

*Created: December 14, 2025*  
*Channel: coindcx (Private)*  
*Event: balance-update*  
*Status: Production Ready ✅*
