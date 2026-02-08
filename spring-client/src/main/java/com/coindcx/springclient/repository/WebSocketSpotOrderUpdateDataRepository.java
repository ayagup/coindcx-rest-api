package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotOrderUpdateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Order Update Data
 */
@Repository
public interface WebSocketSpotOrderUpdateDataRepository extends JpaRepository<WebSocketSpotOrderUpdateData, Long> {
    
    /**
     * Find order update by order ID
     */
    Optional<WebSocketSpotOrderUpdateData> findByOrderId(String orderId);
    
    /**
     * Find all order updates by order ID, ordered by timestamp descending
     */
    List<WebSocketSpotOrderUpdateData> findByOrderIdOrderByTimestampDesc(String orderId);
    
    /**
     * Find order updates by client order ID
     */
    List<WebSocketSpotOrderUpdateData> findByClientOrderIdOrderByTimestampDesc(String clientOrderId);
    
    /**
     * Find order updates by status
     */
    List<WebSocketSpotOrderUpdateData> findByStatusOrderByTimestampDesc(String status);
    
    /**
     * Find order updates by market
     */
    List<WebSocketSpotOrderUpdateData> findByMarketOrderByTimestampDesc(String market);
    
    /**
     * Find order updates by side (buy/sell)
     */
    List<WebSocketSpotOrderUpdateData> findBySideOrderByTimestampDesc(String side);
    
    /**
     * Find order updates by market and status
     */
    @Query("SELECT o FROM WebSocketSpotOrderUpdateData o WHERE o.market = :market AND o.status = :status ORDER BY o.timestamp DESC")
    List<WebSocketSpotOrderUpdateData> findByMarketAndStatus(
        @Param("market") String market,
        @Param("status") String status
    );
    
    /**
     * Find order updates within a time range
     */
    @Query("SELECT o FROM WebSocketSpotOrderUpdateData o WHERE o.timestamp BETWEEN :startTime AND :endTime ORDER BY o.timestamp DESC")
    List<WebSocketSpotOrderUpdateData> findByTimeRange(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find order updates for a market within a time range
     */
    @Query("SELECT o FROM WebSocketSpotOrderUpdateData o WHERE o.market = :market AND o.timestamp BETWEEN :startTime AND :endTime ORDER BY o.timestamp DESC")
    List<WebSocketSpotOrderUpdateData> findByMarketAndTimeRange(
        @Param("market") String market,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find recent order updates (last N records)
     */
    @Query("SELECT o FROM WebSocketSpotOrderUpdateData o ORDER BY o.timestamp DESC LIMIT :limit")
    List<WebSocketSpotOrderUpdateData> findRecentRecords(@Param("limit") int limit);
    
    /**
     * Get all distinct markets with order updates
     */
    @Query("SELECT DISTINCT o.market FROM WebSocketSpotOrderUpdateData o ORDER BY o.market")
    List<String> findDistinctMarkets();
    
    /**
     * Get all distinct statuses
     */
    @Query("SELECT DISTINCT o.status FROM WebSocketSpotOrderUpdateData o ORDER BY o.status")
    List<String> findDistinctStatuses();
    
    /**
     * Find latest order update for each order ID
     */
    @Query("SELECT o FROM WebSocketSpotOrderUpdateData o WHERE o.timestamp = " +
           "(SELECT MAX(o2.timestamp) FROM WebSocketSpotOrderUpdateData o2 WHERE o2.orderId = o.orderId) " +
           "ORDER BY o.timestamp DESC")
    List<WebSocketSpotOrderUpdateData> findLatestOrderUpdates();
    
    /**
     * Find active orders (not filled, cancelled, or rejected)
     */
    @Query("SELECT o FROM WebSocketSpotOrderUpdateData o WHERE o.status NOT IN ('filled', 'cancelled', 'rejected') " +
           "AND o.timestamp = (SELECT MAX(o2.timestamp) FROM WebSocketSpotOrderUpdateData o2 WHERE o2.orderId = o.orderId) " +
           "ORDER BY o.timestamp DESC")
    List<WebSocketSpotOrderUpdateData> findActiveOrders();
    
    /**
     * Delete old order update records before a specific timestamp
     */
    void deleteByTimestampBefore(LocalDateTime timestamp);
    
    /**
     * Count total order update records
     */
    long count();
    
    /**
     * Count order updates for a specific market
     */
    long countByMarket(String market);
    
    /**
     * Count order updates by status
     */
    long countByStatus(String status);
}
