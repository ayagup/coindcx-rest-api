package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for API call log persistence
 */
@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

    /**
     * Find logs by service name
     */
    List<ApiCallLog> findByServiceName(String serviceName);

    /**
     * Find logs by service name and method name
     */
    List<ApiCallLog> findByServiceNameAndMethodName(String serviceName, String methodName);

    /**
     * Find logs by status
     */
    List<ApiCallLog> findByStatus(String status);

    /**
     * Find logs within a time range
     */
    List<ApiCallLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find logs by service name within a time range
     */
    List<ApiCallLog> findByServiceNameAndTimestampBetween(
            String serviceName, 
            LocalDateTime start, 
            LocalDateTime end
    );

    /**
     * Find failed API calls
     */
    @Query("SELECT a FROM ApiCallLog a WHERE a.status = 'FAILURE' OR a.status = 'ERROR' ORDER BY a.timestamp DESC")
    List<ApiCallLog> findFailedCalls();

    /**
     * Get statistics by service name
     */
    @Query("SELECT a.serviceName, COUNT(a), AVG(a.executionTimeMs) " +
           "FROM ApiCallLog a " +
           "GROUP BY a.serviceName")
    List<Object[]> getStatisticsByService();

    /**
     * Get recent logs for a service
     */
    @Query("SELECT a FROM ApiCallLog a WHERE a.serviceName = :serviceName ORDER BY a.timestamp DESC")
    List<ApiCallLog> findRecentByService(@Param("serviceName") String serviceName);

    /**
     * Count calls by status
     */
    @Query("SELECT a.status, COUNT(a) FROM ApiCallLog a GROUP BY a.status")
    List<Object[]> countByStatus();

    /**
     * Find slow API calls (execution time > threshold)
     */
    @Query("SELECT a FROM ApiCallLog a WHERE a.executionTimeMs > :threshold ORDER BY a.executionTimeMs DESC")
    List<ApiCallLog> findSlowCalls(@Param("threshold") Long threshold);

    /**
     * Delete old logs before a certain date
     */
    void deleteByTimestampBefore(LocalDateTime cutoffDate);
}
