package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotDepthUpdateData;
import com.coindcx.springclient.repository.WebSocketSpotDepthUpdateDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for WebSocket Spot Depth Update (Order Book Changes) Data
 */
@RestController
@RequestMapping("/api/websocket/spot-depth-update")
@CrossOrigin(origins = "*")
public class WebSocketSpotDepthUpdateController {

    private final WebSocketSpotDepthUpdateDataRepository repository;

    public WebSocketSpotDepthUpdateController(WebSocketSpotDepthUpdateDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get statistics about depth update data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUpdates", repository.count());
        
        List<String> symbols = repository.findDistinctSymbols();
        stats.put("totalSymbols", symbols.size());
        stats.put("symbols", symbols);
        
        List<Integer> depthLevels = repository.findDistinctDepthLevels();
        stats.put("depthLevels", depthLevels);
        
        // Get stats for each symbol
        Map<String, Map<String, Object>> symbolStats = new HashMap<>();
        for (String symbol : symbols) {
            Map<String, Object> symbolData = new HashMap<>();
            symbolData.put("updateCount", repository.countBySymbol(symbol));
            symbolData.put("depthLevels", repository.findDistinctDepthLevelsBySymbol(symbol));
            symbolData.put("latestVersion", repository.getLatestVersionBySymbol(symbol));
            symbolStats.put(symbol, symbolData);
        }
        stats.put("symbolStats", symbolStats);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get depth updates by symbol
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesBySymbol(
            @PathVariable String symbol,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotDepthUpdateData> updates;
        if (limit != null && limit > 0) {
            updates = repository.findBySymbolWithLimit(symbol, limit);
        } else {
            updates = repository.findBySymbolOrderByEventTimeDesc(symbol);
        }
        return ResponseEntity.ok(updates);
    }

    /**
     * Get latest depth update for a symbol
     */
    @GetMapping("/symbol/{symbol}/latest")
    public ResponseEntity<WebSocketSpotDepthUpdateData> getLatestUpdate(@PathVariable String symbol) {
        Optional<WebSocketSpotDepthUpdateData> update = 
            repository.findFirstBySymbolOrderByEventTimeDesc(symbol);
        return update.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get depth updates by symbol and depth level
     */
    @GetMapping("/symbol/{symbol}/depth/{depthLevel}")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesBySymbolAndDepth(
            @PathVariable String symbol,
            @PathVariable Integer depthLevel) {
        
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findBySymbolAndDepthLevelOrderByEventTimeDesc(symbol, depthLevel);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get depth updates within event time range
     */
    @GetMapping("/event-time-range")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesByEventTimeRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findByEventTimeRange(startTime, endTime);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get depth updates by symbol within event time range
     */
    @GetMapping("/symbol/{symbol}/event-time-range")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesBySymbolAndEventTimeRange(
            @PathVariable String symbol,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findBySymbolAndEventTimeRange(symbol, startTime, endTime);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get depth updates within timestamp range
     */
    @GetMapping("/timestamp-range")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesByTimestampRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get depth updates by symbol within timestamp range
     */
    @GetMapping("/symbol/{symbol}/timestamp-range")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesBySymbolAndTimestampRange(
            @PathVariable String symbol,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findBySymbolAndTimestampRange(symbol, startTime, endTime);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get depth updates within record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesByRecordTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findByRecordTimestampRange(startTime, endTime);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get recent N depth updates
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getRecentUpdates(@PathVariable int limit) {
        List<WebSocketSpotDepthUpdateData> updates = repository.findRecentRecords(limit);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get all distinct symbols
     */
    @GetMapping("/symbols")
    public ResponseEntity<List<String>> getSymbols() {
        List<String> symbols = repository.findDistinctSymbols();
        return ResponseEntity.ok(symbols);
    }

    /**
     * Get all distinct depth levels
     */
    @GetMapping("/depth-levels")
    public ResponseEntity<List<Integer>> getDepthLevels() {
        List<Integer> depthLevels = repository.findDistinctDepthLevels();
        return ResponseEntity.ok(depthLevels);
    }

    /**
     * Get depth levels for a specific symbol
     */
    @GetMapping("/symbol/{symbol}/depth-levels")
    public ResponseEntity<List<Integer>> getDepthLevelsBySymbol(@PathVariable String symbol) {
        List<Integer> depthLevels = repository.findDistinctDepthLevelsBySymbol(symbol);
        return ResponseEntity.ok(depthLevels);
    }

    /**
     * Get depth update by version
     */
    @GetMapping("/version/{version}")
    public ResponseEntity<WebSocketSpotDepthUpdateData> getUpdateByVersion(@PathVariable Long version) {
        Optional<WebSocketSpotDepthUpdateData> update = repository.findByVersion(version);
        return update.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get depth updates by symbol and version range
     */
    @GetMapping("/symbol/{symbol}/version-range")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesByVersionRange(
            @PathVariable String symbol,
            @RequestParam Long minVersion,
            @RequestParam Long maxVersion) {
        
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findBySymbolAndVersionRange(symbol, minVersion, maxVersion);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get latest version for a symbol
     */
    @GetMapping("/symbol/{symbol}/latest-version")
    public ResponseEntity<Map<String, Object>> getLatestVersion(@PathVariable String symbol) {
        Long latestVersion = repository.getLatestVersionBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("latestVersion", latestVersion);
        return ResponseEntity.ok(response);
    }

    /**
     * Count depth updates by symbol
     */
    @GetMapping("/symbol/{symbol}/count")
    public ResponseEntity<Map<String, Object>> countUpdatesBySymbol(@PathVariable String symbol) {
        long count = repository.countBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Count depth updates by symbol and depth level
     */
    @GetMapping("/symbol/{symbol}/depth/{depthLevel}/count")
    public ResponseEntity<Map<String, Object>> countUpdatesBySymbolAndDepth(
            @PathVariable String symbol,
            @PathVariable Integer depthLevel) {
        
        long count = repository.countBySymbolAndDepthLevel(symbol, depthLevel);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("depthLevel", depthLevel);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get depth updates by channel name
     */
    @GetMapping("/channel/{channelName}")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getUpdatesByChannel(@PathVariable String channelName) {
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findByChannelNameOrderByEventTimeDesc(channelName);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get depth update by record ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotDepthUpdateData> getById(@PathVariable Long id) {
        Optional<WebSocketSpotDepthUpdateData> update = repository.findById(id);
        return update.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old depth updates
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldUpdates(@RequestParam int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoff);
        long countAfter = repository.count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("deletedCount", countBefore - countAfter);
        response.put("remainingCount", countAfter);
        response.put("cutoffDate", cutoff);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get updates between two versions for a symbol (version progression)
     */
    @GetMapping("/symbol/{symbol}/version-progression")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getVersionProgression(
            @PathVariable String symbol,
            @RequestParam Long fromVersion,
            @RequestParam Long toVersion) {
        
        List<WebSocketSpotDepthUpdateData> updates = 
            repository.findUpdatesBetweenVersions(symbol, fromVersion, toVersion);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get version history for a symbol
     */
    @GetMapping("/symbol/{symbol}/version-history")
    public ResponseEntity<List<WebSocketSpotDepthUpdateData>> getVersionHistory(
            @PathVariable String symbol,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotDepthUpdateData> updates;
        if (limit != null && limit > 0) {
            updates = repository.findVersionHistoryWithLimit(symbol, limit);
        } else {
            updates = repository.findVersionHistory(symbol);
        }
        return ResponseEntity.ok(updates);
    }

    /**
     * Get orderbook change summary (version delta analysis)
     */
    @GetMapping("/symbol/{symbol}/change-summary")
    public ResponseEntity<Map<String, Object>> getChangeSummary(@PathVariable String symbol) {
        Optional<WebSocketSpotDepthUpdateData> latestUpdate = 
            repository.findFirstBySymbolOrderByEventTimeDesc(symbol);
        
        if (latestUpdate.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        WebSocketSpotDepthUpdateData update = latestUpdate.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", update.getSymbol());
        response.put("version", update.getVersion());
        response.put("eventTime", update.getEventTime());
        response.put("timestamp", update.getTimestamp());
        response.put("depthLevel", update.getDepthLevel());
        response.put("product", update.getProduct());
        response.put("channelName", update.getChannelName());
        
        return ResponseEntity.ok(response);
    }
}
