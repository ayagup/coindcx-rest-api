# Copilot Instructions

## Architecture Overview

Three-tier system — React frontend → Spring Boot backend → MySQL:

| Layer | Location | Port | Purpose |
|---|---|---|---|
| React/Vite (TypeScript) | `react-frontend/` | 3000 | UI: charts, trading, WS data viewer |
| Spring Boot 3.2 (Java 17) | `spring-client/` | 8080 | REST API + WebSocket data ingestion |
| MySQL 8 | localhost:3306 | — | `coindcx` database |
| mysql executable | `C:\Users\Lenovo\Documents\softwares\mysql-8.4.7-winx64\bin` | — | DB CLI |
## Key Components

**Spring Boot services** (`spring-client/src/main/java/com/coindcx/springclient/service/`):
- `CoinDCXWebSocketService` — Socket.IO client to `wss://stream.coindcx.com`; subscribes to candlestick/trade/orderbook/position channels on `@PostConstruct`.
- `BinanceWebSocketService` — Actually a **Yahoo Finance REST poller** (not a real WebSocket); polls `https://query2.finance.yahoo.com/v8/finance/chart/DX-Y.NYB` every 60s/5m/1h/6h to persist DXYUSD candles.
- `WebSocketDataPersistenceService` — Central 2252-line service with one `@Async @Transactional` save method per data type; all WebSocket data flows through here.
- `WebSocketDataCleanupService` — Scheduled cleanup; retains `websocket.data.retention.days` (default 7) of data.

**Python historical data loader** (workspace root):
- `load_btc_all_timeframes.py` — Seeds the `websocket_futures_candlestick_data` table with historical candles. Supports `source='coindcx'` (CoinDCX futures API) and `source='binance'` (Binance USDM). Aggregated timeframes (3m, 15m, 30m, 2h, 4h, 1w) are computed in Python from base candles.

## Critical Data Conventions

### Duration / interval normalization
`WebSocketDataPersistenceService.normalizeDuration()` maps raw strings before DB storage:
- `"1m"→"1"`, `"5m"→"5"`, `"1h"/"60m"→"60"`, `"1d"→"1D"`

Always store and query the **normalized form**. Raw strings from APIs must pass through `normalizeDuration`.

### `open_time` must be milliseconds
The `websocket_futures_candlestick_data.open_time` column stores **epoch milliseconds**. CoinDCX WebSocket sends **seconds** (10 digits); the persistence service converts: `openTimeRaw < 9_999_999_999L ? openTimeRaw * 1000L : openTimeRaw`. Python history loader also writes ms. Never write raw seconds to this column.

### Saving Binance/Yahoo Finance candles (`saveBinanceFuturesCandlestickData`)
Pass a `JsonObject kline` with these exact fields:
```java
kline.addProperty("i", "1m");           // raw interval — normalizeDuration is applied internally
kline.addProperty("s", "DXYUSD");       // stored as pair + symbol
kline.addProperty("t", openTimeMs);     // milliseconds
kline.addProperty("T", closeTimeMs);    // milliseconds
kline.addProperty("o"/"c"/"h"/"l"/"v"/"q", "103.25"); // string-formatted numbers
kline.addProperty("x", true);           // completed candle flag
```

## Pair Format Conventions
- CoinDCX futures: `B-BTC_USDT` (pair), `BTCUSDT` (symbol)
- Channel names: `B-BTC_USDT_1m-futures` (candlestick), `B-BTC_USDT@trades-futures` (trades)
- DXYUSD is sourced from Yahoo Finance (`DX-Y.NYB`); stored in DB as pair/symbol `"DXYUSD"`
- Adding a new CoinDCX pair: add a `('B-XXX_USDT', 'XXXUSDT', 'coindcx')` entry to `PAIRS` in `load_btc_all_timeframes.py`

## Developer Workflows

### Start / restart Spring Boot
```powershell
# Kill current instance
$p = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess
if ($p) { Stop-Process -Id $p -Force }
# Start (logs to logs/coindcx-api.log)
Start-Process "cmd.exe" -ArgumentList "/c cd /d spring-client && mvn spring-boot:run > logs\boot.log 2>&1" -WindowStyle Hidden
```

### Compile only
```powershell
cd spring-client; mvn compile -q
```

### Run Python historical loader (activate venv first)
```powershell
& .\venv\Scripts\Activate.ps1    # or venv38
python load_btc_all_timeframes.py
```

### View Spring Boot logs
```powershell
Get-Content spring-client\logs\coindcx-api.log -Tail 40 -Wait
# or grep for specific service
Select-String -Path spring-client\logs\coindcx-api.log -Pattern "Yahoo|DXYUSD|BinanceWeb"
```

### DB quick checks (MySQL)
```powershell
$env:MYSQL_PWD = "Kakri@1234"
& "C:\Users\Lenovo\Documents\softwares\mysql-8.4.7-winx64\bin\mysql.exe" -u root coindcx -e "SELECT pair, duration, COUNT(*) FROM websocket_futures_candlestick_data GROUP BY pair, duration ORDER BY pair, duration;"
```

## Authentication
- API key/secret hardcoded in `spring-client/src/main/resources/application.properties` — used by `WebSocketConfig`
- Frontend stores credentials in `localStorage` (`apiKey`, `apiSecret`) and sends them as `X-API-KEY` / `X-API-SECRET` headers via `apiClient` interceptor
- Use `publicApiClient` (no auth headers) for public market data endpoints

## Frontend API Clients (`react-frontend/src/services/api.ts`)
- `apiClient` — authenticated, uses localStorage keys
- `publicApiClient` — no auth, for public data

## JPA Schema
`spring.jpa.hibernate.ddl-auto=update` — tables auto-create/update on startup. Entity changes are applied automatically; no migration files needed.

## Adding a New Third-Party Data Source
Follow the pattern in `BinanceWebSocketService`: keep the class name, use `OkHttpClient` for REST polling, build a compatible `JsonObject` and call `persistenceService.saveBinanceFuturesCandlestickData(kline, streamName)`. Add in-memory dedup via `lastSavedOpenTime` per interval.
