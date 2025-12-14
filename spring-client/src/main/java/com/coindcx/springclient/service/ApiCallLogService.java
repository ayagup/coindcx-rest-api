package com.coindcx.springclient.service;

import com.coindcx.springclient.dao.ApiCallLogDao;
import com.coindcx.springclient.dto.ApiCallStatistics;
import com.coindcx.springclient.entity.ApiCallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing and querying API call logs
 */
@Service
public class ApiCallLogService {

    private final ApiCallLogDao apiCallLogDao;

    @Autowired
    public ApiCallLogService(ApiCallLogDao apiCallLogDao) {
        this.apiCallLogDao = apiCallLogDao;
    }

    /**
     * Get all API call logs
     */
    public List<ApiCallLog> getAllLogs() {
        return apiCallLogDao.findAll();
    }

    /**
     * Get logs by service name
     */
    public List<ApiCallLog> getLogsByService(String serviceName) {
        return apiCallLogDao.findByServiceName(serviceName);
    }

    /**
     * Get logs by service and method
     */
    public List<ApiCallLog> getLogsByServiceAndMethod(String serviceName, String methodName) {
        return apiCallLogDao.findByServiceAndMethod(serviceName, methodName);
    }

    /**
     * Get logs within time range
     */
    public List<ApiCallLog> getLogsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return apiCallLogDao.findByTimeRange(start, end);
    }

    /**
     * Get failed API calls
     */
    public List<ApiCallLog> getFailedCalls() {
        return apiCallLogDao.findFailedCalls();
    }

    /**
     * Get slow API calls (over threshold)
     */
    public List<ApiCallLog> getSlowCalls(Long thresholdMs) {
        return apiCallLogDao.findSlowCalls(thresholdMs);
    }

    /**
     * Get statistics for all services
     */
    public List<ApiCallStatistics> getStatistics() {
        Map<String, ApiCallStatistics> statsMap = new HashMap<>();
        
        List<ApiCallLog> allLogs = apiCallLogDao.findAll();
        
        for (ApiCallLog log : allLogs) {
            String serviceName = log.getServiceName();
            
            ApiCallStatistics stats = statsMap.computeIfAbsent(serviceName, k -> {
                ApiCallStatistics s = new ApiCallStatistics();
                s.setServiceName(serviceName);
                s.setTotalCalls(0L);
                s.setSuccessfulCalls(0L);
                s.setFailedCalls(0L);
                s.setAverageExecutionTimeMs(0.0);
                return s;
            });
            
            stats.setTotalCalls(stats.getTotalCalls() + 1);
            
            if ("SUCCESS".equals(log.getStatus())) {
                stats.setSuccessfulCalls(stats.getSuccessfulCalls() + 1);
            } else {
                stats.setFailedCalls(stats.getFailedCalls() + 1);
            }
        }
        
        // Calculate average execution times
        for (ApiCallStatistics stats : statsMap.values()) {
            List<ApiCallLog> serviceLogs = allLogs.stream()
                .filter(log -> log.getServiceName().equals(stats.getServiceName()))
                .collect(Collectors.toList());
            
            double avgTime = serviceLogs.stream()
                .filter(log -> log.getExecutionTimeMs() != null)
                .mapToLong(ApiCallLog::getExecutionTimeMs)
                .average()
                .orElse(0.0);
            
            stats.setAverageExecutionTimeMs(avgTime);
        }
        
        return new ArrayList<>(statsMap.values());
    }

    /**
     * Get statistics for a specific service
     */
    public ApiCallStatistics getStatisticsForService(String serviceName) {
        List<ApiCallLog> logs = apiCallLogDao.findByServiceName(serviceName);
        
        ApiCallStatistics stats = new ApiCallStatistics();
        stats.setServiceName(serviceName);
        stats.setTotalCalls((long) logs.size());
        
        long successCount = logs.stream()
            .filter(log -> "SUCCESS".equals(log.getStatus()))
            .count();
        stats.setSuccessfulCalls(successCount);
        stats.setFailedCalls(stats.getTotalCalls() - successCount);
        
        double avgTime = logs.stream()
            .filter(log -> log.getExecutionTimeMs() != null)
            .mapToLong(ApiCallLog::getExecutionTimeMs)
            .average()
            .orElse(0.0);
        stats.setAverageExecutionTimeMs(avgTime);
        
        OptionalLong minTime = logs.stream()
            .filter(log -> log.getExecutionTimeMs() != null)
            .mapToLong(ApiCallLog::getExecutionTimeMs)
            .min();
        if (minTime.isPresent()) {
            stats.setMinExecutionTimeMs(minTime.getAsLong());
        }
        
        OptionalLong maxTime = logs.stream()
            .filter(log -> log.getExecutionTimeMs() != null)
            .mapToLong(ApiCallLog::getExecutionTimeMs)
            .max();
        if (maxTime.isPresent()) {
            stats.setMaxExecutionTimeMs(maxTime.getAsLong());
        }
        
        return stats;
    }

    /**
     * Get recent logs (last N entries)
     */
    public List<ApiCallLog> getRecentLogs(int limit) {
        List<ApiCallLog> allLogs = apiCallLogDao.findAll();
        return allLogs.stream()
            .sorted(Comparator.comparing(ApiCallLog::getTimestamp).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * Clean up old logs (older than specified days)
     */
    public void cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        apiCallLogDao.deleteOldLogs(cutoffDate);
    }

    /**
     * Get total count of logs
     */
    public long getTotalCount() {
        return apiCallLogDao.count();
    }

    /**
     * Get count by status
     */
    public Map<String, Long> getCountByStatus() {
        List<Object[]> results = apiCallLogDao.countByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        
        for (Object[] result : results) {
            String status = (String) result[0];
            Long count = (Long) result[1];
            statusMap.put(status, count);
        }
        
        return statusMap;
    }
}
