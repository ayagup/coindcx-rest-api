package com.coindcx.springclient.controller;

import com.coindcx.springclient.model.WebSocketBalanceUpdateData;
import com.coindcx.springclient.repository.WebSocketBalanceUpdateDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket/balance-update")
@CrossOrigin(origins = "*")
public class WebSocketBalanceUpdateController {

    @Autowired
    private WebSocketBalanceUpdateDataRepository repository;

    /**
     * Get overall statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", repository.count());
        stats.put("uniqueBalanceIds", repository.findDistinctBalanceIds().size());
        stats.put("uniqueCurrencies", repository.findDistinctCurrencies().size());
        stats.put("balancesWithLocked", repository.countBalancesWithLocked());
        
        // Get detailed statistics
        Map<String, Object> detailedStats = repository.getBalanceStatistics();
        stats.putAll(detailedStats);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all updates for a specific balance ID
     */
    @GetMapping("/balance/{balanceId}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByBalanceId(@PathVariable String balanceId) {
        return ResponseEntity.ok(repository.findByBalanceIdOrderByRecordTimestampDesc(balanceId));
    }

    /**
     * Get latest update for a specific balance ID
     */
    @GetMapping("/balance/{balanceId}/latest")
    public ResponseEntity<WebSocketBalanceUpdateData> getLatestByBalanceId(@PathVariable String balanceId) {
        WebSocketBalanceUpdateData data = repository.findFirstByBalanceIdOrderByRecordTimestampDesc(balanceId);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(data);
    }

