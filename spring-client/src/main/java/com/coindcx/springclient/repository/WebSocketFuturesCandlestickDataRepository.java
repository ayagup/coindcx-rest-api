package com.coindcx.springclient.repository;

import com.coindcx.springclient.model.WebSocketFuturesCandlestickData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface WebSocketFuturesCandlestickDataRepository extends JpaRepository<WebSocketFuturesCandlestickData, Long> {
    
    // Find by pair
    List<WebSocketFuturesCandlestickData> findByPairOrderByOpenTimeDesc(String pair);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE pair = :pair ORDER BY open_time DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findByPairWithLimit(@Param("pair") String pair, @Param("limit") int limit);
    
    // Find by symbol
    List<WebSocketFuturesCandlestickData> findBySymbolOrderByOpenTimeDesc(String symbol);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE symbol = :symbol ORDER BY open_time DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findBySymbolWithLimit(@Param("symbol") String symbol, @Param("limit") int limit);
    
    // Find by duration/interval
    List<WebSocketFuturesCandlestickData> findByDurationOrderByOpenTimeDesc(String duration);
    
    List<WebSocketFuturesCandlestickData> findByIntervalOrderByOpenTimeDesc(String interval);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE duration = :duration ORDER BY open_time DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findByDurationWithLimit(@Param("duration") String duration, @Param("limit") int limit);
    
    // Combined filters
    List<WebSocketFuturesCandlestickData> findByPairAndDurationOrderByOpenTimeDesc(String pair, String duration);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE pair = :pair AND duration = :duration ORDER BY open_time DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findByPairAndDurationWithLimit(@Param("pair") String pair, @Param("duration") String duration, @Param("limit") int limit);
    
    List<WebSocketFuturesCandlestickData> findBySymbolAndDurationOrderByOpenTimeDesc(String symbol, String duration);
    
    WebSocketFuturesCandlestickData findFirstByPairAndDurationOrderByOpenTimeDesc(String pair, String duration);
    
    // Time range queries
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.openTime >= :startTime AND c.openTime <= :endTime ORDER BY c.openTime DESC")
    List<WebSocketFuturesCandlestickData> findByOpenTimeRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.pair = :pair AND c.openTime >= :startTime AND c.openTime <= :endTime ORDER BY c.openTime DESC")
    List<WebSocketFuturesCandlestickData> findByPairAndOpenTimeRange(@Param("pair") String pair, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.pair = :pair AND c.duration = :duration AND c.openTime >= :startTime AND c.openTime <= :endTime ORDER BY c.openTime ASC")
    List<WebSocketFuturesCandlestickData> findByPairDurationAndTimeRange(@Param("pair") String pair, @Param("duration") String duration, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.recordTimestamp >= :startTime AND c.recordTimestamp <= :endTime ORDER BY c.recordTimestamp DESC")
    List<WebSocketFuturesCandlestickData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // Recent candlesticks
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data ORDER BY open_time DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findRecentCandlesticks(@Param("limit") int limit);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findRecentByRecordTimestamp(@Param("limit") int limit);
    
    // OHLC queries
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.high >= :minHigh ORDER BY c.high DESC")
    List<WebSocketFuturesCandlestickData> findByHighGreaterThanEqual(@Param("minHigh") BigDecimal minHigh);
    
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.low <= :maxLow ORDER BY c.low ASC")
    List<WebSocketFuturesCandlestickData> findByLowLessThanEqual(@Param("maxLow") BigDecimal maxLow);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE high >= :minHigh ORDER BY high DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findHighestHighs(@Param("minHigh") BigDecimal minHigh, @Param("limit") int limit);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE low <= :maxLow ORDER BY low ASC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findLowestLows(@Param("maxLow") BigDecimal maxLow, @Param("limit") int limit);
    
    // Volume queries
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.volume >= :minVolume ORDER BY c.volume DESC")
    List<WebSocketFuturesCandlestickData> findByVolumeGreaterThanEqual(@Param("minVolume") BigDecimal minVolume);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE volume >= :minVolume ORDER BY volume DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findHighVolumeCandlesticks(@Param("minVolume") BigDecimal minVolume, @Param("limit") int limit);
    
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.quoteVolume >= :minQuoteVolume ORDER BY c.quoteVolume DESC")
    List<WebSocketFuturesCandlestickData> findByQuoteVolumeGreaterThanEqual(@Param("minQuoteVolume") BigDecimal minQuoteVolume);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE pair = :pair AND volume >= :minVolume ORDER BY volume DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findHighVolumeByPair(@Param("pair") String pair, @Param("minVolume") BigDecimal minVolume, @Param("limit") int limit);
    
    // Price range queries
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.close >= :minPrice AND c.close <= :maxPrice ORDER BY c.openTime DESC")
    List<WebSocketFuturesCandlestickData> findByClosePriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.pair = :pair AND c.close >= :minPrice AND c.close <= :maxPrice ORDER BY c.openTime DESC")
    List<WebSocketFuturesCandlestickData> findByPairAndClosePriceRange(@Param("pair") String pair, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Bullish/Bearish candles
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.close > c.open ORDER BY c.openTime DESC")
    List<WebSocketFuturesCandlestickData> findBullishCandles();
    
    @Query("SELECT c FROM WebSocketFuturesCandlestickData c WHERE c.close < c.open ORDER BY c.openTime DESC")
    List<WebSocketFuturesCandlestickData> findBearishCandles();
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE close > open AND pair = :pair ORDER BY open_time DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findBullishCandlesByPair(@Param("pair") String pair, @Param("limit") int limit);
    
    @Query(value = "SELECT * FROM websocket_futures_candlestick_data WHERE close < open AND pair = :pair ORDER BY open_time DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesCandlestickData> findBearishCandlesByPair(@Param("pair") String pair, @Param("limit") int limit);
    
    // Distinct values
    @Query("SELECT DISTINCT c.pair FROM WebSocketFuturesCandlestickData c ORDER BY c.pair")
    List<String> findDistinctPairs();
    
    @Query("SELECT DISTINCT c.symbol FROM WebSocketFuturesCandlestickData c ORDER BY c.symbol")
    List<String> findDistinctSymbols();
    
    @Query("SELECT DISTINCT c.duration FROM WebSocketFuturesCandlestickData c ORDER BY c.duration")
    List<String> findDistinctDurations();
    
    // Count queries
    long countByPair(String pair);
    
    long countBySymbol(String symbol);
    
    long countByDuration(String duration);
    
    long countByPairAndDuration(String pair, String duration);
    
    // Statistics queries
    @Query("SELECT new map(COUNT(c) as totalRecords, " +
           "COUNT(DISTINCT c.pair) as uniquePairs, " +
           "COUNT(DISTINCT c.symbol) as uniqueSymbols, " +
           "COUNT(DISTINCT c.duration) as uniqueDurations, " +
           "SUM(c.volume) as totalVolume, " +
           "SUM(c.quoteVolume) as totalQuoteVolume, " +
           "AVG(c.volume) as avgVolume, " +
           "MAX(c.high) as maxHigh, " +
           "MIN(c.low) as minLow) " +
           "FROM WebSocketFuturesCandlestickData c")
    Map<String, Object> getCandlestickStatistics();
    
    @Query("SELECT new map(c.pair as pair, " +
           "COUNT(c) as recordCount, " +
           "SUM(c.volume) as totalVolume, " +
           "SUM(c.quoteVolume) as totalQuoteVolume, " +
           "AVG(c.volume) as avgVolume, " +
           "MAX(c.high) as maxHigh, " +
           "MIN(c.low) as minLow, " +
           "MAX(c.openTime) as latestTime) " +
           "FROM WebSocketFuturesCandlestickData c " +
           "GROUP BY c.pair " +
           "ORDER BY totalVolume DESC")
    List<Map<String, Object>> getStatisticsByPair();
    
    @Query("SELECT new map(c.duration as duration, " +
           "COUNT(c) as recordCount, " +
           "SUM(c.volume) as totalVolume, " +
           "AVG(c.volume) as avgVolume) " +
           "FROM WebSocketFuturesCandlestickData c " +
           "GROUP BY c.duration " +
           "ORDER BY c.duration")
    List<Map<String, Object>> getStatisticsByDuration();
    
    @Query("SELECT new map(c.pair as pair, " +
           "c.duration as duration, " +
           "COUNT(c) as recordCount, " +
           "SUM(c.volume) as totalVolume, " +
           "MAX(c.high) as maxHigh, " +
           "MIN(c.low) as minLow) " +
           "FROM WebSocketFuturesCandlestickData c " +
           "WHERE c.pair = :pair " +
           "GROUP BY c.pair, c.duration " +
           "ORDER BY c.duration")
    List<Map<String, Object>> getStatisticsByPairGroupedByDuration(@Param("pair") String pair);
    
    // OHLCV aggregation for specific pair and duration
    @Query("SELECT new map(MIN(c.openTime) as periodStart, " +
           "MAX(c.openTime) as periodEnd, " +
           "(SELECT c2.open FROM WebSocketFuturesCandlestickData c2 WHERE c2.pair = :pair AND c2.duration = :duration AND c2.openTime = MIN(c.openTime)) as open, " +
           "MAX(c.high) as high, " +
           "MIN(c.low) as low, " +
           "(SELECT c3.close FROM WebSocketFuturesCandlestickData c3 WHERE c3.pair = :pair AND c3.duration = :duration AND c3.openTime = MAX(c.openTime)) as close, " +
           "SUM(c.volume) as volume, " +
           "SUM(c.quoteVolume) as quoteVolume) " +
           "FROM WebSocketFuturesCandlestickData c " +
           "WHERE c.pair = :pair AND c.duration = :duration AND c.openTime >= :startTime AND c.openTime <= :endTime")
    Map<String, Object> getOHLCVForPeriod(@Param("pair") String pair, @Param("duration") String duration, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    // Cleanup
    void deleteByRecordTimestampBefore(LocalDateTime cutoffTime);
}
