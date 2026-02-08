package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for WebSocket Spot market data
 */
@Repository
public interface WebSocketSpotDataRepository extends JpaRepository<WebSocketSpotData, Long> {

    /**
     * Find all data for a specific market pair
     */
    List<WebSocketSpotData> findByMarketPairOrderByTimestampDesc(String marketPair);

    /**
     * Find data by market pair and event type
     */
    List<WebSocketSpotData> findByMarketPairAndEventTypeOrderByTimestampDesc(
            String marketPair, String eventType);

    /**
     * Find recent data for a market pair (last N minutes)
     */
    @Query("SELECT w FROM WebSocketSpotData w WHERE w.marketPair = :marketPair " +
           "AND w.timestamp >= :since ORDER BY w.timestamp DESC")
    List<WebSocketSpotData> findRecentData(
            @Param("marketPair") String marketPair,
            @Param("since") LocalDateTime since);

    /**
     * Find latest price for a market pair
     */
    @Query("SELECT w FROM WebSocketSpotData w WHERE w.marketPair = :marketPair " +
           "AND w.eventType = 'price-change' AND w.price IS NOT NULL " +
           "ORDER BY w.timestamp DESC LIMIT 1")
    WebSocketSpotData findLatestPrice(@Param("marketPair") String marketPair);

    /**
     * Delete old data (for cleanup tasks)
     */
    void deleteByTimestampBefore(LocalDateTime before);

    /**
     * Count entries by market pair
     */
    long countByMarketPair(String marketPair);

    /**
     * Find all distinct market pairs
     */
    @Query("SELECT DISTINCT w.marketPair FROM WebSocketSpotData w")
    List<String> findDistinctMarketPairs();
}
