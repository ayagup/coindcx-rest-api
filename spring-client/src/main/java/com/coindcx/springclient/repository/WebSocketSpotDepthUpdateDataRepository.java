package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotDepthUpdateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Depth Update (Order Book Changes) Data
 */
@Repository
public interface WebSocketSpotDepthUpdateDataRepository extends JpaRepository<WebSocketSpotDepthUpdateData, Long> {

    // Find by symbol
    List<WebSocketSpotDepthUpdateData> findBySymbolOrderByEventTimeDesc(String symbol);

    @Query(value = "SELECT * FROM websocket_spot_depth_update_data WHERE symbol = :symbol ORDER BY event_time DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotDepthUpdateData> findBySymbolWithLimit(@Param("symbol") String symbol, @Param("limit") int limit);

    // Find latest update for a symbol
    Optional<WebSocketSpotDepthUpdateData> findFirstBySymbolOrderByEventTimeDesc(String symbol);

    // Find by symbol and depth level
    List<WebSocketSpotDepthUpdateData> findBySymbolAndDepthLevelOrderByEventTimeDesc(String symbol, Integer depthLevel);

    // Find by event time range
    @Query("SELECT d FROM WebSocketSpotDepthUpdateData d WHERE d.eventTime BETWEEN :startTime AND :endTime ORDER BY d.eventTime DESC")
    List<WebSocketSpotDepthUpdateData> findByEventTimeRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // Find by symbol and event time range
    @Query("SELECT d FROM WebSocketSpotDepthUpdateData d WHERE d.symbol = :symbol AND d.eventTime BETWEEN :startTime AND :endTime ORDER BY d.eventTime DESC")
    List<WebSocketSpotDepthUpdateData> findBySymbolAndEventTimeRange(@Param("symbol") String symbol, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // Find by timestamp range
    @Query("SELECT d FROM WebSocketSpotDepthUpdateData d WHERE d.timestamp BETWEEN :startTime AND :endTime ORDER BY d.timestamp DESC")
    List<WebSocketSpotDepthUpdateData> findByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // Find by symbol and timestamp range
    @Query("SELECT d FROM WebSocketSpotDepthUpdateData d WHERE d.symbol = :symbol AND d.timestamp BETWEEN :startTime AND :endTime ORDER BY d.timestamp DESC")
    List<WebSocketSpotDepthUpdateData> findBySymbolAndTimestampRange(@Param("symbol") String symbol, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // Find by record timestamp range
    @Query("SELECT d FROM WebSocketSpotDepthUpdateData d WHERE d.recordTimestamp BETWEEN :startTime AND :endTime ORDER BY d.recordTimestamp DESC")
    List<WebSocketSpotDepthUpdateData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // Find recent records
    @Query(value = "SELECT * FROM websocket_spot_depth_update_data ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotDepthUpdateData> findRecentRecords(@Param("limit") int limit);

    // Find distinct symbols
    @Query("SELECT DISTINCT d.symbol FROM WebSocketSpotDepthUpdateData d ORDER BY d.symbol")
    List<String> findDistinctSymbols();

    // Find distinct depth levels
    @Query("SELECT DISTINCT d.depthLevel FROM WebSocketSpotDepthUpdateData d WHERE d.depthLevel IS NOT NULL ORDER BY d.depthLevel")
    List<Integer> findDistinctDepthLevels();

    // Find distinct depth levels by symbol
    @Query("SELECT DISTINCT d.depthLevel FROM WebSocketSpotDepthUpdateData d WHERE d.symbol = :symbol AND d.depthLevel IS NOT NULL ORDER BY d.depthLevel")
    List<Integer> findDistinctDepthLevelsBySymbol(@Param("symbol") String symbol);

    // Find by version
    Optional<WebSocketSpotDepthUpdateData> findByVersion(Long version);

    // Find by symbol and version range
    @Query("SELECT d FROM WebSocketSpotDepthUpdateData d WHERE d.symbol = :symbol AND d.version BETWEEN :minVersion AND :maxVersion ORDER BY d.version DESC")
    List<WebSocketSpotDepthUpdateData> findBySymbolAndVersionRange(@Param("symbol") String symbol, @Param("minVersion") Long minVersion, @Param("maxVersion") Long maxVersion);

    // Get latest version by symbol
    @Query("SELECT MAX(d.version) FROM WebSocketSpotDepthUpdateData d WHERE d.symbol = :symbol")
    Long getLatestVersionBySymbol(@Param("symbol") String symbol);

    // Count by symbol
    long countBySymbol(String symbol);

    // Count by symbol and depth level
    long countBySymbolAndDepthLevel(String symbol, Integer depthLevel);

    // Delete old records
    void deleteByRecordTimestampBefore(LocalDateTime cutoff);

    // Find by channel name
    List<WebSocketSpotDepthUpdateData> findByChannelNameOrderByEventTimeDesc(String channelName);

    // Find updates between two versions for a symbol
    @Query("SELECT d FROM WebSocketSpotDepthUpdateData d WHERE d.symbol = :symbol AND d.version > :fromVersion AND d.version <= :toVersion ORDER BY d.version ASC")
    List<WebSocketSpotDepthUpdateData> findUpdatesBetweenVersions(@Param("symbol") String symbol, @Param("fromVersion") Long fromVersion, @Param("toVersion") Long toVersion);

    // Get version history for a symbol
    @Query("SELECT d FROM WebSocketSpotDepthUpdateData d WHERE d.symbol = :symbol ORDER BY d.version DESC")
    List<WebSocketSpotDepthUpdateData> findVersionHistory(@Param("symbol") String symbol);

    @Query(value = "SELECT * FROM websocket_spot_depth_update_data WHERE symbol = :symbol ORDER BY version DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotDepthUpdateData> findVersionHistoryWithLimit(@Param("symbol") String symbol, @Param("limit") int limit);
}
