package com.coindcx.springclient.repository;

import com.coindcx.springclient.model.WebSocketBalanceUpdateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface WebSocketBalanceUpdateDataRepository extends JpaRepository<WebSocketBalanceUpdateData, Long> {
    
    // Find by balance ID
    List<WebSocketBalanceUpdateData> findByBalanceIdOrderByRecordTimestampDesc(String balanceId);
    
    @Query(value = "SELECT * FROM websocket_balance_update_data WHERE balance_id = :balanceId ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketBalanceUpdateData> findByBalanceIdWithLimit(@Param("balanceId") String balanceId, @Param("limit") int limit);
    
    WebSocketBalanceUpdateData findFirstByBalanceIdOrderByRecordTimestampDesc(String balanceId);
    
    // Find by currency
    List<WebSocketBalanceUpdateData> findByCurrencyShortNameOrderByRecordTimestampDesc(String currencyShortName);
    
    @Query(value = "SELECT * FROM websocket_balance_update_data WHERE currency_short_name = :currency ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketBalanceUpdateData> findByCurrencyShortNameWithLimit(@Param("currency") String currencyShortName, @Param("limit") int limit);
    
    WebSocketBalanceUpdateData findFirstByCurrencyShortNameOrderByRecordTimestampDesc(String currencyShortName);
    
    // Find by balance ID and currency
    List<WebSocketBalanceUpdateData> findByBalanceIdAndCurrencyShortNameOrderByRecordTimestampDesc(String balanceId, String currencyShortName);
    
    // Time range queries
    @Query("SELECT b FROM WebSocketBalanceUpdateData b WHERE b.recordTimestamp >= :startTime AND b.recordTimestamp <= :endTime ORDER BY b.recordTimestamp DESC")
    List<WebSocketBalanceUpdateData> findByRecordTimestampRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT b FROM WebSocketBalanceUpdateData b WHERE b.balanceId = :balanceId AND b.recordTimestamp >= :startTime AND b.recordTimestamp <= :endTime ORDER BY b.recordTimestamp DESC")
    List<WebSocketBalanceUpdateData> findByBalanceIdAndRecordTimestampRange(@Param("balanceId") String balanceId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT b FROM WebSocketBalanceUpdateData b WHERE b.currencyShortName = :currency AND b.recordTimestamp >= :startTime AND b.recordTimestamp <= :endTime ORDER BY b.recordTimestamp DESC")
    List<WebSocketBalanceUpdateData> findByCurrencyAndRecordTimestampRange(@Param("currency") String currencyShortName, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // Recent updates
    @Query(value = "SELECT * FROM websocket_balance_update_data ORDER BY record_timestamp DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketBalanceUpdateData> findRecentBalanceUpdates(@Param("limit") int limit);
    
    // Balance range queries
    @Query("SELECT b FROM WebSocketBalanceUpdateData b WHERE b.balance >= :minBalance AND b.balance <= :maxBalance ORDER BY b.balance DESC")
    List<WebSocketBalanceUpdateData> findByBalanceRange(@Param("minBalance") BigDecimal minBalance, @Param("maxBalance") BigDecimal maxBalance);
    
    @Query("SELECT b FROM WebSocketBalanceUpdateData b WHERE b.lockedBalance >= :minLocked AND b.lockedBalance <= :maxLocked ORDER BY b.lockedBalance DESC")
    List<WebSocketBalanceUpdateData> findByLockedBalanceRange(@Param("minLocked") BigDecimal minLocked, @Param("maxLocked") BigDecimal maxLocked);
    
    @Query("SELECT b FROM WebSocketBalanceUpdateData b WHERE b.availableBalance >= :minAvailable AND b.availableBalance <= :maxAvailable ORDER BY b.availableBalance DESC")
    List<WebSocketBalanceUpdateData> findByAvailableBalanceRange(@Param("minAvailable") BigDecimal minAvailable, @Param("maxAvailable") BigDecimal maxAvailable);
    
    // Find balances with locked amount
    @Query("SELECT b FROM WebSocketBalanceUpdateData b WHERE b.lockedBalance > 0 ORDER BY b.lockedBalance DESC")
    List<WebSocketBalanceUpdateData> findBalancesWithLocked();
    
    @Query(value = "SELECT * FROM websocket_balance_update_data WHERE locked_balance > 0 ORDER BY locked_balance DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketBalanceUpdateData> findBalancesWithLockedLimit(@Param("limit") int limit);
    
    // Find large balances
    @Query(value = "SELECT * FROM websocket_balance_update_data WHERE balance >= :minBalance ORDER BY balance DESC LIMIT :limit", nativeQuery = true)
    List<WebSocketBalanceUpdateData> findLargeBalances(@Param("minBalance") BigDecimal minBalance, @Param("limit") int limit);
    
    // Distinct values
    @Query("SELECT DISTINCT b.balanceId FROM WebSocketBalanceUpdateData b ORDER BY b.balanceId")
    List<String> findDistinctBalanceIds();
    
    @Query("SELECT DISTINCT b.currencyShortName FROM WebSocketBalanceUpdateData b ORDER BY b.currencyShortName")
    List<String> findDistinctCurrencies();
    
    @Query("SELECT DISTINCT b.currencyId FROM WebSocketBalanceUpdateData b ORDER BY b.currencyId")
    List<String> findDistinctCurrencyIds();
    
    // Count queries
    long countByBalanceId(String balanceId);
    
    long countByCurrencyShortName(String currencyShortName);
    
    @Query("SELECT COUNT(b) FROM WebSocketBalanceUpdateData b WHERE b.lockedBalance > 0")
    long countBalancesWithLocked();
    
    // Statistics queries
    @Query("SELECT new map(COUNT(b) as totalRecords, " +
           "COUNT(DISTINCT b.balanceId) as uniqueBalanceIds, " +
           "COUNT(DISTINCT b.currencyShortName) as uniqueCurrencies, " +
           "SUM(b.balance) as totalBalance, " +
           "SUM(b.lockedBalance) as totalLockedBalance, " +
           "SUM(b.availableBalance) as totalAvailableBalance, " +
           "AVG(b.balance) as avgBalance, " +
           "MAX(b.balance) as maxBalance, " +
           "MIN(b.balance) as minBalance) " +
           "FROM WebSocketBalanceUpdateData b")
    Map<String, Object> getBalanceStatistics();
    
    @Query("SELECT new map(b.currencyShortName as currency, " +
           "COUNT(b) as recordCount, " +
           "SUM(b.balance) as totalBalance, " +
           "SUM(b.lockedBalance) as totalLockedBalance, " +
           "SUM(b.availableBalance) as totalAvailableBalance, " +
           "AVG(b.balance) as avgBalance, " +
           "MAX(b.balance) as maxBalance) " +
           "FROM WebSocketBalanceUpdateData b " +
           "GROUP BY b.currencyShortName " +
           "ORDER BY totalBalance DESC")
    List<Map<String, Object>> getBalancesByCurrency();
    
    @Query("SELECT new map(b.currencyShortName as currency, " +
           "SUM(b.lockedBalance) as totalLocked, " +
           "COUNT(b) as recordCount) " +
           "FROM WebSocketBalanceUpdateData b " +
           "WHERE b.lockedBalance > 0 " +
           "GROUP BY b.currencyShortName " +
           "ORDER BY totalLocked DESC")
    List<Map<String, Object>> getLockedBalancesByCurrency();
    
    @Query("SELECT new map(b.balanceId as balanceId, " +
           "b.currencyShortName as currency, " +
           "b.balance as balance, " +
           "b.lockedBalance as lockedBalance, " +
           "b.availableBalance as availableBalance, " +
           "b.recordTimestamp as lastUpdate) " +
           "FROM WebSocketBalanceUpdateData b " +
           "WHERE b.recordTimestamp = (" +
           "    SELECT MAX(b2.recordTimestamp) " +
           "    FROM WebSocketBalanceUpdateData b2 " +
           "    WHERE b2.balanceId = b.balanceId" +
           ") " +
           "ORDER BY b.balance DESC")
    List<Map<String, Object>> getLatestBalancesForAllIds();
    
    @Query("SELECT new map(b.currencyShortName as currency, " +
           "b.balance as balance, " +
           "b.lockedBalance as lockedBalance, " +
           "b.availableBalance as availableBalance, " +
           "b.recordTimestamp as lastUpdate) " +
           "FROM WebSocketBalanceUpdateData b " +
           "WHERE b.recordTimestamp = (" +
           "    SELECT MAX(b2.recordTimestamp) " +
           "    FROM WebSocketBalanceUpdateData b2 " +
           "    WHERE b2.currencyShortName = b.currencyShortName" +
           ") " +
           "ORDER BY b.balance DESC")
    List<Map<String, Object>> getLatestBalancesForAllCurrencies();
    
    // Cleanup
    void deleteByRecordTimestampBefore(LocalDateTime cutoffTime);
}
