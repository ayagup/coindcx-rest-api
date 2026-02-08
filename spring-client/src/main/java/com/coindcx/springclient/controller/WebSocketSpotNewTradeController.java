package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotNewTradeData;
import com.coindcx.springclient.repository.WebSocketSpotNewTradeDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for WebSocket Spot New Trade Data (latest trade information)
 */
@RestController
@RequestMapping("/api/websocket/spot-new-trade")
@CrossOrigin(origins = "*")
public class WebSocketSpotNewTradeController {

    private final WebSocketSpotNewTradeDataRepository repository;

    public WebSocketSpotNewTradeController(WebSocketSpotNewTradeDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get statistics about trade data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrades", repository.count());
        
        List<String> symbols = repository.findDistinctSymbols();
        List<String> pairs = repository.findDistinctPairs();
        stats.put("symbols", symbols);
        stats.put("pairs", pairs);
        stats.put("symbolCount", symbols.size());
        stats.put("pairCount", pairs.size());
        
        // Get trade statistics by pair
        Long oneDayAgo = Instant.now().minusSeconds(86400).getEpochSecond();
        List<Object[]> pairStats = repository.getTradeStatisticsByPair(oneDayAgo);
        
        List<Map<String, Object>> pairStatsList = new ArrayList<>();
        for (Object[] stat : pairStats) {
            Map<String, Object> pairStat = new HashMap<>();
            pairStat.put("pair", stat[0]);
            pairStat.put("tradeCount", stat[1]);
            pairStat.put("avgPrice", stat[2]);
            pairStat.put("totalVolume", stat[3]);
            pairStatsList.add(pairStat);
        }
        stats.put("pairStatistics24h", pairStatsList);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get trades by symbol
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesBySymbol(
            @PathVariable String symbol,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotNewTradeData> trades;
        if (limit != null && limit > 0) {
            trades = repository.findBySymbolWithLimit(symbol, limit);
        } else {
            trades = repository.findBySymbolOrderByTradeTimestampDesc(symbol);
        }
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by pair
     */
    @GetMapping("/pair/{pair}")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByPair(
            @PathVariable String pair,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotNewTradeData> trades;
        if (limit != null && limit > 0) {
            trades = repository.findByPairWithLimit(pair, limit);
        } else {
            trades = repository.findByPairOrderByTradeTimestampDesc(pair);
        }
        return ResponseEntity.ok(trades);
    }

    /**
     * Get latest trade by symbol
     */
    @GetMapping("/symbol/{symbol}/latest")
    public ResponseEntity<WebSocketSpotNewTradeData> getLatestTradeBySymbol(@PathVariable String symbol) {
        Optional<WebSocketSpotNewTradeData> trade = 
            repository.findFirstBySymbolOrderByTradeTimestampDesc(symbol);
        return trade.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get latest trade by pair
     */
    @GetMapping("/pair/{pair}/latest")
    public ResponseEntity<WebSocketSpotNewTradeData> getLatestTradeByPair(@PathVariable String pair) {
        Optional<WebSocketSpotNewTradeData> trade = 
            repository.findFirstByPairOrderByTradeTimestampDesc(pair);
        return trade.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get trades within timestamp range
     */
    @GetMapping("/timestamp-range")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByTimestampRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by pair within timestamp range
     */
    @GetMapping("/pair/{pair}/timestamp-range")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByPairAndTimestampRange(
            @PathVariable String pair,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findByPairAndTimestampRange(pair, startTime, endTime);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades within record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByRecordTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findByRecordTimestampRange(startTime, endTime);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get recent trades
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getRecentTrades(@PathVariable int limit) {
        List<WebSocketSpotNewTradeData> trades = repository.findRecentTrades(limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by quantity range
     */
    @GetMapping("/quantity-range")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByQuantityRange(
            @RequestParam Double minQuantity,
            @RequestParam Double maxQuantity) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findByQuantityRange(minQuantity, maxQuantity);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by market maker status
     */
    @GetMapping("/market-maker/{isBuyerMarketMaker}")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByMarketMaker(
            @PathVariable Boolean isBuyerMarketMaker) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findByIsBuyerMarketMakerOrderByTradeTimestampDesc(isBuyerMarketMaker);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by pair and market maker status
     */
    @GetMapping("/pair/{pair}/market-maker/{isBuyerMarketMaker}")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByPairAndMarketMaker(
            @PathVariable String pair,
            @PathVariable Boolean isBuyerMarketMaker) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findByPairAndMarketMaker(pair, isBuyerMarketMaker);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get large trades (above quantity threshold)
     */
    @GetMapping("/large-trades/{threshold}/{limit}")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getLargeTrades(
            @PathVariable Double threshold,
            @PathVariable int limit) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findLargeTrades(threshold, limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get all distinct symbols
     */
    @GetMapping("/symbols")
    public ResponseEntity<List<String>> getDistinctSymbols() {
        List<String> symbols = repository.findDistinctSymbols();
        return ResponseEntity.ok(symbols);
    }

    /**
     * Get all distinct pairs
     */
    @GetMapping("/pairs")
    public ResponseEntity<List<String>> getDistinctPairs() {
        List<String> pairs = repository.findDistinctPairs();
        return ResponseEntity.ok(pairs);
    }

    /**
     * Get count of trades by symbol
     */
    @GetMapping("/symbol/{symbol}/count")
    public ResponseEntity<Long> getCountBySymbol(@PathVariable String symbol) {
        Long count = repository.countBySymbol(symbol);
        return ResponseEntity.ok(count);
    }

    /**
     * Get count of trades by pair
     */
    @GetMapping("/pair/{pair}/count")
    public ResponseEntity<Long> getCountByPair(@PathVariable String pair) {
        Long count = repository.countByPair(pair);
        return ResponseEntity.ok(count);
    }

    /**
     * Get count of trades within timestamp range
     */
    @GetMapping("/count-in-range")
    public ResponseEntity<Long> getCountInRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        Long count = repository.countByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(count);
    }

    /**
     * Get average trade price for a pair
     */
    @GetMapping("/pair/{pair}/average-price")
    public ResponseEntity<Double> getAverageTradePrice(
            @PathVariable String pair,
            @RequestParam Long fromTime) {
        
        Double avgPrice = repository.getAverageTradePrice(pair, fromTime);
        return ResponseEntity.ok(avgPrice);
    }

    /**
     * Get total volume for a pair
     */
    @GetMapping("/pair/{pair}/total-volume")
    public ResponseEntity<Double> getTotalVolume(
            @PathVariable String pair,
            @RequestParam Long fromTime) {
        
        Double totalVolume = repository.getTotalVolume(pair, fromTime);
        return ResponseEntity.ok(totalVolume);
    }

    /**
     * Get trade statistics by pair
     */
    @GetMapping("/pair-statistics")
    public ResponseEntity<List<Map<String, Object>>> getTradeStatisticsByPair(
            @RequestParam Long fromTime) {
        
        List<Object[]> stats = repository.getTradeStatisticsByPair(fromTime);
        
        List<Map<String, Object>> statsList = new ArrayList<>();
        for (Object[] stat : stats) {
            Map<String, Object> pairStat = new HashMap<>();
            pairStat.put("pair", stat[0]);
            pairStat.put("tradeCount", stat[1]);
            pairStat.put("avgPrice", stat[2]);
            pairStat.put("totalVolume", stat[3]);
            statsList.add(pairStat);
        }
        
        return ResponseEntity.ok(statsList);
    }

    /**
     * Get trades by channel name
     */
    @GetMapping("/channel/{channelName}")
    public ResponseEntity<List<WebSocketSpotNewTradeData>> getTradesByChannelName(
            @PathVariable String channelName) {
        
        List<WebSocketSpotNewTradeData> trades = 
            repository.findByChannelNameOrderByTradeTimestampDesc(channelName);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trade by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotNewTradeData> getTradeById(@PathVariable Long id) {
        Optional<WebSocketSpotNewTradeData> trade = repository.findById(id);
        return trade.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get complete trade analysis summary
     */
    @GetMapping("/trade-summary")
    public ResponseEntity<Map<String, Object>> getTradeSummary(
            @RequestParam(defaultValue = "10") int topPairs) {
        
        Map<String, Object> summary = new HashMap<>();
        
        // Get trade statistics
        Long oneDayAgo = Instant.now().minusSeconds(86400).getEpochSecond();
        List<Object[]> pairStats = repository.getTradeStatisticsByPair(oneDayAgo);
        
        List<Map<String, Object>> topPairsList = new ArrayList<>();
        int count = 0;
        for (Object[] stat : pairStats) {
            if (count >= topPairs) break;
            Map<String, Object> pairStat = new HashMap<>();
            pairStat.put("pair", stat[0]);
            pairStat.put("tradeCount", stat[1]);
            pairStat.put("avgPrice", stat[2]);
            pairStat.put("totalVolume", stat[3]);
            topPairsList.add(pairStat);
            count++;
        }
        summary.put("topTradingPairs24h", topPairsList);
        
        // Get large trades
        List<WebSocketSpotNewTradeData> largeTrades = repository.findLargeTrades(0.0, 10);
        summary.put("largestTrades", largeTrades);
        
        // Get total counts
        summary.put("totalTrades", repository.count());
        summary.put("totalSymbols", repository.findDistinctSymbols().size());
        summary.put("totalPairs", repository.findDistinctPairs().size());
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Delete old trade data (cleanup)
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldData(@RequestParam LocalDateTime cutoffTime) {
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(cutoffTime);
        long countAfter = repository.count();
        long deleted = countBefore - countAfter;
        
        Map<String, Object> result = new HashMap<>();
        result.put("deleted", deleted);
        result.put("remaining", countAfter);
        result.put("cutoffTime", cutoffTime);
        
        return ResponseEntity.ok(result);
    }
}
