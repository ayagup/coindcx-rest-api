package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotPriceStatsData;
import com.coindcx.springclient.repository.WebSocketSpotPriceStatsDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * REST Controller for WebSocket Spot Price Stats Data (24h market statistics)
 */
@RestController
@RequestMapping("/api/websocket/spot-price-stats")
@CrossOrigin(origins = "*")
public class WebSocketSpotPriceStatsController {

    private final WebSocketSpotPriceStatsDataRepository repository;

    public WebSocketSpotPriceStatsController(WebSocketSpotPriceStatsDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get statistics about price stats data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPriceStats", repository.count());
        
        List<String> intervals = repository.findDistinctIntervals();
        stats.put("intervals", intervals);
        
        // Get stats for each interval
        Map<String, Map<String, Object>> intervalStats = new HashMap<>();
        for (String interval : intervals) {
            Map<String, Object> intervalData = new HashMap<>();
            intervalData.put("updateCount", repository.countByStatsInterval(interval));
            intervalData.put("latestVersion", repository.getLatestVersionByInterval(interval));
            intervalStats.put(interval, intervalData);
        }
        stats.put("intervalStats", intervalStats);
        
        // Get market trends
        Long oneDayAgo = Instant.now().minusSeconds(86400).getEpochSecond();
        Double avgPriceChange = repository.getAveragePriceChange(oneDayAgo);
        Double avgVolume = repository.getAverageVolume(oneDayAgo);
        stats.put("avg24hPriceChange", avgPriceChange);
        stats.put("avg24hVolume", avgVolume);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get price stats by interval
     */
    @GetMapping("/interval/{interval}")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getPriceStatsByInterval(
            @PathVariable String interval,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotPriceStatsData> stats;
        if (limit != null && limit > 0) {
            stats = repository.findByIntervalWithLimit(interval, limit);
        } else {
            stats = repository.findByStatsIntervalOrderByTimestampDesc(interval);
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * Get latest price stats for an interval
     */
    @GetMapping("/interval/{interval}/latest")
    public ResponseEntity<WebSocketSpotPriceStatsData> getLatestPriceStats(@PathVariable String interval) {
        Optional<WebSocketSpotPriceStatsData> stats = 
            repository.findFirstByStatsIntervalOrderByTimestampDesc(interval);
        return stats.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get price stats within timestamp range
     */
    @GetMapping("/timestamp-range")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getPriceStatsByTimestampRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotPriceStatsData> stats = 
            repository.findByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get price stats by interval within timestamp range
     */
    @GetMapping("/interval/{interval}/timestamp-range")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getPriceStatsByIntervalAndTimestampRange(
            @PathVariable String interval,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotPriceStatsData> stats = 
            repository.findByIntervalAndTimestampRange(interval, startTime, endTime);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get price stats within record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getPriceStatsByRecordTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        
        List<WebSocketSpotPriceStatsData> stats = 
            repository.findByRecordTimestampRange(startTime, endTime);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent price stats
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getRecentPriceStats(@PathVariable int limit) {
        List<WebSocketSpotPriceStatsData> stats = repository.findRecentRecords(limit);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all distinct intervals
     */
    @GetMapping("/intervals")
    public ResponseEntity<List<String>> getDistinctIntervals() {
        List<String> intervals = repository.findDistinctIntervals();
        return ResponseEntity.ok(intervals);
    }

    /**
     * Get price stats by version
     */
    @GetMapping("/version/{version}")
    public ResponseEntity<WebSocketSpotPriceStatsData> getPriceStatsByVersion(@PathVariable Long version) {
        Optional<WebSocketSpotPriceStatsData> stats = repository.findByVersion(version);
        return stats.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get price stats by interval and version range
     */
    @GetMapping("/interval/{interval}/version-range")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getPriceStatsByIntervalAndVersionRange(
            @PathVariable String interval,
            @RequestParam Long minVersion,
            @RequestParam Long maxVersion) {
        
        List<WebSocketSpotPriceStatsData> stats = 
            repository.findByIntervalAndVersionRange(interval, minVersion, maxVersion);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get latest version for an interval
     */
    @GetMapping("/interval/{interval}/latest-version")
    public ResponseEntity<Long> getLatestVersion(@PathVariable String interval) {
        Long version = repository.getLatestVersionByInterval(interval);
        return ResponseEntity.ok(version);
    }

    /**
     * Get count of price stats by interval
     */
    @GetMapping("/interval/{interval}/count")
    public ResponseEntity<Long> getCountByInterval(@PathVariable String interval) {
        Long count = repository.countByStatsInterval(interval);
        return ResponseEntity.ok(count);
    }

    /**
     * Get price stats by channel name
     */
    @GetMapping("/channel/{channelName}")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getPriceStatsByChannelName(
            @PathVariable String channelName) {
        
        List<WebSocketSpotPriceStatsData> stats = 
            repository.findByChannelNameOrderByTimestampDesc(channelName);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get price stats by record ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotPriceStatsData> getPriceStatsById(@PathVariable Long id) {
        Optional<WebSocketSpotPriceStatsData> stats = repository.findById(id);
        return stats.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get top gainers (highest positive price change)
     */
    @GetMapping("/top-gainers/{limit}")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getTopGainers(@PathVariable int limit) {
        List<WebSocketSpotPriceStatsData> gainers = repository.findTopGainers(limit);
        return ResponseEntity.ok(gainers);
    }

    /**
     * Get top losers (highest negative price change)
     */
    @GetMapping("/top-losers/{limit}")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getTopLosers(@PathVariable int limit) {
        List<WebSocketSpotPriceStatsData> losers = repository.findTopLosers(limit);
        return ResponseEntity.ok(losers);
    }

    /**
     * Get price stats by price change range
     */
    @GetMapping("/price-change-range")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getPriceStatsByPriceChangeRange(
            @RequestParam Double minChange,
            @RequestParam Double maxChange) {
        
        List<WebSocketSpotPriceStatsData> stats = 
            repository.findByPriceChangeRange(minChange, maxChange);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get high volume pairs (volume above threshold)
     */
    @GetMapping("/high-volume/{threshold}/{limit}")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getHighVolumePairs(
            @PathVariable Double threshold,
            @PathVariable int limit) {
        
        List<WebSocketSpotPriceStatsData> pairs = 
            repository.findHighVolumePairs(threshold, limit);
        return ResponseEntity.ok(pairs);
    }

    /**
     * Get price stats by volume range
     */
    @GetMapping("/volume-range")
    public ResponseEntity<List<WebSocketSpotPriceStatsData>> getPriceStatsByVolumeRange(
            @RequestParam Double minVolume,
            @RequestParam Double maxVolume) {
        
        List<WebSocketSpotPriceStatsData> stats = 
            repository.findByVolumeRange(minVolume, maxVolume);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get average price change since specified time
     */
    @GetMapping("/average-price-change")
    public ResponseEntity<Double> getAveragePriceChange(@RequestParam Long fromTime) {
        Double avgChange = repository.getAveragePriceChange(fromTime);
        return ResponseEntity.ok(avgChange);
    }

    /**
     * Get average volume since specified time
     */
    @GetMapping("/average-volume")
    public ResponseEntity<Double> getAverageVolume(@RequestParam Long fromTime) {
        Double avgVolume = repository.getAverageVolume(fromTime);
        return ResponseEntity.ok(avgVolume);
    }

    /**
     * Get complete market analysis summary
     */
    @GetMapping("/market-summary")
    public ResponseEntity<Map<String, Object>> getMarketSummary(
            @RequestParam(defaultValue = "10") int topCount) {
        
        Map<String, Object> summary = new HashMap<>();
        
        // Get top gainers and losers
        summary.put("topGainers", repository.findTopGainers(topCount));
        summary.put("topLosers", repository.findTopLosers(topCount));
        
        // Get high volume pairs
        List<WebSocketSpotPriceStatsData> highVolume = repository.findHighVolumePairs(0.0, topCount);
        summary.put("highVolumePairs", highVolume);
        
        // Get average statistics
        Long oneDayAgo = Instant.now().minusSeconds(86400).getEpochSecond();
        Double avgPriceChange = repository.getAveragePriceChange(oneDayAgo);
        Double avgVolume = repository.getAverageVolume(oneDayAgo);
        summary.put("avg24hPriceChange", avgPriceChange);
        summary.put("avg24hVolume", avgVolume);
        
        // Get total count
        summary.put("totalPairs", repository.count());
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Get count of price stats within timestamp range
     */
    @GetMapping("/count-in-range")
    public ResponseEntity<Long> getCountInRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        Long count = repository.countByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(count);
    }

    /**
     * Delete old price stats data (cleanup)
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldData(@RequestParam LocalDateTime cutoffTime) {
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoffTime);
        long countAfter = repository.count();
        long deleted = countBefore - countAfter;
        
        Map<String, Object> result = new HashMap<>();
        result.put("deleted", deleted);
        result.put("remaining", countAfter);
        result.put("cutoffTime", cutoffTime);
        
        return ResponseEntity.ok(result);
    }
}
