package com.coindcx.springclient.repository;

import com.coindcx.springclient.model.WebSocketFuturesOrderbookData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WebSocketFuturesOrderbookDataRepository extends JpaRepository<WebSocketFuturesOrderbookData, Long> {
    
    // Find by channel name
    List<WebSocketFuturesOrderbookData> findByChannelNameOrderByTimestampDesc(String channelName);
    
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data WHERE channel_name = :channelName ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderbookData> findByChannelNameWithLimit(@Param("channelName") String channelName, @Param("limit") int limit);
    
    // Find by instrument name
    List<WebSocketFuturesOrderbookData> findByInstrumentNameOrderByTimestampDesc(String instrumentName);
    
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data WHERE instrument_name = :instrumentName ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderbookData> findByInstrumentNameWithLimit(@Param("instrumentName") String instrumentName, @Param("limit") int limit);
    
    // Find by depth
    List<WebSocketFuturesOrderbookData> findByDepthOrderByTimestampDesc(Integer depth);
    
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data WHERE depth = :depth ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderbookData> findByDepthWithLimit(@Param("depth") Integer depth, @Param("limit") int limit);
    
    // Combined filters
    List<WebSocketFuturesOrderbookData> findByInstrumentNameAndDepthOrderByTimestampDesc(String instrumentName, Integer depth);
    
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data WHERE instrument_name = :instrumentName AND depth = :depth ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderbookData> findByInstrumentNameAndDepthWithLimit(@Param("instrumentName") String instrumentName, @Param("depth") Integer depth, @Param("limit") int limit);
    
    // Timestamp range queries
    @Query("SELECT o FROM WebSocketFuturesOrderbookData o WHERE o.timestamp >= :startTime AND o.timestamp <= :endTime ORDER BY o.timestamp DESC")
    List<WebSocketFuturesOrderbookData> findByTimestampRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT o FROM WebSocketFuturesOrderbookData o WHERE o.channelName = :channelName AND o.timestamp >= :startTime AND o.timestamp <= :endTime ORDER BY o.timestamp DESC")
    List<WebSocketFuturesOrderbookData> findByChannelNameAndTimestampRange(@Param("channelName") String channelName, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT o FROM WebSocketFuturesOrderbookData o WHERE o.instrumentName = :instrumentName AND o.timestamp >= :startTime AND o.timestamp <= :endTime ORDER BY o.timestamp DESC")
    List<WebSocketFuturesOrderbookData> findByInstrumentNameAndTimestampRange(@Param("instrumentName") String instrumentName, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    @Query("SELECT o FROM WebSocketFuturesOrderbookData o WHERE o.recordTimestamp >= :startTime AND o.recordTimestamp <= :endTime ORDER BY o.recordTimestamp DESC")
    List<WebSocketFuturesOrderbookData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // Recent snapshots
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderbookData> findRecentOrderbooks(@Param("limit") int limit);
    
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesOrderbookData> findRecentByRecordTimestamp(@Param("limit") int limit);
    
    // Latest by channel/instrument
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data WHERE channel_name = :channelName ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesOrderbookData> findLatestByChannelName(@Param("channelName") String channelName);
    
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data WHERE instrument_name = :instrumentName ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesOrderbookData> findLatestByInstrumentName(@Param("instrumentName") String instrumentName);
    
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data WHERE instrument_name = :instrumentName AND depth = :depth ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesOrderbookData> findLatestByInstrumentNameAndDepth(@Param("instrumentName") String instrumentName, @Param("depth") Integer depth);
    
    // Version sequence queries
    @Query("SELECT o FROM WebSocketFuturesOrderbookData o WHERE o.versionSequence >= :minVersion ORDER BY o.versionSequence DESC")
    List<WebSocketFuturesOrderbookData> findByVersionSequenceGreaterThanEqual(@Param("minVersion") Long minVersion);
    
    @Query("SELECT o FROM WebSocketFuturesOrderbookData o WHERE o.channelName = :channelName AND o.versionSequence >= :minVersion ORDER BY o.versionSequence DESC")
    List<WebSocketFuturesOrderbookData> findByChannelNameAndVersionSequence(@Param("channelName") String channelName, @Param("minVersion") Long minVersion);
    
    @Query(value = "SELECT * FROM websocket_futures_orderbook_data WHERE channel_name = :channelName ORDER BY version_sequence DESC LIMIT 1", nativeQuery = true)
    Optional<WebSocketFuturesOrderbookData> findLatestVersionByChannelName(@Param("channelName") String channelName);
    
    // Distinct values
    @Query("SELECT DISTINCT o.channelName FROM WebSocketFuturesOrderbookData o ORDER BY o.channelName")
    List<String> findDistinctChannelNames();
    
    @Query("SELECT DISTINCT o.instrumentName FROM WebSocketFuturesOrderbookData o ORDER BY o.instrumentName")
    List<String> findDistinctInstrumentNames();
    
    @Query("SELECT DISTINCT o.depth FROM WebSocketFuturesOrderbookData o ORDER BY o.depth")
    List<Integer> findDistinctDepths();
    
    // Count queries
    long countByChannelName(String channelName);
    
    long countByInstrumentName(String instrumentName);
    
    long countByDepth(Integer depth);
    
    long countByInstrumentNameAndDepth(String instrumentName, Integer depth);
    
    // Statistics queries
    @Query("SELECT COUNT(DISTINCT o.channelName) FROM WebSocketFuturesOrderbookData o")
    long countDistinctChannels();
    
    @Query("SELECT COUNT(DISTINCT o.instrumentName) FROM WebSocketFuturesOrderbookData o")
    long countDistinctInstruments();
    
    @Query("SELECT MIN(o.timestamp) FROM WebSocketFuturesOrderbookData o")
    Long findOldestTimestamp();
    
    @Query("SELECT MAX(o.timestamp) FROM WebSocketFuturesOrderbookData o")
    Long findLatestTimestamp();
    
    @Query("SELECT MIN(o.timestamp) FROM WebSocketFuturesOrderbookData o WHERE o.channelName = :channelName")
    Long findOldestTimestampByChannel(@Param("channelName") String channelName);
    
    @Query("SELECT MAX(o.timestamp) FROM WebSocketFuturesOrderbookData o WHERE o.channelName = :channelName")
    Long findLatestTimestampByChannel(@Param("channelName") String channelName);
    
    // Cleanup
    void deleteByRecordTimestampBefore(LocalDateTime cutoffTime);
    
    @Query(value = "DELETE FROM websocket_futures_orderbook_data WHERE channel_name = :channelName AND timestamp < :cutoffTime", nativeQuery = true)
    void deleteByChannelNameAndTimestampBefore(@Param("channelName") String channelName, @Param("cutoffTime") Long cutoffTime);
}
