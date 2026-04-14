package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.model.metatrader.SymbolInfo;
import com.coindcx.springclient.service.MetaTraderService;
import com.coindcx.springclient.service.MetaTraderSymbolService;
import com.coindcx.springclient.service.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Public market data operations
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Market Data", description = "Public APIs for market data (No authentication required)")
public class PublicController {

    private final PublicService publicService;
    private final MetaTraderService metaTraderService;
    private final MetaTraderSymbolService metaTraderSymbolService;

    @Autowired
    public PublicController(PublicService publicService,
                            MetaTraderService metaTraderService,
                            MetaTraderSymbolService metaTraderSymbolService) {
        this.publicService = publicService;
        this.metaTraderService = metaTraderService;
        this.metaTraderSymbolService = metaTraderSymbolService;
    }

    /**
     * Get ticker information for all markets
     * 
     * @return List of ticker data
     */
    @Operation(
            summary = "Get ticker information",
            description = "Retrieves real-time ticker information for all available markets including last price, bid, ask, high, low, and volume"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved ticker data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/ticker")
    public ResponseEntity<List<ExchangeTickerGet200ResponseInner>> getTicker() {
        try {
            List<ExchangeTickerGet200ResponseInner> ticker = publicService.getTicker();
            return ResponseEntity.ok(ticker);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get market details
     * 
     * @return List of market details
     */
    @GetMapping("/markets/details")
    public ResponseEntity<List<ExchangeV1MarketsDetailsGet200ResponseInner>> getMarketDetails() {
        try {
            List<ExchangeV1MarketsDetailsGet200ResponseInner> details = publicService.getMarketDetails();
            return ResponseEntity.ok(details);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get markets information
     * 
     * @return List of markets
     */
    @GetMapping("/markets")
    public ResponseEntity<Object> getMarkets() {
        try {
            Object markets = publicService.getMarkets();
            return ResponseEntity.ok(markets);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get candlestick data
     * 
     * @param pair Trading pair
     * @param interval Time interval
     * @param startTime Start timestamp (optional)
     * @param endTime End timestamp (optional)
     * @param limit Number of candles (optional)
     * @return List of candlestick data
     */
    @GetMapping("/candles")
    public ResponseEntity<List<MarketDataCandlesGet200ResponseInner>> getCandles(
            @RequestParam String pair,
            @RequestParam String interval,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) Integer limit) {
        try {
            List<MarketDataCandlesGet200ResponseInner> candles = publicService.getCandles(pair, interval, startTime, endTime, limit);
            return ResponseEntity.ok(candles);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get orderbook for a market
     * 
     * @param pair Trading pair
     * @return Orderbook data
     */
    @GetMapping("/orderbook")
    public ResponseEntity<MarketDataOrderbookGet200Response> getOrderbook(
            @RequestParam String pair) {
        try {
            MarketDataOrderbookGet200Response orderbook = publicService.getOrderbook(pair);
            return ResponseEntity.ok(orderbook);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get trade history for a market
     * 
     * @param pair Trading pair
     * @param limit Number of trades (optional)
     * @return List of recent trades
     */
    @GetMapping("/trade-history")
    public ResponseEntity<List<MarketDataTradeHistoryGet200ResponseInner>> getTradeHistory(
            @RequestParam String pair,
            @RequestParam(required = false) Integer limit) {
        try {
            List<MarketDataTradeHistoryGet200ResponseInner> trades = publicService.getTradeHistory(pair, limit);
            return ResponseEntity.ok(trades);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get futures depth/orderbook
     * 
     * @param instrument Futures instrument
     * @param depth Depth level
     * @return Futures orderbook depth
     */
    @GetMapping("/futures/depth")
    public ResponseEntity<MarketDataV3OrderbookInstrumentFuturesDepthGet200Response> getFuturesDepth(
            @RequestParam String instrument,
            @RequestParam String depth) {
        try {
            MarketDataV3OrderbookInstrumentFuturesDepthGet200Response futuresDepth = publicService.getFuturesDepth(instrument, depth);
            return ResponseEntity.ok(futuresDepth);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    // -------------------------------------------------------------------------
    // MetaTrader 5 – Market Data
    // -------------------------------------------------------------------------

    @GetMapping("/mt5/ticks")
    @Operation(summary = "MT5 raw tick data for a symbol",
               description = "Returns raw ticks. Omit 'from' for the latest 'count' ticks.")
    public ResponseEntity<List<Object>> mt5Ticks(
            @Parameter(description = "Symbol name, e.g. XAUUSD", required = true)
            @RequestParam String symbol,
            @Parameter(description = "Number of ticks (default 100, max 10000)")
            @RequestParam(required = false) Integer count,
            @Parameter(description = "Tick type filter: all | info | trade")
            @RequestParam(required = false) String flags,
            @Parameter(description = "Start timestamp YYYY-MM-DDTHH:MM:SS")
            @RequestParam(required = false) String from,
            @Parameter(description = "End timestamp YYYY-MM-DDTHH:MM:SS")
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(metaTraderService.getTicks(symbol, count, flags, from, to));
    }

    // -------------------------------------------------------------------------
    // MetaTrader 5 – Symbol Registry
    // -------------------------------------------------------------------------

    @GetMapping("/mt5/registry/stats")
    @Operation(summary = "MT5 symbol registry statistics",
               description = "Returns total, enabled, and disabled symbol counts.")
    public ResponseEntity<Map<String, Long>> mt5RegistryStats() {
        return ResponseEntity.ok(Map.of(
                "total",    metaTraderSymbolService.countTotal(),
                "enabled",  metaTraderSymbolService.countEnabled(),
                "disabled", metaTraderSymbolService.countDisabled()
        ));
    }

    @GetMapping("/mt5/registry/symbols")
    @Operation(summary = "MT5 paginated list of all symbols")
    public ResponseEntity<Page<SymbolInfo>> mt5RegistryListAll(
            @Parameter(description = "0-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, max 200")  @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(metaTraderSymbolService.listAllPaged(page, size));
    }

    @GetMapping("/mt5/registry/symbols/enabled")
    @Operation(summary = "MT5 symbols currently enabled for trading")
    public ResponseEntity<List<SymbolInfo>> mt5RegistryListEnabled() {
        return ResponseEntity.ok(metaTraderSymbolService.listEnabled());
    }

    @GetMapping("/mt5/registry/symbols/search")
    @Operation(summary = "MT5 search symbols by name or description")
    public ResponseEntity<Page<SymbolInfo>> mt5RegistrySearch(
            @Parameter(description = "Search term, e.g. 'gold' or 'XAU'", required = true)
            @RequestParam String q,
            @Parameter(description = "0-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, max 200")  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(metaTraderSymbolService.search(q, page, size));
    }

    @GetMapping("/mt5/registry/symbols/{name}")
    @Operation(summary = "MT5 get symbol details by name")
    public ResponseEntity<SymbolInfo> mt5RegistryGetSymbol(
            @Parameter(description = "MT5 symbol name, case-sensitive", required = true)
            @PathVariable String name) {
        return ResponseEntity.ok(metaTraderSymbolService.getSymbol(name));
    }

    @PatchMapping("/mt5/registry/symbols/{name}/enable")
    @Operation(summary = "MT5 enable trading for a single symbol")
    public ResponseEntity<SymbolInfo> mt5RegistryEnableSymbol(
            @Parameter(description = "MT5 symbol name", required = true)
            @PathVariable String name) {
        return ResponseEntity.ok(metaTraderSymbolService.setEnabled(name, true));
    }

    @PatchMapping("/mt5/registry/symbols/{name}/disable")
    @Operation(summary = "MT5 disable trading for a single symbol")
    public ResponseEntity<SymbolInfo> mt5RegistryDisableSymbol(
            @Parameter(description = "MT5 symbol name", required = true)
            @PathVariable String name) {
        return ResponseEntity.ok(metaTraderSymbolService.setEnabled(name, false));
    }

    @PostMapping("/mt5/registry/symbols/enable-all")
    @Operation(summary = "MT5 enable ALL symbols for trading")
    public ResponseEntity<Map<String, Object>> mt5RegistryEnableAll() {
        int count = metaTraderSymbolService.enableAll();
        return ResponseEntity.ok(Map.of("enabled", count, "message", "All symbols enabled"));
    }

    @PostMapping("/mt5/registry/symbols/disable-all")
    @Operation(summary = "MT5 disable ALL symbols")
    public ResponseEntity<Map<String, Object>> mt5RegistryDisableAll() {
        int count = metaTraderSymbolService.disableAll();
        return ResponseEntity.ok(Map.of("disabled", count, "message", "All symbols disabled"));
    }

    @PostMapping("/mt5/registry/symbols/enable-batch")
    @Operation(summary = "MT5 enable a list of symbols")
    public ResponseEntity<Map<String, Object>> mt5RegistryEnableBatch(@RequestBody List<String> names) {
        int count = metaTraderSymbolService.enableByNames(names);
        return ResponseEntity.ok(Map.of("enabled", count, "requested", names.size()));
    }

    @PostMapping("/mt5/registry/symbols/disable-batch")
    @Operation(summary = "MT5 disable a list of symbols")
    public ResponseEntity<Map<String, Object>> mt5RegistryDisableBatch(@RequestBody List<String> names) {
        int count = metaTraderSymbolService.disableByNames(names);
        return ResponseEntity.ok(Map.of("disabled", count, "requested", names.size()));
    }

    @GetMapping("/mt5/registry/symbols/{name}/tradeable")
    @Operation(summary = "MT5 check whether a symbol is currently enabled for trading")
    public ResponseEntity<Map<String, Object>> mt5RegistryIsTradeable(
            @Parameter(description = "MT5 symbol name", required = true)
            @PathVariable String name) {
        boolean tradeable = metaTraderSymbolService.isSymbolEnabled(name);
        return ResponseEntity.ok(Map.of("symbol", name, "tradeable", tradeable));
    }
}
