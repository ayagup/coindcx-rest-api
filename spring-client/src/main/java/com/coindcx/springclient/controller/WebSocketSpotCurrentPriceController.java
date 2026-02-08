package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotCurrentPriceData;
import com.coindcx.springclient.repository.WebSocketSpotCurrentPriceDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for WebSocket Spot Current Prices Data
 */
@RestController
@RequestMapping("/api/websocket/spot-current-price")
@CrossOrigin(origins = "*")
public class WebSocketSpotCurrentPriceController {

    private final WebSocketSpotCurrentPriceDataRepository repository;

    public WebSocketSpotCurrentPriceController(WebSocketSpotCurrentPriceDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get statistics about current price data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPriceUpdates", repository.count());
        
        List<String> intervals = repository.findDistinctIntervals();
        stats.put("intervals", intervals);
        
        // Get frequency statistics
        List<Object[]> frequencyStats = repository.getUpdateFrequencyStatistics();
        Map<String, Long> intervalCounts = new HashMap<>();
        for (Object[] stat : frequencyStats) {
            intervalCounts.put((String) stat[0], (Long) stat[1]);
        }
        stats.put("updateFrequencyByInterval", intervalCounts);
        
        // Get stats for each interval
        Map<String, Map<String, Object>> intervalStats = new HashMap<>();
        for (String interval : intervals) {
            Map<String, Object> intervalData = new HashMap<>();
            intervalData.put("updateCount", repository.countByPriceInterval(interval));
            intervalData.put("latestVersion", repository.getLatestVersionByInterval(interval));
            intervalStats.put(interval, intervalData);
        }
        stats.put("intervalStats", intervalStats);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get price updates by interval
     */
    @GetMapping("/interval/{interval}")
    public ResponseEntity<List<WebSocketSpotCurrentPriceData>> getPriceUpdatesByInterval(
            @PathVariable String interval,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotCurrentPriceData> updates;
        if (limit != null && limit > 0) {
            updates = repository.findByIntervalWithLimit(interval, limit);
        } else {
            updates = repository.findByPriceIntervalOrderByTimestampDesc(interval);
        }
        return ResponseEntity.ok(updates);
    }

    /**
     * Get latest price update for an interval
     */
    @GetMapping("/interval/{interval}/latest")
    public ResponseEntity<WebSocketSpotCurrentPriceData> getLatestPriceUpdate(@PathVariable String interval) {
        Optional<WebSocketSpotCurrentPriceData> update = 
            repository.findFirstByPriceIntervalOrderByTimestampDesc(interval);
        return update.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get price updates within timestamp range
     */
    @GetMapping("/timestamp-range")
    public ResponseEntity<List<WebSocketSpotCurrentPriceData>> getPriceUpdatesByTimestampRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotCurrentPriceData> updates = 
            repository.findByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get price updates by interval within timestamp range
     */
    @GetMapping("/interval/{interval}/timestamp-range")
    public ResponseEntity<List<WebSocketSpotCurrentPriceData>> getPriceUpdatesByIntervalAndTimestampRange(
            @PathVariable String interval,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotCurrentPriceData> updates = 
            repository.findByIntervalAndTimestampRange(interval, startTime, endTime);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get price updates within record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketSpotCurrentPriceData>> getPriceUpdatesByRecordTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        
        List<WebSocketSpotCurrentPriceData> updates = 
            repository.findByRecordTimestampRange(startTime, endTime);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get recent N price updates
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotCurrentPriceData>> getRecentPriceUpdates(@PathVariable int limit) {
        List<WebSocketSpotCurrentPriceData> updates = repository.findRecentRecords(limit);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get all distinct intervals
     */
    @GetMapping("/intervals")
    public ResponseEntity<List<String>> getIntervals() {
        List<String> intervals = repository.findDistinctIntervals();
        return ResponseEntity.ok(intervals);
    }

    /**
     * Get price update by version
     */
    @GetMapping("/version/{version}")
    public ResponseEntity<WebSocketSpotCurrentPriceData> getPriceUpdateByVersion(@PathVariable Long version) {
        Optional<WebSocketSpotCurrentPriceData> update = repository.findByVersion(version);
        return update.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get price updates by interval and version range
     */
    @GetMapping("/interval/{interval}/version-range")
    public ResponseEntity<List<WebSocketSpotCurrentPriceData>> getPriceUpdatesByVersionRange(
            @PathVariable String interval,
            @RequestParam Long minVersion,
            @RequestParam Long maxVersion) {
        
        List<WebSocketSpotCurrentPriceData> updates = 
            repository.findByIntervalAndVersionRange(interval, minVersion, maxVersion);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get latest version for an interval
     */
    @GetMapping("/interval/{interval}/latest-version")
    public ResponseEntity<Map<String, Object>> getLatestVersion(@PathVariable String interval) {
        Long latestVersion = repository.getLatestVersionByInterval(interval);
        Map<String, Object> response = new HashMap<>();
        response.put("interval", interval);
        response.put("latestVersion", latestVersion);
        return ResponseEntity.ok(response);
    }

    /**
     * Count price updates by interval
     */
    @GetMapping("/interval/{interval}/count")
    public ResponseEntity<Map<String, Object>> countPriceUpdatesByInterval(@PathVariable String interval) {
        long count = repository.countByPriceInterval(interval);
        Map<String, Object> response = new HashMap<>();
        response.put("interval", interval);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get price updates by channel name
     */
    @GetMapping("/channel/{channelName}")
    public ResponseEntity<List<WebSocketSpotCurrentPriceData>> getPriceUpdatesByChannel(@PathVariable String channelName) {
        List<WebSocketSpotCurrentPriceData> updates = 
            repository.findByChannelNameOrderByTimestampDesc(channelName);
        return ResponseEntity.ok(updates);
    }

    /**
     * Get price update by record ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotCurrentPriceData> getById(@PathVariable Long id) {
        Optional<WebSocketSpotCurrentPriceData> update = repository.findById(id);
        return update.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old price updates
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldPriceUpdates(@RequestParam int daysOld) {
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
     * Get recent price updates for a specific interval within time window
     */
    @GetMapping("/interval/{interval}/recent-by-time")
    public ResponseEntity<List<WebSocketSpotCurrentPriceData>> getRecentByInterval(
            @PathVariable String interval,
            @RequestParam Long fromTimestamp) {
        
        List<WebSocketSpotCurrentPriceData> updates = 
            repository.findRecentByInterval(interval, fromTimestamp);
        return ResponseEntity.ok(updates);
    }

    /**
     * Count price updates in time range
     */
    @GetMapping("/count-in-range")
    public ResponseEntity<Map<String, Object>> countInTimeRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        long count = repository.countByTimestampRange(startTime, endTime);
        Map<String, Object> response = new HashMap<>();
        response.put("startTime", startTime);
        response.put("endTime", endTime);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get price update frequency analysis
     */
    @GetMapping("/frequency-analysis")
    public ResponseEntity<Map<String, Object>> getFrequencyAnalysis() {
        List<Object[]> stats = repository.getUpdateFrequencyStatistics();
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Long> intervalCounts = new HashMap<>();
        long totalUpdates = 0;
        
        for (Object[] stat : stats) {
            String interval = (String) stat[0];
            Long count = (Long) stat[1];
            intervalCounts.put(interval, count);
            totalUpdates += count;
        }
        
        response.put("intervalCounts", intervalCounts);
        response.put("totalUpdates", totalUpdates);
        response.put("distinctIntervals", intervalCounts.size());
        
        return ResponseEntity.ok(response);
    }
}
