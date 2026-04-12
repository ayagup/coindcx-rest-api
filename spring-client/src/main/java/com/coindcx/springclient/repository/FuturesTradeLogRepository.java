package com.coindcx.springclient.repository;

import com.coindcx.springclient.entity.FuturesTradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for persisted futures trade lifecycle logs.
 */
@Repository
public interface FuturesTradeLogRepository extends JpaRepository<FuturesTradeLog, Long> {

    List<FuturesTradeLog> findByPositionIdOrderByRecordTimestampDesc(String positionId);

    List<FuturesTradeLog> findByOrderIdOrderByRecordTimestampDesc(String orderId);

    List<FuturesTradeLog> findByPairOrderByRecordTimestampDesc(String pair);

    List<FuturesTradeLog> findByEventTypeOrderByRecordTimestampDesc(String eventType);

    @Query(value = "SELECT * FROM futures_trade_log ORDER BY COALESCE(event_timestamp, 0) DESC, record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<FuturesTradeLog> findRecentLogs(@Param("limit") int limit);

    void deleteByRecordTimestampBefore(LocalDateTime cutoff);
}
