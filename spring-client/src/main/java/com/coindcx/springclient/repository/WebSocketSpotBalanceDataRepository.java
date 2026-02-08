package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotBalanceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Balance Data
 */
@Repository
public interface WebSocketSpotBalanceDataRepository extends JpaRepository<WebSocketSpotBalanceData, Long> {
    
    /**
     * Find all spot balance records for a specific currency, ordered by timestamp descending
     */
    List<WebSocketSpotBalanceData> findByCurrencyOrderByTimestampDesc(String currency);
    
    /**
     * Find the latest spot balance record for a specific currency
     */
    @Query("SELECT b FROM WebSocketSpotBalanceData b WHERE b.currency = :currency ORDER BY b.timestamp DESC LIMIT 1")
    Optional<WebSocketSpotBalanceData> findLatestByCurrency(@Param("currency") String currency);
    
    /**
     * Find all spot balance records for a specific user ID
     */
    List<WebSocketSpotBalanceData> findByUserIdOrderByTimestampDesc(String userId);
    
    /**
     * Find spot balance records within a time range
     */
    @Query("SELECT b FROM WebSocketSpotBalanceData b WHERE b.timestamp BETWEEN :startTime AND :endTime ORDER BY b.timestamp DESC")
    List<WebSocketSpotBalanceData> findByTimeRange(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find spot balance records for a currency within a time range
     */
    @Query("SELECT b FROM WebSocketSpotBalanceData b WHERE b.currency = :currency AND b.timestamp BETWEEN :startTime AND :endTime ORDER BY b.timestamp DESC")
    List<WebSocketSpotBalanceData> findByCurrencyAndTimeRange(
        @Param("currency") String currency,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find recent spot balance records (last N records)
     */
    @Query("SELECT b FROM WebSocketSpotBalanceData b ORDER BY b.timestamp DESC LIMIT :limit")
    List<WebSocketSpotBalanceData> findRecentRecords(@Param("limit") int limit);
    
    /**
     * Get all distinct currencies with spot balance data
     */
    @Query("SELECT DISTINCT b.currency FROM WebSocketSpotBalanceData b ORDER BY b.currency")
    List<String> findDistinctCurrencies();
    
    /**
     * Get latest spot balance for each currency
     */
    @Query("SELECT b FROM WebSocketSpotBalanceData b WHERE b.timestamp = " +
           "(SELECT MAX(b2.timestamp) FROM WebSocketSpotBalanceData b2 WHERE b2.currency = b.currency) " +
           "ORDER BY b.currency")
    List<WebSocketSpotBalanceData> findLatestBalanceForAllCurrencies();
    
    /**
     * Delete old spot balance records before a specific timestamp
     */
    void deleteByTimestampBefore(LocalDateTime timestamp);
    
    /**
     * Count total spot balance records
     */
    long count();
    
    /**
     * Count spot balance records for a specific currency
     */
    long countByCurrency(String currency);
}
