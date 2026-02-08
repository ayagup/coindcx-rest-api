package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketSpotCurrentPriceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WebSocket Spot Current Prices Data
 */
@Repository
public interface WebSocketSpotCurrentPriceDataRepository extends JpaRepository<WebSocketSpotCurrentPriceData, Long> {

    // Find by interval
    List<WebSocketSpotCurrentPriceData> findByPriceIntervalOrderByTimestampDesc(String interval);

    @Query(value = "SELECT * FROM websocket_spot_current_price_data WHERE price_interval = :interval ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotCurrentPriceData> findByIntervalWithLimit(@Param("interval") String interval, @Param("limit") int limit);

    // Find latest price update for an interval
    Optional<WebSocketSpotCurrentPriceData> findFirstByPriceIntervalOrderByTimestampDesc(String interval);

    // Find by timestamp range
    @Query("SELECT d FROM WebSocketSpotCurrentPriceData d WHERE d.timestamp BETWEEN :startTime AND :endTime ORDER BY d.timestamp DESC")
    List<WebSocketSpotCurrentPriceData> findByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // Find by interval and timestamp range
    @Query("SELECT d FROM WebSocketSpotCurrentPriceData d WHERE d.priceInterval = :interval AND d.timestamp BETWEEN :startTime AND :endTime ORDER BY d.timestamp DESC")
    List<WebSocketSpotCurrentPriceData> findByIntervalAndTimestampRange(@Param("interval") String interval, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    // Find by record timestamp range
    @Query("SELECT d FROM WebSocketSpotCurrentPriceData d WHERE d.recordTimestamp BETWEEN :startTime AND :endTime ORDER BY d.recordTimestamp DESC")
    List<WebSocketSpotCurrentPriceData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // Find recent records
    @Query(value = "SELECT * FROM websocket_spot_current_price_data ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketSpotCurrentPriceData> findRecentRecords(@Param("limit") int limit);

    // Find distinct intervals
    @Query("SELECT DISTINCT d.priceInterval FROM WebSocketSpotCurrentPriceData d WHERE d.priceInterval IS NOT NULL ORDER BY d.priceInterval")
    List<String> findDistinctIntervals();

    // Find by version
    Optional<WebSocketSpotCurrentPriceData> findByVersion(Long version);

    // Find by interval and version range
    @Query("SELECT d FROM WebSocketSpotCurrentPriceData d WHERE d.priceInterval = :interval AND d.version BETWEEN :minVersion AND :maxVersion ORDER BY d.version DESC")
    List<WebSocketSpotCurrentPriceData> findByIntervalAndVersionRange(@Param("interval") String interval, @Param("minVersion") Long minVersion, @Param("maxVersion") Long maxVersion);

    // Get latest version by interval
    @Query("SELECT MAX(d.version) FROM WebSocketSpotCurrentPriceData d WHERE d.priceInterval = :interval")
    Long getLatestVersionByInterval(@Param("interval") String interval);

    // Count by interval
    long countByPriceInterval(String interval);

    // Delete old records
    void deleteByRecordTimestampBefore(LocalDateTime cutoff);

    // Find by channel name
    List<WebSocketSpotCurrentPriceData> findByChannelNameOrderByTimestampDesc(String channelName);

    // Get price update frequency statistics
    @Query("SELECT d.priceInterval, COUNT(d) FROM WebSocketSpotCurrentPriceData d GROUP BY d.priceInterval")
    List<Object[]> getUpdateFrequencyStatistics();

    // Find records within specific time window
    @Query("SELECT d FROM WebSocketSpotCurrentPriceData d WHERE d.priceInterval = :interval AND d.timestamp >= :fromTime ORDER BY d.timestamp DESC")
    List<WebSocketSpotCurrentPriceData> findRecentByInterval(@Param("interval") String interval, @Param("fromTime") Long fromTime);

    // Count records in time range
    @Query("SELECT COUNT(d) FROM WebSocketSpotCurrentPriceData d WHERE d.timestamp BETWEEN :startTime AND :endTime")
    long countByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
