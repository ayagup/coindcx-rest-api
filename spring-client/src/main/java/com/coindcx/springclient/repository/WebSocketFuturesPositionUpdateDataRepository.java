package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketFuturesPositionUpdateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Futures Position Update Data
 */
@Repository
public interface WebSocketFuturesPositionUpdateDataRepository extends JpaRepository<WebSocketFuturesPositionUpdateData, Long> {

    /**
     * Find position updates by position ID
     */
    List<WebSocketFuturesPositionUpdateData> findByPositionIdOrderByUpdateTimestampDesc(String positionId);

    /**
     * Find position updates by position ID with limit
     */
    @Query(value = "SELECT * FROM websocket_futures_position_update_data WHERE position_id = :positionId ORDER BY update_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesPositionUpdateData> findByPositionIdWithLimit(@Param("positionId") String positionId, @Param("limit") int limit);

    /**
     * Find latest position update by position ID
     */
    Optional<WebSocketFuturesPositionUpdateData> findFirstByPositionIdOrderByUpdateTimestampDesc(String positionId);

    /**
     * Find position updates by pair
     */
    List<WebSocketFuturesPositionUpdateData> findByPairOrderByUpdateTimestampDesc(String pair);

    /**
     * Find position updates by pair with limit
     */
    @Query(value = "SELECT * FROM websocket_futures_position_update_data WHERE pair = :pair ORDER BY update_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesPositionUpdateData> findByPairWithLimit(@Param("pair") String pair, @Param("limit") int limit);

    /**
     * Find position updates by side (long/short)
     */
    List<WebSocketFuturesPositionUpdateData> findBySideOrderByUpdateTimestampDesc(String side);

    /**
     * Find position updates by status
     */
    List<WebSocketFuturesPositionUpdateData> findByStatusOrderByUpdateTimestampDesc(String status);

    /**
     * Find position updates by pair and side
     */
    List<WebSocketFuturesPositionUpdateData> findByPairAndSideOrderByUpdateTimestampDesc(String pair, String side);

    /**
     * Find position updates by pair and status
     */
    List<WebSocketFuturesPositionUpdateData> findByPairAndStatusOrderByUpdateTimestampDesc(String pair, String status);

    /**
     * Find position updates within timestamp range
     */
    @Query("SELECT p FROM WebSocketFuturesPositionUpdateData p WHERE p.updateTimestamp >= :startTime AND p.updateTimestamp <= :endTime ORDER BY p.updateTimestamp DESC")
    List<WebSocketFuturesPositionUpdateData> findByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find position updates by position ID within timestamp range
     */
    @Query("SELECT p FROM WebSocketFuturesPositionUpdateData p WHERE p.positionId = :positionId AND p.updateTimestamp >= :startTime AND p.updateTimestamp <= :endTime ORDER BY p.updateTimestamp DESC")
    List<WebSocketFuturesPositionUpdateData> findByPositionIdAndTimestampRange(@Param("positionId") String positionId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find position updates by pair within timestamp range
     */
    @Query("SELECT p FROM WebSocketFuturesPositionUpdateData p WHERE p.pair = :pair AND p.updateTimestamp >= :startTime AND p.updateTimestamp <= :endTime ORDER BY p.updateTimestamp DESC")
    List<WebSocketFuturesPositionUpdateData> findByPairAndTimestampRange(@Param("pair") String pair, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find position updates within record timestamp range
     */
    @Query("SELECT p FROM WebSocketFuturesPositionUpdateData p WHERE p.recordTimestamp >= :startTime AND p.recordTimestamp <= :endTime ORDER BY p.recordTimestamp DESC")
    List<WebSocketFuturesPositionUpdateData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Find recent position updates with limit
     */
    @Query(value = "SELECT * FROM websocket_futures_position_update_data ORDER BY update_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesPositionUpdateData> findRecentPositionUpdates(@Param("limit") int limit);

    /**
     * Find position updates by PnL range
     */
    @Query("SELECT p FROM WebSocketFuturesPositionUpdateData p WHERE p.totalPnl >= :minPnl AND p.totalPnl <= :maxPnl ORDER BY p.updateTimestamp DESC")
    List<WebSocketFuturesPositionUpdateData> findByPnlRange(@Param("minPnl") Double minPnl, @Param("maxPnl") Double maxPnl);

    /**
     * Find position updates by leverage range
     */
    @Query("SELECT p FROM WebSocketFuturesPositionUpdateData p WHERE p.leverage >= :minLeverage AND p.leverage <= :maxLeverage ORDER BY p.updateTimestamp DESC")
    List<WebSocketFuturesPositionUpdateData> findByLeverageRange(@Param("minLeverage") Double minLeverage, @Param("maxLeverage") Double maxLeverage);

    /**
     * Find position updates by margin currency
     */
    List<WebSocketFuturesPositionUpdateData> findByMarginCurrencyOrderByUpdateTimestampDesc(String marginCurrency);

    /**
     * Find position updates by position margin type
     */
    List<WebSocketFuturesPositionUpdateData> findByPositionMarginTypeOrderByUpdateTimestampDesc(String positionMarginType);

    /**
     * Get distinct position IDs
     */
    @Query("SELECT DISTINCT p.positionId FROM WebSocketFuturesPositionUpdateData p")
    List<String> findDistinctPositionIds();

    /**
     * Get distinct pairs
     */
    @Query("SELECT DISTINCT p.pair FROM WebSocketFuturesPositionUpdateData p")
    List<String> findDistinctPairs();

    /**
     * Get distinct status values
     */
    @Query("SELECT DISTINCT p.status FROM WebSocketFuturesPositionUpdateData p")
    List<String> findDistinctStatuses();

    /**
     * Count position updates by position ID
     */
    Long countByPositionId(String positionId);

    /**
     * Count position updates by pair
     */
    Long countByPair(String pair);

    /**
     * Count position updates by status
     */
    Long countByStatus(String status);

    /**
     * Count position updates within timestamp range
     */
    @Query("SELECT COUNT(p) FROM WebSocketFuturesPositionUpdateData p WHERE p.updateTimestamp >= :startTime AND p.updateTimestamp <= :endTime")
    Long countByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Get total unrealized PnL by pair
     */
    @Query("SELECT p.pair, SUM(p.unrealizedPnl) FROM WebSocketFuturesPositionUpdateData p WHERE p.status = 'active' GROUP BY p.pair")
    List<Object[]> getTotalUnrealizedPnlByPair();

    /**
     * Get total realized PnL by pair
     */
    @Query("SELECT p.pair, SUM(p.realizedPnl) FROM WebSocketFuturesPositionUpdateData p GROUP BY p.pair")
    List<Object[]> getTotalRealizedPnlByPair();

    /**
     * Get average leverage by pair
     */
    @Query("SELECT p.pair, AVG(p.leverage) FROM WebSocketFuturesPositionUpdateData p WHERE p.status = 'active' GROUP BY p.pair")
    List<Object[]> getAverageLeverageByPair();

    /**
     * Get position statistics
     */
    @Query(value = "SELECT " +
                   "pair, " +
                   "side, " +
                   "COUNT(*) as updateCount, " +
                   "AVG(leverage) as avgLeverage, " +
                   "SUM(unrealized_pnl) as totalUnrealizedPnl, " +
                   "SUM(realized_pnl) as totalRealizedPnl, " +
                   "AVG(roi) as avgRoi " +
                   "FROM websocket_futures_position_update_data " +
                   "WHERE update_timestamp >= :fromTime " +
                   "GROUP BY pair, side", 
           nativeQuery = true)
    List<Object[]> getPositionStatistics(@Param("fromTime") Long fromTime);

    /**
     * Find positions near liquidation (within specified percentage of liquidation price)
     */
    @Query("SELECT p FROM WebSocketFuturesPositionUpdateData p WHERE " +
           "p.status = 'active' AND " +
           "ABS(p.currentPrice - p.liquidationPrice) / p.liquidationPrice * 100 <= :percentage " +
           "ORDER BY ABS(p.currentPrice - p.liquidationPrice) ASC")
    List<WebSocketFuturesPositionUpdateData> findPositionsNearLiquidation(@Param("percentage") Double percentage);

    /**
     * Get top profitable positions
     */
    @Query(value = "SELECT * FROM websocket_futures_position_update_data WHERE status = 'active' ORDER BY total_pnl DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesPositionUpdateData> getTopProfitablePositions(@Param("limit") int limit);

    /**
     * Get top losing positions
     */
    @Query(value = "SELECT * FROM websocket_futures_position_update_data WHERE status = 'active' ORDER BY total_pnl ASC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesPositionUpdateData> getTopLosingPositions(@Param("limit") int limit);

    /**
     * Find positions by channel name
     */
    List<WebSocketFuturesPositionUpdateData> findByChannelNameOrderByUpdateTimestampDesc(String channelName);

    /**
     * Delete old position updates by record timestamp
     */
    void deleteByRecordTimestampBefore(LocalDateTime cutoff);
}
