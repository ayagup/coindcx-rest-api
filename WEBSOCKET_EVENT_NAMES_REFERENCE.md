# CoinDCX WebSocket Event Names - Official API Reference

This document contains the **official** WebSocket channel names and event names as documented in the CoinDCX API documentation.

## Socket.io Version Requirement

**CRITICAL**: Must use Socket.io v2.4.0 ONLY

```bash
npm install socket.io-client@2.4.0
```

## Private Channels (Authentication Required)

### 1. Balance Update
- **Channel**: `coindcx` (Private)
- **Event**: `balance-update`
- **Description**: Wallet balance information whenever there is a change in wallet balance

### 2. Order Update
- **Channel**: `coindcx` (Private)
- **Event**: `order-update`
- **Description**: Order info whenever there is a change in order status

### 3. Trade Update
- **Channel**: `coindcx` (Private)
- **Event**: `trade-update`
- **Description**: Trade updates for user's orders

## Public Channels - SPOT

### 1. Candlestick (SPOT)
- **Channel Format**: `{pair}_{interval}` (e.g., `B-BTC_USDT_1m`)
- **Event**: `candlestick`
- **Available Intervals**: `["1m", "5m", "15m", "30m", "1h", "4h", "8h", "1d", "3d", "1w", "1M"]`
- **Example Channel**: `B-BTC_USDT_1m`
- **Description**: Current candlestick bars for a given pair at the resolution

### 2. Depth Snapshot (SPOT - Orderbook)
- **Channel Format**: `{pair}@orderbook@{depth}` (e.g., `B-BTC_USDT@orderbook@20`)
- **Event**: `depth-snapshot`
- **Depth Options**: `10`, `20`, `50`
- **Example Channel**: `B-BTC_USDT@orderbook@20`
- **Description**: Orderbook data of the pair as a snapshot

### 3. Depth Update (SPOT - Orderbook)
- **Channel Format**: `{pair}@orderbook@{depth}` (e.g., `B-BTC_USDT@orderbook@20`)
- **Event**: `depth-update`
- **Depth Options**: `10`, `20`, `50`
- **Example Channel**: `B-BTC_USDT@orderbook@20`
- **Description**: Changes in the orderbook data when depth changes

### 4. Current Prices (SPOT)
- **Channel Format**: `currentPrices@spot@{interval}` (e.g., `currentPrices@spot@10s`)
- **Event**: `currentPrices@spot#update`
- **Interval Options**: `1s`, `10s`
- **Example Channel**: `currentPrices@spot@10s`
- **Description**: Current prices of pairs whose price got updated in the last interval

### 5. Price Stats (SPOT)
- **Channel Format**: `priceStats@spot@{interval}` (e.g., `priceStats@spot@60s`)
- **Event**: `priceStats@spot#update`
- **Interval Options**: `60s`
- **Example Channel**: `priceStats@spot@60s`
- **Description**: 24hrs price change of pairs whose price got updated in the last 60s

### 6. New Trade (SPOT)
- **Channel Format**: `{pair}@trades` (e.g., `B-BTC_USDT@trades`)
- **Event**: `new-trade`
- **Example Channel**: `B-BTC_USDT@trades`
- **Description**: Latest trade info of the given pair

### 7. Price Change / LTP (SPOT)
- **Channel Format**: `{pair}@prices` (e.g., `B-BTC_USDT@prices`)
- **Event**: `price-change`
- **Example Channel**: `B-BTC_USDT@prices`
- **Description**: Latest price info whenever there is a price change

## Public Channels - FUTURES

Based on the API documentation pattern, Futures channels use similar event names but with futures-specific channel patterns:

### 1. Candlestick (FUTURES)
- **Channel Format**: Similar to spot but with futures markets
- **Event**: `candlestick` (SAME as spot)
- **Note**: Handler distinguishes futures from spot using channel name patterns

### 2. Depth Update (FUTURES - Orderbook)
- **Channel Format**: Futures orderbook channels (e.g., `B-BTC_USDT@depth-futures@rt`)
- **Event**: `depth-update` (SAME as spot)
- **Note**: Handler distinguishes futures from spot using channel name patterns

