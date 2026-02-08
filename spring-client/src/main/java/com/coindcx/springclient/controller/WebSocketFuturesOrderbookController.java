package com.coindcx.springclient.controller;

import com.coindcx.springclient.model.WebSocketFuturesOrderbookData;
import com.coindcx.springclient.repository.WebSocketFuturesOrderbookDataRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for WebSocket Futures Orderbook Data
 * Provides endpoints to access orderbook snapshot data from WebSocket streams
 * 
 * Channel format: [instrument_name]@orderbook@{depth}-futures
 * Example: B-ID_USDT@orderbook@50-futures
 * 
 * Supported depths: 10, 20, 50 levels
 */
@RestController
@RequestMapping("/api/websocket/futures-orderbook")
@CrossOrigin(origins = "http://localhost:3000")
public class WebSocketFuturesOrderbookController {

    private final WebSocketFuturesOrderbookDataRepository repository;

    public WebSocketFuturesOrderbookController(WebSocketFuturesOrderbookDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all orderbook snapshots
     */
    @GetMapping
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getAllOrderbooks(
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(repository.findRecentOrderbooks(limit));
    }

    /**
     * Get orderbook snapshots by channel name
     */
    @GetMapping("/channel/{channelName}")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByChannel(
            @PathVariable String channelName,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(repository.findByChannelNameWithLimit(channelName, limit));
    }

    /**
     * Get orderbook snapshots by instrument name
     */
    @GetMapping("/instrument/{instrumentName}")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByInstrument(
            @PathVariable String instrumentName,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(repository.findByInstrumentNameWithLimit(instrumentName, limit));
    }

    /**
     * Get orderbook snapshots by depth level
     */
    @GetMapping("/depth/{depth}")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByDepth(
            @PathVariable Integer depth,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(repository.findByDepthWithLimit(depth, limit));
    }

    /**
     * Get orderbook snapshots by instrument and depth
     */
    @GetMapping("/instrument/{instrumentName}/depth/{depth}")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByInstrumentAndDepth(
            @PathVariable String instrumentName,
            @PathVariable Integer depth,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(repository.findByInstrumentNameAndDepthWithLimit(instrumentName, depth, limit));
    }

    /**
     * Get recent orderbook snapshots
     */
    @GetMapping("/recent")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getRecentOrderbooks(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(repository.findRecentOrderbooks(limit));
    }

    /**
     * Get recent orderbook snapshots by record timestamp
     */
    @GetMapping("/recent/by-record-time")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getRecentOrderbooksByRecordTime(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(repository.findRecentByRecordTimestamp(limit));
    }

    /**
     * Get latest orderbook snapshot for a specific channel
     */
    @GetMapping("/latest/channel/{channelName}")
    public ResponseEntity<?> getLatestOrderbookByChannel(@PathVariable String channelName) {
        Optional<WebSocketFuturesOrderbookData> latest = repository.findLatestByChannelName(channelName);
        if (latest.isPresent()) {
            return ResponseEntity.ok(latest.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No orderbook data found for channel: " + channelName);
    }

    /**
     * Get latest orderbook snapshot for a specific instrument
     */
    @GetMapping("/latest/instrument/{instrumentName}")
    public ResponseEntity<?> getLatestOrderbookByInstrument(@PathVariable String instrumentName) {
        Optional<WebSocketFuturesOrderbookData> latest = repository.findLatestByInstrumentName(instrumentName);
        if (latest.isPresent()) {
            return ResponseEntity.ok(latest.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No orderbook data found for instrument: " + instrumentName);
    }

    /**
     * Get latest orderbook snapshot for a specific instrument and depth
     */
    @GetMapping("/latest/instrument/{instrumentName}/depth/{depth}")
    public ResponseEntity<?> getLatestOrderbookByInstrumentAndDepth(
            @PathVariable String instrumentName,
            @PathVariable Integer depth) {
        Optional<WebSocketFuturesOrderbookData> latest = 
                repository.findLatestByInstrumentNameAndDepth(instrumentName, depth);
        if (latest.isPresent()) {
            return ResponseEntity.ok(latest.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No orderbook data found for instrument " + instrumentName + " at depth " + depth);
    }

    /**
     * Get orderbook snapshots within a time range
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Long startTimestamp = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        Long endTimestamp = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        return ResponseEntity.ok(repository.findByTimestampRange(startTimestamp, endTimestamp));
    }

    /**
     * Get orderbook snapshots for a channel within a time range
     */
    @GetMapping("/channel/{channelName}/time-range")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByChannelAndTimeRange(
            @PathVariable String channelName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Long startTimestamp = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        Long endTimestamp = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        return ResponseEntity.ok(repository.findByChannelNameAndTimestampRange(
                channelName, startTimestamp, endTimestamp));
    }

    /**
     * Get orderbook snapshots for an instrument within a time range
     */
    @GetMapping("/instrument/{instrumentName}/time-range")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByInstrumentAndTimeRange(
            @PathVariable String instrumentName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Long startTimestamp = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        Long endTimestamp = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        return ResponseEntity.ok(repository.findByInstrumentNameAndTimestampRange(
                instrumentName, startTimestamp, endTimestamp));
    }

    /**
     * Get orderbook snapshots within a record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByRecordTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByRecordTimestampRange(startTime, endTime));
    }

    /**
     * Get orderbook snapshots by version sequence
     */
    @GetMapping("/version/{minVersion}")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByVersion(
            @PathVariable Long minVersion) {
        return ResponseEntity.ok(repository.findByVersionSequenceGreaterThanEqual(minVersion));
    }

    /**
     * Get orderbook snapshots by channel and version sequence
     */
    @GetMapping("/channel/{channelName}/version/{minVersion}")
    public ResponseEntity<List<WebSocketFuturesOrderbookData>> getOrderbooksByChannelAndVersion(
            @PathVariable String channelName,
            @PathVariable Long minVersion) {
        return ResponseEntity.ok(repository.findByChannelNameAndVersionSequence(channelName, minVersion));
    }

    /**
     * Get latest version sequence for a channel
     */
    @GetMapping("/channel/{channelName}/latest-version")
    public ResponseEntity<?> getLatestVersionByChannel(@PathVariable String channelName) {
        Optional<WebSocketFuturesOrderbookData> latest = repository.findLatestVersionByChannelName(channelName);
        if (latest.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("channelName", channelName);
            response.put("latestVersion", latest.get().getVersionSequence());
            response.put("timestamp", latest.get().getTimestamp());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No version data found for channel: " + channelName);
    }

    /**
     * Get all distinct channel names
     */
    @GetMapping("/channels")
    public ResponseEntity<List<String>> getDistinctChannels() {
        return ResponseEntity.ok(repository.findDistinctChannelNames());
    }

    /**
     * Get all distinct instrument names
     */
    @GetMapping("/instruments")
    public ResponseEntity<List<String>> getDistinctInstruments() {
        return ResponseEntity.ok(repository.findDistinctInstrumentNames());
    }

    /**
     * Get all distinct depth levels
     */
    @GetMapping("/depths")
    public ResponseEntity<List<Integer>> getDistinctDepths() {
        return ResponseEntity.ok(repository.findDistinctDepths());
    }

    /**
     * Get count of orderbook snapshots by channel
     */
    @GetMapping("/channel/{channelName}/count")
    public ResponseEntity<Map<String, Object>> getCountByChannel(@PathVariable String channelName) {
        long count = repository.countByChannelName(channelName);
        Map<String, Object> response = new HashMap<>();
        response.put("channelName", channelName);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count of orderbook snapshots by instrument
     */
    @GetMapping("/instrument/{instrumentName}/count")
    public ResponseEntity<Map<String, Object>> getCountByInstrument(@PathVariable String instrumentName) {
        long count = repository.countByInstrumentName(instrumentName);
        Map<String, Object> response = new HashMap<>();
        response.put("instrumentName", instrumentName);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count of orderbook snapshots by depth
     */
    @GetMapping("/depth/{depth}/count")
    public ResponseEntity<Map<String, Object>> getCountByDepth(@PathVariable Integer depth) {
        long count = repository.countByDepth(depth);
        Map<String, Object> response = new HashMap<>();
        response.put("depth", depth);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count of orderbook snapshots by instrument and depth
     */
    @GetMapping("/instrument/{instrumentName}/depth/{depth}/count")
    public ResponseEntity<Map<String, Object>> getCountByInstrumentAndDepth(
            @PathVariable String instrumentName,
            @PathVariable Integer depth) {
        long count = repository.countByInstrumentNameAndDepth(instrumentName, depth);
        Map<String, Object> response = new HashMap<>();
        response.put("instrumentName", instrumentName);
        response.put("depth", depth);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count of distinct channels
     */
    @GetMapping("/stats/distinct-channels")
    public ResponseEntity<Map<String, Object>> getDistinctChannelCount() {
        long count = repository.countDistinctChannels();
        Map<String, Object> response = new HashMap<>();
        response.put("distinctChannels", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count of distinct instruments
     */
    @GetMapping("/stats/distinct-instruments")
    public ResponseEntity<Map<String, Object>> getDistinctInstrumentCount() {
        long count = repository.countDistinctInstruments();
        Map<String, Object> response = new HashMap<>();
        response.put("distinctInstruments", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get timestamp range (oldest and latest)
     */
    @GetMapping("/stats/timestamp-range")
    public ResponseEntity<Map<String, Object>> getTimestampRange() {
        Long oldest = repository.findOldestTimestamp();
        Long latest = repository.findLatestTimestamp();
        
        Map<String, Object> response = new HashMap<>();
        response.put("oldestTimestamp", oldest);
        response.put("latestTimestamp", latest);
        
        if (oldest != null && latest != null) {
            response.put("rangeMilliseconds", latest - oldest);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get timestamp range for a specific channel
     */
    @GetMapping("/channel/{channelName}/timestamp-range")
    public ResponseEntity<Map<String, Object>> getTimestampRangeByChannel(@PathVariable String channelName) {
        Long oldest = repository.findOldestTimestampByChannel(channelName);
        Long latest = repository.findLatestTimestampByChannel(channelName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("channelName", channelName);
        response.put("oldestTimestamp", oldest);
        response.put("latestTimestamp", latest);
        
        if (oldest != null && latest != null) {
            response.put("rangeMilliseconds", latest - oldest);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get comprehensive statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalRecords", repository.count());
        stats.put("distinctChannels", repository.countDistinctChannels());
        stats.put("distinctInstruments", repository.countDistinctInstruments());
        
        Long oldest = repository.findOldestTimestamp();
        Long latest = repository.findLatestTimestamp();
        stats.put("oldestTimestamp", oldest);
        stats.put("latestTimestamp", latest);
        
        if (oldest != null && latest != null) {
            stats.put("rangeMilliseconds", latest - oldest);
        }
        
        // Depth level statistics
        List<Integer> depths = repository.findDistinctDepths();
        Map<Integer, Long> depthCounts = new HashMap<>();
        for (Integer depth : depths) {
            depthCounts.put(depth, repository.countByDepth(depth));
        }
        stats.put("depthCounts", depthCounts);
        
        // Channel statistics
        List<String> channels = repository.findDistinctChannelNames();
        stats.put("channels", channels);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Delete old orderbook records
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldRecords(
            @RequestParam(defaultValue = "7") int daysToKeep) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoffTime);
        long countAfter = repository.count();
        long deleted = countBefore - countAfter;
        
        Map<String, Object> response = new HashMap<>();
        response.put("recordsDeleted", deleted);
        response.put("cutoffTime", cutoffTime);
        response.put("remainingRecords", countAfter);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete old orderbook records for a specific channel
     */
    @DeleteMapping("/channel/{channelName}/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldRecordsByChannel(
            @PathVariable String channelName,
            @RequestParam(defaultValue = "7") int daysToKeep) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
        Long cutoffTimestamp = cutoffTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        
        long countBefore = repository.countByChannelName(channelName);
        repository.deleteByChannelNameAndTimestampBefore(channelName, cutoffTimestamp);
        long countAfter = repository.countByChannelName(channelName);
        long deleted = countBefore - countAfter;
        
        Map<String, Object> response = new HashMap<>();
        response.put("channelName", channelName);
        response.put("recordsDeleted", deleted);
        response.put("cutoffTime", cutoffTime);
        response.put("remainingRecords", countAfter);
        
        return ResponseEntity.ok(response);
    }
}
