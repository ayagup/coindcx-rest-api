package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.WebSocketFuturesInstrumentPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WebSocketFuturesInstrumentPriceRepository extends JpaRepository<WebSocketFuturesInstrumentPrice, Long> {
    
    /**
     * Find all prices for a specific instrument ordered by event timestamp
     */
    List<WebSocketFuturesInstrumentPrice> findByInstrumentOrderByEventTimestampDesc(String instrument);
    
    /**
     * Find recent prices for a specific instrument
     */
    @Query(value = "SELECT * FROM websocket_futures_instrument_price WHERE instrument = :instrument ORDER BY event_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketFuturesInstrumentPrice> findRecentByInstrument(@Param("instrument") String instrument, @Param("limit") int limit);
    
    /**
     * Find latest price for a specific instrument
     */
    Optional<WebSocketFuturesInstrumentPrice> findFirstByInstrumentOrderByEventTimestampDesc(String instrument);
    
    /**
     * Find all prices by parent timestamp (all instruments from same snapshot)
     */
    List<WebSocketFuturesInstrumentPrice> findByParentTimestampOrderByInstrument(Long parentTimestamp);
    
    /**
     * Find prices for a specific instrument within timestamp range
     */
    @Query("SELECT p FROM WebSocketFuturesInstrumentPrice p WHERE p.instrument = :instrument AND p.eventTimestamp >= :startTime AND p.eventTimestamp <= :endTime ORDER BY p.eventTimestamp DESC")
    List<WebSocketFuturesInstrumentPrice> findByInstrumentAndTimestampRange(
        @Param("instrument") String instrument,
        @Param("startTime") Long startTime,
        @Param("endTime") Long endTime
    );
    
    /**
     * Find all distinct instruments
     */
    @Query("SELECT DISTINCT p.instrument FROM WebSocketFuturesInstrumentPrice p ORDER BY p.instrument")
    List<String> findAllDistinctInstruments();
    
    /**
     * Find recent prices for all instruments (latest snapshot)
     */
    @Query(value = "SELECT * FROM websocket_futures_instrument_price WHERE parent_timestamp = (SELECT MAX(parent_timestamp) FROM websocket_futures_instrument_price) ORDER BY instrument", nativeQuery = true)
    List<WebSocketFuturesInstrumentPrice> findLatestPricesAllInstruments();
    
    /**
     * Find prices with mark price only (excluding instruments without mark price)
     */
    @Query("SELECT p FROM WebSocketFuturesInstrumentPrice p WHERE p.instrument = :instrument AND p.markPrice IS NOT NULL ORDER BY p.eventTimestamp DESC")
    List<WebSocketFuturesInstrumentPrice> findByInstrumentWithMarkPrice(@Param("instrument") String instrument);
    
    /**
     * Delete old records by record timestamp
     */
    @Query("DELETE FROM WebSocketFuturesInstrumentPrice p WHERE p.recordTimestamp < :cutoffTime")
    void deleteOldRecords(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Count records for a specific instrument
     */
    long countByInstrument(String instrument);
    
    /**
     * Find prices by version sequence
     */
    List<WebSocketFuturesInstrumentPrice> findByVersionSequenceOrderByInstrument(Long versionSequence);
}
