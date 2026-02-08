package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotOrderUpdateData;
import com.coindcx.springclient.repository.WebSocketSpotOrderUpdateDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for WebSocket Spot Order Update Data
 */
@RestController
@RequestMapping("/api/websocket/spot-order-update")
public class WebSocketSpotOrderUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketSpotOrderUpdateController.class);

    private final WebSocketSpotOrderUpdateDataRepository spotOrderUpdateDataRepository;

    @Autowired
    public WebSocketSpotOrderUpdateController(WebSocketSpotOrderUpdateDataRepository spotOrderUpdateDataRepository) {
        this.spotOrderUpdateDataRepository = spotOrderUpdateDataRepository;
    }

    /**
     * Get statistics about spot order update data
     * Example: GET /api/websocket/spot-order-update/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        logger.info("Fetching spot order update data statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", spotOrderUpdateDataRepository.count());
        stats.put("markets", spotOrderUpdateDataRepository.findDistinctMarkets());
        stats.put("marketCount", spotOrderUpdateDataRepository.findDistinctMarkets().size());
        stats.put("statuses", spotOrderUpdateDataRepository.findDistinctStatuses());
        stats.put("statusCount", spotOrderUpdateDataRepository.findDistinctStatuses().size());

        return ResponseEntity.ok(stats);
    }

    /**
     * Get order update by order ID
     * Example: GET /api/websocket/spot-order-update/order/123e4567-e89b-12d3-a456-426614174000
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<WebSocketSpotOrderUpdateData> getByOrderId(@PathVariable String orderId) {
        logger.info("Fetching spot order update for order ID: {}", orderId);

        Optional<WebSocketSpotOrderUpdateData> orderUpdate = spotOrderUpdateDataRepository.findByOrderId(orderId);
        
        return orderUpdate
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all order updates for an order ID (history)
     * Example: GET /api/websocket/spot-order-update/order/123e4567-e89b-12d3-a456-426614174000/history
     */
    @GetMapping("/order/{orderId}/history")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getOrderHistory(@PathVariable String orderId) {
        logger.info("Fetching order history for order ID: {}", orderId);

        List<WebSocketSpotOrderUpdateData> history = spotOrderUpdateDataRepository.findByOrderIdOrderByTimestampDesc(orderId);
        
        return ResponseEntity.ok(history);
    }

    /**
     * Get order updates by client order ID
     * Example: GET /api/websocket/spot-order-update/client-order/my-order-123
     */
    @GetMapping("/client-order/{clientOrderId}")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getByClientOrderId(@PathVariable String clientOrderId) {
        logger.info("Fetching spot order updates for client order ID: {}", clientOrderId);

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findByClientOrderIdOrderByTimestampDesc(clientOrderId);
        
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order updates by status
     * Example: GET /api/websocket/spot-order-update/status/filled
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getByStatus(
            @PathVariable String status,
            @RequestParam(required = false, defaultValue = "100") int limit) {
        
        logger.info("Fetching spot order updates with status: {}", status);

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findByStatusOrderByTimestampDesc(status);
        
        // Limit results
        if (limit > 0 && orders.size() > limit) {
            orders = orders.subList(0, limit);
        }

        return ResponseEntity.ok(orders);
    }

    /**
     * Get order updates by market
     * Example: GET /api/websocket/spot-order-update/market/BTCUSDT
     */
    @GetMapping("/market/{market}")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getByMarket(
            @PathVariable String market,
            @RequestParam(required = false, defaultValue = "100") int limit) {
        
        logger.info("Fetching spot order updates for market: {}", market);

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findByMarketOrderByTimestampDesc(market);
        
        // Limit results
        if (limit > 0 && orders.size() > limit) {
            orders = orders.subList(0, limit);
        }

        return ResponseEntity.ok(orders);
    }

    /**
     * Get order updates by side (buy/sell)
     * Example: GET /api/websocket/spot-order-update/side/buy
     */
    @GetMapping("/side/{side}")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getBySide(
            @PathVariable String side,
            @RequestParam(required = false, defaultValue = "100") int limit) {
        
        logger.info("Fetching spot order updates for side: {}", side);

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findBySideOrderByTimestampDesc(side);
        
        // Limit results
        if (limit > 0 && orders.size() > limit) {
            orders = orders.subList(0, limit);
        }

        return ResponseEntity.ok(orders);
    }

    /**
     * Get order updates by market and status
     * Example: GET /api/websocket/spot-order-update/market/BTCUSDT/status/filled
     */
    @GetMapping("/market/{market}/status/{status}")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getByMarketAndStatus(
            @PathVariable String market,
            @PathVariable String status,
            @RequestParam(required = false, defaultValue = "100") int limit) {
        
        logger.info("Fetching spot order updates for market: {} with status: {}", market, status);

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findByMarketAndStatus(market, status);
        
        // Limit results
        if (limit > 0 && orders.size() > limit) {
            orders = orders.subList(0, limit);
        }

        return ResponseEntity.ok(orders);
    }

    /**
     * Get order updates within a time range
     * Example: GET /api/websocket/spot-order-update/range?start=2025-12-14T00:00:00&end=2025-12-14T23:59:59
     */
    @GetMapping("/range")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        logger.info("Fetching spot order updates from {} to {}", start, end);

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findByTimeRange(start, end);
        
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order updates for a market within a time range
     * Example: GET /api/websocket/spot-order-update/market/BTCUSDT/range?start=2025-12-14T00:00:00&end=2025-12-14T23:59:59
     */
    @GetMapping("/market/{market}/range")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getByMarketAndTimeRange(
            @PathVariable String market,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        logger.info("Fetching spot order updates for {} from {} to {}", market, start, end);

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findByMarketAndTimeRange(market, start, end);
        
        return ResponseEntity.ok(orders);
    }

    /**
     * Get recent order updates
     * Example: GET /api/websocket/spot-order-update/recent/50
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getRecentOrderUpdates(@PathVariable int limit) {
        logger.info("Fetching {} recent spot order updates", limit);

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findRecentRecords(limit);
        
        return ResponseEntity.ok(orders);
    }

    /**
     * Get all distinct markets with order updates
     * Example: GET /api/websocket/spot-order-update/markets
     */
    @GetMapping("/markets")
    public ResponseEntity<List<String>> getAllMarkets() {
        logger.info("Fetching all markets with spot order updates");

        List<String> markets = spotOrderUpdateDataRepository.findDistinctMarkets();
        
        return ResponseEntity.ok(markets);
    }

    /**
     * Get all distinct statuses
     * Example: GET /api/websocket/spot-order-update/statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getAllStatuses() {
        logger.info("Fetching all order statuses");

        List<String> statuses = spotOrderUpdateDataRepository.findDistinctStatuses();
        
        return ResponseEntity.ok(statuses);
    }

    /**
     * Get latest order updates for all orders
     * Example: GET /api/websocket/spot-order-update/latest-all
     */
    @GetMapping("/latest-all")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getLatestOrderUpdates() {
        logger.info("Fetching latest order updates for all orders");

        List<WebSocketSpotOrderUpdateData> orders = spotOrderUpdateDataRepository.findLatestOrderUpdates();
        
        return ResponseEntity.ok(orders);
    }

    /**
     * Get active orders (not filled, cancelled, or rejected)
     * Example: GET /api/websocket/spot-order-update/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<WebSocketSpotOrderUpdateData>> getActiveOrders() {
        logger.info("Fetching active spot orders");

        List<WebSocketSpotOrderUpdateData> activeOrders = spotOrderUpdateDataRepository.findActiveOrders();
        
        return ResponseEntity.ok(activeOrders);
    }

    /**
     * Get order update record by ID
     * Example: GET /api/websocket/spot-order-update/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotOrderUpdateData> getById(@PathVariable Long id) {
        logger.info("Fetching spot order update record with ID: {}", id);

        Optional<WebSocketSpotOrderUpdateData> order = spotOrderUpdateDataRepository.findById(id);
        
        return order
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get count of order updates for a market
     * Example: GET /api/websocket/spot-order-update/market/BTCUSDT/count
     */
    @GetMapping("/market/{market}/count")
    public ResponseEntity<Map<String, Object>> getOrderCountByMarket(@PathVariable String market) {
        logger.info("Counting spot order updates for market: {}", market);

        long count = spotOrderUpdateDataRepository.countByMarket(market);
        
        Map<String, Object> response = new HashMap<>();
        response.put("market", market);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get count of order updates by status
     * Example: GET /api/websocket/spot-order-update/status/filled/count
     */
    @GetMapping("/status/{status}/count")
    public ResponseEntity<Map<String, Object>> getOrderCountByStatus(@PathVariable String status) {
        logger.info("Counting spot order updates with status: {}", status);

        long count = spotOrderUpdateDataRepository.countByStatus(status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete old order update records before a specific date
     * Example: DELETE /api/websocket/spot-order-update/cleanup?before=2025-12-01T00:00:00
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanupOldRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        
        logger.info("Deleting spot order update records before {}", before);

        try {
            spotOrderUpdateDataRepository.deleteByTimestampBefore(before);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Deleted spot order update records before " + before);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to cleanup old spot order update records", e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to cleanup records: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
