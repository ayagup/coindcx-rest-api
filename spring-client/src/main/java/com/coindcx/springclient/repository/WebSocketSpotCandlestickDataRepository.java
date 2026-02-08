package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotCandlestickData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Candlestick Data
 */
@Repository
public interface WebSocketSpotCandlestickDataRepository extends JpaRepository<WebSocketSpotCandlestickData, Long> {

    /**
     * Find candlesticks by symbol
     */
    List<WebSocketSpotCandlestickData> findBySymbolOrderByStartTimestampDesc(String symbol);

    /**
     * Find candlesticks by symbol and interval
     */
    List<WebSocketSpotCandlestickData> findBySymbolAndCandleIntervalOrderByStartTimestampDesc(String symbol, String interval);

    /**
     * Find candlesticks by symbol and interval with limit
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol AND c.candleInterval = :interval ORDER BY c.startTimestamp DESC LIMIT :limit")
    List<WebSocketSpotCandlestickData> findBySymbolAndIntervalWithLimit(@Param("symbol") String symbol, @Param("interval") String interval, @Param("limit") int limit);

    /**
     * Find completed candlesticks only
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.isCompleted = true ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findCompletedCandlesticks();

    /**
     * Find incomplete candlesticks
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.isCompleted = false ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findIncompleteCandlesticks();

    /**
     * Find candlesticks by symbol and completion status
     */
    List<WebSocketSpotCandlestickData> findBySymbolAndIsCompletedOrderByStartTimestampDesc(String symbol, Boolean isCompleted);

    /**
     * Find candlesticks within start timestamp range
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.startTimestamp >= :startTime AND c.startTimestamp <= :endTime ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findByStartTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find candlesticks by symbol within start timestamp range
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol AND c.startTimestamp >= :startTime AND c.startTimestamp <= :endTime ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findBySymbolAndStartTimestampRange(@Param("symbol") String symbol, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find candlesticks by symbol, interval within start timestamp range
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol AND c.candleInterval = :interval AND c.startTimestamp >= :startTime AND c.startTimestamp <= :endTime ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findBySymbolIntervalAndStartTimestampRange(@Param("symbol") String symbol, @Param("interval") String interval, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find candlesticks within record timestamp range
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.timestamp >= :startTime AND c.timestamp <= :endTime ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findByTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Find recent N candlesticks
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c ORDER BY c.startTimestamp DESC LIMIT :limit")
    List<WebSocketSpotCandlestickData> findRecentRecords(@Param("limit") int limit);

    /**
     * Find recent N candlesticks for a symbol
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol ORDER BY c.startTimestamp DESC LIMIT :limit")
    List<WebSocketSpotCandlestickData> findRecentBySymbol(@Param("symbol") String symbol, @Param("limit") int limit);

    /**
     * Find all distinct symbols
     */
    @Query("SELECT DISTINCT c.symbol FROM WebSocketSpotCandlestickData c ORDER BY c.symbol")
    List<String> findDistinctSymbols();

    /**
     * Find all distinct intervals
     */
    @Query("SELECT DISTINCT c.candleInterval FROM WebSocketSpotCandlestickData c ORDER BY c.candleInterval")
    List<String> findDistinctIntervals();

    /**
     * Find all distinct intervals for a symbol
     */
    @Query("SELECT DISTINCT c.candleInterval FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol ORDER BY c.candleInterval")
    List<String> findDistinctIntervalsBySymbol(@Param("symbol") String symbol);

    /**
     * Get latest candlestick for a symbol and interval
     */
    Optional<WebSocketSpotCandlestickData> findFirstBySymbolAndCandleIntervalOrderByStartTimestampDesc(String symbol, String interval);

    /**
     * Get latest completed candlestick for a symbol and interval
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol AND c.candleInterval = :interval AND c.isCompleted = true ORDER BY c.startTimestamp DESC LIMIT 1")
    Optional<WebSocketSpotCandlestickData> findLatestCompletedBySymbolAndInterval(@Param("symbol") String symbol, @Param("interval") String interval);

    /**
     * Count candlesticks by symbol
     */
    long countBySymbol(String symbol);

    /**
     * Count candlesticks by symbol and interval
     */
    long countBySymbolAndCandleInterval(String symbol, String interval);

    /**
     * Count completed candlesticks by symbol and interval
     */
    @Query("SELECT COUNT(c) FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol AND c.candleInterval = :interval AND c.isCompleted = true")
    long countCompletedBySymbolAndInterval(@Param("symbol") String symbol, @Param("interval") String interval);

    /**
     * Get total volume for a symbol and interval
     */
    @Query("SELECT SUM(CAST(c.baseAssetVolume AS double)) FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol AND c.candleInterval = :interval")
    Double getTotalVolumeBySymbolAndInterval(@Param("symbol") String symbol, @Param("interval") String interval);

    /**
     * Get total number of trades for a symbol and interval
     */
    @Query("SELECT SUM(c.numberOfTrades) FROM WebSocketSpotCandlestickData c WHERE c.symbol = :symbol AND c.candleInterval = :interval")
    Long getTotalTradesBySymbolAndInterval(@Param("symbol") String symbol, @Param("interval") String interval);

    /**
     * Find candlesticks by price range
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE CAST(c.openPrice AS double) >= :minPrice AND CAST(c.openPrice AS double) <= :maxPrice ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Find candlesticks by high price threshold
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE CAST(c.highPrice AS double) >= :minHigh ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findByHighPriceGreaterThan(@Param("minHigh") Double minHigh);

    /**
     * Find candlesticks by low price threshold
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE CAST(c.lowPrice AS double) <= :maxLow ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findByLowPriceLessThan(@Param("maxLow") Double maxLow);

    /**
     * Find candlesticks by minimum volume
     */
    @Query("SELECT c FROM WebSocketSpotCandlestickData c WHERE CAST(c.baseAssetVolume AS double) >= :minVolume ORDER BY c.startTimestamp DESC")
    List<WebSocketSpotCandlestickData> findByVolumeGreaterThan(@Param("minVolume") Double minVolume);

    /**
     * Delete old candlestick data
     */
    void deleteByTimestampBefore(LocalDateTime cutoff);
}
