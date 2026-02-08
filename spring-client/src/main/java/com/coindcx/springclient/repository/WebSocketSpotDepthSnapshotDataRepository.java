package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotDepthSnapshotData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Depth Snapshot Data
 */
@Repository
public interface WebSocketSpotDepthSnapshotDataRepository extends JpaRepository<WebSocketSpotDepthSnapshotData, Long> {

    /**
     * Find depth snapshots by symbol
     */
    List<WebSocketSpotDepthSnapshotData> findBySymbolOrderBySnapshotTimestampDesc(String symbol);

    /**
     * Find depth snapshots by symbol with limit
     */
    @Query("SELECT d FROM WebSocketSpotDepthSnapshotData d WHERE d.symbol = :symbol ORDER BY d.snapshotTimestamp DESC LIMIT :limit")
    List<WebSocketSpotDepthSnapshotData> findBySymbolWithLimit(@Param("symbol") String symbol, @Param("limit") int limit);

    /**
     * Find latest depth snapshot for a symbol
     */
    Optional<WebSocketSpotDepthSnapshotData> findFirstBySymbolOrderBySnapshotTimestampDesc(String symbol);

    /**
     * Find depth snapshots by symbol and depth level
     */
    List<WebSocketSpotDepthSnapshotData> findBySymbolAndDepthLevelOrderBySnapshotTimestampDesc(String symbol, Integer depthLevel);

    /**
     * Find depth snapshots within snapshot timestamp range
     */
    @Query("SELECT d FROM WebSocketSpotDepthSnapshotData d WHERE d.snapshotTimestamp >= :startTime AND d.snapshotTimestamp <= :endTime ORDER BY d.snapshotTimestamp DESC")
    List<WebSocketSpotDepthSnapshotData> findBySnapshotTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find depth snapshots by symbol within snapshot timestamp range
     */
    @Query("SELECT d FROM WebSocketSpotDepthSnapshotData d WHERE d.symbol = :symbol AND d.snapshotTimestamp >= :startTime AND d.snapshotTimestamp <= :endTime ORDER BY d.snapshotTimestamp DESC")
    List<WebSocketSpotDepthSnapshotData> findBySymbolAndSnapshotTimestampRange(@Param("symbol") String symbol, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * Find depth snapshots within record timestamp range
     */
    @Query("SELECT d FROM WebSocketSpotDepthSnapshotData d WHERE d.timestamp >= :startTime AND d.timestamp <= :endTime ORDER BY d.snapshotTimestamp DESC")
    List<WebSocketSpotDepthSnapshotData> findByTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Find recent N depth snapshots
     */
    @Query("SELECT d FROM WebSocketSpotDepthSnapshotData d ORDER BY d.snapshotTimestamp DESC LIMIT :limit")
    List<WebSocketSpotDepthSnapshotData> findRecentRecords(@Param("limit") int limit);

    /**
     * Find all distinct symbols
     */
    @Query("SELECT DISTINCT d.symbol FROM WebSocketSpotDepthSnapshotData d ORDER BY d.symbol")
    List<String> findDistinctSymbols();

    /**
     * Find all distinct depth levels
     */
    @Query("SELECT DISTINCT d.depthLevel FROM WebSocketSpotDepthSnapshotData d ORDER BY d.depthLevel")
    List<Integer> findDistinctDepthLevels();

    /**
     * Find depth levels for a specific symbol
     */
    @Query("SELECT DISTINCT d.depthLevel FROM WebSocketSpotDepthSnapshotData d WHERE d.symbol = :symbol ORDER BY d.depthLevel")
    List<Integer> findDistinctDepthLevelsBySymbol(@Param("symbol") String symbol);

    /**
     * Find depth snapshot by version
     */
    Optional<WebSocketSpotDepthSnapshotData> findByVersion(Long version);

    /**
     * Find depth snapshots by symbol and version range
     */
    @Query("SELECT d FROM WebSocketSpotDepthSnapshotData d WHERE d.symbol = :symbol AND d.version >= :minVersion AND d.version <= :maxVersion ORDER BY d.version DESC")
    List<WebSocketSpotDepthSnapshotData> findBySymbolAndVersionRange(@Param("symbol") String symbol, @Param("minVersion") Long minVersion, @Param("maxVersion") Long maxVersion);

    /**
     * Get latest version for a symbol
     */
    @Query("SELECT MAX(d.version) FROM WebSocketSpotDepthSnapshotData d WHERE d.symbol = :symbol")
    Long getLatestVersionBySymbol(@Param("symbol") String symbol);

    /**
     * Count depth snapshots by symbol
     */
    long countBySymbol(String symbol);

    /**
     * Count depth snapshots by symbol and depth level
     */
    long countBySymbolAndDepthLevel(String symbol, Integer depthLevel);

    /**
     * Delete old depth snapshot data
     */
    void deleteByTimestampBefore(LocalDateTime cutoff);

    /**
     * Find depth snapshots by channel name
     */
    List<WebSocketSpotDepthSnapshotData> findByChannelNameOrderBySnapshotTimestampDesc(String channelName);
}
