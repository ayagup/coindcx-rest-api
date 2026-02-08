package com.coindcx.springclient.repository;

import com.coindcx.springclient.model.WebSocketFuturesCurrentPricesData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WebSocketFuturesCurrentPricesDataRepository extends JpaRepository<WebSocketFuturesCurrentPricesData, Long> {
    
    // Find all ordered by timestamp
    List<WebSocketFuturesCurrentPricesData> findAllByOrderByTimestampDesc();
    
    @Query(value = "SELECT * FROM websocket_futures_current_prices_data ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCurrentPricesData> findRecentPrices(@Param("limit") int limit);
    
    @Query(value = "SELECT * FROM websocket_futures_current_prices_data ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCurrentPricesData> findRecentByRecordTimestamp(@Param("limit") int limit);
    
    // Timestamp range queries
    @Query("SELECT p FROM WebSocketFuturesCurrentPricesData p WHERE p.timestamp >= :startTime AND p.timestamp <= :endTime ORDER BY p.timestamp DESC")
    List<WebSocketFuturesCurrentPricesData> findByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT p FROM WebSocketFuturesCurrentPricesData p WHERE p.recordTimestamp >= :startTime AND p.recordTimestamp <= :endTime ORDER BY p.recordTimestamp DESC")
    List<WebSocketFuturesCurrentPricesData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // Latest queries
    @Query(value = "SELECT * FROM websocket_futures_current_prices_data ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesCurrentPricesData> findLatestByTimestamp();
    
    @Query(value = "SELECT * FROM websocket_futures_current_prices_data ORDER BY record_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesCurrentPricesData> findLatestByRecordTimestamp();
    
    // Version sequence queries
    @Query("SELECT p FROM WebSocketFuturesCurrentPricesData p WHERE p.versionSequence >= :minVersion ORDER BY p.versionSequence DESC")
    List<WebSocketFuturesCurrentPricesData> findByVersionSequenceGreaterThanEqual(@Param("minVersion") Long minVersion);
    
    @Query(value = "SELECT * FROM websocket_futures_current_prices_data ORDER BY version_sequence DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesCurrentPricesData> findLatestByVersionSequence();
    
    @Query("SELECT p FROM WebSocketFuturesCurrentPricesData p WHERE p.versionSequence >= :minVersion AND p.versionSequence <= :maxVersion ORDER BY p.versionSequence DESC")
    List<WebSocketFuturesCurrentPricesData> findByVersionSequenceRange(@Param("minVersion") Long minVersion, @Param("maxVersion") Long maxVersion);
    
    // Product type queries
    List<WebSocketFuturesCurrentPricesData> findByProductTypeOrderByTimestampDesc(String productType);
    
    @Query(value = "SELECT * FROM websocket_futures_current_prices_data WHERE product_type = :productType ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCurrentPricesData> findByProductTypeWithLimit(@Param("productType") String productType, @Param("limit") int limit);
    
    // Statistics queries
    @Query("SELECT COUNT(p) FROM WebSocketFuturesCurrentPricesData p")
    long countAll();
    
    @Query("SELECT COUNT(p) FROM WebSocketFuturesCurrentPricesData p WHERE p.productType = :productType")
    long countByProductType(@Param("productType") String productType);
    
    @Query("SELECT MIN(p.timestamp) FROM WebSocketFuturesCurrentPricesData p")
    Long findOldestTimestamp();
    
    @Query("SELECT MAX(p.timestamp) FROM WebSocketFuturesCurrentPricesData p")
    Long findLatestTimestamp();
    
    @Query("SELECT MIN(p.versionSequence) FROM WebSocketFuturesCurrentPricesData p")
    Long findOldestVersionSequence();
    
    @Query("SELECT MAX(p.versionSequence) FROM WebSocketFuturesCurrentPricesData p")
    Long findLatestVersionSequence();
    
    @Query("SELECT COUNT(DISTINCT p.versionSequence) FROM WebSocketFuturesCurrentPricesData p")
    long countDistinctVersions();
    
    @Query("SELECT DISTINCT p.productType FROM WebSocketFuturesCurrentPricesData p ORDER BY p.productType")
    List<String> findDistinctProductTypes();
    
    // Cleanup
    void deleteByRecordTimestampBefore(LocalDateTime cutoffTime);
    
    @Query(value = "DELETE FROM websocket_futures_current_prices_data WHERE timestamp < :cutoffTime", nativeQuery = true)
    void deleteByTimestampBefore(@Param("cutoffTime") Long cutoffTime);
}
