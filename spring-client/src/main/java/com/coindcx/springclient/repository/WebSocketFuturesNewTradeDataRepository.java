package com.coindcx.springclient.repository;

import com.coindcx.springclient.model.WebSocketFuturesNewTradeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WebSocketFuturesNewTradeDataRepository extends JpaRepository<WebSocketFuturesNewTradeData, Long> {
    
    // Find by symbol
    List<WebSocketFuturesNewTradeData> findBySymbolOrderByTradeTimestampDesc(String symbol);
    
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data WHERE symbol = :symbol ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesNewTradeData> findBySymbolWithLimit(@Param("symbol") String symbol, @Param("limit") int limit);
    
    // Find by channel
    List<WebSocketFuturesNewTradeData> findByChannelNameOrderByTradeTimestampDesc(String channelName);
    
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data WHERE channel_name = :channelName ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesNewTradeData> findByChannelNameWithLimit(@Param("channelName") String channelName, @Param("limit") int limit);
    
    // Find by buyer maker flag (trade side)
    List<WebSocketFuturesNewTradeData> findByIsBuyerMakerOrderByTradeTimestampDesc(Integer isBuyerMaker);
    
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data WHERE is_buyer_maker = :isBuyerMaker ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesNewTradeData> findByIsBuyerMakerWithLimit(@Param("isBuyerMaker") Integer isBuyerMaker, @Param("limit") int limit);
    
    // Combined filters
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data WHERE symbol = :symbol AND is_buyer_maker = :isBuyerMaker ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesNewTradeData> findBySymbolAndIsBuyerMakerWithLimit(@Param("symbol") String symbol, @Param("isBuyerMaker") Integer isBuyerMaker, @Param("limit") int limit);
    
    // Timestamp range queries
    @Query("SELECT t FROM WebSocketFuturesNewTradeData t WHERE t.tradeTimestamp >= :startTime AND t.tradeTimestamp <= :endTime ORDER BY t.tradeTimestamp DESC")
    List<WebSocketFuturesNewTradeData> findByTradeTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT t FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol AND t.tradeTimestamp >= :startTime AND t.tradeTimestamp <= :endTime ORDER BY t.tradeTimestamp DESC")
    List<WebSocketFuturesNewTradeData> findBySymbolAndTradeTimestampRange(@Param("symbol") String symbol, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT t FROM WebSocketFuturesNewTradeData t WHERE t.channelName = :channelName AND t.tradeTimestamp >= :startTime AND t.tradeTimestamp <= :endTime ORDER BY t.tradeTimestamp DESC")
    List<WebSocketFuturesNewTradeData> findByChannelNameAndTradeTimestampRange(@Param("channelName") String channelName, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT t FROM WebSocketFuturesNewTradeData t WHERE t.recordTimestamp >= :startTime AND t.recordTimestamp <= :endTime ORDER BY t.recordTimestamp DESC")
    List<WebSocketFuturesNewTradeData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // Price range queries
    @Query("SELECT t FROM WebSocketFuturesNewTradeData t WHERE t.price >= :minPrice AND t.price <= :maxPrice ORDER BY t.tradeTimestamp DESC")
    List<WebSocketFuturesNewTradeData> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT t FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol AND t.price >= :minPrice AND t.price <= :maxPrice ORDER BY t.tradeTimestamp DESC")
    List<WebSocketFuturesNewTradeData> findBySymbolAndPriceRange(@Param("symbol") String symbol, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Quantity range queries
    @Query("SELECT t FROM WebSocketFuturesNewTradeData t WHERE t.quantity >= :minQuantity AND t.quantity <= :maxQuantity ORDER BY t.tradeTimestamp DESC")
    List<WebSocketFuturesNewTradeData> findByQuantityRange(@Param("minQuantity") BigDecimal minQuantity, @Param("maxQuantity") BigDecimal maxQuantity);
    
    @Query("SELECT t FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol AND t.quantity >= :minQuantity AND t.quantity <= :maxQuantity ORDER BY t.tradeTimestamp DESC")
    List<WebSocketFuturesNewTradeData> findBySymbolAndQuantityRange(@Param("symbol") String symbol, @Param("minQuantity") BigDecimal minQuantity, @Param("maxQuantity") BigDecimal maxQuantity);
    
    // Recent trades
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesNewTradeData> findRecentTrades(@Param("limit") int limit);
    
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesNewTradeData> findRecentByRecordTimestamp(@Param("limit") int limit);
    
    // Latest trade queries
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data WHERE symbol = :symbol ORDER BY trade_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesNewTradeData> findLatestBySymbol(@Param("symbol") String symbol);
    
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data WHERE channel_name = :channelName ORDER BY trade_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesNewTradeData> findLatestByChannelName(@Param("channelName") String channelName);
    
    @Query(value = "SELECT * FROM websocket_futures_new_trade_data ORDER BY trade_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesNewTradeData> findLatestTrade();
    
    // Aggregation queries
    @Query("SELECT SUM(t.quantity) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol")
    BigDecimal sumQuantityBySymbol(@Param("symbol") String symbol);
    
    @Query("SELECT SUM(t.quantity) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol AND t.tradeTimestamp >= :startTime AND t.tradeTimestamp <= :endTime")
    BigDecimal sumQuantityBySymbolAndTimeRange(@Param("symbol") String symbol, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT AVG(t.price) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol")
    BigDecimal avgPriceBySymbol(@Param("symbol") String symbol);
    
    @Query("SELECT AVG(t.price) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol AND t.tradeTimestamp >= :startTime AND t.tradeTimestamp <= :endTime")
    BigDecimal avgPriceBySymbolAndTimeRange(@Param("symbol") String symbol, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT MIN(t.price) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol")
    BigDecimal minPriceBySymbol(@Param("symbol") String symbol);
    
    @Query("SELECT MAX(t.price) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol")
    BigDecimal maxPriceBySymbol(@Param("symbol") String symbol);
    
    // Distinct values
    @Query("SELECT DISTINCT t.symbol FROM WebSocketFuturesNewTradeData t ORDER BY t.symbol")
    List<String> findDistinctSymbols();
    
    @Query("SELECT DISTINCT t.channelName FROM WebSocketFuturesNewTradeData t ORDER BY t.channelName")
    List<String> findDistinctChannelNames();
    
    @Query("SELECT DISTINCT t.productType FROM WebSocketFuturesNewTradeData t ORDER BY t.productType")
    List<String> findDistinctProductTypes();
    
    // Count queries
    long countBySymbol(String symbol);
    
    long countByChannelName(String channelName);
    
    long countByIsBuyerMaker(Integer isBuyerMaker);
    
    @Query("SELECT COUNT(t) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol AND t.isBuyerMaker = :isBuyerMaker")
    long countBySymbolAndIsBuyerMaker(@Param("symbol") String symbol, @Param("isBuyerMaker") Integer isBuyerMaker);
    
    @Query("SELECT COUNT(DISTINCT t.symbol) FROM WebSocketFuturesNewTradeData t")
    long countDistinctSymbols();
    
    @Query("SELECT COUNT(DISTINCT t.channelName) FROM WebSocketFuturesNewTradeData t")
    long countDistinctChannels();
    
    // Statistics queries
    @Query("SELECT MIN(t.tradeTimestamp) FROM WebSocketFuturesNewTradeData t")
    Long findOldestTradeTimestamp();
    
    @Query("SELECT MAX(t.tradeTimestamp) FROM WebSocketFuturesNewTradeData t")
    Long findLatestTradeTimestamp();
    
    @Query("SELECT MIN(t.tradeTimestamp) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol")
    Long findOldestTradeTimestampBySymbol(@Param("symbol") String symbol);
    
    @Query("SELECT MAX(t.tradeTimestamp) FROM WebSocketFuturesNewTradeData t WHERE t.symbol = :symbol")
    Long findLatestTradeTimestampBySymbol(@Param("symbol") String symbol);
    
    // Cleanup
    void deleteByRecordTimestampBefore(LocalDateTime cutoffTime);
    
    @Query(value = "DELETE FROM websocket_futures_new_trade_data WHERE symbol = :symbol AND trade_timestamp < :cutoffTime", nativeQuery = true)
    void deleteBySymbolAndTradeTimestampBefore(@Param("symbol") String symbol, @Param("cutoffTime") Long cutoffTime);
}
