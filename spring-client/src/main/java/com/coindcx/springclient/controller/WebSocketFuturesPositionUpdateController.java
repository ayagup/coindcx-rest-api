package com.coindcx.springclient.controller;

import com.coindcx.springclient.config.WebSocketConfig;
import com.coindcx.springclient.entity.WebSocketFuturesPositionUpdateData;
import com.coindcx.springclient.repository.WebSocketFuturesPositionUpdateDataRepository;
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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.HexFormat;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/websocket/futures-position-update")
@CrossOrigin(origins = "*")
public class WebSocketFuturesPositionUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketFuturesPositionUpdateController.class);
    private static final String POSITIONS_URL = "https://api.coindcx.com/exchange/v1/derivatives/futures/positions";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private WebSocketFuturesPositionUpdateDataRepository repository;

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
        
        List<String> positionIds = repository.findDistinctPositionIds();
        stats.put("uniquePositions", positionIds.size());
        
        List<String> pairs = repository.findDistinctPairs();
        stats.put("uniquePairs", pairs.size());
        stats.put("pairs", pairs);
        
        // Get latest positions
        List<WebSocketFuturesPositionUpdateData> latestPositions = new ArrayList<>();
        for (String positionId : positionIds) {
            Optional<WebSocketFuturesPositionUpdateData> position = repository.findFirstByPositionIdOrderByUpdateTimestampDesc(positionId);
            position.ifPresent(latestPositions::add);
        }
        
        // Calculate aggregate metrics
        double totalUnrealizedPnl = latestPositions.stream()
            .filter(p -> p.getUnrealizedPnl() != null)
            .mapToDouble(WebSocketFuturesPositionUpdateData::getUnrealizedPnl)
            .sum();
        
        double totalRealizedPnl = latestPositions.stream()
            .filter(p -> p.getRealizedPnl() != null)
            .mapToDouble(WebSocketFuturesPositionUpdateData::getRealizedPnl)
            .sum();
        
        double totalMargin = latestPositions.stream()
            .filter(p -> p.getMargin() != null)
            .mapToDouble(WebSocketFuturesPositionUpdateData::getMargin)
            .sum();
        
        stats.put("totalUnrealizedPnl", totalUnrealizedPnl);
        stats.put("totalRealizedPnl", totalRealizedPnl);
        stats.put("totalPnl", totalUnrealizedPnl + totalRealizedPnl);
        stats.put("totalMargin", totalMargin);
        
        // Count active vs inactive positions
        long activeCount = latestPositions.stream()
            .filter(p -> "active".equals(p.getStatus()))
            .count();
        long inactiveCount = latestPositions.size() - activeCount;
        
        stats.put("activePositions", activeCount);
        stats.put("inactivePositions", inactiveCount);
        
        // Count long vs short positions
        long longCount = latestPositions.stream()
            .filter(p -> "long".equals(p.getSide()))
            .count();
        long shortCount = latestPositions.stream()
            .filter(p -> "short".equals(p.getSide()))
            .count();
        
        stats.put("longPositions", longCount);
        stats.put("shortPositions", shortCount);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all updates for a specific position
     */
    @GetMapping("/position/{positionId}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPositionId(
            @PathVariable String positionId) {
        return ResponseEntity.ok(repository.findByPositionIdOrderByUpdateTimestampDesc(positionId));
    }

    /**
     * Get latest update for a specific position
     */
    @GetMapping("/position/{positionId}/latest")
    public ResponseEntity<WebSocketFuturesPositionUpdateData> getLatestByPositionId(
            @PathVariable String positionId) {
        return repository.findFirstByPositionIdOrderByUpdateTimestampDesc(positionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get position history with limit
     */
    @GetMapping("/position/{positionId}/history/{limit}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getPositionHistory(
            @PathVariable String positionId,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findByPositionIdWithLimit(positionId, Math.min(limit, 1000)));
    }

    /**
     * Get all positions for a specific pair
     */
    @GetMapping("/pair/{pair}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPair(
            @PathVariable String pair) {
        return ResponseEntity.ok(repository.findByPairOrderByUpdateTimestampDesc(pair));
    }

    /**
     * Get latest positions for a specific pair
     */
    @GetMapping("/pair/{pair}/latest")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getLatestByPair(
            @PathVariable String pair,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(repository.findByPairWithLimit(pair, Math.min(limit, 100)));
    }

    /**
     * Get positions by side (long/short)
     */
    @GetMapping("/side/{side}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getBySide(
            @PathVariable String side) {
        return ResponseEntity.ok(repository.findBySideOrderByUpdateTimestampDesc(side));
    }

    /**
     * Get positions by status. Falls back to CoinDCX REST API when DB is empty.
     * Accepted status values: "active", "closed" (maps to inactive/zero position).
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByStatus(
            @PathVariable String status) {
        // Normalise aliases: "open" -> "active", "closed" -> "inactive"
        String dbStatus = status;
        if ("open".equalsIgnoreCase(status)) dbStatus = "active";
        else if ("closed".equalsIgnoreCase(status)) dbStatus = "inactive";

        List<WebSocketFuturesPositionUpdateData> data = repository.findByStatusOrderByUpdateTimestampDesc(dbStatus);
        // Also try secondary alias: "inactive" for "closed"
        if ((data == null || data.isEmpty()) && "inactive".equals(dbStatus)) {
            data = repository.findByStatusOrderByUpdateTimestampDesc("closed");
        }
        if (data == null || data.isEmpty()) {
            data = fetchPositionsFromRestApi(dbStatus, 100);
        }
        return ResponseEntity.ok(data != null ? data : Collections.emptyList());
    }

    /**
     * Get positions by pair and side
     */
    @GetMapping("/pair/{pair}/side/{side}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPairAndSide(
            @PathVariable String pair,
            @PathVariable String side) {
        return ResponseEntity.ok(repository.findByPairAndSideOrderByUpdateTimestampDesc(pair, side));
    }

    /**
     * Get positions within timestamp range
     */
    @GetMapping("/timestamp-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByTimestampRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByTimestampRange(startTime, endTime));
    }

    /**
     * Get position updates within timestamp range
     */
    @GetMapping("/position/{positionId}/timestamp-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPositionIdAndTimestampRange(
            @PathVariable String positionId,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByPositionIdAndTimestampRange(positionId, startTime, endTime));
    }

    /**
     * Get pair positions within timestamp range
     */
    @GetMapping("/pair/{pair}/timestamp-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPairAndTimestampRange(
            @PathVariable String pair,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByPairAndTimestampRange(pair, startTime, endTime));
    }

    /**
     * Get positions within record time range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByRecordTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByRecordTimestampRange(startTime, endTime));
    }

    /**
     * Get recent position updates. Falls back to CoinDCX REST API when DB is empty.
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getRecent(
            @PathVariable int limit) {
        int cap = Math.min(limit, 1000);
        List<WebSocketFuturesPositionUpdateData> data = repository.findRecentPositionUpdates(cap);
        if (data == null || data.isEmpty()) {
            data = fetchPositionsFromRestApi(null, cap);
        }
        return ResponseEntity.ok(data != null ? data : Collections.emptyList());
    }

    /**
     * Get positions within PnL range
     */
    @GetMapping("/pnl-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPnlRange(
            @RequestParam double minPnl,
            @RequestParam double maxPnl) {
        return ResponseEntity.ok(repository.findByPnlRange(minPnl, maxPnl));
    }

    /**
     * Get positions within leverage range
     */
    @GetMapping("/leverage-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByLeverageRange(
            @RequestParam double minLeverage,
            @RequestParam double maxLeverage) {
        return ResponseEntity.ok(repository.findByLeverageRange(minLeverage, maxLeverage));
    }

    /**
     * Get positions by margin currency
     */
    @GetMapping("/margin-currency/{currency}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByMarginCurrency(
            @PathVariable String currency) {
        return ResponseEntity.ok(repository.findByMarginCurrencyOrderByUpdateTimestampDesc(currency));
    }

    /**
     * Get positions by margin type
     */
    @GetMapping("/margin-type/{type}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByMarginType(
            @PathVariable String type) {
        return ResponseEntity.ok(repository.findByPositionMarginTypeOrderByUpdateTimestampDesc(type));
    }

    /**
     * Get positions near liquidation
     */
    @GetMapping("/near-liquidation/{percentage}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getNearLiquidation(
            @PathVariable double percentage) {
        return ResponseEntity.ok(repository.findPositionsNearLiquidation(percentage));
    }

    /**
     * Get top profitable positions
     */
    @GetMapping("/top-profitable/{limit}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getTopProfitable(
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.getTopProfitablePositions(Math.min(limit, 100)));
    }

    /**
     * Get top losing positions
     */
    @GetMapping("/top-losing/{limit}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getTopLosing(
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.getTopLosingPositions(Math.min(limit, 100)));
    }

    /**
     * Get comprehensive position statistics
     */
    @GetMapping("/position-statistics")
    public ResponseEntity<List<Object[]>> getPositionStatistics(
            @RequestParam(required = false, defaultValue = "0") Long fromTime) {
        if (fromTime == 0) {
            // Default to last 24 hours
            fromTime = System.currentTimeMillis() / 1000 - 86400;
        }
        return ResponseEntity.ok(repository.getPositionStatistics(fromTime));
    }

    /**
     * Get total unrealized PnL by pair
     */
    @GetMapping("/total-unrealized-pnl")
    public ResponseEntity<Map<String, Double>> getTotalUnrealizedPnl() {
        List<Object[]> results = repository.getTotalUnrealizedPnlByPair();
        if (!results.isEmpty()) {
            Map<String, Double> pnlMap = results.stream()
                    .collect(Collectors.toMap(
                            r -> (String) r[0],
                            r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0
                    ));
            return ResponseEntity.ok(pnlMap);
        }
        // REST fallback: fetch active positions and compute unrealized PnL per pair
        List<WebSocketFuturesPositionUpdateData> positions = fetchPositionsFromRestApi("active", 100);
        Map<String, Double> pnlMap = new HashMap<>();
        for (WebSocketFuturesPositionUpdateData pos : positions) {
            if (pos.getPair() != null && pos.getUnrealizedPnl() != null) {
                pnlMap.merge(pos.getPair(), pos.getUnrealizedPnl(), Double::sum);
            }
        }
        return ResponseEntity.ok(pnlMap);
    }

    /**
     * Get total realized PnL by pair
     */
    @GetMapping("/total-realized-pnl")
    public ResponseEntity<Map<String, Double>> getTotalRealizedPnl() {
        List<Object[]> results = repository.getTotalRealizedPnlByPair();
        if (!results.isEmpty()) {
            Map<String, Double> pnlMap = results.stream()
                    .collect(Collectors.toMap(
                            r -> (String) r[0],
                            r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0
                    ));
            return ResponseEntity.ok(pnlMap);
        }
        // REST fallback: fetch closed positions and sum realized PnL per pair.
        // CoinDCX does not expose realized_pnl in the positions API so values will
        // be 0 per pair, but the map keys correctly reflect all traded pairs.
        List<WebSocketFuturesPositionUpdateData> positions = fetchPositionsFromRestApi("closed", 100);
        Map<String, Double> pnlMap = new HashMap<>();
        for (WebSocketFuturesPositionUpdateData pos : positions) {
            if (pos.getPair() != null) {
                pnlMap.merge(pos.getPair(),
                        pos.getRealizedPnl() != null ? pos.getRealizedPnl() : 0.0,
                        Double::sum);
            }
        }
        return ResponseEntity.ok(pnlMap);
    }

    /**
     * Get average leverage by pair
     */
    @GetMapping("/average-leverage")
    public ResponseEntity<Map<String, Double>> getAverageLeverage() {
        List<Object[]> results = repository.getAverageLeverageByPair();
        Map<String, Double> leverageMap = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0
                ));
        return ResponseEntity.ok(leverageMap);
    }

    /**
     * Get all distinct position IDs
     */
    @GetMapping("/position-ids")
    public ResponseEntity<List<String>> getPositionIds() {
        return ResponseEntity.ok(repository.findDistinctPositionIds());
    }

    /**
     * Get all distinct trading pairs
     */
    @GetMapping("/pairs")
    public ResponseEntity<List<String>> getPairs() {
        return ResponseEntity.ok(repository.findDistinctPairs());
    }

    /**
     * Get all distinct statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getStatuses() {
        return ResponseEntity.ok(repository.findDistinctStatuses());
    }

    /**
     * Get complete position summary with analytics
     */
    @GetMapping("/position-summary")
    public ResponseEntity<Map<String, Object>> getPositionSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        List<String> positionIds = repository.findDistinctPositionIds();
        List<Map<String, Object>> positionDetails = new ArrayList<>();
        
        for (String positionId : positionIds) {
            Optional<WebSocketFuturesPositionUpdateData> positionOpt = 
                repository.findFirstByPositionIdOrderByUpdateTimestampDesc(positionId);
            
            if (positionOpt.isPresent()) {
                WebSocketFuturesPositionUpdateData latest = positionOpt.get();
                Map<String, Object> detail = new HashMap<>();
                detail.put("positionId", positionId);
                detail.put("pair", latest.getPair());
                detail.put("side", latest.getSide());
                detail.put("status", latest.getStatus());
                detail.put("entryPrice", latest.getEntryPrice());
                detail.put("currentPrice", latest.getCurrentPrice());
                detail.put("liquidationPrice", latest.getLiquidationPrice());
                detail.put("quantity", latest.getQuantity());
                detail.put("leverage", latest.getLeverage());
                detail.put("margin", latest.getMargin());
                detail.put("unrealizedPnl", latest.getUnrealizedPnl());
                detail.put("realizedPnl", latest.getRealizedPnl());
                detail.put("totalPnl", latest.getTotalPnl());
                detail.put("roi", latest.getRoi());
                detail.put("marginType", latest.getPositionMarginType());
                detail.put("updateTimestamp", latest.getUpdateTimestamp());
                
                // Calculate distance to liquidation
                if (latest.getCurrentPrice() != null && latest.getLiquidationPrice() != null 
                        && latest.getCurrentPrice() > 0) {
                    double distance = Math.abs(latest.getCurrentPrice() - latest.getLiquidationPrice()) 
                            / latest.getCurrentPrice() * 100;
                    detail.put("liquidationDistance", distance);
                }
                
                positionDetails.add(detail);
            }
        }
        
        summary.put("totalPositions", positionIds.size());
        summary.put("positions", positionDetails);
        
        // Sort by absolute PnL
        positionDetails.sort((a, b) -> {
            Double pnlA = (Double) a.getOrDefault("totalPnl", 0.0);
            Double pnlB = (Double) b.getOrDefault("totalPnl", 0.0);
            return Double.compare(Math.abs(pnlB), Math.abs(pnlA));
        });
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Get position update by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketFuturesPositionUpdateData> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old records
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanup(
            @RequestParam(defaultValue = "7") int daysToKeep) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoff);
        long countAfter = repository.count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("recordsBefore", countBefore);
        result.put("recordsAfter", countAfter);
        result.put("recordsDeleted", countBefore - countAfter);
        result.put("cutoffDate", cutoff);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Fetch futures positions from the CoinDCX REST API.
     * @param statusFilter "active", "closed", or null (all). In-memory only — not persisted.
     */
    private List<WebSocketFuturesPositionUpdateData> fetchPositionsFromRestApi(String statusFilter, int limit) {
        String apiKey = webSocketConfig.getApiKey();
        String apiSecret = webSocketConfig.getApiSecret();
        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            logger.debug("REST fallback skipped: API credentials not configured");
            return Collections.emptyList();
        }
        try {
            long timestamp = System.currentTimeMillis();
            int size = Math.min(limit, 100);
            String payload = "{\"timestamp\":\"" + timestamp + "\",\"page\":\"1\",\"size\":\"" + size + "\"}";
            String signature = hmacSha256(payload, apiSecret);

            RequestBody body = RequestBody.create(payload, JSON_MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(POSITIONS_URL)
                    .post(body)
                    .addHeader("X-AUTH-APIKEY", apiKey)
                    .addHeader("X-AUTH-SIGNATURE", signature)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.warn("REST fallback positions: HTTP {}", response.code());
                    return Collections.emptyList();
                }
                String bodyStr = response.body() != null ? response.body().string() : null;
                if (bodyStr == null || bodyStr.isBlank()) return Collections.emptyList();

                // Response may be array or {"positions": [...]}
                JsonArray arr = null;
                JsonElement parsed = JsonParser.parseString(bodyStr);
                if (parsed.isJsonArray()) {
                    arr = parsed.getAsJsonArray();
                } else if (parsed.isJsonObject()) {
                    JsonObject obj = parsed.getAsJsonObject();
                    if (obj.has("positions") && obj.get("positions").isJsonArray())
                        arr = obj.getAsJsonArray("positions");
                    else if (obj.has("data") && obj.get("data").isJsonArray())
                        arr = obj.getAsJsonArray("data");
                }
                if (arr == null) return Collections.emptyList();

                List<WebSocketFuturesPositionUpdateData> result = new ArrayList<>();
                for (JsonElement el : arr) {
                    if (!el.isJsonObject()) continue;
                    JsonObject p = el.getAsJsonObject();

                    WebSocketFuturesPositionUpdateData entity = new WebSocketFuturesPositionUpdateData();
                    mapPositionJsonToEntity(p, entity);
                    entity.setChannelName("rest-api");

                    // Filter by requested status (support aliases: active/open, inactive/closed)
                    if (statusFilter != null && !statusFilter.isBlank()) {
                        String es = entity.getStatus();
                        boolean match = statusFilter.equalsIgnoreCase(es)
                                || ("closed".equalsIgnoreCase(statusFilter) && "inactive".equalsIgnoreCase(es))
                                || ("inactive".equalsIgnoreCase(statusFilter) && "closed".equalsIgnoreCase(es))
                                || ("open".equalsIgnoreCase(statusFilter) && "active".equalsIgnoreCase(es))
                                || ("active".equalsIgnoreCase(statusFilter) && "open".equalsIgnoreCase(es));
                        if (!match) continue;
                    }
                    result.add(entity);
                }
                return result;
            }
        } catch (Exception e) {
            logger.warn("REST fallback positions failed: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    /** Map a REST API position JSON object to an entity instance. */
    private void mapPositionJsonToEntity(JsonObject j, WebSocketFuturesPositionUpdateData e) {
        if (j.has("id") && !j.get("id").isJsonNull()) e.setPositionId(j.get("id").getAsString());
        if (j.has("pair") && !j.get("pair").isJsonNull()) e.setPair(j.get("pair").getAsString());
        if (j.has("avg_price") && !j.get("avg_price").isJsonNull()) e.setEntryPrice(j.get("avg_price").getAsDouble());
        if (j.has("mark_price") && !j.get("mark_price").isJsonNull()) e.setCurrentPrice(j.get("mark_price").getAsDouble());
        if (j.has("liquidation_price") && !j.get("liquidation_price").isJsonNull()) e.setLiquidationPrice(j.get("liquidation_price").getAsDouble());
        if (j.has("leverage") && !j.get("leverage").isJsonNull()) e.setLeverage(j.get("leverage").getAsDouble());
        if (j.has("locked_margin") && !j.get("locked_margin").isJsonNull()) e.setMargin(j.get("locked_margin").getAsDouble());
        if (j.has("locked_user_margin") && !j.get("locked_user_margin").isJsonNull()) e.setInitialMargin(j.get("locked_user_margin").getAsDouble());
        if (j.has("maintenance_margin") && !j.get("maintenance_margin").isJsonNull()) e.setMaintenanceMargin(j.get("maintenance_margin").getAsDouble());
        if (j.has("margin_type") && !j.get("margin_type").isJsonNull()) e.setPositionMarginType(j.get("margin_type").getAsString());
        if (j.has("margin_currency_short_name") && !j.get("margin_currency_short_name").isJsonNull()) e.setMarginCurrency(j.get("margin_currency_short_name").getAsString());
        if (j.has("updated_at") && !j.get("updated_at").isJsonNull()) e.setUpdateTimestamp(j.get("updated_at").getAsLong());

        // Determine quantity and side from active_pos
        double activePos = 0;
        if (j.has("active_pos") && !j.get("active_pos").isJsonNull()) {
            activePos = j.get("active_pos").getAsDouble();
            e.setQuantity(activePos);
        }
        if (activePos > 0) {
            e.setSide("long");
            e.setStatus("active");
        } else if (activePos < 0) {
            e.setSide("short");
            e.setStatus("active");
        } else {
            double inactiveBuy = j.has("inactive_pos_buy") && !j.get("inactive_pos_buy").isJsonNull() ? j.get("inactive_pos_buy").getAsDouble() : 0;
            double inactiveSell = j.has("inactive_pos_sell") && !j.get("inactive_pos_sell").isJsonNull() ? j.get("inactive_pos_sell").getAsDouble() : 0;
            e.setSide(inactiveBuy != 0 ? "long" : (inactiveSell != 0 ? "short" : "long"));
            e.setStatus("closed");  // use "closed" for REST fallback (frontend expects "closed")
            // Restore quantity from inactive positions if present
            if (inactiveBuy != 0) e.setQuantity(inactiveBuy);
            else if (inactiveSell != 0) e.setQuantity(Math.abs(inactiveSell));
        }

        // Calculate PnL if we have the needed prices
        if (e.getQuantity() != null && e.getEntryPrice() != null && e.getCurrentPrice() != null && e.getQuantity() != 0) {
            double unrealizedPnl = e.getQuantity() * (e.getCurrentPrice() - e.getEntryPrice());
            e.setUnrealizedPnl(unrealizedPnl);
            e.setTotalPnl(unrealizedPnl);
            if (e.getMargin() != null && e.getMargin() != 0) {
                e.setRoi((unrealizedPnl / e.getMargin()) * 100);
            }
        }
        // Map realized_pnl if available directly from REST response
        if (j.has("realized_pnl") && !j.get("realized_pnl").isJsonNull()) e.setRealizedPnl(j.get("realized_pnl").getAsDouble());

        e.setRecordTimestamp(LocalDateTime.now());
    }

    /** Generate HMAC-SHA256 hex signature. */
    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
