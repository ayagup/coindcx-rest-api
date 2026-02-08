package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotCandlestickData;
import com.coindcx.springclient.repository.WebSocketSpotCandlestickDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for WebSocket Spot Candlestick Data
 */
@RestController
@RequestMapping("/api/websocket/spot-candlestick")
@CrossOrigin(origins = "*")
public class WebSocketSpotCandlestickController {

    private final WebSocketSpotCandlestickDataRepository repository;

    public WebSocketSpotCandlestickController(WebSocketSpotCandlestickDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get statistics about candlestick data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCandlesticks", repository.count());
        
        List<String> symbols = repository.findDistinctSymbols();
        stats.put("totalSymbols", symbols.size());
        stats.put("symbols", symbols);
        
        List<String> intervals = repository.findDistinctIntervals();
        stats.put("intervals", intervals);
        
        // Get stats for each symbol
        Map<String, Map<String, Object>> symbolStats = new HashMap<>();
        for (String symbol : symbols) {
            Map<String, Object> symbolData = new HashMap<>();
            symbolData.put("candlestickCount", repository.countBySymbol(symbol));
            symbolData.put("intervals", repository.findDistinctIntervalsBySymbol(symbol));
            symbolStats.put(symbol, symbolData);
        }
        stats.put("symbolStats", symbolStats);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get candlesticks by symbol
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksBySymbol(@PathVariable String symbol) {
        List<WebSocketSpotCandlestickData> candlesticks = repository.findBySymbolOrderByStartTimestampDesc(symbol);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks by symbol and interval
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksBySymbolAndInterval(
            @PathVariable String symbol,
            @PathVariable String interval,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotCandlestickData> candlesticks;
        if (limit != null && limit > 0) {
            candlesticks = repository.findBySymbolAndIntervalWithLimit(symbol, interval, limit);
        } else {
            candlesticks = repository.findBySymbolAndCandleIntervalOrderByStartTimestampDesc(symbol, interval);
        }
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get latest candlestick for symbol and interval
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}/latest")
    public ResponseEntity<WebSocketSpotCandlestickData> getLatestCandlestick(
            @PathVariable String symbol,
            @PathVariable String interval) {
        
        Optional<WebSocketSpotCandlestickData> candlestick = 
            repository.findFirstBySymbolAndCandleIntervalOrderByStartTimestampDesc(symbol, interval);
        return candlestick.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get latest completed candlestick for symbol and interval
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}/latest-completed")
    public ResponseEntity<WebSocketSpotCandlestickData> getLatestCompletedCandlestick(
            @PathVariable String symbol,
            @PathVariable String interval) {
        
        Optional<WebSocketSpotCandlestickData> candlestick = 
            repository.findLatestCompletedBySymbolAndInterval(symbol, interval);
        return candlestick.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all completed candlesticks
     */
    @GetMapping("/completed")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCompletedCandlesticks() {
        List<WebSocketSpotCandlestickData> candlesticks = repository.findCompletedCandlesticks();
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get all incomplete candlesticks
     */
    @GetMapping("/incomplete")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getIncompleteCandlesticks() {
        List<WebSocketSpotCandlestickData> candlesticks = repository.findIncompleteCandlesticks();
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks by symbol and completion status
     */
    @GetMapping("/symbol/{symbol}/completed/{isCompleted}")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksByCompletion(
            @PathVariable String symbol,
            @PathVariable Boolean isCompleted) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findBySymbolAndIsCompletedOrderByStartTimestampDesc(symbol, isCompleted);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks within start timestamp range
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksByTimeRange(
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findByStartTimestampRange(startTime, endTime);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks by symbol within start timestamp range
     */
    @GetMapping("/symbol/{symbol}/time-range")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksBySymbolAndTimeRange(
            @PathVariable String symbol,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findBySymbolAndStartTimestampRange(symbol, startTime, endTime);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks by symbol, interval within start timestamp range
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}/time-range")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksBySymbolIntervalAndTimeRange(
            @PathVariable String symbol,
            @PathVariable String interval,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findBySymbolIntervalAndStartTimestampRange(symbol, interval, startTime, endTime);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks within record timestamp range
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksByRecordTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findByTimestampRange(startTime, endTime);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get recent N candlesticks
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getRecentCandlesticks(@PathVariable int limit) {
        List<WebSocketSpotCandlestickData> candlesticks = repository.findRecentRecords(limit);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get recent N candlesticks for a symbol
     */
    @GetMapping("/symbol/{symbol}/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getRecentCandlesticksBySymbol(
            @PathVariable String symbol,
            @PathVariable int limit) {
        
        List<WebSocketSpotCandlestickData> candlesticks = repository.findRecentBySymbol(symbol, limit);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get all distinct symbols
     */
    @GetMapping("/symbols")
    public ResponseEntity<List<String>> getSymbols() {
        List<String> symbols = repository.findDistinctSymbols();
        return ResponseEntity.ok(symbols);
    }

    /**
     * Get all distinct intervals
     */
    @GetMapping("/intervals")
    public ResponseEntity<List<String>> getIntervals() {
        List<String> intervals = repository.findDistinctIntervals();
        return ResponseEntity.ok(intervals);
    }

    /**
     * Get all distinct intervals for a symbol
     */
    @GetMapping("/symbol/{symbol}/intervals")
    public ResponseEntity<List<String>> getIntervalsBySymbol(@PathVariable String symbol) {
        List<String> intervals = repository.findDistinctIntervalsBySymbol(symbol);
        return ResponseEntity.ok(intervals);
    }

    /**
     * Count candlesticks by symbol
     */
    @GetMapping("/symbol/{symbol}/count")
    public ResponseEntity<Map<String, Object>> countCandlesticksBySymbol(@PathVariable String symbol) {
        long count = repository.countBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Count candlesticks by symbol and interval
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}/count")
    public ResponseEntity<Map<String, Object>> countCandlesticksBySymbolAndInterval(
            @PathVariable String symbol,
            @PathVariable String interval) {
        
        long count = repository.countBySymbolAndCandleInterval(symbol, interval);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("interval", interval);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Count completed candlesticks by symbol and interval
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}/count-completed")
    public ResponseEntity<Map<String, Object>> countCompletedCandlesticks(
            @PathVariable String symbol,
            @PathVariable String interval) {
        
        long count = repository.countCompletedBySymbolAndInterval(symbol, interval);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("interval", interval);
        response.put("completedCount", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total volume for a symbol and interval
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}/volume")
    public ResponseEntity<Map<String, Object>> getTotalVolume(
            @PathVariable String symbol,
            @PathVariable String interval) {
        
        Double totalVolume = repository.getTotalVolumeBySymbolAndInterval(symbol, interval);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("interval", interval);
        response.put("totalVolume", totalVolume != null ? totalVolume : 0.0);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total number of trades for a symbol and interval
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}/trades")
    public ResponseEntity<Map<String, Object>> getTotalTrades(
            @PathVariable String symbol,
            @PathVariable String interval) {
        
        Long totalTrades = repository.getTotalTradesBySymbolAndInterval(symbol, interval);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("interval", interval);
        response.put("totalTrades", totalTrades != null ? totalTrades : 0L);
        return ResponseEntity.ok(response);
    }

    /**
     * Get candlesticks by price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks with high price above threshold
     */
    @GetMapping("/high-price")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksByHighPrice(
            @RequestParam Double minHigh) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findByHighPriceGreaterThan(minHigh);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks with low price below threshold
     */
    @GetMapping("/low-price")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksByLowPrice(
            @RequestParam Double maxLow) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findByLowPriceLessThan(maxLow);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlesticks with volume above threshold
     */
    @GetMapping("/high-volume")
    public ResponseEntity<List<WebSocketSpotCandlestickData>> getCandlesticksByVolume(
            @RequestParam Double minVolume) {
        
        List<WebSocketSpotCandlestickData> candlesticks = 
            repository.findByVolumeGreaterThan(minVolume);
        return ResponseEntity.ok(candlesticks);
    }

    /**
     * Get candlestick by record ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotCandlestickData> getById(@PathVariable Long id) {
        Optional<WebSocketSpotCandlestickData> candlestick = repository.findById(id);
        return candlestick.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old candlesticks
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldCandlesticks(@RequestParam int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        long countBefore = repository.count();
        repository.deleteByTimestampBefore(cutoff);
        long countAfter = repository.count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("deletedCount", countBefore - countAfter);
        response.put("remainingCount", countAfter);
        response.put("cutoffDate", cutoff);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get OHLC summary for a symbol and interval
     */
    @GetMapping("/symbol/{symbol}/interval/{interval}/ohlc-summary")
    public ResponseEntity<Map<String, Object>> getOHLCSummary(
            @PathVariable String symbol,
            @PathVariable String interval,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotCandlestickData> candlesticks;
        if (limit != null && limit > 0) {
            candlesticks = repository.findBySymbolAndIntervalWithLimit(symbol, interval, limit);
        } else {
            candlesticks = repository.findBySymbolAndCandleIntervalOrderByStartTimestampDesc(symbol, interval);
        }
        
        if (candlesticks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Calculate aggregate statistics
        double totalVolume = 0.0;
        long totalTrades = 0L;
        double highestHigh = Double.MIN_VALUE;
        double lowestLow = Double.MAX_VALUE;
        
        for (WebSocketSpotCandlestickData candle : candlesticks) {
            try {
                if (candle.getBaseAssetVolume() != null) {
                    totalVolume += Double.parseDouble(candle.getBaseAssetVolume());
                }
                if (candle.getNumberOfTrades() != null) {
                    totalTrades += candle.getNumberOfTrades();
                }
                if (candle.getHighPrice() != null) {
                    double high = Double.parseDouble(candle.getHighPrice());
                    if (high > highestHigh) highestHigh = high;
                }
                if (candle.getLowPrice() != null) {
                    double low = Double.parseDouble(candle.getLowPrice());
                    if (low < lowestLow) lowestLow = low;
                }
            } catch (NumberFormatException e) {
                // Skip invalid numbers
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("interval", interval);
        response.put("candleCount", candlesticks.size());
        response.put("totalVolume", totalVolume);
        response.put("totalTrades", totalTrades);
        response.put("highestHigh", highestHigh != Double.MIN_VALUE ? highestHigh : null);
        response.put("lowestLow", lowestLow != Double.MAX_VALUE ? lowestLow : null);
        response.put("firstCandle", candlesticks.get(candlesticks.size() - 1));
        response.put("lastCandle", candlesticks.get(0));
        
        return ResponseEntity.ok(response);
    }
}
