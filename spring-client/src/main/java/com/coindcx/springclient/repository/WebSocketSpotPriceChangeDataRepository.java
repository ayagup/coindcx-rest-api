package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotPriceChangeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Price Change Data (Spot-LTP)
 */
@Repository
public interface WebSocketSpotPriceChangeDataRepository extends JpaRepository<WebSocketSpotPriceChangeData, Long> {

    /**
     * Find price changes by pair ordered by timestamp descending
     */
    List<WebSocketSpotPriceChangeData> findByPairOrderByTradeTimestampDesc(String pair);

    /**
     * Find price changes by pair with limit
     */
    @Query(value = "SELECT * FROM websocket_spot_price_change_data WHERE pair = :pair ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotPriceChangeData> findByPairWithLimit(@Param("pair") String pair, @Param("limit") int limit);

    /**
     * Find latest price change by pair
     */
    Optional<WebSocketSpotPriceChangeData> findFirstByPairOrderByTradeTimestampDesc(String pair);

    /**
     * Find price changes by product
     */
    List<WebSocketSpotPriceChangeData> findByProductOrderByTradeTimestampDesc(String product);

    /**
     * Find price changes within timestamp range
     */
    @Query("SELECT p FROM WebSocketSpotPriceChangeData p WHERE p.tradeTimestamp >= :startTime AND p.tradeTimestamp <= :endTime ORDER BY p.tradeTimestamp DESC")
    List<WebSocketSpotPriceChangeData> findByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find price changes by pair within timestamp range
     */
    @Query("SELECT p FROM WebSocketSpotPriceChangeData p WHERE p.pair = :pair AND p.tradeTimestamp >= :startTime AND p.tradeTimestamp <= :endTime ORDER BY p.tradeTimestamp DESC")
    List<WebSocketSpotPriceChangeData> findByPairAndTimestampRange(@Param("pair") String pair, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find price changes within record timestamp range
     */
    @Query("SELECT p FROM WebSocketSpotPriceChangeData p WHERE p.recordTimestamp >= :startTime AND p.recordTimestamp <= :endTime ORDER BY p.recordTimestamp DESC")
    List<WebSocketSpotPriceChangeData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Find recent price changes with limit
     */
    @Query(value = "SELECT * FROM websocket_spot_price_change_data ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotPriceChangeData> findRecentPriceChanges(@Param("limit") int limit);

    /**
     * Find price changes by price range
     */
    @Query("SELECT p FROM WebSocketSpotPriceChangeData p WHERE p.tradePrice >= :minPrice AND p.tradePrice <= :maxPrice ORDER BY p.tradeTimestamp DESC")
    List<WebSocketSpotPriceChangeData> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Find price changes by pair and price range
     */
    @Query("SELECT p FROM WebSocketSpotPriceChangeData p WHERE p.pair = :pair AND p.tradePrice >= :minPrice AND p.tradePrice <= :maxPrice ORDER BY p.tradeTimestamp DESC")
    List<WebSocketSpotPriceChangeData> findByPairAndPriceRange(@Param("pair") String pair, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Get distinct pairs
     */
    @Query("SELECT DISTINCT p.pair FROM WebSocketSpotPriceChangeData p")
    List<String> findDistinctPairs();

    /**
     * Get distinct products
     */
    @Query("SELECT DISTINCT p.product FROM WebSocketSpotPriceChangeData p")
    List<String> findDistinctProducts();

    /**
     * Count price changes by pair
     */
    Long countByPair(String pair);

    /**
     * Count price changes by product
     */
    Long countByProduct(String product);

    /**
     * Count price changes within timestamp range
     */
    @Query("SELECT COUNT(p) FROM WebSocketSpotPriceChangeData p WHERE p.tradeTimestamp >= :startTime AND p.tradeTimestamp <= :endTime")
    Long countByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Get highest price for a pair
     */
    @Query("SELECT MAX(p.tradePrice) FROM WebSocketSpotPriceChangeData p WHERE p.pair = :pair AND p.tradeTimestamp >= :fromTime")
    Double getHighestPrice(@Param("pair") String pair, @Param("fromTime") Long fromTime);

    /**
     * Get lowest price for a pair
     */
    @Query("SELECT MIN(p.tradePrice) FROM WebSocketSpotPriceChangeData p WHERE p.pair = :pair AND p.tradeTimestamp >= :fromTime")
    Double getLowestPrice(@Param("pair") String pair, @Param("fromTime") Long fromTime);

    /**
     * Get average price for a pair
     */
    @Query("SELECT AVG(p.tradePrice) FROM WebSocketSpotPriceChangeData p WHERE p.pair = :pair AND p.tradeTimestamp >= :fromTime")
    Double getAveragePrice(@Param("pair") String pair, @Param("fromTime") Long fromTime);

    /**
     * Get price history for a pair (OHLC-like data)
     */
    @Query(value = "SELECT pair, MIN(trade_price) as low, MAX(trade_price) as high, " +
                   "(SELECT trade_price FROM websocket_spot_price_change_data WHERE pair = :pair AND trade_timestamp >= :fromTime ORDER BY trade_timestamp ASC LIMIT 1) as open, " +
                   "(SELECT trade_price FROM websocket_spot_price_change_data WHERE pair = :pair AND trade_timestamp >= :fromTime ORDER BY trade_timestamp DESC LIMIT 1) as close " +
                   "FROM websocket_spot_price_change_data WHERE pair = :pair AND trade_timestamp >= :fromTime GROUP BY pair", 
           nativeQuery = true)
    Object[] getPriceHistory(@Param("pair") String pair, @Param("fromTime") Long fromTime);

    /**
     * Get price statistics by pair
     */
    @Query(value = "SELECT pair, COUNT(*) as changeCount, MIN(trade_price) as minPrice, MAX(trade_price) as maxPrice, AVG(trade_price) as avgPrice " +
                   "FROM websocket_spot_price_change_data WHERE trade_timestamp >= :fromTime GROUP BY pair", 
           nativeQuery = true)
    List<Object[]> getPriceStatisticsByPair(@Param("fromTime") Long fromTime);

    /**
     * Find price changes by channel name
     */
    List<WebSocketSpotPriceChangeData> findByChannelNameOrderByTradeTimestampDesc(String channelName);

    /**
     * Get price change percentage for a pair
     */
    @Query(value = "SELECT (latest.trade_price - earliest.trade_price) / earliest.trade_price * 100 as priceChangePercent " +
                   "FROM " +
                   "(SELECT trade_price FROM websocket_spot_price_change_data WHERE pair = :pair AND trade_timestamp >= :fromTime ORDER BY trade_timestamp DESC LIMIT 1) as latest, " +
                   "(SELECT trade_price FROM websocket_spot_price_change_data WHERE pair = :pair AND trade_timestamp >= :fromTime ORDER BY trade_timestamp ASC LIMIT 1) as earliest",
           nativeQuery = true)
    Double getPriceChangePercent(@Param("pair") String pair, @Param("fromTime") Long fromTime);

    /**
     * Delete old price changes by record timestamp
     */
    void deleteByRecordTimestampBefore(LocalDateTime cutoff);
}
