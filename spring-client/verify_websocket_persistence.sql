-- WebSocket Data Persistence Verification Script
-- Run this after restarting the application to verify the fix

-- ============================================
-- 1. Check Recent New Trade Data
-- ============================================
SELECT
    'New Trades' AS DataType,
    COUNT(*) AS RecordCount,
    MIN(record_timestamp) AS OldestRecord,
    MAX(record_timestamp) AS NewestRecord
FROM websocket_futures_new_trade_data
WHERE record_timestamp > NOW() - INTERVAL 10 MINUTE;

-- View latest trades
SELECT
    trade_timestamp,
    symbol,
    price,
    quantity,
    is_buyer_maker,
    channel_name,
    record_timestamp
FROM websocket_futures_new_trade_data
ORDER BY record_timestamp DESC
LIMIT 10;

-- ============================================
-- 2. Check Current Prices Data
-- ============================================
SELECT
    'Current Prices' AS DataType,
    COUNT(*) AS RecordCount,
    MIN(record_timestamp) AS OldestRecord,
    MAX(record_timestamp) AS NewestRecord
FROM websocket_futures_current_prices_data
WHERE record_timestamp > NOW() - INTERVAL 10 MINUTE;

-- View latest prices
SELECT
    timestamp,
    version_sequence,
    product_type,
    prices_sent_timestamp,
    record_timestamp
FROM websocket_futures_current_prices_data
ORDER BY record_timestamp DESC
LIMIT 10;

-- ============================================
-- 3. Check Orderbook Data
-- ============================================
SELECT
    'Orderbook Updates' AS DataType,
    COUNT(*) AS RecordCount,
    MIN(record_timestamp) AS OldestRecord,
    MAX(record_timestamp) AS NewestRecord
FROM websocket_futures_orderbook_data
WHERE record_timestamp > NOW() - INTERVAL 10 MINUTE;

-- View latest orderbook updates
SELECT
    timestamp,
    version_sequence,
    instrument_name,
    product_type,
    channel_name,
    record_timestamp
FROM websocket_futures_orderbook_data
ORDER BY record_timestamp DESC
LIMIT 10;

-- ============================================
-- 4. Check Candlestick Data
-- ============================================
SELECT
    'Candlesticks' AS DataType,
    COUNT(*) AS RecordCount,
    MIN(record_timestamp) AS OldestRecord,
    MAX(record_timestamp) AS NewestRecord
FROM websocket_futures_candlestick_data
WHERE record_timestamp > NOW() - INTERVAL 10 MINUTE;

-- ============================================
-- 5. Verify No NULL Required Fields
-- ============================================
-- Should return 0 for all queries

-- Check for NULL timestamps in new trades
SELECT COUNT(*) AS NullTradeTimestamps
FROM websocket_futures_new_trade_data
WHERE trade_timestamp IS NULL
  AND record_timestamp > NOW() - INTERVAL 10 MINUTE;

-- Check for NULL timestamps in current prices
SELECT COUNT(*) AS NullPriceTimestamps
FROM websocket_futures_current_prices_data
WHERE timestamp IS NULL
  AND record_timestamp > NOW() - INTERVAL 10 MINUTE;

-- Check for NULL timestamps in orderbook
SELECT COUNT(*) AS NullOrderbookTimestamps
FROM websocket_futures_orderbook_data
WHERE timestamp IS NULL
  AND record_timestamp > NOW() - INTERVAL 10 MINUTE;

-- ============================================
-- 6. Summary Report
-- ============================================
SELECT
    'Summary - Last 10 Minutes' AS Report,
    (SELECT COUNT(*) FROM websocket_futures_new_trade_data
     WHERE record_timestamp > NOW() - INTERVAL 10 MINUTE) AS NewTrades,
    (SELECT COUNT(*) FROM websocket_futures_current_prices_data
     WHERE record_timestamp > NOW() - INTERVAL 10 MINUTE) AS PriceUpdates,
    (SELECT COUNT(*) FROM websocket_futures_orderbook_data
     WHERE record_timestamp > NOW() - INTERVAL 10 MINUTE) AS OrderbookUpdates,
    (SELECT COUNT(*) FROM websocket_futures_candlestick_data
     WHERE record_timestamp > NOW() - INTERVAL 10 MINUTE) AS Candlesticks;

-- ============================================
-- 7. Check API Call Logs for Persistence Operations
-- ============================================
SELECT
    method_name,
    status,
    COUNT(*) AS CallCount,
    AVG(execution_time_ms) AS AvgExecutionTime,
    MAX(timestamp) AS LastCall
FROM api_call_logs
WHERE service_name = 'WebSocketDataPersistenceService'
  AND timestamp > NOW() - INTERVAL 10 MINUTE
GROUP BY method_name, status
ORDER BY method_name, status;

-- ============================================
-- Expected Results:
-- ============================================
-- ✅ RecordCount should be > 0 for active channels
-- ✅ NullTimestamps should all be 0
-- ✅ Status should be 'SUCCESS' in api_call_logs
-- ✅ Timestamps should be recent (within last 10 minutes)
--
-- If all checks pass, the fix is working correctly! 🎉

