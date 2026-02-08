package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketSpotTradeUpdateData;
import com.coindcx.springclient.repository.WebSocketSpotTradeUpdateDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for WebSocket Spot Trade Update Data
 */
@RestController
@RequestMapping("/api/websocket/spot-trade-update")
@CrossOrigin(origins = "*")
public class WebSocketSpotTradeUpdateController {

    private final WebSocketSpotTradeUpdateDataRepository repository;

    public WebSocketSpotTradeUpdateController(WebSocketSpotTradeUpdateDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Get statistics about trade update data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrades", repository.count());
        
        List<String> symbols = repository.findDistinctSymbols();
        stats.put("totalSymbols", symbols.size());
        stats.put("symbols", symbols);
        
        List<String> statuses = repository.findDistinctStatuses();
        stats.put("statuses", statuses);
        
        // Get volume and value for each symbol
        Map<String, Map<String, Object>> symbolStats = new HashMap<>();
        for (String symbol : symbols) {
            Map<String, Object> symbolData = new HashMap<>();
            symbolData.put("totalVolume", repository.getTotalVolumeBySymbol(symbol));
            symbolData.put("totalValue", repository.getTotalValueBySymbol(symbol));
            symbolData.put("tradeCount", repository.countBySymbol(symbol));
            symbolStats.put(symbol, symbolData);
        }
        stats.put("symbolStats", symbolStats);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get a single trade by trade ID
     */
    @GetMapping("/trade/{tradeId}")
    public ResponseEntity<WebSocketSpotTradeUpdateData> getByTradeId(@PathVariable String tradeId) {
        Optional<WebSocketSpotTradeUpdateData> trade = repository.findByTradeId(tradeId);
        return trade.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all trades for a specific order ID
     */
    @GetMapping("/order/{orderId}/trades")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getTradesByOrderId(@PathVariable String orderId) {
        List<WebSocketSpotTradeUpdateData> trades = repository.findByOrderIdOrderByTimestampDesc(orderId);
        return ResponseEntity.ok(trades);
    }

    /**
     * Count trades for a specific order ID
     */
    @GetMapping("/order/{orderId}/count")
    public ResponseEntity<Map<String, Object>> countTradesByOrderId(@PathVariable String orderId) {
        long count = repository.countByOrderId(orderId);
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("tradeCount", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all trades for a specific client order ID
     */
    @GetMapping("/client-order/{clientOrderId}/trades")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getTradesByClientOrderId(@PathVariable String clientOrderId) {
        List<WebSocketSpotTradeUpdateData> trades = repository.findByClientOrderIdOrderByTimestampDesc(clientOrderId);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get all trades for a specific symbol
     */
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getTradesBySymbol(
            @PathVariable String symbol,
            @RequestParam(required = false) Integer limit) {
        
        List<WebSocketSpotTradeUpdateData> trades;
        if (limit != null && limit > 0) {
            trades = repository.findBySymbolWithLimit(symbol, limit);
        } else {
            trades = repository.findBySymbolOrderByTimestampDesc(symbol);
        }
        return ResponseEntity.ok(trades);
    }

    /**
     * Count trades for a specific symbol
     */
    @GetMapping("/symbol/{symbol}/count")
    public ResponseEntity<Map<String, Object>> countTradesBySymbol(@PathVariable String symbol) {
        long count = repository.countBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("tradeCount", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total volume for a specific symbol
     */
    @GetMapping("/symbol/{symbol}/volume")
    public ResponseEntity<Map<String, Object>> getVolumeBySymbol(@PathVariable String symbol) {
        Double totalVolume = repository.getTotalVolumeBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("totalVolume", totalVolume != null ? totalVolume : 0.0);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total value (price * quantity) for a specific symbol
     */
    @GetMapping("/symbol/{symbol}/value")
    public ResponseEntity<Map<String, Object>> getValueBySymbol(@PathVariable String symbol) {
        Double totalValue = repository.getTotalValueBySymbol(symbol);
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("totalValue", totalValue != null ? totalValue : 0.0);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all maker trades (buyer is maker)
     */
    @GetMapping("/maker-trades")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getMakerTrades() {
        List<WebSocketSpotTradeUpdateData> trades = repository.findMakerTrades();
        return ResponseEntity.ok(trades);
    }

    /**
     * Get all taker trades (buyer is not maker)
     */
    @GetMapping("/taker-trades")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getTakerTrades() {
        List<WebSocketSpotTradeUpdateData> trades = repository.findTakerTrades();
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades within a time range
     */
    @GetMapping("/range")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getTradesByTimeRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<WebSocketSpotTradeUpdateData> trades = repository.findByTimeRange(start, end);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trades for a symbol within a time range
     */
    @GetMapping("/symbol/{symbol}/range")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getTradesBySymbolAndTimeRange(
            @PathVariable String symbol,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<WebSocketSpotTradeUpdateData> trades = repository.findBySymbolAndTimeRange(symbol, start, end);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get recent N trades
     */
    @GetMapping("/recent/{limit}")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getRecentTrades(@PathVariable int limit) {
        List<WebSocketSpotTradeUpdateData> trades = repository.findRecentRecords(limit);
        return ResponseEntity.ok(trades);
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
     * Get all distinct statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getStatuses() {
        List<String> statuses = repository.findDistinctStatuses();
        return ResponseEntity.ok(statuses);
    }

    /**
     * Get trades within a price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getTradesByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<WebSocketSpotTradeUpdateData> trades = repository.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(trades);
    }

    /**
     * Get latest trade for all symbols
     */
    @GetMapping("/latest-all")
    public ResponseEntity<List<WebSocketSpotTradeUpdateData>> getLatestTradesForAllSymbols() {
        List<WebSocketSpotTradeUpdateData> trades = repository.findLatestTradeForAllSymbols();
        return ResponseEntity.ok(trades);
    }

    /**
     * Get trade by record ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WebSocketSpotTradeUpdateData> getById(@PathVariable Long id) {
        Optional<WebSocketSpotTradeUpdateData> trade = repository.findById(id);
        return trade.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cleanup old trades
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldTrades(@RequestParam int daysOld) {
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
     * Get trade volume breakdown by maker/taker
     */
    @GetMapping("/symbol/{symbol}/maker-taker-stats")
    public ResponseEntity<Map<String, Object>> getMakerTakerStats(@PathVariable String symbol) {
        List<WebSocketSpotTradeUpdateData> allTrades = repository.findBySymbolOrderByTimestampDesc(symbol);
        
        double makerVolume = allTrades.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsBuyerMaker()))
                .mapToDouble(t -> {
                    try {
                        return t.getQuantity() != null ? Double.parseDouble(t.getQuantity()) : 0.0;
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .sum();
        
        double takerVolume = allTrades.stream()
                .filter(t -> !Boolean.TRUE.equals(t.getIsBuyerMaker()))
                .mapToDouble(t -> {
                    try {
                        return t.getQuantity() != null ? Double.parseDouble(t.getQuantity()) : 0.0;
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .sum();
        
        long makerCount = allTrades.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsBuyerMaker()))
                .count();
        
        long takerCount = allTrades.stream()
                .filter(t -> !Boolean.TRUE.equals(t.getIsBuyerMaker()))
                .count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("makerVolume", makerVolume);
        response.put("takerVolume", takerVolume);
        response.put("makerCount", makerCount);
        response.put("takerCount", takerCount);
        response.put("totalVolume", makerVolume + takerVolume);
        response.put("totalCount", makerCount + takerCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get fee statistics for a symbol
     */
    @GetMapping("/symbol/{symbol}/fee-stats")
    public ResponseEntity<Map<String, Object>> getFeeStats(@PathVariable String symbol) {
        List<WebSocketSpotTradeUpdateData> trades = repository.findBySymbolOrderByTimestampDesc(symbol);
        
        double totalFees = trades.stream()
                .mapToDouble(t -> {
                    try {
                        return t.getFeeAmount() != null ? Double.parseDouble(t.getFeeAmount()) : 0.0;
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .sum();
        
        double avgFee = trades.isEmpty() ? 0.0 : totalFees / trades.size();
        
        double maxFee = trades.stream()
                .mapToDouble(t -> {
                    try {
                        return t.getFeeAmount() != null ? Double.parseDouble(t.getFeeAmount()) : 0.0;
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .max()
                .orElse(0.0);
        
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("totalFees", totalFees);
        response.put("averageFee", avgFee);
        response.put("maxFee", maxFee);
        response.put("tradeCount", trades.size());
        
        return ResponseEntity.ok(response);
    }
}
