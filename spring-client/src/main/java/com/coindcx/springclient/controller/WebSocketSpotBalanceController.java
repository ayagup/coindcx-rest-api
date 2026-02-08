package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotBalanceData;
import com.coindcx.springclient.repository.WebSocketSpotBalanceDataRepository;
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
 * REST Controller for WebSocket Spot Balance Data
 */
@RestController
@RequestMapping("/api/websocket/spot-balance")
public class WebSocketSpotBalanceController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketSpotBalanceController.class);

    private final WebSocketSpotBalanceDataRepository spotBalanceDataRepository;

    @Autowired
    public WebSocketSpotBalanceController(WebSocketSpotBalanceDataRepository spotBalanceDataRepository) {
        this.spotBalanceDataRepository = spotBalanceDataRepository;
    }

    /**
     * Get statistics about spot balance data
     * Example: GET /api/websocket/spot-balance/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        logger.info("Fetching spot balance data statistics");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", spotBalanceDataRepository.count());
        stats.put("currencies", spotBalanceDataRepository.findDistinctCurrencies());
        stats.put("currencyCount", spotBalanceDataRepository.findDistinctCurrencies().size());

        return ResponseEntity.ok(stats);
    }

    /**
     * Get all spot balance records for a specific currency
     * Example: GET /api/websocket/spot-balance/currency/BTC
     */
    @GetMapping("/currency/{currency}")
    public ResponseEntity<List<WebSocketSpotBalanceData>> getSpotBalanceByCurrency(
            @PathVariable String currency,
            @RequestParam(required = false, defaultValue = "100") int limit) {
        
        logger.info("Fetching spot balance records for currency: {}", currency);

        List<WebSocketSpotBalanceData> records = spotBalanceDataRepository.findByCurrencyOrderByTimestampDesc(currency);
        
        // Limit results
        if (limit > 0 && records.size() > limit) {
            records = records.subList(0, limit);
        }

        return ResponseEntity.ok(records);
    }

    /**
     * Get the latest spot balance for a specific currency
     * Example: GET /api/websocket/spot-balance/currency/BTC/latest
     */
    @GetMapping("/currency/{currency}/latest")
    public ResponseEntity<WebSocketSpotBalanceData> getLatestSpotBalanceByCurrency(@PathVariable String currency) {
        logger.info("Fetching latest spot balance for currency: {}", currency);

        Optional<WebSocketSpotBalanceData> latestBalance = spotBalanceDataRepository.findLatestByCurrency(currency);
        
        return latestBalance
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all spot balance records for a specific user
     * Example: GET /api/websocket/spot-balance/user/user123
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WebSocketSpotBalanceData>> getSpotBalanceByUserId(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "100") int limit) {
        
        logger.info("Fetching spot balance records for user: {}", userId);

        List<WebSocketSpotBalanceData> records = spotBalanceDataRepository.findByUserIdOrderByTimestampDesc(userId);
        
        // Limit results
        if (limit > 0 && records.size() > limit) {
            records = records.subList(0, limit);
        }

        return ResponseEntity.ok(records);
    }

    /**
     * Get spot balance records within a time range
     * Example: GET /api/websocket/spot-balance/range?start=2025-12-14T00:00:00&end=2025-12-14T23:59:59
     */
    @GetMapping("/range")
    public ResponseEntity<List<WebSocketSpotBalanceData>> getSpotBalanceByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        logger.info("Fetching spot balance records from {} to {}", start, end);

        List<WebSocketSpotBalanceData> records = spotBalanceDataRepository.findByTimeRange(start, end);
        
        return ResponseEntity.ok(records);
    }

    /**
     * Get spot balance records for a currency within a time range
     * Example: GET /api/websocket/spot-balance/currency/BTC/range?start=2025-12-14T00:00:00&end=2025-12-14T23:59:59
     */
    @GetMapping("/currency/{currency}/range")
    public ResponseEntity<List<WebSocketSpotBalanceData>> getSpotBalanceByCurrencyAndTimeRange(
            @PathVariable String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        logger.info("Fetching spot balance records for {} from {} to {}", currency, start, end);

        List<WebSocketSpotBalanceData> records = spotBalanceDataRepository.findByCurrencyAndTimeRange(currency, start, end);
        
        return ResponseEntity.ok(records);
    }

    /**
     * Get recent spot balance records
     * Example: GET /api/websocket/spot-balance/recent/50
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotBalanceData>> getRecentSpotBalanceRecords(@PathVariable int limit) {
        logger.info("Fetching {} recent spot balance records", limit);

        List<WebSocketSpotBalanceData> records = spotBalanceDataRepository.findRecentRecords(limit);
        
        return ResponseEntity.ok(records);
    }

    /**
     * Get all distinct currencies with spot balance data
     * Example: GET /api/websocket/spot-balance/currencies
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getAllCurrencies() {
        logger.info("Fetching all currencies with spot balance data");

        List<String> currencies = spotBalanceDataRepository.findDistinctCurrencies();
        
        return ResponseEntity.ok(currencies);
    }

    /**
     * Get latest spot balance for all currencies
     * Example: GET /api/websocket/spot-balance/latest-all
     */
    @GetMapping("/latest-all")
    public ResponseEntity<List<WebSocketSpotBalanceData>> getLatestSpotBalanceForAllCurrencies() {
        logger.info("Fetching latest spot balance for all currencies");

        List<WebSocketSpotBalanceData> records = spotBalanceDataRepository.findLatestBalanceForAllCurrencies();
        
        return ResponseEntity.ok(records);
    }

    /**
     * Get spot balance record by ID
     * Example: GET /api/websocket/spot-balance/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotBalanceData> getSpotBalanceById(@PathVariable Long id) {
        logger.info("Fetching spot balance record with ID: {}", id);

        Optional<WebSocketSpotBalanceData> balance = spotBalanceDataRepository.findById(id);
        
        return balance
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get count of spot balance records for a currency
     * Example: GET /api/websocket/spot-balance/currency/BTC/count
     */
    @GetMapping("/currency/{currency}/count")
    public ResponseEntity<Map<String, Object>> getSpotBalanceCountByCurrency(@PathVariable String currency) {
        logger.info("Counting spot balance records for currency: {}", currency);

        long count = spotBalanceDataRepository.countByCurrency(currency);
        
        Map<String, Object> response = new HashMap<>();
        response.put("currency", currency);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete old spot balance records before a specific date
     * Example: DELETE /api/websocket/spot-balance/cleanup?before=2025-12-01T00:00:00
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanupOldRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        
        logger.info("Deleting spot balance records before {}", before);

        try {
            spotBalanceDataRepository.deleteByTimestampBefore(before);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Deleted spot balance records before " + before);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to cleanup old spot balance records", e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to cleanup records: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
