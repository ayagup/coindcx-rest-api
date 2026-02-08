package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketFuturesOrderUpdateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Futures Order Update Data
 */
@Repository
public interface WebSocketFuturesOrderUpdateDataRepository extends JpaRepository<WebSocketFuturesOrderUpdateData, Long> {

    /**
     * Find order updates by order ID
     */
    List<WebSocketFuturesOrderUpdateData> findByOrderIdOrderByUpdatedAtDesc(String orderId);

    /**
     * Find order updates by order ID with limit
     */
    @Query(value = "SELECT * FROM websocket_futures_order_update_data WHERE order_id = :orderId ORDER BY updated_at DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderUpdateData> findByOrderIdWithLimit(@Param("orderId") String orderId, @Param("limit") int limit);

    /**
     * Find latest order update by order ID
     */
    Optional<WebSocketFuturesOrderUpdateData> findFirstByOrderIdOrderByUpdatedAtDesc(String orderId);

    /**
     * Find order updates by pair
     */
    List<WebSocketFuturesOrderUpdateData> findByPairOrderByUpdatedAtDesc(String pair);

    /**
     * Find order updates by pair with limit
     */
    @Query(value = "SELECT * FROM websocket_futures_order_update_data WHERE pair = :pair ORDER BY updated_at DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderUpdateData> findByPairWithLimit(@Param("pair") String pair, @Param("limit") int limit);

    /**
     * Find order updates by side
     */
    List<WebSocketFuturesOrderUpdateData> findBySideOrderByUpdatedAtDesc(String side);

    /**
     * Find order updates by status
     */
    List<WebSocketFuturesOrderUpdateData> findByStatusOrderByUpdatedAtDesc(String status);

    /**
     * Find order updates by order type
     */
    List<WebSocketFuturesOrderUpdateData> findByOrderTypeOrderByUpdatedAtDesc(String orderType);

    /**
     * Find order updates by order category
     */
    List<WebSocketFuturesOrderUpdateData> findByOrderCategoryOrderByUpdatedAtDesc(String orderCategory);

    /**
     * Find order updates by pair and side
     */
    List<WebSocketFuturesOrderUpdateData> findByPairAndSideOrderByUpdatedAtDesc(String pair, String side);

    /**
     * Find order updates by pair and status
     */
    List<WebSocketFuturesOrderUpdateData> findByPairAndStatusOrderByUpdatedAtDesc(String pair, String status);

    /**
     * Find order updates by status and order type
     */
    List<WebSocketFuturesOrderUpdateData> findByStatusAndOrderTypeOrderByUpdatedAtDesc(String status, String orderType);

    /**
     * Find order updates within created timestamp range
     */
    @Query("SELECT o FROM WebSocketFuturesOrderUpdateData o WHERE o.createdAt >= :startTime AND o.createdAt <= :endTime ORDER BY o.createdAt DESC")
    List<WebSocketFuturesOrderUpdateData> findByCreatedAtRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find order updates within updated timestamp range
     */
    @Query("SELECT o FROM WebSocketFuturesOrderUpdateData o WHERE o.updatedAt >= :startTime AND o.updatedAt <= :endTime ORDER BY o.updatedAt DESC")
    List<WebSocketFuturesOrderUpdateData> findByUpdatedAtRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find order updates by order ID within updated timestamp range
     */
    @Query("SELECT o FROM WebSocketFuturesOrderUpdateData o WHERE o.orderId = :orderId AND o.updatedAt >= :startTime AND o.updatedAt <= :endTime ORDER BY o.updatedAt DESC")
    List<WebSocketFuturesOrderUpdateData> findByOrderIdAndUpdatedAtRange(@Param("orderId") String orderId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find order updates by pair within updated timestamp range
     */
    @Query("SELECT o FROM WebSocketFuturesOrderUpdateData o WHERE o.pair = :pair AND o.updatedAt >= :startTime AND o.updatedAt <= :endTime ORDER BY o.updatedAt DESC")
    List<WebSocketFuturesOrderUpdateData> findByPairAndUpdatedAtRange(@Param("pair") String pair, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find order updates within record timestamp range
     */
    @Query("SELECT o FROM WebSocketFuturesOrderUpdateData o WHERE o.recordTimestamp >= :startTime AND o.recordTimestamp <= :endTime ORDER BY o.recordTimestamp DESC")
    List<WebSocketFuturesOrderUpdateData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Find recent order updates with limit
     */
    @Query(value = "SELECT * FROM websocket_futures_order_update_data ORDER BY updated_at DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderUpdateData> findRecentOrderUpdates(@Param("limit") int limit);

    /**
     * Find order updates by price range
     */
    @Query("SELECT o FROM WebSocketFuturesOrderUpdateData o WHERE o.price >= :minPrice AND o.price <= :maxPrice ORDER BY o.updatedAt DESC")
    List<WebSocketFuturesOrderUpdateData> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Find order updates by leverage range
     */
    @Query("SELECT o FROM WebSocketFuturesOrderUpdateData o WHERE o.leverage >= :minLeverage AND o.leverage <= :maxLeverage ORDER BY o.updatedAt DESC")
    List<WebSocketFuturesOrderUpdateData> findByLeverageRange(@Param("minLeverage") Double minLeverage, @Param("maxLeverage") Double maxLeverage);

    /**
     * Find order updates by margin currency
     */
    List<WebSocketFuturesOrderUpdateData> findByMarginCurrencyShortNameOrderByUpdatedAtDesc(String marginCurrency);

    /**
     * Find orders with TP/SL
     */
    @Query("SELECT o FROM WebSocketFuturesOrderUpdateData o WHERE (o.takeProfitPrice IS NOT NULL OR o.stopLossPrice IS NOT NULL) ORDER BY o.updatedAt DESC")
    List<WebSocketFuturesOrderUpdateData> findOrdersWithTpSl();

    /**
     * Find orders by group ID
     */
    List<WebSocketFuturesOrderUpdateData> findByGroupIdOrderByUpdatedAtDesc(String groupId);

    /**
     * Get distinct order IDs
     */
    @Query("SELECT DISTINCT o.orderId FROM WebSocketFuturesOrderUpdateData o")
    List<String> findDistinctOrderIds();

    /**
     * Get distinct pairs
     */
    @Query("SELECT DISTINCT o.pair FROM WebSocketFuturesOrderUpdateData o")
    List<String> findDistinctPairs();

    /**
     * Get distinct statuses
     */
    @Query("SELECT DISTINCT o.status FROM WebSocketFuturesOrderUpdateData o")
    List<String> findDistinctStatuses();

    /**
     * Get distinct order types
     */
    @Query("SELECT DISTINCT o.orderType FROM WebSocketFuturesOrderUpdateData o")
    List<String> findDistinctOrderTypes();

    /**
     * Get distinct order categories
     */
    @Query("SELECT DISTINCT o.orderCategory FROM WebSocketFuturesOrderUpdateData o")
    List<String> findDistinctOrderCategories();

    /**
     * Count order updates by order ID
     */
    Long countByOrderId(String orderId);

    /**
     * Count order updates by pair
     */
    Long countByPair(String pair);

    /**
     * Count order updates by status
     */
    Long countByStatus(String status);

    /**
     * Count order updates by order type
     */
    Long countByOrderType(String orderType);

    /**
     * Get order statistics by pair
     */
    @Query(value = "SELECT " +
                   "pair, " +
                   "status, " +
                   "order_type, " +
                   "COUNT(*) as orderCount, " +
                   "SUM(total_quantity) as totalQuantity, " +
                   "SUM(filled_quantity) as filledQuantity, " +
                   "SUM(fee_amount) as totalFees, " +
                   "AVG(leverage) as avgLeverage " +
                   "FROM websocket_futures_order_update_data " +
                   "WHERE updated_at >= :fromTime " +
                   "GROUP BY pair, status, order_type", 
           nativeQuery = true)
    List<Object[]> getOrderStatistics(@Param("fromTime") Long fromTime);

    /**
     * Get total volume by pair
     */
    @Query("SELECT o.pair, SUM(o.totalQuantity * o.avgPrice) FROM WebSocketFuturesOrderUpdateData o WHERE o.status = 'filled' GROUP BY o.pair")
    List<Object[]> getTotalVolumeByPair();

    /**
     * Get total fees by pair
     */
    @Query("SELECT o.pair, SUM(o.feeAmount) FROM WebSocketFuturesOrderUpdateData o GROUP BY o.pair")
    List<Object[]> getTotalFeesByPair();

    /**
     * Get average leverage by pair
     */
    @Query("SELECT o.pair, AVG(o.leverage) FROM WebSocketFuturesOrderUpdateData o GROUP BY o.pair")
    List<Object[]> getAverageLeverageByPair();

    /**
     * Find orders with high leverage
     */
    @Query(value = "SELECT * FROM websocket_futures_order_update_data WHERE leverage >= :minLeverage ORDER BY leverage DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderUpdateData> findHighLeverageOrders(@Param("minLeverage") Double minLeverage, @Param("limit") int limit);

    /**
     * Find large orders
     */
    @Query(value = "SELECT * FROM websocket_futures_order_update_data WHERE total_quantity * price >= :minValue ORDER BY (total_quantity * price) DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderUpdateData> findLargeOrders(@Param("minValue") Double minValue, @Param("limit") int limit);

    /**
     * Find orders by channel name
     */
    List<WebSocketFuturesOrderUpdateData> findByChannelNameOrderByUpdatedAtDesc(String channelName);

    /**
     * Delete old order updates by record timestamp
     */
    void deleteByRecordTimestampBefore(LocalDateTime cutoff);
}
