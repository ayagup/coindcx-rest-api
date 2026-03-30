package com.coindcx.springclient.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Yahoo Finance REST polling service for the US Dollar Index (DXY / DX-Y.NYB).
 *
 * <p>Since Yahoo Finance does not offer a simple WebSocket feed, this service polls
 * the Yahoo Finance chart v8 REST API on a schedule for four intervals (1m, 5m, 1h, 1d)
 * and persists completed candles into the shared
 * {@code websocket_futures_candlestick_data} table via
 * {@link WebSocketDataPersistenceService#saveYahooFinanceCandlestickData}.
 *
 * <p>Candles are deduplicated in memory by {@code openTime}: within a single JVM
 * session, a candle with a given openTime is only persisted once per interval.
 */
@Service
public class YahooFinanceDxyService {

    private static final Logger logger = LoggerFactory.getLogger(YahooFinanceDxyService.class);

    // ─── Yahoo Finance config ───────────────────────────────────────────────

    /** Yahoo Finance chart endpoint — DX-Y.NYB = US Dollar Index on NYSE Arca. */
    private static final String YAHOO_BASE =
            "https://query2.finance.yahoo.com/v8/finance/chart/DX-Y.NYB";

    /** Symbol stored in the DB — kept as "DXYUSD" for UI / query consistency. */
    private static final String DB_SYMBOL = "DXYUSD";

    /**
     * Poll configuration rows:
     * [0] intervalLabel   – label used in stream names and JsonObject "i" field ("1m","5m","1h","1d")
     * [1] yahooInterval   – Yahoo Finance ?interval= value ("1m","5m","60m","1d")
     * [2] yahooRange      – Yahoo Finance ?range= value  ("1d","5d","10d","max")
     * [3] intervalSeconds – candle width in seconds (for closeTime calculation)
     * [4] pollPeriod      – how often to fire the poll
     * [5] pollUnit        – TimeUnit for pollPeriod
     * [6] initialDelay    – seconds before first poll fires (stagger polls)
     */
    private static final Object[][] POLL_CONFIGS = {
        //  label   yInterval  yRange  ivSec   period  unit               delay
        {   "1m",   "1m",     "1d",    60L,    60L,    TimeUnit.SECONDS,  5L  },
        {   "5m",   "5m",     "5d",    300L,   5L,     TimeUnit.MINUTES,  15L },
        {   "1h",   "60m",    "10d",   3600L,  1L,     TimeUnit.HOURS,    30L },
        {   "1d",   "1d",     "max",   86400L, 6L,     TimeUnit.HOURS,    60L },
    };

    // ─── State ──────────────────────────────────────────────────────────────

    private final WebSocketDataPersistenceService persistenceService;
    private final OkHttpClient httpClient;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            POLL_CONFIGS.length,
            r -> { Thread t = new Thread(r, "yahoo-poller"); t.setDaemon(true); return t; });

    /**
     * Track the last successfully saved open-time (ms) per interval label.
     * Prevents duplicate rows within the same JVM session.
     */
    private final Map<String, Long> lastSavedOpenTime = new ConcurrentHashMap<>();

    @Autowired
    public YahooFinanceDxyService(WebSocketDataPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    @PostConstruct
    public void initialize() {
        logger.info("Initializing Yahoo Finance DXY poller — {} intervals", POLL_CONFIGS.length);
        for (Object[] cfg : POLL_CONFIGS) {
            String label       = (String)   cfg[0];
            long   period      = (Long)     cfg[4];
            TimeUnit unit      = (TimeUnit) cfg[5];
            long   initialDly  = (Long)     cfg[6];

            scheduler.scheduleAtFixedRate(
                    () -> poll(cfg),
                    initialDly, period, unit
            );
            logger.info("  Scheduled DXY {} poll every {} {}, first in {} s",
                    label, period, unit, initialDly);
        }
    }

    @PreDestroy
    public void destroy() {
        logger.info("Shutting down Yahoo Finance DXY poller");
        scheduler.shutdownNow();
        httpClient.dispatcher().executorService().shutdown();
    }

    // ─── Polling logic ───────────────────────────────────────────────────────

    /**
     * Fetch the latest candles from Yahoo Finance for one interval configuration,
     * then persist any newly completed candles to the DB.
     *
     * @param cfg one row from {@link #POLL_CONFIGS}
     */
    private void poll(Object[] cfg) {
        String   label       = (String)   cfg[0];
        String   yhInterval  = (String)   cfg[1];
        String   yhRange     = (String)   cfg[2];
        long     intervalSec = (Long)     cfg[3];
        String   streamName  = "dxyusd@kline_" + label;

        String url = YAHOO_BASE + "?interval=" + yhInterval + "&range=" + yhRange;

        try {
            Request req = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .addHeader("Accept",     "application/json")
                    .build();

            try (Response response = httpClient.newCall(req).execute()) {
                if (!response.isSuccessful()) {
                    logger.warn("Yahoo Finance responded {} for interval={}", response.code(), label);
                    return;
                }
                String body = response.body() != null ? response.body().string() : "";
                processResponse(body, label, intervalSec, streamName);
            }
        } catch (Exception e) {
            logger.error("Yahoo Finance poll error for interval={}: {}", label, e.getMessage());
        }
    }

    /**
     * Parse the Yahoo Finance JSON response and persist candles that are:
     * <ul>
     *   <li>not null / NaN</li>
     *   <li>a <em>completed</em> candle (openTime + intervalSec &lt;= now)</li>
     *   <li>not already saved this session (dedup via {@link #lastSavedOpenTime})</li>
     * </ul>
     */
    private void processResponse(String body, String label, long intervalSec, String streamName) {
        try {
            JsonObject root   = JsonParser.parseString(body).getAsJsonObject();
            JsonObject chart  = root.getAsJsonObject("chart");
            JsonArray  results = chart.getAsJsonArray("result");

            if (results == null || results.size() == 0) {
                logger.debug("Yahoo Finance: no results for interval={}", label);
                return;
            }

            JsonObject result     = results.get(0).getAsJsonObject();
            JsonArray  timestamps = result.getAsJsonArray("timestamp");

            if (timestamps == null || timestamps.size() == 0) {
                logger.debug("Yahoo Finance: empty timestamp array for interval={}", label);
                return;
            }

            JsonObject indicators = result.getAsJsonObject("indicators");
            JsonObject quote      = indicators.getAsJsonArray("quote").get(0).getAsJsonObject();

            JsonArray opens   = quote.getAsJsonArray("open");
            JsonArray closes  = quote.getAsJsonArray("close");
            JsonArray highs   = quote.getAsJsonArray("high");
            JsonArray lows    = quote.getAsJsonArray("low");
            JsonArray volumes = quote.getAsJsonArray("volume");

            long nowMs       = System.currentTimeMillis();
            long lastSaved   = lastSavedOpenTime.getOrDefault(label, 0L);
            int  savedCount  = 0;
            int  total       = timestamps.size();

            // Iterate from newest to oldest; stop once we're past already-saved entries
            for (int i = total - 1; i >= 0; i--) {
                JsonElement tsElem = timestamps.get(i);
                if (tsElem.isJsonNull()) continue;

                long openTimeSec = tsElem.getAsLong();
                long openTimeMs  = openTimeSec * 1000L;
                long closeTimeMs = (openTimeSec + intervalSec) * 1000L - 1L;

                // Skip the in-progress (current, unclosed) candle
                if (closeTimeMs >= nowMs) continue;

                // Stop if we've already saved this and older candles this session
                if (openTimeMs <= lastSaved) break;

                // Skip candles with null OHLCV
                if (isNullOrNull(opens, i) || isNullOrNull(closes, i)
                        || isNullOrNull(highs, i) || isNullOrNull(lows, i)) {
                    continue;
                }

                double open   = opens.get(i).getAsDouble();
                double close  = closes.get(i).getAsDouble();
                double high   = highs.get(i).getAsDouble();
                double low    = lows.get(i).getAsDouble();
                double volume = (volumes == null || isNullOrNull(volumes, i)) ? 0.0
                                : volumes.get(i).getAsDouble();

                // Build a JsonObject for saveYahooFinanceCandlestickData
                JsonObject kline = new JsonObject();
                kline.addProperty("i", label);                    // "1m","5m","1h","1d"
                kline.addProperty("s", DB_SYMBOL);                // "DXYUSD"
                kline.addProperty("t", openTimeMs);               // open time ms
                kline.addProperty("T", closeTimeMs);              // close time ms
                kline.addProperty("o", String.format("%.5f", open));
                kline.addProperty("c", String.format("%.5f", close));
                kline.addProperty("h", String.format("%.5f", high));
                kline.addProperty("l", String.format("%.5f", low));
                kline.addProperty("v", String.format("%.2f", volume));
                kline.addProperty("q", "0");                      // no quoteVolume for DXY
                kline.addProperty("x", true);                     // completed candle

                persistenceService.saveYahooFinanceCandlestickData(kline, streamName);
                savedCount++;

                // Update dedup tracker to the newest openTime we've persisted
                if (openTimeMs > lastSaved) {
                    lastSavedOpenTime.put(label, openTimeMs);
                    lastSaved = openTimeMs;
                }
            }

            if (savedCount > 0) {
                logger.info("Yahoo Finance DXY {}: saved {} new candle(s)", label, savedCount);
            } else {
                logger.debug("Yahoo Finance DXY {}: no new candles (latest already saved)", label);
            }

        } catch (Exception e) {
            logger.error("Yahoo Finance parse error for interval={}: {}", label, e.getMessage());
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** @return true if the element at {@code index} in {@code arr} is null or JSON null. */
    private static boolean isNullOrNull(JsonArray arr, int index) {
        if (arr == null || index >= arr.size()) return true;
        JsonElement el = arr.get(index);
        return el == null || el.isJsonNull();
    }

    // ─── Public status ────────────────────────────────────────────────────────

    /**
     * Always returns {@code true} — this service uses REST polling, not a persistent
     * connection, so there is no "connected" state to track.
     */
    public boolean isConnected() {
        return true;
    }
}
