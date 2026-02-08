package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotTradeUpdateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Trade Update Data
 */
@Repository
public interface WebSocketSpotTradeUpdateDataRepository extends JpaRepository<WebSocketSpotTradeUpdateData, Long> {
    
    /**
     * Find trade by trade ID
     */
    Optional<WebSocketSpotTradeUpdateData> findByTradeId(String tradeId);
    
    /**
     * Find all trades for an order ID
     */
    List<WebSocketSpotTradeUpdateData> findByOrderIdOrderByTimestampDesc(String orderId);
    
    /**
     * Find trades by client order ID
     */
    List<WebSocketSpotTradeUpdateData> findByClientOrderIdOrderByTimestampDesc(String clientOrderId);
    
    /**
     * Find trades by symbol
     */
    List<WebSocketSpotTradeUpdateData> findBySymbolOrderByTimestampDesc(String symbol);
    
    /**
     * Find trades by symbol with limit
     */
    @Query("SELECT t FROM WebSocketSpotTradeUpdateData t WHERE t.symbol = :symbol ORDER BY t.timestamp DESC LIMIT :limit")
    List<WebSocketSpotTradeUpdateData> findBySymbolWithLimit(@Param("symbol") String symbol, @Param("limit") int limit);
    
    /**
     * Find maker trades (buyer is maker)
     */
    @Query("SELECT t FROM WebSocketSpotTradeUpdateData t WHERE t.isBuyerMaker = true ORDER BY t.timestamp DESC")
    List<WebSocketSpotTradeUpdateData> findMakerTrades();
    
    /**
     * Find taker trades (buyer is taker)
     */
    @Query("SELECT t FROM WebSocketSpotTradeUpdateData t WHERE t.isBuyerMaker = false ORDER BY t.timestamp DESC")
    List<WebSocketSpotTradeUpdateData> findTakerTrades();
    
    /**
     * Find trades within a time range
     */
    @Query("SELECT t FROM WebSocketSpotTradeUpdateData t WHERE t.timestamp BETWEEN :startTime AND :endTime ORDER BY t.timestamp DESC")
    List<WebSocketSpotTradeUpdateData> findByTimeRange(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find trades for a symbol within a time range
     */
    @Query("SELECT t FROM WebSocketSpotTradeUpdateData t WHERE t.symbol = :symbol AND t.timestamp BETWEEN :startTime AND :endTime ORDER BY t.timestamp DESC")
    List<WebSocketSpotTradeUpdateData> findBySymbolAndTimeRange(
        @Param("symbol") String symbol,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find recent trade records (last N records)
     */
    @Query("SELECT t FROM WebSocketSpotTradeUpdateData t ORDER BY t.timestamp DESC LIMIT :limit")
    List<WebSocketSpotTradeUpdateData> findRecentRecords(@Param("limit") int limit);
    
    /**
     * Get all distinct symbols with trade data
     */
    @Query("SELECT DISTINCT t.symbol FROM WebSocketSpotTradeUpdateData t ORDER BY t.symbol")
    List<String> findDistinctSymbols();
    
    /**
     * Get all distinct statuses
     */
    @Query("SELECT DISTINCT t.status FROM WebSocketSpotTradeUpdateData t ORDER BY t.status")
    List<String> findDistinctStatuses();
    
    /**
     * Calculate total trade volume for a symbol
     */
    @Query("SELECT SUM(CAST(t.quantity AS double)) FROM WebSocketSpotTradeUpdateData t WHERE t.symbol = :symbol")
    Double getTotalVolumeBySymbol(@Param("symbol") String symbol);
    
    /**
     * Calculate total trade value for a symbol
     */
    @Query("SELECT SUM(CAST(t.price AS double) * CAST(t.quantity AS double)) FROM WebSocketSpotTradeUpdateData t WHERE t.symbol = :symbol")
    Double getTotalValueBySymbol(@Param("symbol") String symbol);
    
    /**
     * Get latest trades for each symbol
     */
    @Query("SELECT t FROM WebSocketSpotTradeUpdateData t WHERE t.timestamp = " +
           "(SELECT MAX(t2.timestamp) FROM WebSocketSpotTradeUpdateData t2 WHERE t2.symbol = t.symbol) " +
           "ORDER BY t.symbol")
    List<WebSocketSpotTradeUpdateData> findLatestTradeForAllSymbols();
    
    /**
     * Find trades by price range
     */
    @Query("SELECT t FROM WebSocketSpotTradeUpdateData t WHERE " +
           "t.price >= :minPrice AND t.price <= :maxPrice " +
           "ORDER BY t.timestamp DESC")
    List<WebSocketSpotTradeUpdateData> findByPriceRange(
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice
    );
    
    /**
     * Delete old trade records before a specific timestamp
     */
    void deleteByTimestampBefore(LocalDateTime timestamp);
    
    /**
     * Count total trade records
     */
    long count();
    
    /**
     * Count trades for a specific symbol
     */
    long countBySymbol(String symbol);
    
    /**
     * Count trades for an order ID
     */
    long countByOrderId(String orderId);
}
