package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketFuturesData;
import com.coindcx.springclient.entity.WebSocketSpotData;
import com.coindcx.springclient.model.WebSocketFuturesCurrentPricesData;
import com.coindcx.springclient.repository.WebSocketFuturesCurrentPricesDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotDataRepository;
import com.coindcx.springclient.service.CoinDCXWebSocketService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for WebSocket data storage management
 */
@RestController
@RequestMapping("/api/websocket/data")
public class WebSocketDataController {

    private final WebSocketSpotDataRepository spotDataRepository;
    private final WebSocketFuturesDataRepository futuresDataRepository;
    private final WebSocketFuturesCurrentPricesDataRepository futuresCurrentPricesDataRepository;
    private final CoinDCXWebSocketService webSocketService;

    @Autowired
    public WebSocketDataController(
            WebSocketSpotDataRepository spotDataRepository,
            WebSocketFuturesDataRepository futuresDataRepository,
            WebSocketFuturesCurrentPricesDataRepository futuresCurrentPricesDataRepository,
            CoinDCXWebSocketService webSocketService) {
        this.spotDataRepository = spotDataRepository;
        this.futuresDataRepository = futuresDataRepository;
        this.futuresCurrentPricesDataRepository = futuresCurrentPricesDataRepository;
        this.webSocketService = webSocketService;
    }

    /**
     * Get storage statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStorageStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long spotCount = spotDataRepository.count();
        long futuresCount = futuresDataRepository.count();
        
        stats.put("spotRecords", spotCount);
        stats.put("futuresRecords", futuresCount);
        stats.put("totalRecords", spotCount + futuresCount);
        stats.put("spotMarkets", spotDataRepository.findDistinctMarketPairs());
        stats.put("futuresContracts", futuresDataRepository.findDistinctContractSymbols());
        stats.put("subscribedChannels", webSocketService.getSubscribedChannels());
        stats.put("connectionStatus", webSocketService.getConnectionStatus());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent spot market data
     */
    @GetMapping("/spot/{marketPair}")
    public ResponseEntity<List<WebSocketSpotData>> getSpotData(
            @PathVariable String marketPair,
            @RequestParam(defaultValue = "100") int limit) {
        
        List<WebSocketSpotData> data = spotDataRepository.findByMarketPairOrderByTimestampDesc(marketPair);
        
        // Limit results
        if (data.size() > limit) {
            data = data.subList(0, limit);
        }
        
        return ResponseEntity.ok(data);
    }

    /**
     * Get recent futures market data
     */
    @GetMapping("/futures/{contractSymbol}")
    public ResponseEntity<List<WebSocketFuturesData>> getFuturesData(
            @PathVariable String contractSymbol,
            @RequestParam(defaultValue = "100") int limit) {
        
        List<WebSocketFuturesData> data = futuresDataRepository.findByContractSymbolOrderByTimestampDesc(contractSymbol);
        
        // Limit results
        if (data.size() > limit) {
            data = data.subList(0, limit);
        }
        
        return ResponseEntity.ok(data);
    }

    /**
     * Get spot data by event type
     */
    @GetMapping("/spot/{marketPair}/event/{eventType}")
    public ResponseEntity<List<WebSocketSpotData>> getSpotDataByEvent(
            @PathVariable String marketPair,
            @PathVariable String eventType,
            @RequestParam(defaultValue = "100") int limit) {
        
        List<WebSocketSpotData> data = spotDataRepository
                .findByMarketPairAndEventTypeOrderByTimestampDesc(marketPair, eventType);
        
        if (data.size() > limit) {
            data = data.subList(0, limit);
        }
        
        return ResponseEntity.ok(data);
    }

    /**
     * Get futures data by event type
     */
    @GetMapping("/futures/{contractSymbol}/event/{eventType}")
    public ResponseEntity<List<WebSocketFuturesData>> getFuturesDataByEvent(
            @PathVariable String contractSymbol,
            @PathVariable String eventType,
            @RequestParam(defaultValue = "100") int limit) {
        
        List<WebSocketFuturesData> data = futuresDataRepository
                .findByContractSymbolAndEventTypeOrderByTimestampDesc(contractSymbol, eventType);
        
        if (data.size() > limit) {
            data = data.subList(0, limit);
        }
        
        return ResponseEntity.ok(data);
    }

