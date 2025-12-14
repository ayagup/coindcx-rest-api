package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.service.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Public market data operations
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Market Data", description = "Public APIs for market data (No authentication required)")
public class PublicController {

    private final PublicService publicService;

    @Autowired
    public PublicController(PublicService publicService) {
        this.publicService = publicService;
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
}
