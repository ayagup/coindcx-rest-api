package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketFuturesData;
import com.coindcx.springclient.entity.WebSocketFuturesInstrumentPrice;
import com.coindcx.springclient.entity.WebSocketSpotData;
import com.coindcx.springclient.model.WebSocketFuturesCandlestickData;
import com.coindcx.springclient.repository.WebSocketFuturesCandlestickDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotDataRepository;
import com.coindcx.springclient.service.CoinDCXWebSocketService;
import com.coindcx.springclient.service.FuturesInstrumentPriceParsingService;
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

    /**
     * Maps user-facing timeframe labels to the 'duration' value stored in
     * websocket_futures_candlestick_data.
     */
    private static final Map<String, String> TIMEFRAME_TO_DURATION = new HashMap<>();
    static {
        TIMEFRAME_TO_DURATION.put("1m",  "1");
        TIMEFRAME_TO_DURATION.put("3m",  "3");
        TIMEFRAME_TO_DURATION.put("5m",  "5");
        TIMEFRAME_TO_DURATION.put("15m", "15");
        TIMEFRAME_TO_DURATION.put("30m", "30");
        TIMEFRAME_TO_DURATION.put("1h",  "60");
        TIMEFRAME_TO_DURATION.put("2h",  "120");
        TIMEFRAME_TO_DURATION.put("4h",  "240");
        TIMEFRAME_TO_DURATION.put("1d",  "1D");
        TIMEFRAME_TO_DURATION.put("1w",  "1W");
    }

    private final WebSocketSpotDataRepository spotDataRepository;
    private final WebSocketFuturesDataRepository futuresDataRepository;
    private final WebSocketFuturesCandlestickDataRepository candlestickRepository;
    private final CoinDCXWebSocketService webSocketService;
    private final FuturesInstrumentPriceParsingService futuresInstrumentPriceParsingService;

    @Autowired
    public WebSocketDataController(
            WebSocketSpotDataRepository spotDataRepository,
            WebSocketFuturesDataRepository futuresDataRepository,
            WebSocketFuturesCandlestickDataRepository candlestickRepository,
            CoinDCXWebSocketService webSocketService,
            FuturesInstrumentPriceParsingService futuresInstrumentPriceParsingService) {
        this.spotDataRepository = spotDataRepository;
        this.futuresDataRepository = futuresDataRepository;
        this.candlestickRepository = candlestickRepository;
        this.webSocketService = webSocketService;
        this.futuresInstrumentPriceParsingService = futuresInstrumentPriceParsingService;
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
     * Get recent futures market data.
     *
     * When {@code timeframe} is supplied (e.g. "1m", "5m", "1h", "1d", "1w") the response
     * contains OHLCV candlestick rows from websocket_futures_candlestick_data ordered by
     * open_time DESC.
     *
     * Supported timeframes: 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 1d, 1w
     *
     * Without {@code timeframe} the original behaviour is preserved: mark-price events are
     * returned, falling back to currentPrices@futures@rt instrument prices.
     */
    @GetMapping("/futures/{contractSymbol}")
    public ResponseEntity<List<?>> getFuturesData(
            @PathVariable String contractSymbol,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String timeframe) {

        // ── Candlestick path ──────────────────────────────────────────────────
        if (timeframe != null && !timeframe.isBlank()) {
            String duration = TIMEFRAME_TO_DURATION.get(timeframe.toLowerCase());
            if (duration == null) {
                return ResponseEntity.badRequest().build();
            }
            List<WebSocketFuturesCandlestickData> candles =
                    candlestickRepository.findByPairAndDurationWithLimit(contractSymbol, duration, limit);
            return ResponseEntity.ok(candles);
        }

        // ── Legacy mark-price path ────────────────────────────────────────────
        List<WebSocketFuturesData> data = futuresDataRepository.findByContractSymbolOrderByTimestampDesc(contractSymbol);

        if (!data.isEmpty()) {
            if (data.size() > limit) {
                data = data.subList(0, limit);
            }
            return ResponseEntity.ok(data);
        }

        // Fallback: instrument price data from currentPrices@futures@rt channel
        List<WebSocketFuturesInstrumentPrice> instrumentPrices =
                futuresInstrumentPriceParsingService.getRecentPrices(contractSymbol, limit);
        return ResponseEntity.ok(instrumentPrices);
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
     * First tries WebSocketFuturesData (mark-price events), then falls back
     * to WebSocketFuturesInstrumentPrice (currentPrices@futures@rt channel).
     */
    @GetMapping("/futures/{contractSymbol}/latest-price")
    public ResponseEntity<?> getLatestFuturesPrice(@PathVariable String contractSymbol) {
        WebSocketFuturesData data = futuresDataRepository.findLatestMarkPrice(contractSymbol);

        if (data != null) {
            return ResponseEntity.ok(data);
        }

        // Fallback: instrument price data from currentPrices@futures@rt channel
        WebSocketFuturesInstrumentPrice instrumentPrice =
                futuresInstrumentPriceParsingService.getLatestPrice(contractSymbol);

        if (instrumentPrice != null) {
            return ResponseEntity.ok(instrumentPrice);
        }

        return ResponseEntity.notFound().build();
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