    /**
     * Get latest price for a spot market
     */
    @GetMapping("/spot/{marketPair}/latest-price")
    public ResponseEntity<WebSocketSpotData> getLatestSpotPrice(@PathVariable String marketPair) {
        WebSocketSpotData data = spotDataRepository.findLatestPrice(marketPair);
        
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(data);
    }

    /**
     * Get latest mark price for a futures contract.
     * First checks WebSocketFuturesData; falls back to the live
     * currentPrices@futures#update snapshot (keys stored as e.g. "B-BTC_USDT").
     * Also accepts short-form symbols like "BTCUSDT" by normalising the key.
     */
    @GetMapping("/futures/{contractSymbol}/latest-price")
    public ResponseEntity<WebSocketFuturesData> getLatestFuturesPrice(@PathVariable String contractSymbol) {
        WebSocketFuturesData data = futuresDataRepository.findLatestMarkPrice(contractSymbol);
        if (data != null) {
            return ResponseEntity.ok(data);
        }

        // Fall back to the live current-prices snapshot
        Optional<WebSocketFuturesCurrentPricesData> latestSnapshot =
                futuresCurrentPricesDataRepository.findLatestByRecordTimestamp();
        if (latestSnapshot.isEmpty() || latestSnapshot.get().getPrices() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            JsonObject prices = JsonParser.parseString(latestSnapshot.get().getPrices()).getAsJsonObject();

            // Try exact match first (e.g. "B-BTC_USDT"), then normalised match (e.g. "BTCUSDT")
            String matchedKey = null;
            if (prices.has(contractSymbol)) {
                matchedKey = contractSymbol;
            } else {
                for (String key : prices.keySet()) {
                    String normalised = key.replaceFirst("^B-", "").replace("_", "");
                    if (normalised.equalsIgnoreCase(contractSymbol)) {
                        matchedKey = key;
                        break;
                    }
                }
            }

            if (matchedKey == null || !prices.get(matchedKey).isJsonObject()) {
                return ResponseEntity.notFound().build();
            }

            JsonObject priceEntry = prices.getAsJsonObject(matchedKey);
            if (!priceEntry.has("mp") || priceEntry.get("mp").isJsonNull()) {
                return ResponseEntity.notFound().build();
            }

            WebSocketFuturesData syntheticData = new WebSocketFuturesData();
            syntheticData.setContractSymbol(contractSymbol);
            syntheticData.setChannelName("currentPrices@futures@rt");
            syntheticData.setEventType("price-change");
            syntheticData.setMarkPrice(priceEntry.get("mp").getAsBigDecimal());
            syntheticData.setTimestamp(LocalDateTime.now());
            if (latestSnapshot.get().getTimestamp() != null) {
                syntheticData.setExchangeTimestamp(latestSnapshot.get().getTimestamp());
            }
            return ResponseEntity.ok(syntheticData);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get data within time range
     */
    @GetMapping("/spot/{marketPair}/range")
    public ResponseEntity<List<WebSocketSpotData>> getSpotDataInRange(
            @PathVariable String marketPair,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        
        List<WebSocketSpotData> data = spotDataRepository.findRecentData(marketPair, since);
        return ResponseEntity.ok(data);
    }

    /**
     * Get futures data within time range
     */
    @GetMapping("/futures/{contractSymbol}/range")
    public ResponseEntity<List<WebSocketFuturesData>> getFuturesDataInRange(
            @PathVariable String contractSymbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        
        List<WebSocketFuturesData> data = futuresDataRepository.findRecentData(contractSymbol, since);
        return ResponseEntity.ok(data);
    }

    /**
     * Delete old data (cleanup)
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanupOldData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        
        spotDataRepository.deleteByTimestampBefore(before);
        futuresDataRepository.deleteByTimestampBefore(before);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cleaned up data before " + before);
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all distinct spot markets
     */
    @GetMapping("/spot/markets")
    public ResponseEntity<List<String>> getAllSpotMarkets() {
        return ResponseEntity.ok(spotDataRepository.findDistinctMarketPairs());
    }

    /**
     * Get all distinct futures contracts
     */
    @GetMapping("/futures/contracts")
    public ResponseEntity<List<String>> getAllFuturesContracts() {
        return ResponseEntity.ok(futuresDataRepository.findDistinctContractSymbols());
    }
}
