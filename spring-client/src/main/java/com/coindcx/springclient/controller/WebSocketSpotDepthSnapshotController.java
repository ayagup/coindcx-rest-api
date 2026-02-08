package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotDepthSnapshotData;
import com.coindcx.springclient.repository.WebSocketSpotDepthSnapshotDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for WebSocket Spot Depth Snapshot (Order Book) Data
 */
@RestController
@RequestMapping("/api/websocket/spot-depth-snapshot")
@CrossOrigin(origins = "*")
public class WebSocketSpotDepthSnapshotController {

    private final WebSocketSpotDepthSnapshotDataRepository repository;

    public WebSocketSpotDepthSnapshotController(WebSocketSpotDepthSnapshotDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get statistics about depth snapshot data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSnapshots", repository.count());
        
        List<String> symbols = repository.findDistinctSymbols();
        stats.put("totalSymbols", symbols.size());
        stats.put("symbols", symbols);
        
        List<Integer> depthLevels = repository.findDistinctDepthLevels();
        stats.put("depthLevels", depthLevels);
        
        // Get stats for each symbol
        Map<String, Map<String, Object>> symbolStats = new HashMap<>();
        for (String symbol : symbols) {
            Map<String, Object> symbolData = new HashMap<>();
            symbolData.put("snapshotCount", repository.countBySymbol(symbol));
            symbolData.put("depthLevels", repository.findDistinctDepthLevelsBySymbol(symbol));
            symbolData.put("latestVersion", repository.getLatestVersionBySymbol(symbol));
            symbolStats.put(symbol, symbolData);
        }
        stats.put("symbolStats", symbolStats);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get depth snapshots by symbol
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<WebSocketSpotDepthSnapshotData>> getSnapshotsBySymbol(
            @PathVariable String symbol,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotDepthSnapshotData> snapshots;
        if (limit != null && limit > 0) {
            snapshots = repository.findBySymbolWithLimit(symbol, limit);
        } else {
            snapshots = repository.findBySymbolOrderBySnapshotTimestampDesc(symbol);
        }
        return ResponseEntity.ok(snapshots);
    }

    /**
     * Get latest depth snapshot for a symbol
     */
    @GetMapping("/symbol/{symbol}/latest")
    public ResponseEntity<WebSocketSpotDepthSnapshotData> getLatestSnapshot(@PathVariable String symbol) {
        Optional<WebSocketSpotDepthSnapshotData> snapshot = 
            repository.findFirstBySymbolOrderBySnapshotTimestampDesc(symbol);
        return snapshot.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get depth snapshots by symbol and depth level
     */
    @GetMapping("/symbol/{symbol}/depth/{depthLevel}")
    public ResponseEntity<List<WebSocketSpotDepthSnapshotData>> getSnapshotsBySymbolAndDepth(
            @PathVariable String symbol,
            @PathVariable Integer depthLevel) {
        
        List<WebSocketSpotDepthSnapshotData> snapshots = 
            repository.findBySymbolAndDepthLevelOrderBySnapshotTimestampDesc(symbol, depthLevel);
        return ResponseEntity.ok(snapshots);
    }

    /**
     * Get depth snapshots within snapshot timestamp range
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<WebSocketSpotDepthSnapshotData>> getSnapshotsByTimeRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotDepthSnapshotData> snapshots = 
            repository.findBySnapshotTimestampRange(startTime, endTime);
        return ResponseEntity.ok(snapshots);
    }

    /**
     * Get depth snapshots by symbol within snapshot timestamp range
     */
    @GetMapping("/symbol/{symbol}/time-range")
    public ResponseEntity<List<WebSocketSpotDepthSnapshotData>> getSnapshotsBySymbolAndTimeRange(
            @PathVariable String symbol,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotDepthSnapshotData> snapshots = 
            repository.findBySymbolAndSnapshotTimestampRange(symbol, startTime, endTime);
        return ResponseEntity.ok(snapshots);
    }

    /**
     * Get depth snapshots within record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketSpotDepthSnapshotData>> getSnapshotsByRecordTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        
        List<WebSocketSpotDepthSnapshotData> snapshots = 
            repository.findByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(snapshots);
    }

    /**
     * Get recent N depth snapshots
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotDepthSnapshotData>> getRecentSnapshots(@PathVariable int limit) {
        List<WebSocketSpotDepthSnapshotData> snapshots = repository.findRecentRecords(limit);
        return ResponseEntity.ok(snapshots);
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
     * Get depth snapshot by version
     */
    @GetMapping("/version/{version}")
    public ResponseEntity<WebSocketSpotDepthSnapshotData> getSnapshotByVersion(@PathVariable Long version) {
        Optional<WebSocketSpotDepthSnapshotData> snapshot = repository.findByVersion(version);
        return snapshot.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get depth snapshots by symbol and version range
     */
    @GetMapping("/symbol/{symbol}/version-range")
    public ResponseEntity<List<WebSocketSpotDepthSnapshotData>> getSnapshotsByVersionRange(
            @PathVariable String symbol,
            @RequestParam Long minVersion,
            @RequestParam Long maxVersion) {
        
        List<WebSocketSpotDepthSnapshotData> snapshots = 
            repository.findBySymbolAndVersionRange(symbol, minVersion, maxVersion);
        return ResponseEntity.ok(snapshots);
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
     * Count depth snapshots by symbol
     */
    @GetMapping("/symbol/{symbol}/count")
    public ResponseEntity<Map<String, Object>> countSnapshotsBySymbol(@PathVariable String symbol) {
        long count = repository.countBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Count depth snapshots by symbol and depth level
     */
    @GetMapping("/symbol/{symbol}/depth/{depthLevel}/count")
    public ResponseEntity<Map<String, Object>> countSnapshotsBySymbolAndDepth(
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
     * Get depth snapshots by channel name
     */
    @GetMapping("/channel/{channelName}")
    public ResponseEntity<List<WebSocketSpotDepthSnapshotData>> getSnapshotsByChannel(@PathVariable String channelName) {
        List<WebSocketSpotDepthSnapshotData> snapshots = 
            repository.findByChannelNameOrderBySnapshotTimestampDesc(channelName);
        return ResponseEntity.ok(snapshots);
    }

    /**
     * Get depth snapshot by record ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotDepthSnapshotData> getById(@PathVariable Long id) {
        Optional<WebSocketSpotDepthSnapshotData> snapshot = repository.findById(id);
        return snapshot.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old depth snapshots
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldSnapshots(@RequestParam int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        long countBefore = repository.count();
        repository.deleteByTimestampBefore(cutoff);
        long countAfter = repository.count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("deletedCount", countBefore - countAfter);
        response.put("remainingCount", countAfter);
        response.put("cutoffDate", cutoff);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get order book summary (bid/ask spread analysis)
     */
    @GetMapping("/symbol/{symbol}/orderbook-summary")
    public ResponseEntity<Map<String, Object>> getOrderBookSummary(@PathVariable String symbol) {
        Optional<WebSocketSpotDepthSnapshotData> latestSnapshot = 
            repository.findFirstBySymbolOrderBySnapshotTimestampDesc(symbol);
        
        if (latestSnapshot.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        WebSocketSpotDepthSnapshotData snapshot = latestSnapshot.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", snapshot.getSymbol());
        response.put("version", snapshot.getVersion());
        response.put("snapshotTimestamp", snapshot.getSnapshotTimestamp());
        response.put("depthLevel", snapshot.getDepthLevel());
        response.put("bids", snapshot.getBids());
        response.put("asks", snapshot.getAsks());
        response.put("product", snapshot.getProduct());
        
        return ResponseEntity.ok(response);
    }
}
