package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotNewTradeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot New Trade Data
 */
@Repository
public interface WebSocketSpotNewTradeDataRepository extends JpaRepository<WebSocketSpotNewTradeData, Long> {

    /**
     * Find trades by symbol ordered by timestamp descending
     */
    List<WebSocketSpotNewTradeData> findBySymbolOrderByTradeTimestampDesc(String symbol);

    /**
     * Find trades by symbol with limit
     */
    @Query(value = "SELECT * FROM websocket_spot_new_trade_data WHERE symbol = :symbol ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotNewTradeData> findBySymbolWithLimit(@Param("symbol") String symbol, @Param("limit") int limit);

    /**
     * Find trades by pair ordered by timestamp descending
     */
    List<WebSocketSpotNewTradeData> findByPairOrderByTradeTimestampDesc(String pair);

    /**
     * Find trades by pair with limit
     */
    @Query(value = "SELECT * FROM websocket_spot_new_trade_data WHERE pair = :pair ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotNewTradeData> findByPairWithLimit(@Param("pair") String pair, @Param("limit") int limit);

    /**
     * Find latest trade by symbol
     */
    Optional<WebSocketSpotNewTradeData> findFirstBySymbolOrderByTradeTimestampDesc(String symbol);

    /**
     * Find latest trade by pair
     */
    Optional<WebSocketSpotNewTradeData> findFirstByPairOrderByTradeTimestampDesc(String pair);

    /**
     * Find trades within timestamp range
     */
    @Query("SELECT t FROM WebSocketSpotNewTradeData t WHERE t.tradeTimestamp >= :startTime AND t.tradeTimestamp <= :endTime ORDER BY t.tradeTimestamp DESC")
    List<WebSocketSpotNewTradeData> findByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find trades by pair within timestamp range
     */
    @Query("SELECT t FROM WebSocketSpotNewTradeData t WHERE t.pair = :pair AND t.tradeTimestamp >= :startTime AND t.tradeTimestamp <= :endTime ORDER BY t.tradeTimestamp DESC")
    List<WebSocketSpotNewTradeData> findByPairAndTimestampRange(@Param("pair") String pair, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find trades within record timestamp range
     */
    @Query("SELECT t FROM WebSocketSpotNewTradeData t WHERE t.recordTimestamp >= :startTime AND t.recordTimestamp <= :endTime ORDER BY t.recordTimestamp DESC")
    List<WebSocketSpotNewTradeData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Find recent trades with limit
     */
    @Query(value = "SELECT * FROM websocket_spot_new_trade_data ORDER BY trade_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotNewTradeData> findRecentTrades(@Param("limit") int limit);

    /**
     * Find trades by price range
     */
    @Query("SELECT t FROM WebSocketSpotNewTradeData t WHERE t.tradePrice >= :minPrice AND t.tradePrice <= :maxPrice ORDER BY t.tradeTimestamp DESC")
    List<WebSocketSpotNewTradeData> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Find trades by quantity range
     */
    @Query("SELECT t FROM WebSocketSpotNewTradeData t WHERE t.quantity >= :minQuantity AND t.quantity <= :maxQuantity ORDER BY t.tradeTimestamp DESC")
    List<WebSocketSpotNewTradeData> findByQuantityRange(@Param("minQuantity") Double minQuantity, @Param("maxQuantity") Double maxQuantity);

    /**
     * Find trades by market maker status
     */
    List<WebSocketSpotNewTradeData> findByIsBuyerMarketMakerOrderByTradeTimestampDesc(Boolean isBuyerMarketMaker);

    /**
     * Find trades by pair and market maker status
     */
    @Query("SELECT t FROM WebSocketSpotNewTradeData t WHERE t.pair = :pair AND t.isBuyerMarketMaker = :isBuyerMarketMaker ORDER BY t.tradeTimestamp DESC")
    List<WebSocketSpotNewTradeData> findByPairAndMarketMaker(@Param("pair") String pair, @Param("isBuyerMarketMaker") Boolean isBuyerMarketMaker);

    /**
     * Find large trades (above quantity threshold)
     */
    @Query(value = "SELECT * FROM websocket_spot_new_trade_data WHERE quantity > :threshold ORDER BY quantity DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotNewTradeData> findLargeTrades(@Param("threshold") Double threshold, @Param("limit") int limit);

    /**
     * Get distinct symbols
     */
    @Query("SELECT DISTINCT t.symbol FROM WebSocketSpotNewTradeData t")
    List<String> findDistinctSymbols();

    /**
     * Get distinct pairs
     */
    @Query("SELECT DISTINCT t.pair FROM WebSocketSpotNewTradeData t")
    List<String> findDistinctPairs();

    /**
     * Count trades by symbol
     */
    Long countBySymbol(String symbol);

    /**
     * Count trades by pair
     */
    Long countByPair(String pair);

    /**
     * Count trades within timestamp range
     */
    @Query("SELECT COUNT(t) FROM WebSocketSpotNewTradeData t WHERE t.tradeTimestamp >= :startTime AND t.tradeTimestamp <= :endTime")
    Long countByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Get average trade price for a pair
     */
    @Query("SELECT AVG(t.tradePrice) FROM WebSocketSpotNewTradeData t WHERE t.pair = :pair AND t.tradeTimestamp >= :fromTime")
    Double getAverageTradePrice(@Param("pair") String pair, @Param("fromTime") Long fromTime);

    /**
     * Get total volume for a pair
     */
    @Query("SELECT SUM(t.quantity) FROM WebSocketSpotNewTradeData t WHERE t.pair = :pair AND t.tradeTimestamp >= :fromTime")
    Double getTotalVolume(@Param("pair") String pair, @Param("fromTime") Long fromTime);

    /**
     * Get trade statistics by pair
     */
    @Query(value = "SELECT pair, COUNT(*) as tradeCount, AVG(trade_price) as avgPrice, SUM(quantity) as totalVolume FROM websocket_spot_new_trade_data WHERE trade_timestamp >= :fromTime GROUP BY pair", nativeQuery = true)
    List<Object[]> getTradeStatisticsByPair(@Param("fromTime") Long fromTime);

    /**
     * Find trades by channel name
     */
    List<WebSocketSpotNewTradeData> findByChannelNameOrderByTradeTimestampDesc(String channelName);

    /**
     * Delete old trades by record timestamp
     */
    void deleteByRecordTimestampBefore(LocalDateTime cutoff);
}
