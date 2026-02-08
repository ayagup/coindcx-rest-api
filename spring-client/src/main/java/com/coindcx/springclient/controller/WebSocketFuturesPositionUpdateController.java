package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketFuturesPositionUpdateData;
import com.coindcx.springclient.repository.WebSocketFuturesPositionUpdateDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/websocket/futures-position-update")
@CrossOrigin(origins = "*")
public class WebSocketFuturesPositionUpdateController {

    @Autowired
    private WebSocketFuturesPositionUpdateDataRepository repository;

    /**
     * Get overall statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", repository.count());
        
        List<String> positionIds = repository.findDistinctPositionIds();
        stats.put("uniquePositions", positionIds.size());
        
        List<String> pairs = repository.findDistinctPairs();
        stats.put("uniquePairs", pairs.size());
        stats.put("pairs", pairs);
        
        // Get latest positions
        List<WebSocketFuturesPositionUpdateData> latestPositions = new ArrayList<>();
        for (String positionId : positionIds) {
            Optional<WebSocketFuturesPositionUpdateData> position = repository.findFirstByPositionIdOrderByUpdateTimestampDesc(positionId);
            position.ifPresent(latestPositions::add);
        }
        
        // Calculate aggregate metrics
        double totalUnrealizedPnl = latestPositions.stream()
            .filter(p -> p.getUnrealizedPnl() != null)
            .mapToDouble(WebSocketFuturesPositionUpdateData::getUnrealizedPnl)
            .sum();
        
        double totalRealizedPnl = latestPositions.stream()
            .filter(p -> p.getRealizedPnl() != null)
            .mapToDouble(WebSocketFuturesPositionUpdateData::getRealizedPnl)
            .sum();
        
        double totalMargin = latestPositions.stream()
            .filter(p -> p.getMargin() != null)
            .mapToDouble(WebSocketFuturesPositionUpdateData::getMargin)
            .sum();
        
        stats.put("totalUnrealizedPnl", totalUnrealizedPnl);
        stats.put("totalRealizedPnl", totalRealizedPnl);
        stats.put("totalPnl", totalUnrealizedPnl + totalRealizedPnl);
        stats.put("totalMargin", totalMargin);
        
        // Count active vs inactive positions
        long activeCount = latestPositions.stream()
            .filter(p -> "active".equals(p.getStatus()))
            .count();
        long inactiveCount = latestPositions.size() - activeCount;
        
        stats.put("activePositions", activeCount);
        stats.put("inactivePositions", inactiveCount);
        
        // Count long vs short positions
        long longCount = latestPositions.stream()
            .filter(p -> "long".equals(p.getSide()))
            .count();
        long shortCount = latestPositions.stream()
            .filter(p -> "short".equals(p.getSide()))
            .count();
        
        stats.put("longPositions", longCount);
        stats.put("shortPositions", shortCount);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get all updates for a specific position
     */
    @GetMapping("/position/{positionId}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPositionId(
            @PathVariable String positionId) {
        return ResponseEntity.ok(repository.findByPositionIdOrderByUpdateTimestampDesc(positionId));
    }

    /**
     * Get latest update for a specific position
     */
    @GetMapping("/position/{positionId}/latest")
    public ResponseEntity<WebSocketFuturesPositionUpdateData> getLatestByPositionId(
            @PathVariable String positionId) {
        return repository.findFirstByPositionIdOrderByUpdateTimestampDesc(positionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get position history with limit
     */
    @GetMapping("/position/{positionId}/history/{limit}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getPositionHistory(
            @PathVariable String positionId,
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findByPositionIdWithLimit(positionId, Math.min(limit, 1000)));
    }

    /**
     * Get all positions for a specific pair
     */
    @GetMapping("/pair/{pair}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPair(
            @PathVariable String pair) {
        return ResponseEntity.ok(repository.findByPairOrderByUpdateTimestampDesc(pair));
    }

    /**
     * Get latest positions for a specific pair
     */
    @GetMapping("/pair/{pair}/latest")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getLatestByPair(
            @PathVariable String pair,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(repository.findByPairWithLimit(pair, Math.min(limit, 100)));
    }

    /**
     * Get positions by side (long/short)
     */
    @GetMapping("/side/{side}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getBySide(
            @PathVariable String side) {
        return ResponseEntity.ok(repository.findBySideOrderByUpdateTimestampDesc(side));
    }

    /**
     * Get positions by status (active/inactive)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(repository.findByStatusOrderByUpdateTimestampDesc(status));
    }

    /**
     * Get positions by pair and side
     */
    @GetMapping("/pair/{pair}/side/{side}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPairAndSide(
            @PathVariable String pair,
            @PathVariable String side) {
        return ResponseEntity.ok(repository.findByPairAndSideOrderByUpdateTimestampDesc(pair, side));
    }

    /**
     * Get positions within timestamp range
     */
    @GetMapping("/timestamp-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByTimestampRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByTimestampRange(startTime, endTime));
    }

    /**
     * Get position updates within timestamp range
     */
    @GetMapping("/position/{positionId}/timestamp-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPositionIdAndTimestampRange(
            @PathVariable String positionId,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByPositionIdAndTimestampRange(positionId, startTime, endTime));
    }

