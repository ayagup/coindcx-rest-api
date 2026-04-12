package com.coindcx.springclient.controller;

import com.coindcx.springclient.config.WebSocketConfig;
import com.coindcx.springclient.model.WebSocketBalanceUpdateData;
import com.coindcx.springclient.repository.WebSocketBalanceUpdateDataRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket/balance-update")
@CrossOrigin(origins = "*")
public class WebSocketBalanceUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketBalanceUpdateController.class);
    private static final String BALANCES_URL = "https://api.coindcx.com/exchange/v1/users/balances";
    private static final String FUTURES_WALLETS_URL = "https://api.coindcx.com/exchange/v1/derivatives/futures/wallets";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private WebSocketBalanceUpdateDataRepository repository;

    @Autowired
    private WebSocketConfig webSocketConfig;

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Get overall statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", repository.count());
        stats.put("uniqueBalanceIds", repository.findDistinctBalanceIds().size());
        stats.put("uniqueCurrencies", repository.findDistinctCurrencies().size());
        stats.put("balancesWithLocked", repository.countBalancesWithLocked());
        
        // Get detailed statistics
        Map<String, Object> detailedStats = repository.getBalanceStatistics();
        stats.putAll(detailedStats);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all updates for a specific balance ID
     */
    @GetMapping("/balance/{balanceId}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByBalanceId(@PathVariable String balanceId) {
        return ResponseEntity.ok(repository.findByBalanceIdOrderByRecordTimestampDesc(balanceId));
    }

    /**
     * Get latest update for a specific balance ID
     */
    @GetMapping("/balance/{balanceId}/latest")
    public ResponseEntity<WebSocketBalanceUpdateData> getLatestByBalanceId(@PathVariable String balanceId) {
        WebSocketBalanceUpdateData data = repository.findFirstByBalanceIdOrderByRecordTimestampDesc(balanceId);
        return ResponseEntity.ok(data); // null when no data exists yet
    }

    /**
     * Get balance history with limit
     */
    @GetMapping("/balance/{balanceId}/history/{limit}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getBalanceHistory(
            @PathVariable String balanceId,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findByBalanceIdWithLimit(balanceId, limit));
    }

    /**
     * Get all balances for a specific currency
     */
    @GetMapping("/currency/{currency}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByCurrency(@PathVariable String currency) {
        return ResponseEntity.ok(repository.findByCurrencyShortNameOrderByRecordTimestampDesc(currency));
    }

    /**
     * Get latest N balances for a currency
     */
    @GetMapping("/currency/{currency}/latest")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getLatestByCurrency(
            @PathVariable String currency,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(repository.findByCurrencyShortNameWithLimit(currency, limit));
    }

    /**
     * Get latest balance for a currency.
     * Priority: (1) futures wallet API — real trading balance,
     *            (2) spot balances API,
     *            (3) most recent DB record.
     */
    @GetMapping("/currency/{currency}/current")
    public ResponseEntity<WebSocketBalanceUpdateData> getLatestBalanceForCurrency(@PathVariable String currency) {
        WebSocketBalanceUpdateData data = fetchFromFuturesWalletApi(currency);
        if (data == null) {
            data = fetchFromRestApi(currency);
        }
        if (data == null) {
            data = repository.findFirstByCurrencyShortNameOrderByRecordTimestampDesc(currency);
        }
        return ResponseEntity.ok(data);
    }

    /**
     * Get balances for specific balance ID and currency
     */
    @GetMapping("/balance/{balanceId}/currency/{currency}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByBalanceIdAndCurrency(
            @PathVariable String balanceId,
            @PathVariable String currency) {
        return ResponseEntity.ok(repository.findByBalanceIdAndCurrencyShortNameOrderByRecordTimestampDesc(balanceId, currency));
    }

    /**
     * Get balances in a time range
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByRecordTimestampRange(startTime, endTime));
    }

    /**
     * Get balance history in time range
     */
    @GetMapping("/balance/{balanceId}/time-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getBalanceIdTimeRange(
            @PathVariable String balanceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByBalanceIdAndRecordTimestampRange(balanceId, startTime, endTime));
    }

    /**
     * Get currency balances in time range
     */
    @GetMapping("/currency/{currency}/time-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getCurrencyTimeRange(
            @PathVariable String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByCurrencyAndRecordTimestampRange(currency, startTime, endTime));
    }

    /**
     * Get recent balance updates
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getRecentBalances(@PathVariable int limit) {
        return ResponseEntity.ok(repository.findRecentBalanceUpdates(limit));
    }

    /**
     * Get balances in a specific range
     */
    @GetMapping("/balance-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByBalanceRange(
            @RequestParam BigDecimal minBalance,
            @RequestParam BigDecimal maxBalance) {
        return ResponseEntity.ok(repository.findByBalanceRange(minBalance, maxBalance));
    }

    /**
     * Get balances with locked amount in range
     */
    @GetMapping("/locked-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByLockedBalanceRange(
            @RequestParam BigDecimal minLocked,
            @RequestParam BigDecimal maxLocked) {
        return ResponseEntity.ok(repository.findByLockedBalanceRange(minLocked, maxLocked));
    }

    /**
     * Get balances with available amount in range
     */
    @GetMapping("/available-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByAvailableBalanceRange(
            @RequestParam BigDecimal minAvailable,
            @RequestParam BigDecimal maxAvailable) {
        return ResponseEntity.ok(repository.findByAvailableBalanceRange(minAvailable, maxAvailable));
    }

    /**
     * Get all balances with locked amount
     */
    @GetMapping("/with-locked")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getBalancesWithLocked() {
        return ResponseEntity.ok(repository.findBalancesWithLocked());
    }

    /**
     * Get top N balances with locked amount
     */
    @GetMapping("/with-locked/top/{limit}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getTopBalancesWithLocked(@PathVariable int limit) {
        return ResponseEntity.ok(repository.findBalancesWithLockedLimit(limit));
    }

    /**
     * Get large balances (above minimum threshold)
     */
    @GetMapping("/large-balances")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getLargeBalances(
            @RequestParam BigDecimal minBalance,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(repository.findLargeBalances(minBalance, limit));
    }

    /**
     * Get all distinct balance IDs
     */
    @GetMapping("/balance-ids")
    public ResponseEntity<List<String>> getDistinctBalanceIds() {
        return ResponseEntity.ok(repository.findDistinctBalanceIds());
    }

    /**
     * Get all distinct currencies
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getDistinctCurrencies() {
        return ResponseEntity.ok(repository.findDistinctCurrencies());
    }

    /**
     * Get all distinct currency IDs
     */
    @GetMapping("/currency-ids")
    public ResponseEntity<List<String>> getDistinctCurrencyIds() {
        return ResponseEntity.ok(repository.findDistinctCurrencyIds());
    }

    /**
     * Get count by balance ID
     */
    @GetMapping("/balance/{balanceId}/count")
    public ResponseEntity<Long> getCountByBalanceId(@PathVariable String balanceId) {
        return ResponseEntity.ok(repository.countByBalanceId(balanceId));
    }

    /**
     * Get count by currency
     */
    @GetMapping("/currency/{currency}/count")
    public ResponseEntity<Long> getCountByCurrency(@PathVariable String currency) {
        return ResponseEntity.ok(repository.countByCurrencyShortName(currency));
    }

    /**
     * Get balances grouped by currency with statistics
     */
    @GetMapping("/by-currency")
    public ResponseEntity<List<Map<String, Object>>> getBalancesByCurrency() {
        return ResponseEntity.ok(repository.getBalancesByCurrency());
    }

    /**
     * Get locked balances grouped by currency
     */
    @GetMapping("/locked-by-currency")
    public ResponseEntity<List<Map<String, Object>>> getLockedBalancesByCurrency() {
        return ResponseEntity.ok(repository.getLockedBalancesByCurrency());
    }

    /**
     * Get latest balances for all balance IDs
     */
    @GetMapping("/latest-all-ids")
    public ResponseEntity<List<Map<String, Object>>> getLatestBalancesForAllIds() {
        return ResponseEntity.ok(repository.getLatestBalancesForAllIds());
    }

    /**
     * Get latest balances for all currencies
     */
    @GetMapping("/latest-all-currencies")
    public ResponseEntity<List<Map<String, Object>>> getLatestBalancesForAllCurrencies() {
        return ResponseEntity.ok(repository.getLatestBalancesForAllCurrencies());
    }

    /**
     * Get balance by record ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketBalanceUpdateData> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old balance data
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanup(
            @RequestParam(required = false, defaultValue = "7") int daysToKeep) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoff);
        long countAfter = repository.count();
        long deleted = countBefore - countAfter;
        
        Map<String, Object> result = new HashMap<>();
        result.put("cutoffDate", cutoff);
        result.put("recordsDeleted", deleted);
        result.put("recordsRemaining", countAfter);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Fetch balance for a single currency from the CoinDCX futures wallet API.
     * GET /exchange/v1/derivatives/futures/wallets — auth by signing an empty string.
     * Returns null if the currency is not found or the call fails.
     */
    private WebSocketBalanceUpdateData fetchFromFuturesWalletApi(String currency) {
        String apiKey = webSocketConfig.getApiKey();
        String apiSecret = webSocketConfig.getApiSecret();
        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) return null;
        try {
            // This GET endpoint requires signing an empty body
            String signature = hmacSha256("", apiSecret);
            Request request = new Request.Builder()
                    .url(FUTURES_WALLETS_URL)
                    .get()
                    .addHeader("X-AUTH-APIKEY", apiKey)
                    .addHeader("X-AUTH-SIGNATURE", signature)
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.warn("Futures wallet API: HTTP {} for currency {}", response.code(), currency);
                    return null;
                }
                String bodyStr = response.body() != null ? response.body().string() : null;
                if (bodyStr == null || bodyStr.isBlank()) return null;

                JsonArray wallets = JsonParser.parseString(bodyStr).getAsJsonArray();
                for (JsonElement el : wallets) {
                    JsonObject w = el.getAsJsonObject();
                    String curr = w.has("currency_short_name") && !w.get("currency_short_name").isJsonNull()
                            ? w.get("currency_short_name").getAsString() : null;
                    if (currency.equalsIgnoreCase(curr)) {
                        BigDecimal bal = w.has("balance") && !w.get("balance").isJsonNull()
                                ? new BigDecimal(w.get("balance").getAsString()) : BigDecimal.ZERO;
                        BigDecimal locked = w.has("locked_balance") && !w.get("locked_balance").isJsonNull()
                                ? new BigDecimal(w.get("locked_balance").getAsString()) : BigDecimal.ZERO;
                        BigDecimal crossOrderMargin = w.has("cross_order_margin") && !w.get("cross_order_margin").isJsonNull()
                                ? new BigDecimal(w.get("cross_order_margin").getAsString()) : BigDecimal.ZERO;

                        WebSocketBalanceUpdateData data = new WebSocketBalanceUpdateData();
                        data.setBalanceId(curr);
                        data.setCurrencyShortName(curr);
                        data.setCurrencyId(curr);
                        data.setBalance(bal);
                        data.setLockedBalance(locked.add(crossOrderMargin));
                        data.setAvailableBalance(bal.subtract(locked).subtract(crossOrderMargin));
                        data.setChannelName("futures-wallet-api");
                        data.setRecordTimestamp(LocalDateTime.now());
                        return data;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Futures wallet API failed for currency {}: {}", currency, e.getMessage());
        }
        return null;
    }

    /**
     * Fetch balance for a single currency from the CoinDCX REST API.
     * Used as a fallback when the WebSocket has not yet delivered a balance-update event.
     * The result is NOT persisted — it is returned in-memory so the caller sees current data.
     */
    private WebSocketBalanceUpdateData fetchFromRestApi(String currency) {
        String apiKey = webSocketConfig.getApiKey();
        String apiSecret = webSocketConfig.getApiSecret();
        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            logger.debug("REST API balance fallback skipped: API credentials not configured");
            return null;
        }
        try {
            long timestamp = System.currentTimeMillis();
            String payload = "{\"timestamp\":" + timestamp + "}";
            String signature = hmacSha256(payload, apiSecret);

            RequestBody body = RequestBody.create(payload, JSON_MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(BALANCES_URL)
                    .post(body)
                    .addHeader("X-AUTH-APIKEY", apiKey)
                    .addHeader("X-AUTH-SIGNATURE", signature)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.warn("REST API balance fallback: HTTP {} for currency {}", response.code(), currency);
                    return null;
                }
                String responseBodyStr = response.body() != null ? response.body().string() : null;
                if (responseBodyStr == null || responseBodyStr.isBlank()) return null;

                JsonArray balances = JsonParser.parseString(responseBodyStr).getAsJsonArray();
                for (JsonElement element : balances) {
                    JsonObject b = element.getAsJsonObject();
                    String curr = b.has("currency") && !b.get("currency").isJsonNull()
                            ? b.get("currency").getAsString() : null;
                    if (currency.equalsIgnoreCase(curr)) {
                        BigDecimal bal = b.has("balance") && !b.get("balance").isJsonNull()
                                ? b.get("balance").getAsBigDecimal() : BigDecimal.ZERO;
                        BigDecimal locked = b.has("locked_balance") && !b.get("locked_balance").isJsonNull()
                                ? b.get("locked_balance").getAsBigDecimal() : BigDecimal.ZERO;

                        WebSocketBalanceUpdateData data = new WebSocketBalanceUpdateData();
                        data.setBalanceId(curr);
                        data.setCurrencyShortName(curr);
                        data.setCurrencyId(curr);
                        data.setBalance(bal);
                        data.setLockedBalance(locked);
                        data.setAvailableBalance(bal.subtract(locked));
                        data.setChannelName("rest-api");
                        data.setRecordTimestamp(LocalDateTime.now());
                        return data;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("REST API balance fallback failed for currency {}: {}", currency, e.getMessage());
        }
        return null;
    }

    /** Generate HMAC-SHA256 hex signature for the CoinDCX REST API. */
    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(rawHmac);
    }
}
