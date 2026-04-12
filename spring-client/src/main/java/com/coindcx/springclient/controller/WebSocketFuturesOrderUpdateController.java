package com.coindcx.springclient.controller;

import com.coindcx.springclient.config.WebSocketConfig;
import com.coindcx.springclient.entity.WebSocketFuturesOrderUpdateData;
import com.coindcx.springclient.repository.WebSocketFuturesOrderUpdateDataRepository;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.HexFormat;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/websocket/futures-order-update")
@CrossOrigin(origins = "*")
public class WebSocketFuturesOrderUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketFuturesOrderUpdateController.class);
    private static final String FUTURES_ORDERS_URL = "https://api.coindcx.com/exchange/v1/derivatives/futures/orders";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private WebSocketFuturesOrderUpdateDataRepository repository;

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
        
        List<String> orderIds = repository.findDistinctOrderIds();
        stats.put("uniqueOrders", orderIds.size());
        
        List<String> pairs = repository.findDistinctPairs();
        stats.put("uniquePairs", pairs.size());
        stats.put("pairs", pairs);
        
        List<String> statuses = repository.findDistinctStatuses();
        stats.put("statuses", statuses);
        
        List<String> orderTypes = repository.findDistinctOrderTypes();
        stats.put("orderTypes", orderTypes);
        
        List<String> orderCategories = repository.findDistinctOrderCategories();
        stats.put("orderCategories", orderCategories);
        
        // Get order status distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        for (String status : statuses) {
            statusDistribution.put(status, repository.countByStatus(status));
        }
        stats.put("statusDistribution", statusDistribution);
        
        // Get order type distribution
        Map<String, Long> typeDistribution = new HashMap<>();
        for (String orderType : orderTypes) {
            typeDistribution.put(orderType, repository.countByOrderType(orderType));
        }
        stats.put("typeDistribution", typeDistribution);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all updates for a specific order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByOrderId(
            @PathVariable String orderId) {
        return ResponseEntity.ok(repository.findByOrderIdOrderByUpdatedAtDesc(orderId));
    }

    /**
     * Get latest update for a specific order
     */
    @GetMapping("/order/{orderId}/latest")
    public ResponseEntity<WebSocketFuturesOrderUpdateData> getLatestByOrderId(
            @PathVariable String orderId) {
        return repository.findFirstByOrderIdOrderByUpdatedAtDesc(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get order history with limit
     */
    @GetMapping("/order/{orderId}/history/{limit}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getOrderHistory(
            @PathVariable String orderId,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findByOrderIdWithLimit(orderId, Math.min(limit, 1000)));
    }

    /**
     * Get all orders for a specific pair
     */
    @GetMapping("/pair/{pair}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByPair(
            @PathVariable String pair) {
        return ResponseEntity.ok(repository.findByPairOrderByUpdatedAtDesc(pair));
    }

    /**
     * Get latest orders for a specific pair
     */
    @GetMapping("/pair/{pair}/latest")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getLatestByPair(
            @PathVariable String pair,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(repository.findByPairWithLimit(pair, Math.min(limit, 100)));
    }

    /**
     * Get orders by side (buy/sell)
     */
    @GetMapping("/side/{side}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getBySide(
            @PathVariable String side) {
        return ResponseEntity.ok(repository.findBySideOrderByUpdatedAtDesc(side));
    }

    /**
     * Get orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(repository.findByStatusOrderByUpdatedAtDesc(status));
    }

    /**
     * Get orders by order type
     */
    @GetMapping("/order-type/{type}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByOrderType(
            @PathVariable String type) {
        return ResponseEntity.ok(repository.findByOrderTypeOrderByUpdatedAtDesc(type));
    }

    /**
     * Get orders by order category
     */
    @GetMapping("/order-category/{category}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByOrderCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(repository.findByOrderCategoryOrderByUpdatedAtDesc(category));
    }

    /**
     * Get orders by pair and side
     */
    @GetMapping("/pair/{pair}/side/{side}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByPairAndSide(
            @PathVariable String pair,
            @PathVariable String side) {
        return ResponseEntity.ok(repository.findByPairAndSideOrderByUpdatedAtDesc(pair, side));
    }

    /**
     * Get orders by pair and status.
     * Falls back to the CoinDCX REST API when the local DB is empty.
     * Status alias: "open" matches CoinDCX statuses "open", "partially_filled", and "untriggered".
     */
    @GetMapping("/pair/{pair}/status/{status}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByPairAndStatus(
            @PathVariable String pair,
            @PathVariable String status) {
        // Expand "open" alias to the set of statuses CoinDCX uses for active orders
        Set<String> matchStatuses = expandStatus(status);

        // Query DB for each matching status, merge results
        List<WebSocketFuturesOrderUpdateData> dbResults = new ArrayList<>();
        for (String s : matchStatuses) {
            dbResults.addAll(repository.findByPairAndStatusOrderByUpdatedAtDesc(pair, s));
        }
        dbResults.sort(Comparator.comparingLong(o -> o.getUpdatedAt() != null ? -o.getUpdatedAt() : 0));

        if (!dbResults.isEmpty()) {
            return ResponseEntity.ok(dbResults);
        }

        // DB is empty — fall back to REST API with pair + status filters
        List<WebSocketFuturesOrderUpdateData> restResults = fetchFromRestApi(100, pair, matchStatuses);
        return ResponseEntity.ok(restResults);
    }

    /**
     * Expand a user-facing status label to the set of CoinDCX API status values it covers.
     * "open" → {open, partially_filled, untriggered}  (all in-flight / active order states)
     * anything else → the literal value wrapped in a singleton set.
     */
    private Set<String> expandStatus(String status) {
        if (status == null) return Collections.emptySet();
        String lower = status.toLowerCase();
        if ("open".equals(lower)) {
            // "untriggered" = stop/TP/SL orders waiting to be triggered (TP/SL on active positions)
            return new HashSet<>(Arrays.asList("open", "partially_filled", "untriggered"));
        }
        return Collections.singleton(lower);
    }

    /**
     * Get orders by status and order type
     */
    @GetMapping("/status/{status}/type/{type}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByStatusAndOrderType(
            @PathVariable String status,
            @PathVariable String type) {
        return ResponseEntity.ok(repository.findByStatusAndOrderTypeOrderByUpdatedAtDesc(status, type));
    }

    /**
     * Get orders within created timestamp range
     */
    @GetMapping("/created-range")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByCreatedAtRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByCreatedAtRange(startTime, endTime));
    }

    /**
     * Get orders within updated timestamp range
     */
    @GetMapping("/updated-range")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByUpdatedAtRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByUpdatedAtRange(startTime, endTime));
    }

    /**
     * Get order updates within timestamp range for specific order
     */
    @GetMapping("/order/{orderId}/updated-range")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByOrderIdAndUpdatedAtRange(
            @PathVariable String orderId,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByOrderIdAndUpdatedAtRange(orderId, startTime, endTime));
    }

    /**
     * Get pair orders within updated timestamp range
     */
    @GetMapping("/pair/{pair}/updated-range")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByPairAndUpdatedAtRange(
            @PathVariable String pair,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByPairAndUpdatedAtRange(pair, startTime, endTime));
    }

    /**
     * Get orders within record time range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByRecordTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByRecordTimestampRange(startTime, endTime));
    }

    /**
     * Get recent order updates. Falls back to CoinDCX REST API when DB is empty.
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getRecent(
            @PathVariable int limit) {
        int cap = Math.min(limit, 1000);
        List<WebSocketFuturesOrderUpdateData> data = repository.findRecentOrderUpdates(cap);
        if (data == null || data.isEmpty()) {
            data = fetchFromRestApi(cap, null, null);
        }
        return ResponseEntity.ok(data != null ? data : Collections.emptyList());
    }

    /**
     * Get orders within price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        return ResponseEntity.ok(repository.findByPriceRange(minPrice, maxPrice));
    }

    /**
     * Get orders within leverage range
     */
    @GetMapping("/leverage-range")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByLeverageRange(
            @RequestParam double minLeverage,
            @RequestParam double maxLeverage) {
        return ResponseEntity.ok(repository.findByLeverageRange(minLeverage, maxLeverage));
    }

    /**
     * Get orders by margin currency
     */
    @GetMapping("/margin-currency/{currency}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByMarginCurrency(
            @PathVariable String currency) {
        return ResponseEntity.ok(repository.findByMarginCurrencyShortNameOrderByUpdatedAtDesc(currency));
    }

    /**
     * Get orders with TP/SL
     */
    @GetMapping("/with-tpsl")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getOrdersWithTpSl() {
        return ResponseEntity.ok(repository.findOrdersWithTpSl());
    }

    /**
     * Get orders by group ID
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByGroupId(
            @PathVariable String groupId) {
        return ResponseEntity.ok(repository.findByGroupIdOrderByUpdatedAtDesc(groupId));
    }

    /**
     * Get all distinct order IDs
     */
    @GetMapping("/order-ids")
    public ResponseEntity<List<String>> getOrderIds() {
        return ResponseEntity.ok(repository.findDistinctOrderIds());
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
     * Get all distinct order types
     */
    @GetMapping("/order-types")
    public ResponseEntity<List<String>> getOrderTypes() {
        return ResponseEntity.ok(repository.findDistinctOrderTypes());
    }

    /**
     * Get all distinct order categories
     */
    @GetMapping("/order-categories")
    public ResponseEntity<List<String>> getOrderCategories() {
        return ResponseEntity.ok(repository.findDistinctOrderCategories());
    }

    /**
     * Get comprehensive order statistics
     */
    @GetMapping("/order-statistics")
    public ResponseEntity<List<Object[]>> getOrderStatistics(
            @RequestParam(required = false, defaultValue = "0") Long fromTime) {
        if (fromTime == 0) {
            // Default to last 24 hours
            fromTime = System.currentTimeMillis() - 86400000;
        }
        return ResponseEntity.ok(repository.getOrderStatistics(fromTime));
    }

    /**
     * Get total volume by pair
     */
    @GetMapping("/total-volume")
    public ResponseEntity<Map<String, Double>> getTotalVolume() {
        List<Object[]> results = repository.getTotalVolumeByPair();
        Map<String, Double> volumeMap = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0
                ));
        return ResponseEntity.ok(volumeMap);
    }

    /**
     * Get total fees by pair
     */
    @GetMapping("/total-fees")
    public ResponseEntity<Map<String, Double>> getTotalFees() {
        List<Object[]> results = repository.getTotalFeesByPair();
        Map<String, Double> feesMap = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0
                ));
        return ResponseEntity.ok(feesMap);
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
     * Get high leverage orders
     */
    @GetMapping("/high-leverage")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getHighLeverageOrders(
            @RequestParam(defaultValue = "10") double minLeverage,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(repository.findHighLeverageOrders(minLeverage, Math.min(limit, 100)));
    }

    /**
     * Get large orders
     */
    @GetMapping("/large-orders")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getLargeOrders(
            @RequestParam(defaultValue = "10000") double minValue,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(repository.findLargeOrders(minValue, Math.min(limit, 100)));
    }

    /**
     * Get order update by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketFuturesOrderUpdateData> getById(@PathVariable Long id) {
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
     * Fetch futures orders from the CoinDCX REST API.
     * Used as a fallback when the DB table has no WebSocket-persisted records.
     * Results are in-memory only (not persisted).
     *
     * @param limit       max records per page (capped at 100)
     * @param pairFilter  optional pair to pass as server-side filter (null = all pairs)
     * @param statuses    optional set of statuses to filter in-memory (null/empty = all statuses)
     */
    private List<WebSocketFuturesOrderUpdateData> fetchFromRestApi(
            int limit, String pairFilter, Set<String> statuses) {
        String apiKey = webSocketConfig.getApiKey();
        String apiSecret = webSocketConfig.getApiSecret();
        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            logger.debug("REST fallback skipped: API credentials not configured");
            return Collections.emptyList();
        }
        try {
            long timestamp = System.currentTimeMillis();
            int size = Math.min(limit, 100);

            // Build payload — include optional pair filter for server-side narrowing
            StringBuilder payloadSb = new StringBuilder();
            payloadSb.append("{\"timestamp\":\"").append(timestamp)
                     .append("\",\"page\":\"1\",\"size\":\"").append(size).append("\"");
            if (pairFilter != null && !pairFilter.isBlank()) {
                payloadSb.append(",\"pairs\":\"").append(pairFilter).append("\"");
            }
            payloadSb.append("}");
            String payload = payloadSb.toString();
            String signature = hmacSha256(payload, apiSecret);

            RequestBody body = RequestBody.create(payload, JSON_MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(FUTURES_ORDERS_URL)
                    .post(body)
                    .addHeader("X-AUTH-APIKEY", apiKey)
                    .addHeader("X-AUTH-SIGNATURE", signature)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.warn("REST fallback futures orders: HTTP {}", response.code());
                    return Collections.emptyList();
                }
                String bodyStr = response.body() != null ? response.body().string() : null;
                if (bodyStr == null || bodyStr.isBlank()) return Collections.emptyList();

                // Response may be a JSON array or {"orders": [...]}
                JsonArray ordersArray = null;
                com.google.gson.JsonElement parsed = JsonParser.parseString(bodyStr);
                if (parsed.isJsonArray()) {
                    ordersArray = parsed.getAsJsonArray();
                } else if (parsed.isJsonObject()) {
                    JsonObject obj = parsed.getAsJsonObject();
                    if (obj.has("orders") && obj.get("orders").isJsonArray()) {
                        ordersArray = obj.getAsJsonArray("orders");
                    }
                }
                if (ordersArray == null) return Collections.emptyList();

                List<WebSocketFuturesOrderUpdateData> result = new ArrayList<>();
                for (JsonElement el : ordersArray) {
                    if (!el.isJsonObject()) continue;
                    JsonObject o = el.getAsJsonObject();
                    WebSocketFuturesOrderUpdateData entity = new WebSocketFuturesOrderUpdateData();
                    mapJsonToEntity(o, entity);
                    entity.setChannelName("rest-api");
                    // Apply in-memory status filter when requested
                    if (statuses != null && !statuses.isEmpty()) {
                        String s = entity.getStatus();
                        if (s == null || !statuses.contains(s.toLowerCase())) continue;
                    }
                    result.add(entity);
                }
                return result;
            }
        } catch (Exception e) {
            logger.warn("REST fallback futures orders failed: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    /** Map a REST API / WebSocket JSON object to a entity instance. */
    private void mapJsonToEntity(JsonObject j, WebSocketFuturesOrderUpdateData e) {
        if (j.has("id") && !j.get("id").isJsonNull()) e.setOrderId(j.get("id").getAsString());
        if (j.has("pair") && !j.get("pair").isJsonNull()) e.setPair(j.get("pair").getAsString());
        if (j.has("side") && !j.get("side").isJsonNull()) e.setSide(j.get("side").getAsString());
        if (j.has("status") && !j.get("status").isJsonNull()) e.setStatus(j.get("status").getAsString());
        if (j.has("order_type") && !j.get("order_type").isJsonNull()) e.setOrderType(j.get("order_type").getAsString());
        if (j.has("stop_trigger_instruction") && !j.get("stop_trigger_instruction").isJsonNull()) e.setStopTriggerInstruction(j.get("stop_trigger_instruction").getAsString());
        if (j.has("notification") && !j.get("notification").isJsonNull()) e.setNotification(j.get("notification").getAsString());
        if (j.has("leverage") && !j.get("leverage").isJsonNull()) e.setLeverage(j.get("leverage").getAsDouble());
        if (j.has("maker_fee") && !j.get("maker_fee").isJsonNull()) e.setMakerFee(j.get("maker_fee").getAsDouble());
        if (j.has("taker_fee") && !j.get("taker_fee").isJsonNull()) e.setTakerFee(j.get("taker_fee").getAsDouble());
        if (j.has("fee_amount") && !j.get("fee_amount").isJsonNull()) e.setFeeAmount(j.get("fee_amount").getAsDouble());
        if (j.has("price") && !j.get("price").isJsonNull()) e.setPrice(j.get("price").getAsDouble());
        if (j.has("stop_price") && !j.get("stop_price").isJsonNull()) e.setStopPrice(j.get("stop_price").getAsDouble());
        if (j.has("avg_price") && !j.get("avg_price").isJsonNull()) e.setAvgPrice(j.get("avg_price").getAsDouble());
        if (j.has("take_profit_price") && !j.get("take_profit_price").isJsonNull()) e.setTakeProfitPrice(j.get("take_profit_price").getAsDouble());
        if (j.has("stop_loss_price") && !j.get("stop_loss_price").isJsonNull()) e.setStopLossPrice(j.get("stop_loss_price").getAsDouble());
        if (j.has("total_quantity") && !j.get("total_quantity").isJsonNull()) e.setTotalQuantity(j.get("total_quantity").getAsDouble());
        if (j.has("remaining_quantity") && !j.get("remaining_quantity").isJsonNull()) e.setRemainingQuantity(j.get("remaining_quantity").getAsDouble());
        if (j.has("cancelled_quantity") && !j.get("cancelled_quantity").isJsonNull()) e.setCancelledQuantity(j.get("cancelled_quantity").getAsDouble());
        if (j.has("ideal_margin") && !j.get("ideal_margin").isJsonNull()) e.setIdealMargin(j.get("ideal_margin").getAsDouble());
        if (j.has("order_category") && !j.get("order_category").isJsonNull()) e.setOrderCategory(j.get("order_category").getAsString());
        if (j.has("stage") && !j.get("stage").isJsonNull()) e.setStage(j.get("stage").getAsString());
        if (j.has("created_at") && !j.get("created_at").isJsonNull()) e.setCreatedAt(j.get("created_at").getAsLong());
        if (j.has("updated_at") && !j.get("updated_at").isJsonNull()) e.setUpdatedAt(j.get("updated_at").getAsLong());
        if (j.has("display_message") && !j.get("display_message").isJsonNull()) e.setDisplayMessage(j.get("display_message").getAsString());
        if (j.has("group_status") && !j.get("group_status").isJsonNull()) e.setGroupStatus(j.get("group_status").getAsString());
        if (j.has("group_id") && !j.get("group_id").isJsonNull()) e.setGroupId(j.get("group_id").getAsString());
        if (j.has("margin_currency_short_name") && !j.get("margin_currency_short_name").isJsonNull()) e.setMarginCurrencyShortName(j.get("margin_currency_short_name").getAsString());
        if (j.has("settlement_currency_conversion_price") && !j.get("settlement_currency_conversion_price").isJsonNull()) e.setSettlementCurrencyConversionPrice(j.get("settlement_currency_conversion_price").getAsDouble());
        if (j.has("trade_count") && !j.get("trade_count").isJsonNull()) e.setTradeCount(j.get("trade_count").getAsInt());
        // filled_quantity derived
        if (e.getTotalQuantity() != null && e.getRemainingQuantity() != null) {
            double filled = e.getTotalQuantity() - e.getRemainingQuantity()
                    - (e.getCancelledQuantity() != null ? e.getCancelledQuantity() : 0.0);
            e.setFilledQuantity(filled);
        }
        e.setRecordTimestamp(LocalDateTime.now());
    }

    /** Generate HMAC-SHA256 hex signature. */
    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