### 3. Current Prices (FUTURES)
- **Channel Format**: `currentPrices@futures@{interval}` (e.g., `currentPrices@futures@10s`)
- **Event**: `currentPrices@futures#update`
- **Example Channel**: `currentPrices@futures@10s`

### 4. New Trade (FUTURES)
- **Channel Format**: `{pair}@trades-futures`
- **Event**: `new-trade` (SAME as spot)
- **Example Channel**: `B-BTC_USDT@trades-futures`
- **Note**: Handler checks if channel ends with `@trades-futures`

## Key Pattern Observations

1. **Event Names Are Shared**: Spot and Futures use the SAME event names (`candlestick`, `depth-update`, `new-trade`)
2. **Channel Names Distinguish Markets**: The channel name determines whether it's spot or futures
3. **Handler Logic**: Event handlers check channel name patterns to route data correctly
   - `contains("-futures")` → Futures candlestick
   - `endsWith("@trades-futures")` → Futures trade
   - Otherwise → Spot market

## Real-Time (@rt) Suffix

Many channels have an optional `@rt` suffix for real-time updates:
- `B-BTC_USDT@candles_1m@rt` (real-time)
- `B-BTC_USDT@depth@rt` (real-time)
- `currentPrices@rt` (real-time)

## Pair Format

- Spot: `B-BTC_USDT`
- Futures: `B-BTC_USDT` (same format)
- Use `pair` value from Markets API response

## Example JavaScript Code

```javascript
import io from 'socket.io-client';

const socket = io('https://stream.coindcx.com', {
  transports: ['websocket']
});

socket.on("connect", () => {
  // Join SPOT candlestick channel
  socket.emit('join', {
    'channelName': "B-BTC_USDT_1m"
  });
});

// Listen for candlestick event
socket.on("candlestick", (response) => {
  console.log(response);
});

// Join SPOT orderbook channel
socket.emit('join', {
  'channelName': "B-BTC_USDT@orderbook@20"
});

// Listen for depth updates
socket.on("depth-update", (response) => {
  console.log(response);
});

// Join SPOT current prices channel
socket.emit('join', {
  'channelName': "currentPrices@spot@10s"
});

// Listen for spot price updates
socket.on("currentPrices@spot#update", (response) => {
  console.log(response);
});

// Join FUTURES current prices channel
socket.emit('join', {
  'channelName': "currentPrices@futures@10s"
});

// Listen for futures price updates
socket.on("currentPrices@futures#update", (response) => {
  console.log(response);
});
```

## Summary for Implementation

### Spot WebSocket Event Names (Correct):
1. **Candlestick**: Event = `candlestick`, Channel = `B-BTC_USDT_1m`
2. **Depth Update**: Event = `depth-update`, Channel = `B-BTC_USDT@orderbook@20`
3. **Depth Snapshot**: Event = `depth-snapshot`, Channel = `B-BTC_USDT@orderbook@20`
4. **Current Prices**: Event = `currentPrices@spot#update`, Channel = `currentPrices@spot@10s`
5. **New Trade**: Event = `new-trade`, Channel = `B-BTC_USDT@trades`
6. **Price Change (LTP)**: Event = `price-change`, Channel = `B-BTC_USDT@prices`
7. **Price Stats**: Event = `priceStats@spot#update`, Channel = `priceStats@spot@60s`

### Futures WebSocket Event Names (Correct):
1. **Candlestick**: Event = `candlestick` (SAME as spot)
2. **Depth Update**: Event = `depth-update` (SAME as spot)
3. **Current Prices**: Event = `currentPrices@futures#update`
4. **New Trade**: Event = `new-trade` (SAME as spot), Channel ends with `@trades-futures`

### Private WebSocket Event Names:
1. **Balance Update**: Event = `balance-update`
2. **Order Update**: Event = `order-update`
3. **Trade Update**: Event = `trade-update`
