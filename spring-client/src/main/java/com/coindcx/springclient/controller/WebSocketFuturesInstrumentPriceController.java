package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.WebSocketFuturesInstrumentPrice;
import com.coindcx.springclient.model.MarketDataTradeHistoryGet200ResponseInner;
import com.coindcx.springclient.service.FuturesInstrumentPriceParsingService;
import com.coindcx.springclient.service.MetaTraderService;
import com.coindcx.springclient.service.MetaTraderSymbolService;
import com.coindcx.springclient.service.PublicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * REST API controller for individual Futures Instrument Prices
 * Provides endpoints to query individual instrument prices parsed from
 * currentPrices@futures@rt channel data
 */
@RestController
@RequestMapping("/api/websocket/futures-instrument-prices")
@CrossOrigin(origins = "*")
public class WebSocketFuturesInstrumentPriceController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketFuturesInstrumentPriceController.class);

    private final FuturesInstrumentPriceParsingService parsingService;
    private final MetaTraderService metaTraderService;
    private final MetaTraderSymbolService metaTraderSymbolService;
    private final PublicService publicService;

    public WebSocketFuturesInstrumentPriceController(FuturesInstrumentPriceParsingService parsingService,
                                                      MetaTraderService metaTraderService,
                                                      MetaTraderSymbolService metaTraderSymbolService,
                                                      PublicService publicService) {
        this.parsingService = parsingService;
        this.metaTraderService = metaTraderService;
        this.metaTraderSymbolService = metaTraderSymbolService;
        this.publicService = publicService;
    }

    /**
     * Get all distinct instruments that have price data
     * 
     * @return List of instrument symbols
     */
    @GetMapping("/instruments")
    public ResponseEntity<List<String>> getAllInstruments() {
        List<String> instruments = parsingService.getAllInstruments();
        return ResponseEntity.ok(instruments);
    }

    /**
     * Get the latest price for a specific instrument
     * 
     * @param instrument Instrument symbol (e.g., "B-AVA_USDT")
     * @return Latest price data for the instrument
     */
    @GetMapping("/instrument/{instrument}/latest")
    public ResponseEntity<?> getLatestPrice(@PathVariable String instrument) {
        // 1. Try CoinDCX WebSocket DB (prefers records with non-null markPrice)
        WebSocketFuturesInstrumentPrice latestPrice = parsingService.getLatestPrice(instrument);
        if (latestPrice != null) {
            return ResponseEntity.ok(latestPrice);
        }

        // 2. Fall back to CoinDCX trade-history REST API for exchange pairs (contain '_')
        //    Uses a per-symbol call (limit=1) so it's fast.
        //    WebSocket continues running in the background; this is just an immediate response.
        if (instrument.contains("_")) {
            try {
                List<MarketDataTradeHistoryGet200ResponseInner> trades =
                        publicService.getTradeHistory(instrument, 1);
                if (trades != null && !trades.isEmpty()) {
                    MarketDataTradeHistoryGet200ResponseInner last = trades.get(0);
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("instrument", instrument);
                    response.put("markPrice", last.getP());
                    response.put("eventTimestamp", last.getT() != null
                            ? (long) last.getT() * 1000L : null);
                    response.put("productType", "futures");
                    response.put("source", "rest_api_fallback");
                    return ResponseEntity.ok(response);
                }
            } catch (Exception e) {
                logger.warn("REST trade-history fallback failed for {}: {}", instrument, e.getMessage());
            }
        }

        // 3. Fall back to MT5 ticks (handles XAUUSD, EURUSD and other MT5 symbols without '_')
        String mt5Error = null;
        try {
            List<Object> ticks = metaTraderService.getTicks(instrument, 1, null, null, null);
            if (ticks != null && !ticks.isEmpty()) {
                return ResponseEntity.ok(ticks.get(0));
            }
        } catch (Exception e) {
            mt5Error = e.getMessage();
        }

        Map<String, Object> notFound = new java.util.LinkedHashMap<>();
        notFound.put("error", "No price data found for instrument: " + instrument);
        if (mt5Error != null) notFound.put("mt5_error", mt5Error);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
    }

    /**
     * Get recent prices for a specific instrument
     * 
     * @param instrument Instrument symbol (e.g., "B-AVA_USDT")
     * @param limit Maximum number of records to return (default: 50)
     * @return List of recent prices for the instrument
     */
    @GetMapping("/instrument/{instrument}")
    public ResponseEntity<List<WebSocketFuturesInstrumentPrice>> getInstrumentPrices(
            @PathVariable String instrument,
            @RequestParam(defaultValue = "50") int limit) {
        List<WebSocketFuturesInstrumentPrice> prices = parsingService.getRecentPrices(instrument, limit);
        return ResponseEntity.ok(prices);
    }

    /**
     * Get prices for a specific instrument within a time range
     * 
     * @param instrument Instrument symbol
     * @param startTime Start timestamp (ISO 8601 format)
     * @param endTime End timestamp (ISO 8601 format)
     * @return List of prices within the time range
     */
    @GetMapping("/instrument/{instrument}/time-range")
    public ResponseEntity<List<WebSocketFuturesInstrumentPrice>> getPricesByTimeRange(
            @PathVariable String instrument,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Long startTimestamp = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        Long endTimestamp = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        
        List<WebSocketFuturesInstrumentPrice> prices = 
            parsingService.getPricesByTimestampRange(instrument, startTimestamp, endTimestamp);
        
        return ResponseEntity.ok(prices);
    }

    /**
     * Get the latest prices for all instruments (most recent snapshot)
     * 
     * @return List of latest prices for all instruments
     */
    @GetMapping("/latest-all")
    public ResponseEntity<List<WebSocketFuturesInstrumentPrice>> getLatestPricesAllInstruments() {
        List<WebSocketFuturesInstrumentPrice> prices = parsingService.getLatestPricesAllInstruments();
        return ResponseEntity.ok(prices);
    }

    /**
     * Get statistics about instrument prices
     * 
     * @return Map containing statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<String> instruments = parsingService.getAllInstruments();
        stats.put("totalInstruments", instruments.size());
        stats.put("instruments", instruments);
        
        List<WebSocketFuturesInstrumentPrice> latestPrices = parsingService.getLatestPricesAllInstruments();
        stats.put("latestSnapshotCount", latestPrices.size());
        
        // Count instruments with mark prices
        long withMarkPrice = latestPrices.stream()
                .filter(p -> p.getMarkPrice() != null)
                .count();
        stats.put("instrumentsWithMarkPrice", withMarkPrice);
        stats.put("instrumentsWithoutMarkPrice", latestPrices.size() - withMarkPrice);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Search instruments by partial name match
     * 
     * @param query Search query (case-insensitive)
     * @return List of matching instrument symbols
     */
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchInstruments(@RequestParam String query) {
        List<String> allInstruments = parsingService.getAllInstruments();
        
        List<String> matchingInstruments = allInstruments.stream()
                .filter(inst -> inst.toLowerCase().contains(query.toLowerCase()))
                .toList();
        
        return ResponseEntity.ok(matchingInstruments);
    }

    /**
     * Get a summary of price changes for an instrument
     * Compares the latest price with the oldest price in the recent history
     * 
     * @param instrument Instrument symbol
     * @param limit Number of historical records to consider (default: 100)
     * @return Map containing price change summary
     */
    @GetMapping("/instrument/{instrument}/price-change")
    public ResponseEntity<?> getPriceChangeSummary(
            @PathVariable String instrument,
            @RequestParam(defaultValue = "100") int limit) {
        
        List<WebSocketFuturesInstrumentPrice> prices = parsingService.getRecentPrices(instrument, limit);
        
        if (prices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No price data found for instrument: " + instrument);
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("instrument", instrument);
        summary.put("priceCount", prices.size());
        
        // Latest price (first in the list as they're ordered DESC)
        WebSocketFuturesInstrumentPrice latest = prices.get(0);
        WebSocketFuturesInstrumentPrice oldest = prices.get(prices.size() - 1);
        
        summary.put("latestPrice", latest.getMarkPrice());
        summary.put("latestTimestamp", latest.getEventTimestamp());
        summary.put("oldestPrice", oldest.getMarkPrice());
        summary.put("oldestTimestamp", oldest.getEventTimestamp());
        
        if (latest.getMarkPrice() != null && oldest.getMarkPrice() != null) {
            double priceChange = latest.getMarkPrice() - oldest.getMarkPrice();
            double priceChangePercent = (priceChange / oldest.getMarkPrice()) * 100;
            
            summary.put("priceChange", priceChange);
            summary.put("priceChangePercent", priceChangePercent);
        }
        
        return ResponseEntity.ok(summary);
    }
}