    /**
     * Get pair positions within timestamp range
     */
    @GetMapping("/pair/{pair}/timestamp-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPairAndTimestampRange(
            @PathVariable String pair,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        return ResponseEntity.ok(repository.findByPairAndTimestampRange(pair, startTime, endTime));
    }

    /**
     * Get positions within record time range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByRecordTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(repository.findByRecordTimestampRange(startTime, endTime));
    }

    /**
     * Get recent position updates
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getRecent(
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.findRecentPositionUpdates(Math.min(limit, 1000)));
    }

    /**
     * Get positions within PnL range
     */
    @GetMapping("/pnl-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByPnlRange(
            @RequestParam double minPnl,
            @RequestParam double maxPnl) {
        return ResponseEntity.ok(repository.findByPnlRange(minPnl, maxPnl));
    }

    /**
     * Get positions within leverage range
     */
    @GetMapping("/leverage-range")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByLeverageRange(
            @RequestParam double minLeverage,
            @RequestParam double maxLeverage) {
        return ResponseEntity.ok(repository.findByLeverageRange(minLeverage, maxLeverage));
    }

    /**
     * Get positions by margin currency
     */
    @GetMapping("/margin-currency/{currency}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByMarginCurrency(
            @PathVariable String currency) {
        return ResponseEntity.ok(repository.findByMarginCurrencyOrderByUpdateTimestampDesc(currency));
    }

    /**
     * Get positions by margin type
     */
    @GetMapping("/margin-type/{type}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getByMarginType(
            @PathVariable String type) {
        return ResponseEntity.ok(repository.findByPositionMarginTypeOrderByUpdateTimestampDesc(type));
    }

    /**
     * Get positions near liquidation
     */
    @GetMapping("/near-liquidation/{percentage}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getNearLiquidation(
            @PathVariable double percentage) {
        return ResponseEntity.ok(repository.findPositionsNearLiquidation(percentage));
    }