    /**
     * Get balance history with limit
     */
    @GetMapping("/balance/{balanceId}/history/{limit}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getBalanceHistory(
            @PathVariable String balanceId,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findByBalanceIdWithLimit(balanceId, limit));
    }

    /**
     * Get all balances for a specific currency
     */
    @GetMapping("/currency/{currency}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByCurrency(@PathVariable String currency) {
        return ResponseEntity.ok(repository.findByCurrencyShortNameOrderByRecordTimestampDesc(currency));
    }

    /**
     * Get latest N balances for a currency
     */
    @GetMapping("/currency/{currency}/latest")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getLatestByCurrency(
            @PathVariable String currency,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(repository.findByCurrencyShortNameWithLimit(currency, limit));
    }

    /**
     * Get latest update for a currency
     */
    @GetMapping("/currency/{currency}/current")
    public ResponseEntity<WebSocketBalanceUpdateData> getLatestBalanceForCurrency(@PathVariable String currency) {
        WebSocketBalanceUpdateData data = repository.findFirstByCurrencyShortNameOrderByRecordTimestampDesc(currency);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(data);
    }

    /**
     * Get balances for specific balance ID and currency
     */
    @GetMapping("/balance/{balanceId}/currency/{currency}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByBalanceIdAndCurrency(
            @PathVariable String balanceId,
            @PathVariable String currency) {
        return ResponseEntity.ok(repository.findByBalanceIdAndCurrencyShortNameOrderByRecordTimestampDesc(balanceId, currency));
    }

    /**
     * Get balances in a time range
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByRecordTimestampRange(startTime, endTime));
    }

    /**
     * Get balance history in time range
     */
    @GetMapping("/balance/{balanceId}/time-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getBalanceIdTimeRange(
            @PathVariable String balanceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByBalanceIdAndRecordTimestampRange(balanceId, startTime, endTime));
    }

    /**
     * Get currency balances in time range
     */
    @GetMapping("/currency/{currency}/time-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getCurrencyTimeRange(
            @PathVariable String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByCurrencyAndRecordTimestampRange(currency, startTime, endTime));
    }

    /**
     * Get recent balance updates
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getRecentBalances(@PathVariable int limit) {
        return ResponseEntity.ok(repository.findRecentBalanceUpdates(limit));
    }

    /**
     * Get balances in a specific range
     */
    @GetMapping("/balance-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByBalanceRange(
            @RequestParam BigDecimal minBalance,
            @RequestParam BigDecimal maxBalance) {
        return ResponseEntity.ok(repository.findByBalanceRange(minBalance, maxBalance));
    }

    /**
     * Get balances with locked amount in range
     */
    @GetMapping("/locked-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByLockedBalanceRange(
            @RequestParam BigDecimal minLocked,
            @RequestParam BigDecimal maxLocked) {
        return ResponseEntity.ok(repository.findByLockedBalanceRange(minLocked, maxLocked));
    }

    /**
     * Get balances with available amount in range
     */
    @GetMapping("/available-range")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getByAvailableBalanceRange(
            @RequestParam BigDecimal minAvailable,
            @RequestParam BigDecimal maxAvailable) {
        return ResponseEntity.ok(repository.findByAvailableBalanceRange(minAvailable, maxAvailable));
    }

    /**
     * Get all balances with locked amount
     */
    @GetMapping("/with-locked")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getBalancesWithLocked() {
        return ResponseEntity.ok(repository.findBalancesWithLocked());
    }

    /**
     * Get top N balances with locked amount
     */
    @GetMapping("/with-locked/top/{limit}")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getTopBalancesWithLocked(@PathVariable int limit) {
        return ResponseEntity.ok(repository.findBalancesWithLockedLimit(limit));
    }

    /**
     * Get large balances (above minimum threshold)
     */
    @GetMapping("/large-balances")
    public ResponseEntity<List<WebSocketBalanceUpdateData>> getLargeBalances(
            @RequestParam BigDecimal minBalance,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(repository.findLargeBalances(minBalance, limit));
    }

    /**
     * Get all distinct balance IDs
     */
    @GetMapping("/balance-ids")
    public ResponseEntity<List<String>> getDistinctBalanceIds() {
        return ResponseEntity.ok(repository.findDistinctBalanceIds());
    }

    /**
     * Get all distinct currencies
     */
    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getDistinctCurrencies() {
        return ResponseEntity.ok(repository.findDistinctCurrencies());
    }

    /**
     * Get all distinct currency IDs
     */
    @GetMapping("/currency-ids")
    public ResponseEntity<List<String>> getDistinctCurrencyIds() {
        return ResponseEntity.ok(repository.findDistinctCurrencyIds());
    }

    /**
     * Get count by balance ID
     */
    @GetMapping("/balance/{balanceId}/count")
    public ResponseEntity<Long> getCountByBalanceId(@PathVariable String balanceId) {
        return ResponseEntity.ok(repository.countByBalanceId(balanceId));
    }

    /**
     * Get count by currency
     */
    @GetMapping("/currency/{currency}/count")
    public ResponseEntity<Long> getCountByCurrency(@PathVariable String currency) {
        return ResponseEntity.ok(repository.countByCurrencyShortName(currency));
    }

    /**
     * Get balances grouped by currency with statistics
     */
    @GetMapping("/by-currency")
    public ResponseEntity<List<Map<String, Object>>> getBalancesByCurrency() {
        return ResponseEntity.ok(repository.getBalancesByCurrency());
    }

    /**
     * Get locked balances grouped by currency
     */
    @GetMapping("/locked-by-currency")
    public ResponseEntity<List<Map<String, Object>>> getLockedBalancesByCurrency() {
        return ResponseEntity.ok(repository.getLockedBalancesByCurrency());
    }

    /**
     * Get latest balances for all balance IDs
     */
    @GetMapping("/latest-all-ids")
    public ResponseEntity<List<Map<String, Object>>> getLatestBalancesForAllIds() {
        return ResponseEntity.ok(repository.getLatestBalancesForAllIds());
    }

    /**
     * Get latest balances for all currencies
     */
    @GetMapping("/latest-all-currencies")
    public ResponseEntity<List<Map<String, Object>>> getLatestBalancesForAllCurrencies() {
        return ResponseEntity.ok(repository.getLatestBalancesForAllCurrencies());
    }

    /**
     * Get balance by record ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketBalanceUpdateData> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old balance data
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanup(
            @RequestParam(required = false, defaultValue = "7") int daysToKeep) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoff);
        long countAfter = repository.count();
        long deleted = countBefore - countAfter;
        
        Map<String, Object> result = new HashMap<>();
        result.put("cutoffDate", cutoff);
        result.put("recordsDeleted", deleted);
        result.put("recordsRemaining", countAfter);
        
        return ResponseEntity.ok(result);
    }
}
