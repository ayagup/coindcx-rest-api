package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotPriceChangeData;
import com.coindcx.springclient.repository.WebSocketSpotPriceChangeDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for WebSocket Spot Price Change Data (latest price info whenever there is a price change)
 */
@RestController
@RequestMapping("/api/websocket/spot-price-change")
@CrossOrigin(origins = "*")
public class WebSocketSpotPriceChangeController {

    private final WebSocketSpotPriceChangeDataRepository repository;

    public WebSocketSpotPriceChangeController(WebSocketSpotPriceChangeDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get statistics about price change data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPriceChanges", repository.count());
        
        List<String> pairs = repository.findDistinctPairs();
        stats.put("pairs", pairs);
        stats.put("pairCount", pairs.size());
        
        // Get latest prices for all pairs
        List<Map<String, Object>> latestPrices = new ArrayList<>();
        for (String pair : pairs) {
            Optional<WebSocketSpotPriceChangeData> latestData = repository.findFirstByPairOrderByTradeTimestampDesc(pair);
            if (latestData.isPresent()) {
                Map<String, Object> priceInfo = new HashMap<>();
                priceInfo.put("pair", pair);
                priceInfo.put("currentPrice", latestData.get().getTradePrice());
                
                // Calculate 24h price change
                Long oneDayAgo = Instant.now().minusSeconds(86400).getEpochSecond();
                Double priceChangePercentage = repository.getPriceChangePercent(pair, oneDayAgo);
                if (priceChangePercentage != null) {
                    priceInfo.put("priceChange24h", priceChangePercentage);
                }
                
                latestPrices.add(priceInfo);
            }
        }
        stats.put("latestPrices", latestPrices);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all price changes by pair
     */
    @GetMapping("/pair/{pair}")
    public ResponseEntity<List<WebSocketSpotPriceChangeData>> getPriceChangesByPair(
            @PathVariable String pair,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotPriceChangeData> priceChanges;
        if (limit != null && limit > 0) {
            priceChanges = repository.findByPairWithLimit(pair, limit);
        } else {
            priceChanges = repository.findByPairOrderByTradeTimestampDesc(pair);
        }
        return ResponseEntity.ok(priceChanges);
    }

    /**
     * Get latest price change for a specific pair
     */
    @GetMapping("/pair/{pair}/latest")
    public ResponseEntity<WebSocketSpotPriceChangeData> getLatestPriceChangeByPair(@PathVariable String pair) {
        Optional<WebSocketSpotPriceChangeData> latestPriceChange = repository.findFirstByPairOrderByTradeTimestampDesc(pair);
        return latestPriceChange.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get price history for a specific pair
     */
    @GetMapping("/pair/{pair}/price-history/{limit}")
    public ResponseEntity<List<WebSocketSpotPriceChangeData>> getPriceHistory(
            @PathVariable String pair,
            @PathVariable int limit) {
        
        List<WebSocketSpotPriceChangeData> priceHistory = repository.findByPairWithLimit(pair, limit);
        return ResponseEntity.ok(priceHistory);
    }

    /**
     * Get price changes by product type
     */
    @GetMapping("/product/{product}")
    public ResponseEntity<List<WebSocketSpotPriceChangeData>> getPriceChangesByProduct(
            @PathVariable String product) {
        
        List<WebSocketSpotPriceChangeData> priceChanges = repository.findByProductOrderByTradeTimestampDesc(product);
        return ResponseEntity.ok(priceChanges);
    }

    /**
     * Get price changes by timestamp range
     */
    @GetMapping("/timestamp-range")
    public ResponseEntity<List<WebSocketSpotPriceChangeData>> getPriceChangesByTimestampRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotPriceChangeData> priceChanges = repository.findByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(priceChanges);
    }

    /**
     * Get price changes by pair and timestamp range
     */
    @GetMapping("/pair/{pair}/timestamp-range")
    public ResponseEntity<List<WebSocketSpotPriceChangeData>> getPriceChangesByPairAndTimestampRange(
            @PathVariable String pair,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotPriceChangeData> priceChanges = repository.findByPairAndTimestampRange(pair, startTime, endTime);
        return ResponseEntity.ok(priceChanges);
    }

    /**
     * Get price changes by record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketSpotPriceChangeData>> getPriceChangesByRecordTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        List<WebSocketSpotPriceChangeData> priceChanges = repository.findByRecordTimestampRange(start, end);
        return ResponseEntity.ok(priceChanges);
    }

    /**
     * Get recent price changes
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotPriceChangeData>> getRecentPriceChanges(@PathVariable int limit) {
        List<WebSocketSpotPriceChangeData> priceChanges = repository.findRecentPriceChanges(limit);
        return ResponseEntity.ok(priceChanges);
    }

    /**
     * Get price changes by price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<WebSocketSpotPriceChangeData>> getPriceChangesByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        
        List<WebSocketSpotPriceChangeData> priceChanges = repository.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(priceChanges);
    }

    /**
     * Get all distinct trading pairs
     */
    @GetMapping("/pairs")
    public ResponseEntity<List<String>> getDistinctPairs() {
        List<String> pairs = repository.findDistinctPairs();
        return ResponseEntity.ok(pairs);
    }

    /**
     * Get current price for a specific pair
     */
    @GetMapping("/pair/{pair}/current-price")
    public ResponseEntity<Map<String, Object>> getCurrentPrice(@PathVariable String pair) {
        Optional<WebSocketSpotPriceChangeData> latestData = repository.findFirstByPairOrderByTradeTimestampDesc(pair);
        
        if (latestData.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("pair", pair);
            result.put("currentPrice", latestData.get().getTradePrice());
            result.put("timestamp", latestData.get().getTradeTimestamp());
            return ResponseEntity.ok(result);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get average price for a specific pair over a time period
     */
    @GetMapping("/pair/{pair}/average-price")
    public ResponseEntity<Map<String, Object>> getAveragePrice(
            @PathVariable String pair,
            @RequestParam(required = false) Long fromTime) {
        
        Long startTime = fromTime != null ? fromTime : Instant.now().minusSeconds(86400).getEpochSecond();
        Double averagePrice = repository.getAveragePrice(pair, startTime);
        
        if (averagePrice != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("pair", pair);
            result.put("averagePrice", averagePrice);
            result.put("fromTimestamp", startTime);
            result.put("toTimestamp", Instant.now().getEpochSecond());
            return ResponseEntity.ok(result);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get price change percentage for a specific pair
     */
    @GetMapping("/pair/{pair}/price-change-percentage")
    public ResponseEntity<Map<String, Object>> getPriceChangePercentage(
            @PathVariable String pair,
            @RequestParam(required = false) Long fromTime) {
        
        Long startTime = fromTime != null ? fromTime : Instant.now().minusSeconds(86400).getEpochSecond();
        Double priceChangePercentage = repository.getPriceChangePercent(pair, startTime);
        
        if (priceChangePercentage != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("pair", pair);
            result.put("priceChangePercentage", priceChangePercentage);
            result.put("fromTimestamp", startTime);
            result.put("toTimestamp", Instant.now().getEpochSecond());
            
            Optional<WebSocketSpotPriceChangeData> latestData = repository.findFirstByPairOrderByTradeTimestampDesc(pair);
            if (latestData.isPresent()) {
                result.put("currentPrice", latestData.get().getTradePrice());
            }
            
            return ResponseEntity.ok(result);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get count of price changes by pair
     */
    @GetMapping("/pair/{pair}/count")
    public ResponseEntity<Map<String, Object>> getCountByPair(@PathVariable String pair) {
        Long count = repository.countByPair(pair);
        
        Map<String, Object> result = new HashMap<>();
        result.put("pair", pair);
        result.put("count", count);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Get count of price changes in time range
     */
    @GetMapping("/count-in-range")
    public ResponseEntity<Map<String, Object>> getCountInRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        Long count = repository.countByTimestampRange(startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Get price change record by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotPriceChangeData> getPriceChangeById(@PathVariable Long id) {
        Optional<WebSocketSpotPriceChangeData> priceChange = repository.findById(id);
        return priceChange.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get comprehensive price summary for all pairs
     */
    @GetMapping("/price-summary")
    public ResponseEntity<Map<String, Object>> getPriceSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        List<String> pairs = repository.findDistinctPairs();
        Long oneDayAgo = Instant.now().minusSeconds(86400).getEpochSecond();
        
        List<Map<String, Object>> pairSummaries = new ArrayList<>();
        for (String pair : pairs) {
            Map<String, Object> pairSummary = new HashMap<>();
            pairSummary.put("pair", pair);
            
            // Current price
            Optional<WebSocketSpotPriceChangeData> latestData = repository.findFirstByPairOrderByTradeTimestampDesc(pair);
            if (latestData.isPresent()) {
                pairSummary.put("currentPrice", latestData.get().getTradePrice());
            }
            
            // Average price (24h)
            Double averagePrice = repository.getAveragePrice(pair, oneDayAgo);
            if (averagePrice != null) {
                pairSummary.put("averagePrice24h", averagePrice);
            }
            
            // Price change percentage (24h)
            Double priceChangePercentage = repository.getPriceChangePercent(pair, oneDayAgo);
            if (priceChangePercentage != null) {
                pairSummary.put("priceChange24h", priceChangePercentage);
            }
            
            // Price change count
            Long priceChangeCount = repository.countByPair(pair);
            pairSummary.put("priceChangeCount", priceChangeCount);
            
            pairSummaries.add(pairSummary);
        }
        
        summary.put("pairs", pairSummaries);
        summary.put("totalPairs", pairs.size());
        summary.put("totalPriceChanges", repository.count());
        summary.put("timestamp", Instant.now().getEpochSecond());
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Cleanup old price change records
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldRecords(
            @RequestParam(required = false) Integer days) {
        
        int daysToKeep = days != null && days > 0 ? days : 7;
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoff);
        long countAfter = repository.count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("recordsDeleted", countBefore - countAfter);
        result.put("remainingRecords", countAfter);
        result.put("cutoffDate", cutoff.toString());
        
        return ResponseEntity.ok(result);
    }
}
