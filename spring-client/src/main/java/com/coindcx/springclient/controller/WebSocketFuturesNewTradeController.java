package com.coindcx.springclient.controller;

import com.coindcx.springclient.model.WebSocketFuturesNewTradeData;
import com.coindcx.springclient.repository.WebSocketFuturesNewTradeDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * REST controller for WebSocket Futures New Trade data endpoints
 */
@RestController
@RequestMapping("/api/websocket/futures-new-trades")
@CrossOrigin(origins = "*")
public class WebSocketFuturesNewTradeController {

    private final WebSocketFuturesNewTradeDataRepository repository;

    public WebSocketFuturesNewTradeController(WebSocketFuturesNewTradeDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all futures new trade records with limit
     * GET /api/websocket/futures-new-trades?limit=100
     */
    @GetMapping
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getAllTrades(
            @RequestParam(defaultValue = "100") int limit) {
        List<WebSocketFuturesNewTradeData> trades = repository.findRecentTrades(limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by symbol
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT?limit=50
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesBySymbol(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "100") int limit) {
        List<WebSocketFuturesNewTradeData> trades = repository.findBySymbolWithLimit(symbol, limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by channel name
     * GET /api/websocket/futures-new-trades/channel/B-BTC_USDT@trades-futures?limit=50
     */
    @GetMapping("/channel/{channelName}")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesByChannel(
            @PathVariable String channelName,
            @RequestParam(defaultValue = "100") int limit) {
        List<WebSocketFuturesNewTradeData> trades = repository.findByChannelNameWithLimit(channelName, limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get buy trades (isBuyerMaker = 0)
     * GET /api/websocket/futures-new-trades/buy-trades?limit=100
     */
    @GetMapping("/buy-trades")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getBuyTrades(
            @RequestParam(defaultValue = "100") int limit) {
        List<WebSocketFuturesNewTradeData> trades = repository.findByIsBuyerMakerWithLimit(0, limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get sell trades (isBuyerMaker = 1)
     * GET /api/websocket/futures-new-trades/sell-trades?limit=100
     */
    @GetMapping("/sell-trades")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getSellTrades(
            @RequestParam(defaultValue = "100") int limit) {
        List<WebSocketFuturesNewTradeData> trades = repository.findByIsBuyerMakerWithLimit(1, limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by symbol and side
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/side/0?limit=50
     * side: 0 = buy, 1 = sell
     */
    @GetMapping("/symbol/{symbol}/side/{side}")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesBySymbolAndSide(
            @PathVariable String symbol,
            @PathVariable Integer side,
            @RequestParam(defaultValue = "100") int limit) {
        List<WebSocketFuturesNewTradeData> trades = repository.findBySymbolAndIsBuyerMakerWithLimit(symbol, side, limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get recent trades
     * GET /api/websocket/futures-new-trades/recent?limit=100
     */
    @GetMapping("/recent")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getRecentTrades(
            @RequestParam(defaultValue = "100") int limit) {
        List<WebSocketFuturesNewTradeData> trades = repository.findRecentTrades(limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get recent trades by record timestamp
     * GET /api/websocket/futures-new-trades/recent/by-record?limit=100
     */
    @GetMapping("/recent/by-record")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getRecentByRecordTimestamp(
            @RequestParam(defaultValue = "100") int limit) {
        List<WebSocketFuturesNewTradeData> trades = repository.findRecentByRecordTimestamp(limit);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get latest trade
     * GET /api/websocket/futures-new-trades/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<WebSocketFuturesNewTradeData> getLatestTrade() {
        Optional<WebSocketFuturesNewTradeData> trade = repository.findLatestTrade();
        return trade.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get latest trade by symbol
     * GET /api/websocket/futures-new-trades/latest/symbol/B-BTC_USDT
     */
    @GetMapping("/latest/symbol/{symbol}")
    public ResponseEntity<WebSocketFuturesNewTradeData> getLatestBySymbol(@PathVariable String symbol) {
        Optional<WebSocketFuturesNewTradeData> trade = repository.findLatestBySymbol(symbol);
        return trade.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get latest trade by channel
     * GET /api/websocket/futures-new-trades/latest/channel/B-BTC_USDT@trades-futures
     */
    @GetMapping("/latest/channel/{channelName}")
    public ResponseEntity<WebSocketFuturesNewTradeData> getLatestByChannel(@PathVariable String channelName) {
        Optional<WebSocketFuturesNewTradeData> trade = repository.findLatestByChannelName(channelName);
        return trade.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get trades by time range
     * GET /api/websocket/futures-new-trades/time-range?start=1705516361108&end=1705602761108
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesByTimeRange(
            @RequestParam Long start,
            @RequestParam Long end) {
        List<WebSocketFuturesNewTradeData> trades = repository.findByTradeTimestampRange(start, end);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by symbol and time range
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/time-range?start=1705516361108&end=1705602761108
     */
    @GetMapping("/symbol/{symbol}/time-range")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesBySymbolAndTimeRange(
            @PathVariable String symbol,
            @RequestParam Long start,
            @RequestParam Long end) {
        List<WebSocketFuturesNewTradeData> trades = repository.findBySymbolAndTradeTimestampRange(symbol, start, end);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by channel and time range
     * GET /api/websocket/futures-new-trades/channel/B-BTC_USDT@trades-futures/time-range?start=1705516361108&end=1705602761108
     */
    @GetMapping("/channel/{channelName}/time-range")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesByChannelAndTimeRange(
            @PathVariable String channelName,
            @RequestParam Long start,
            @RequestParam Long end) {
        List<WebSocketFuturesNewTradeData> trades = repository.findByChannelNameAndTradeTimestampRange(channelName, start, end);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by record timestamp range
     * GET /api/websocket/futures-new-trades/record-time-range?start=2024-01-01T00:00:00&end=2024-01-02T00:00:00
     */
    @GetMapping("/record-time-range")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesByRecordTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<WebSocketFuturesNewTradeData> trades = repository.findByRecordTimestampRange(start, end);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by price range
     * GET /api/websocket/futures-new-trades/price-range?min=50000&max=60000
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<WebSocketFuturesNewTradeData> trades = repository.findByPriceRange(min, max);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by symbol and price range
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/price-range?min=50000&max=60000
     */
    @GetMapping("/symbol/{symbol}/price-range")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesBySymbolAndPriceRange(
            @PathVariable String symbol,
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<WebSocketFuturesNewTradeData> trades = repository.findBySymbolAndPriceRange(symbol, min, max);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by quantity range
     * GET /api/websocket/futures-new-trades/quantity-range?min=1&max=100
     */
    @GetMapping("/quantity-range")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesByQuantityRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<WebSocketFuturesNewTradeData> trades = repository.findByQuantityRange(min, max);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades by symbol and quantity range
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/quantity-range?min=1&max=100
     */
    @GetMapping("/symbol/{symbol}/quantity-range")
    public ResponseEntity<List<WebSocketFuturesNewTradeData>> getTradesBySymbolAndQuantityRange(
            @PathVariable String symbol,
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<WebSocketFuturesNewTradeData> trades = repository.findBySymbolAndQuantityRange(symbol, min, max);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get total quantity by symbol
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/volume
     */
    @GetMapping("/symbol/{symbol}/volume")
    public ResponseEntity<Map<String, Object>> getTotalQuantityBySymbol(@PathVariable String symbol) {
        BigDecimal totalQuantity = repository.sumQuantityBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("totalVolume", totalQuantity != null ? totalQuantity : BigDecimal.ZERO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total quantity by symbol and time range
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/volume/time-range?start=1705516361108&end=1705602761108
     */
    @GetMapping("/symbol/{symbol}/volume/time-range")
    public ResponseEntity<Map<String, Object>> getTotalQuantityBySymbolAndTimeRange(
            @PathVariable String symbol,
            @RequestParam Long start,
            @RequestParam Long end) {
        BigDecimal totalQuantity = repository.sumQuantityBySymbolAndTimeRange(symbol, start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("startTime", start);
        response.put("endTime", end);
        response.put("totalVolume", totalQuantity != null ? totalQuantity : BigDecimal.ZERO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get average price by symbol
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/avg-price
     */
    @GetMapping("/symbol/{symbol}/avg-price")
    public ResponseEntity<Map<String, Object>> getAveragePriceBySymbol(@PathVariable String symbol) {
        BigDecimal avgPrice = repository.avgPriceBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("averagePrice", avgPrice != null ? avgPrice : BigDecimal.ZERO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get average price by symbol and time range
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/avg-price/time-range?start=1705516361108&end=1705602761108
     */
    @GetMapping("/symbol/{symbol}/avg-price/time-range")
    public ResponseEntity<Map<String, Object>> getAveragePriceBySymbolAndTimeRange(
            @PathVariable String symbol,
            @RequestParam Long start,
            @RequestParam Long end) {
        BigDecimal avgPrice = repository.avgPriceBySymbolAndTimeRange(symbol, start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("startTime", start);
        response.put("endTime", end);
        response.put("averagePrice", avgPrice != null ? avgPrice : BigDecimal.ZERO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get price statistics by symbol
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/price-stats
     */
    @GetMapping("/symbol/{symbol}/price-stats")
    public ResponseEntity<Map<String, Object>> getPriceStatsBySymbol(@PathVariable String symbol) {
        BigDecimal minPrice = repository.minPriceBySymbol(symbol);
        BigDecimal maxPrice = repository.maxPriceBySymbol(symbol);
        BigDecimal avgPrice = repository.avgPriceBySymbol(symbol);
        
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("minPrice", minPrice != null ? minPrice : BigDecimal.ZERO);
        response.put("maxPrice", maxPrice != null ? maxPrice : BigDecimal.ZERO);
        response.put("averagePrice", avgPrice != null ? avgPrice : BigDecimal.ZERO);
        return ResponseEntity.ok(response);
    }

    /**
     * Get distinct symbols
     * GET /api/websocket/futures-new-trades/symbols
     */
    @GetMapping("/symbols")
    public ResponseEntity<List<String>> getDistinctSymbols() {
        List<String> symbols = repository.findDistinctSymbols();
        return ResponseEntity.ok(symbols);
    }

    /**
     * Get distinct channels
     * GET /api/websocket/futures-new-trades/channels
     */
    @GetMapping("/channels")
    public ResponseEntity<List<String>> getDistinctChannels() {
        List<String> channels = repository.findDistinctChannelNames();
        return ResponseEntity.ok(channels);
    }

    /**
     * Get distinct product types
     * GET /api/websocket/futures-new-trades/product-types
     */
    @GetMapping("/product-types")
    public ResponseEntity<List<String>> getDistinctProductTypes() {
        List<String> productTypes = repository.findDistinctProductTypes();
        return ResponseEntity.ok(productTypes);
    }

    /**
     * Get count by symbol
     * GET /api/websocket/futures-new-trades/count/symbol/B-BTC_USDT
     */
    @GetMapping("/count/symbol/{symbol}")
    public ResponseEntity<Map<String, Object>> getCountBySymbol(@PathVariable String symbol) {
        long count = repository.countBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count by channel
     * GET /api/websocket/futures-new-trades/count/channel/B-BTC_USDT@trades-futures
     */
    @GetMapping("/count/channel/{channelName}")
    public ResponseEntity<Map<String, Object>> getCountByChannel(@PathVariable String channelName) {
        long count = repository.countByChannelName(channelName);
        Map<String, Object> response = new HashMap<>();
        response.put("channelName", channelName);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count by trade side
     * GET /api/websocket/futures-new-trades/count/side/0
     * side: 0 = buy, 1 = sell
     */
    @GetMapping("/count/side/{side}")
    public ResponseEntity<Map<String, Object>> getCountBySide(@PathVariable Integer side) {
        long count = repository.countByIsBuyerMaker(side);
        Map<String, Object> response = new HashMap<>();
        response.put("side", side == 0 ? "buy" : "sell");
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get count by symbol and side
     * GET /api/websocket/futures-new-trades/count/symbol/B-BTC_USDT/side/0
     * side: 0 = buy, 1 = sell
     */
    @GetMapping("/count/symbol/{symbol}/side/{side}")
    public ResponseEntity<Map<String, Object>> getCountBySymbolAndSide(
            @PathVariable String symbol,
            @PathVariable Integer side) {
        long count = repository.countBySymbolAndIsBuyerMaker(symbol, side);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("side", side == 0 ? "buy" : "sell");
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get distinct symbol count
     * GET /api/websocket/futures-new-trades/count/distinct-symbols
     */
    @GetMapping("/count/distinct-symbols")
    public ResponseEntity<Map<String, Object>> getDistinctSymbolCount() {
        long count = repository.countDistinctSymbols();
        Map<String, Object> response = new HashMap<>();
        response.put("distinctSymbolCount", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get distinct channel count
     * GET /api/websocket/futures-new-trades/count/distinct-channels
     */
    @GetMapping("/count/distinct-channels")
    public ResponseEntity<Map<String, Object>> getDistinctChannelCount() {
        long count = repository.countDistinctChannels();
        Map<String, Object> response = new HashMap<>();
        response.put("distinctChannelCount", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get oldest trade timestamp
     * GET /api/websocket/futures-new-trades/timestamp/oldest
     */
    @GetMapping("/timestamp/oldest")
    public ResponseEntity<Map<String, Object>> getOldestTradeTimestamp() {
        Long timestamp = repository.findOldestTradeTimestamp();
        Map<String, Object> response = new HashMap<>();
        response.put("oldestTradeTimestamp", timestamp);
        return ResponseEntity.ok(response);
    }

    /**
     * Get latest trade timestamp
     * GET /api/websocket/futures-new-trades/timestamp/latest
     */
    @GetMapping("/timestamp/latest")
    public ResponseEntity<Map<String, Object>> getLatestTradeTimestamp() {
        Long timestamp = repository.findLatestTradeTimestamp();
        Map<String, Object> response = new HashMap<>();
        response.put("latestTradeTimestamp", timestamp);
        return ResponseEntity.ok(response);
    }

    /**
     * Get oldest trade timestamp by symbol
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/timestamp/oldest
     */
    @GetMapping("/symbol/{symbol}/timestamp/oldest")
    public ResponseEntity<Map<String, Object>> getOldestTradeTimestampBySymbol(@PathVariable String symbol) {
        Long timestamp = repository.findOldestTradeTimestampBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("oldestTradeTimestamp", timestamp);
        return ResponseEntity.ok(response);
    }

    /**
     * Get latest trade timestamp by symbol
     * GET /api/websocket/futures-new-trades/symbol/B-BTC_USDT/timestamp/latest
     */
    @GetMapping("/symbol/{symbol}/timestamp/latest")
    public ResponseEntity<Map<String, Object>> getLatestTradeTimestampBySymbol(@PathVariable String symbol) {
        Long timestamp = repository.findLatestTradeTimestampBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("latestTradeTimestamp", timestamp);
        return ResponseEntity.ok(response);
    }

    /**
     * Get comprehensive statistics
     * GET /api/websocket/futures-new-trades/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total count
        stats.put("totalTrades", repository.count());
        
        // Distinct counts
        stats.put("distinctSymbols", repository.countDistinctSymbols());
        stats.put("distinctChannels", repository.countDistinctChannels());
        
        // Buy/Sell counts
        stats.put("buyTrades", repository.countByIsBuyerMaker(0));
        stats.put("sellTrades", repository.countByIsBuyerMaker(1));
        
        // Timestamp range
        stats.put("oldestTradeTimestamp", repository.findOldestTradeTimestamp());
        stats.put("latestTradeTimestamp", repository.findLatestTradeTimestamp());
        
        // List of symbols and channels
        stats.put("symbols", repository.findDistinctSymbols());
        stats.put("channels", repository.findDistinctChannelNames());
        stats.put("productTypes", repository.findDistinctProductTypes());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Cleanup old records
     * DELETE /api/websocket/futures-new-trades/cleanup?before=2024-01-01T00:00:00
     */
    @DeleteMapping("/cleanup")
    @Transactional
    public ResponseEntity<Map<String, Object>> cleanupOldRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        long countBefore = repository.count();
        repository.deleteByRecordTimestampBefore(before);
        long countAfter = repository.count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("deletedRecords", countBefore - countAfter);
        response.put("remainingRecords", countAfter);
        return ResponseEntity.ok(response);
    }

    /**
     * Cleanup old records by symbol
     * DELETE /api/websocket/futures-new-trades/cleanup/symbol/B-BTC_USDT?before=1705516361108
     */
    @DeleteMapping("/cleanup/symbol/{symbol}")
    @Transactional
    public ResponseEntity<Map<String, Object>> cleanupOldRecordsBySymbol(
            @PathVariable String symbol,
            @RequestParam Long before) {
        long countBefore = repository.countBySymbol(symbol);
        repository.deleteBySymbolAndTradeTimestampBefore(symbol, before);
        long countAfter = repository.countBySymbol(symbol);
        
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("deletedRecords", countBefore - countAfter);
        response.put("remainingRecords", countAfter);
        return ResponseEntity.ok(response);
    }
}
