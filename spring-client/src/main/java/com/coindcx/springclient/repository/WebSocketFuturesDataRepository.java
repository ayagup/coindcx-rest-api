package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketFuturesData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for WebSocket Futures market data
 */
@Repository
public interface WebSocketFuturesDataRepository extends JpaRepository<WebSocketFuturesData, Long> {

    /**
     * Find all data for a specific contract symbol
     */
    List<WebSocketFuturesData> findByContractSymbolOrderByTimestampDesc(String contractSymbol);

    /**
     * Find data by contract symbol and event type
     */
    List<WebSocketFuturesData> findByContractSymbolAndEventTypeOrderByTimestampDesc(
            String contractSymbol, String eventType);

    /**
     * Find recent data for a contract (last N minutes)
     */
    @Query("SELECT w FROM WebSocketFuturesData w WHERE w.contractSymbol = :contractSymbol " +
           "AND w.timestamp >= :since ORDER BY w.timestamp DESC")
    List<WebSocketFuturesData> findRecentData(
            @Param("contractSymbol") String contractSymbol,
            @Param("since") LocalDateTime since);

    /**
     * Find latest mark price for a contract
     */
    @Query("SELECT w FROM WebSocketFuturesData w WHERE w.contractSymbol = :contractSymbol " +
           "AND w.eventType = 'price-change' AND w.markPrice IS NOT NULL " +
           "ORDER BY w.timestamp DESC LIMIT 1")
    WebSocketFuturesData findLatestMarkPrice(@Param("contractSymbol") String contractSymbol);

    /**
     * Find latest position data for a contract
     */
    @Query("SELECT w FROM WebSocketFuturesData w WHERE w.contractSymbol = :contractSymbol " +
           "AND w.eventType = 'position-update' " +
           "ORDER BY w.timestamp DESC LIMIT 1")
    WebSocketFuturesData findLatestPosition(@Param("contractSymbol") String contractSymbol);

    /**
     * Delete old data (for cleanup tasks)
     */
    void deleteByTimestampBefore(LocalDateTime before);

    /**
     * Count entries by contract symbol
     */
    long countByContractSymbol(String contractSymbol);

    /**
     * Find all distinct contract symbols
     */
    @Query("SELECT DISTINCT w.contractSymbol FROM WebSocketFuturesData w")
    List<String> findDistinctContractSymbols();
}