    /**
     * Get top profitable positions
     */
    @GetMapping("/top-profitable/{limit}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getTopProfitable(
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.getTopProfitablePositions(Math.min(limit, 100)));
    }

    /**
     * Get top losing positions
     */
    @GetMapping("/top-losing/{limit}")
    public ResponseEntity<List<WebSocketFuturesPositionUpdateData>> getTopLosing(
            @PathVariable int limit) {
        return ResponseEntity.ok(repository.getTopLosingPositions(Math.min(limit, 100)));
    }

    /**
     * Get comprehensive position statistics
     */
    @GetMapping("/position-statistics")
    public ResponseEntity<List<Object[]>> getPositionStatistics(
            @RequestParam(required = false, defaultValue = "0") Long fromTime) {
        if (fromTime == 0) {
            // Default to last 24 hours
            fromTime = System.currentTimeMillis() / 1000 - 86400;
        }
        return ResponseEntity.ok(repository.getPositionStatistics(fromTime));
    }

    /**
     * Get total unrealized PnL by pair
     */
    @GetMapping("/total-unrealized-pnl")
    public ResponseEntity<Map<String, Double>> getTotalUnrealizedPnl() {
        List<Object[]> results = repository.getTotalUnrealizedPnlByPair();
        Map<String, Double> pnlMap = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0
                ));
        return ResponseEntity.ok(pnlMap);
    }

    /**
     * Get total realized PnL by pair
     */
    @GetMapping("/total-realized-pnl")
    public ResponseEntity<Map<String, Double>> getTotalRealizedPnl() {
        List<Object[]> results = repository.getTotalRealizedPnlByPair();
        Map<String, Double> pnlMap = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0
                ));
        return ResponseEntity.ok(pnlMap);
    }

    /**
     * Get average leverage by pair
     */
    @GetMapping("/average-leverage")
    public ResponseEntity<Map<String, Double>> getAverageLeverage() {
        List<Object[]> results = repository.getAverageLeverageByPair();
        Map<String, Double> leverageMap = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0
                ));
        return ResponseEntity.ok(leverageMap);
    }

    /**
     * Get all distinct position IDs
     */
    @GetMapping("/position-ids")
    public ResponseEntity<List<String>> getPositionIds() {
        return ResponseEntity.ok(repository.findDistinctPositionIds());
    }

    /**
     * Get all distinct trading pairs
     */
    @GetMapping("/pairs")
    public ResponseEntity<List<String>> getPairs() {
        return ResponseEntity.ok(repository.findDistinctPairs());
    }

    /**
     * Get all distinct statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getStatuses() {
        return ResponseEntity.ok(repository.findDistinctStatuses());
    }

    /**
     * Get complete position summary with analytics
     */
    @GetMapping("/position-summary")
    public ResponseEntity<Map<String, Object>> getPositionSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        List<String> positionIds = repository.findDistinctPositionIds();
        List<Map<String, Object>> positionDetails = new ArrayList<>();
        
        for (String positionId : positionIds) {
            Optional<WebSocketFuturesPositionUpdateData> positionOpt = 
                repository.findFirstByPositionIdOrderByUpdateTimestampDesc(positionId);
            
            if (positionOpt.isPresent()) {
                WebSocketFuturesPositionUpdateData latest = positionOpt.get();
                Map<String, Object> detail = new HashMap<>();
                detail.put("positionId", positionId);
                detail.put("pair", latest.getPair());
                detail.put("side", latest.getSide());
                detail.put("status", latest.getStatus());
                detail.put("entryPrice", latest.getEntryPrice());
                detail.put("currentPrice", latest.getCurrentPrice());
                detail.put("liquidationPrice", latest.getLiquidationPrice());
                detail.put("quantity", latest.getQuantity());
                detail.put("leverage", latest.getLeverage());
                detail.put("margin", latest.getMargin());
                detail.put("unrealizedPnl", latest.getUnrealizedPnl());
                detail.put("realizedPnl", latest.getRealizedPnl());
                detail.put("totalPnl", latest.getTotalPnl());
                detail.put("roi", latest.getRoi());
                detail.put("marginType", latest.getPositionMarginType());
                detail.put("updateTimestamp", latest.getUpdateTimestamp());
                
                // Calculate distance to liquidation
                if (latest.getCurrentPrice() != null && latest.getLiquidationPrice() != null 
                        && latest.getCurrentPrice() > 0) {
                    double distance = Math.abs(latest.getCurrentPrice() - latest.getLiquidationPrice()) 
                            / latest.getCurrentPrice() * 100;
                    detail.put("liquidationDistance", distance);
                }
                
                positionDetails.add(detail);
            }
        }
        
        summary.put("totalPositions", positionIds.size());
        summary.put("positions", positionDetails);
        
        // Sort by absolute PnL
        positionDetails.sort((a, b) -> {
            Double pnlA = (Double) a.getOrDefault("totalPnl", 0.0);
            Double pnlB = (Double) b.getOrDefault("totalPnl", 0.0);
            return Double.compare(Math.abs(pnlB), Math.abs(pnlA));
        });
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Get position update by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketFuturesPositionUpdateData> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old records
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanup(
            @RequestParam(defaultValue = "7") int daysToKeep) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoff);
        long countAfter = repository.count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("recordsBefore", countBefore);
        result.put("recordsAfter", countAfter);
        result.put("recordsDeleted", countBefore - countAfter);
        result.put("cutoffDate", cutoff);
        
        return ResponseEntity.ok(result);
    }
}
