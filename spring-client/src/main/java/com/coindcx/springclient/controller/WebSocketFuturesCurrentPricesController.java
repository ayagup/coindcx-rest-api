package com.coindcx.springclient.controller;

import com.coindcx.springclient.model.WebSocketFuturesCurrentPricesData;
import com.coindcx.springclient.repository.WebSocketFuturesCurrentPricesDataRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * REST API controller for Futures Current Prices WebSocket data
 * Channel: currentPrices@futures@rt
 * Event: currentPrices@futures#update
 */
@RestController
@RequestMapping("/api/websocket/futures-current-prices")
@CrossOrigin(origins = "*")
public class WebSocketFuturesCurrentPricesController {

    private final WebSocketFuturesCurrentPricesDataRepository repository;

    public WebSocketFuturesCurrentPricesController(WebSocketFuturesCurrentPricesDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all current prices snapshots
     */
    @GetMapping
    public ResponseEntity<List<WebSocketFuturesCurrentPricesData>> getAllPrices(
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(repository.findRecentPrices(limit));
    }

    /**
     * Get recent current prices snapshots
     */
    @GetMapping("/recent")
    public ResponseEntity<List<WebSocketFuturesCurrentPricesData>> getRecentPrices(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(repository.findRecentPrices(limit));
    }

    /**
     * Get recent current prices by record timestamp
     */
    @GetMapping("/recent/by-record-time")
    public ResponseEntity<List<WebSocketFuturesCurrentPricesData>> getRecentByRecordTime(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(repository.findRecentByRecordTimestamp(limit));
    }

    /**
     * Get latest current prices snapshot
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestPrices() {
        Optional<WebSocketFuturesCurrentPricesData> latest = repository.findLatestByTimestamp();
        if (latest.isPresent()) {
            return ResponseEntity.ok(latest.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No current prices data found");
    }

    /**
     * Get latest current prices by record timestamp
     */
    @GetMapping("/latest/by-record-time")
    public ResponseEntity<?> getLatestByRecordTime() {
        Optional<WebSocketFuturesCurrentPricesData> latest = repository.findLatestByRecordTimestamp();
        if (latest.isPresent()) {
            return ResponseEntity.ok(latest.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No current prices data found");
    }

    /**
     * Get current prices within a time range
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<WebSocketFuturesCurrentPricesData>> getPricesByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Long startTimestamp = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        Long endTimestamp = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        return ResponseEntity.ok(repository.findByTimestampRange(startTimestamp, endTimestamp));
    }

    /**
     * Get current prices within a record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketFuturesCurrentPricesData>> getPricesByRecordTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByRecordTimestampRange(startTime, endTime));
    }

    /**
     * Get current prices by version sequence
     */
    @GetMapping("/version/{minVersion}")
    public ResponseEntity<List<WebSocketFuturesCurrentPricesData>> getPricesByVersion(
            @PathVariable Long minVersion) {
        return ResponseEntity.ok(repository.findByVersionSequenceGreaterThanEqual(minVersion));
    }

    /**
     * Get latest version sequence
     */
    @GetMapping("/latest-version")
    public ResponseEntity<?> getLatestVersion() {
        Optional<WebSocketFuturesCurrentPricesData> latest = repository.findLatestByVersionSequence();
        if (latest.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("latestVersion", latest.get().getVersionSequence());
            response.put("timestamp", latest.get().getTimestamp());
            response.put("pricesSentTimestamp", latest.get().getPricesSentTimestamp());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No version data found");
    }

    /**
     * Get current prices by version range
     */
    @GetMapping("/version-range")
    public ResponseEntity<List<WebSocketFuturesCurrentPricesData>> getPricesByVersionRange(
            @RequestParam Long minVersion,
            @RequestParam Long maxVersion) {
        return ResponseEntity.ok(repository.findByVersionSequenceRange(minVersion, maxVersion));
    }

    /**
     * Get current prices by product type
     */
    @GetMapping("/product-type/{productType}")
    public ResponseEntity<List<WebSocketFuturesCurrentPricesData>> getPricesByProductType(
            @PathVariable String productType,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(repository.findByProductTypeWithLimit(productType, limit));
    }

    /**
     * Get all distinct product types
     */
    @GetMapping("/product-types")
    public ResponseEntity<List<String>> getDistinctProductTypes() {
        return ResponseEntity.ok(repository.findDistinctProductTypes());
    }

    /**
     * Get timestamp range
     */
    @GetMapping("/stats/timestamp-range")
    public ResponseEntity<?> getTimestampRange() {
        Long oldestTimestamp = repository.findOldestTimestamp();
        Long latestTimestamp = repository.findLatestTimestamp();
        
        if (oldestTimestamp == null || latestTimestamp == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No timestamp data found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("oldestTimestamp", oldestTimestamp);
        response.put("latestTimestamp", latestTimestamp);
        response.put("durationMs", latestTimestamp - oldestTimestamp);
        return ResponseEntity.ok(response);
    }

    /**
     * Get version sequence range
     */
    @GetMapping("/stats/version-range")
    public ResponseEntity<?> getVersionRange() {
        Long oldestVersion = repository.findOldestVersionSequence();
        Long latestVersion = repository.findLatestVersionSequence();
        
        if (oldestVersion == null || latestVersion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No version data found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("oldestVersion", oldestVersion);
        response.put("latestVersion", latestVersion);
        response.put("versionCount", repository.countDistinctVersions());
        return ResponseEntity.ok(response);
    }

    /**
     * Get comprehensive statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        long totalCount = repository.countAll();
        
        if (totalCount == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No current prices data found");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalRecords", totalCount);
        response.put("productTypes", repository.findDistinctProductTypes());
        response.put("distinctVersions", repository.countDistinctVersions());
        
        Long oldestTimestamp = repository.findOldestTimestamp();
        Long latestTimestamp = repository.findLatestTimestamp();
        if (oldestTimestamp != null && latestTimestamp != null) {
            response.put("oldestTimestamp", oldestTimestamp);
            response.put("latestTimestamp", latestTimestamp);
            response.put("durationMs", latestTimestamp - oldestTimestamp);
        }
        
        Long oldestVersion = repository.findOldestVersionSequence();
        Long latestVersion = repository.findLatestVersionSequence();
        if (oldestVersion != null && latestVersion != null) {
            response.put("oldestVersion", oldestVersion);
            response.put("latestVersion", latestVersion);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get count by product type
     */
    @GetMapping("/count/product-type/{productType}")
    public ResponseEntity<Long> getCountByProductType(@PathVariable String productType) {
        return ResponseEntity.ok(repository.countByProductType(productType));
    }

    /**
     * Delete old records (cleanup)
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<?> deleteOldRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(before);
        long countAfter = repository.count();
        long deleted = countBefore - countAfter;
        
        Map<String, Object> response = new HashMap<>();
        response.put("recordsDeleted", deleted);
        response.put("recordsRemaining", countAfter);
        return ResponseEntity.ok(response);
    }
}
