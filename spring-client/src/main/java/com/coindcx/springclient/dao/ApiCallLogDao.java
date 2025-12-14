package com.coindcx.springclient.dao;

import com.coindcx.springclient.entity.ApiCallLog;
import com.coindcx.springclient.repository.ApiCallLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DAO layer for API call log operations
 */
@Component
public class ApiCallLogDao {

    private final ApiCallLogRepository repository;

    @Autowired
    public ApiCallLogDao(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Save an API call log
     */
    @Transactional
    public ApiCallLog save(ApiCallLog apiCallLog) {
        return repository.save(apiCallLog);
    }

    /**
     * Save all API call logs
     */
    @Transactional
    public List<ApiCallLog> saveAll(List<ApiCallLog> logs) {
        return repository.saveAll(logs);
    }

    /**
     * Find log by ID
     */
    public Optional<ApiCallLog> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Find all logs
     */
    public List<ApiCallLog> findAll() {
        return repository.findAll();
    }

    /**
     * Find logs by service name
     */
    public List<ApiCallLog> findByServiceName(String serviceName) {
        return repository.findByServiceName(serviceName);
    }

    /**
     * Find logs by service and method
     */
    public List<ApiCallLog> findByServiceAndMethod(String serviceName, String methodName) {
        return repository.findByServiceNameAndMethodName(serviceName, methodName);
    }

    /**
     * Find logs by status
     */
    public List<ApiCallLog> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    /**
     * Find logs within time range
     */
    public List<ApiCallLog> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByTimestampBetween(start, end);
    }

    /**
     * Find logs by service within time range
     */
    public List<ApiCallLog> findByServiceAndTimeRange(
            String serviceName, 
            LocalDateTime start, 
            LocalDateTime end
    ) {
        return repository.findByServiceNameAndTimestampBetween(serviceName, start, end);
    }

    /**
     * Find failed API calls
     */
    public List<ApiCallLog> findFailedCalls() {
        return repository.findFailedCalls();
    }

    /**
     * Get statistics by service
     */
    public List<Object[]> getStatisticsByService() {
        return repository.getStatisticsByService();
    }

    /**
     * Get recent logs for a service
     */
    public List<ApiCallLog> findRecentByService(String serviceName) {
        return repository.findRecentByService(serviceName);
    }

    /**
     * Count calls by status
     */
    public List<Object[]> countByStatus() {
        return repository.countByStatus();
    }

    /**
     * Find slow API calls
     */
    public List<ApiCallLog> findSlowCalls(Long thresholdMs) {
        return repository.findSlowCalls(thresholdMs);
    }

    /**
     * Delete log by ID
     */
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    /**
     * Delete old logs
     */
    @Transactional
    public void deleteOldLogs(LocalDateTime cutoffDate) {
        repository.deleteByTimestampBefore(cutoffDate);
    }

    /**
     * Count all logs
     */
    public long count() {
        return repository.count();
    }

    /**
     * Check if log exists
     */
    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    /**
     * Delete all logs
     */
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }
}
