package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketFuturesOrderUpdateData;
import com.coindcx.springclient.repository.WebSocketFuturesOrderUpdateDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/websocket/futures-order-update")
@CrossOrigin(origins = "*")
public class WebSocketFuturesOrderUpdateController {

    @Autowired
    private WebSocketFuturesOrderUpdateDataRepository repository;

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
     * Get orders by pair and status
     */
    @GetMapping("/pair/{pair}/status/{status}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getByPairAndStatus(
            @PathVariable String pair,
            @PathVariable String status) {
        return ResponseEntity.ok(repository.findByPairAndStatusOrderByUpdatedAtDesc(pair, status));
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
     * Get recent order updates
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketFuturesOrderUpdateData>> getRecent(
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findRecentOrderUpdates(Math.min(limit, 1000)));
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
}
