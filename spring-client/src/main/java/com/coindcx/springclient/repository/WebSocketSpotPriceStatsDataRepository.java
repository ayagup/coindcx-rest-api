package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotPriceStatsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Price Stats Data
 */
@Repository
public interface WebSocketSpotPriceStatsDataRepository extends JpaRepository<WebSocketSpotPriceStatsData, Long> {

    // Find by interval
    List<WebSocketSpotPriceStatsData> findByStatsIntervalOrderByTimestampDesc(String interval);

    @Query(value = "SELECT * FROM websocket_spot_price_stats_data WHERE stats_interval = :interval ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotPriceStatsData> findByIntervalWithLimit(@Param("interval") String interval, @Param("limit") int limit);

    // Find latest stats for an interval
    Optional<WebSocketSpotPriceStatsData> findFirstByStatsIntervalOrderByTimestampDesc(String interval);

    // Find by timestamp range
    @Query("SELECT d FROM WebSocketSpotPriceStatsData d WHERE d.timestamp BETWEEN :startTime AND :endTime ORDER BY d.timestamp DESC")
    List<WebSocketSpotPriceStatsData> findByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // Find by interval and timestamp range
    @Query("SELECT d FROM WebSocketSpotPriceStatsData d WHERE d.statsInterval = :interval AND d.timestamp BETWEEN :startTime AND :endTime ORDER BY d.timestamp DESC")
    List<WebSocketSpotPriceStatsData> findByIntervalAndTimestampRange(@Param("interval") String interval, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // Find by record timestamp range
    @Query("SELECT d FROM WebSocketSpotPriceStatsData d WHERE d.recordTimestamp BETWEEN :startTime AND :endTime ORDER BY d.recordTimestamp DESC")
    List<WebSocketSpotPriceStatsData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // Find recent records
    @Query(value = "SELECT * FROM websocket_spot_price_stats_data ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotPriceStatsData> findRecentRecords(@Param("limit") int limit);

    // Find distinct intervals
    @Query("SELECT DISTINCT d.statsInterval FROM WebSocketSpotPriceStatsData d WHERE d.statsInterval IS NOT NULL ORDER BY d.statsInterval")
    List<String> findDistinctIntervals();

    // Find by version
    Optional<WebSocketSpotPriceStatsData> findByVersion(Long version);

    // Find by interval and version range
    @Query("SELECT d FROM WebSocketSpotPriceStatsData d WHERE d.statsInterval = :interval AND d.version BETWEEN :minVersion AND :maxVersion ORDER BY d.version DESC")
    List<WebSocketSpotPriceStatsData> findByIntervalAndVersionRange(@Param("interval") String interval, @Param("minVersion") Long minVersion, @Param("maxVersion") Long maxVersion);

    // Get latest version by interval
    @Query("SELECT MAX(d.version) FROM WebSocketSpotPriceStatsData d WHERE d.statsInterval = :interval")
    Long getLatestVersionByInterval(@Param("interval") String interval);

    // Count by interval
    long countByStatsInterval(String interval);

    // Delete old records
    void deleteByRecordTimestampBefore(LocalDateTime cutoff);

    // Find by channel name
    List<WebSocketSpotPriceStatsData> findByChannelNameOrderByTimestampDesc(String channelName);

    // Find by price change range (for gainers/losers)
    @Query("SELECT d FROM WebSocketSpotPriceStatsData d WHERE d.priceChangePercent BETWEEN :minChange AND :maxChange ORDER BY d.priceChangePercent DESC")
    List<WebSocketSpotPriceStatsData> findByPriceChangeRange(@Param("minChange") Double minChange, @Param("maxChange") Double maxChange);

    // Find top gainers
    @Query(value = "SELECT * FROM websocket_spot_price_stats_data WHERE price_change_percent > 0 ORDER BY price_change_percent DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotPriceStatsData> findTopGainers(@Param("limit") int limit);

    // Find top losers
    @Query(value = "SELECT * FROM websocket_spot_price_stats_data WHERE price_change_percent < 0 ORDER BY price_change_percent ASC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotPriceStatsData> findTopLosers(@Param("limit") int limit);

    // Find by volume range
    @Query("SELECT d FROM WebSocketSpotPriceStatsData d WHERE d.volume24h BETWEEN :minVolume AND :maxVolume ORDER BY d.volume24h DESC")
    List<WebSocketSpotPriceStatsData> findByVolumeRange(@Param("minVolume") Double minVolume, @Param("maxVolume") Double maxVolume);

    // Find high volume pairs
    @Query(value = "SELECT * FROM websocket_spot_price_stats_data WHERE volume24h > :volumeThreshold ORDER BY volume24h DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotPriceStatsData> findHighVolumePairs(@Param("volumeThreshold") Double volumeThreshold, @Param("limit") int limit);

    // Get average price change
    @Query("SELECT AVG(d.priceChangePercent) FROM WebSocketSpotPriceStatsData d WHERE d.timestamp >= :fromTime")
    Double getAveragePriceChange(@Param("fromTime") Long fromTime);

    // Get average volume
    @Query("SELECT AVG(d.volume24h) FROM WebSocketSpotPriceStatsData d WHERE d.timestamp >= :fromTime")
    Double getAverageVolume(@Param("fromTime") Long fromTime);

    // Count records in time range
    @Query("SELECT COUNT(d) FROM WebSocketSpotPriceStatsData d WHERE d.timestamp BETWEEN :startTime AND :endTime")
    long countByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
