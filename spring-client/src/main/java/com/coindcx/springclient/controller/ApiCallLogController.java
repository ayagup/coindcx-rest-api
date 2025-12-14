package com.coindcx.springclient.controller;

import com.coindcx.springclient.dto.ApiCallStatistics;
import com.coindcx.springclient.entity.ApiCallLog;
import com.coindcx.springclient.service.ApiCallLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for API call log management
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "API Monitoring", description = "APIs for monitoring and analyzing API call logs and statistics")
public class ApiCallLogController {

    private final ApiCallLogService apiCallLogService;

    @Autowired
    public ApiCallLogController(ApiCallLogService apiCallLogService) {
        this.apiCallLogService = apiCallLogService;
    }

    /**
     * Get all API call logs
     * Example: GET /api/logs
     */
    @GetMapping
    public ResponseEntity<List<ApiCallLog>> getAllLogs() {
        List<ApiCallLog> logs = apiCallLogService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs by service name
     * Example: GET /api/logs/service/OrderService
     */
    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<ApiCallLog>> getLogsByService(@PathVariable String serviceName) {
        List<ApiCallLog> logs = apiCallLogService.getLogsByService(serviceName);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs by service and method
     * Example: GET /api/logs/service/OrderService/method/createOrder
     */
    @GetMapping("/service/{serviceName}/method/{methodName}")
    public ResponseEntity<List<ApiCallLog>> getLogsByServiceAndMethod(
            @PathVariable String serviceName,
            @PathVariable String methodName) {
        List<ApiCallLog> logs = apiCallLogService.getLogsByServiceAndMethod(serviceName, methodName);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs within time range
     * Example: GET /api/logs/range?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
     */
    @GetMapping("/range")
    public ResponseEntity<List<ApiCallLog>> getLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ApiCallLog> logs = apiCallLogService.getLogsByTimeRange(start, end);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get failed API calls
     * Example: GET /api/logs/failed
     */
    @GetMapping("/failed")
    public ResponseEntity<List<ApiCallLog>> getFailedCalls() {
        List<ApiCallLog> logs = apiCallLogService.getFailedCalls();
        return ResponseEntity.ok(logs);
    }

    /**
     * Get slow API calls (over threshold in milliseconds)
     * Example: GET /api/logs/slow?threshold=5000
     */
    @GetMapping("/slow")
    public ResponseEntity<List<ApiCallLog>> getSlowCalls(@RequestParam Long threshold) {
        List<ApiCallLog> logs = apiCallLogService.getSlowCalls(threshold);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get statistics for all services
     * Example: GET /api/logs/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<List<ApiCallStatistics>> getStatistics() {
        List<ApiCallStatistics> statistics = apiCallLogService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get statistics for a specific service
     * Example: GET /api/logs/statistics/OrderService
     */
    @GetMapping("/statistics/{serviceName}")
    public ResponseEntity<ApiCallStatistics> getStatisticsForService(@PathVariable String serviceName) {
        ApiCallStatistics statistics = apiCallLogService.getStatisticsForService(serviceName);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get recent logs
     * Example: GET /api/logs/recent?limit=100
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ApiCallLog>> getRecentLogs(@RequestParam(defaultValue = "50") int limit) {
        List<ApiCallLog> logs = apiCallLogService.getRecentLogs(limit);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get count by status
     * Example: GET /api/logs/count/status
     */
    @GetMapping("/count/status")
    public ResponseEntity<Map<String, Long>> getCountByStatus() {
        Map<String, Long> counts = apiCallLogService.getCountByStatus();
        return ResponseEntity.ok(counts);
    }

    /**
     * Get total count
     * Example: GET /api/logs/count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCount() {
        long count = apiCallLogService.getTotalCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Clean up old logs
     * Example: DELETE /api/logs/cleanup?days=30
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanupOldLogs(@RequestParam(defaultValue = "30") int days) {
        apiCallLogService.cleanupOldLogs(days);
        return ResponseEntity.ok("Cleaned up logs older than " + days + " days");
    }
}
