package com.coindcx.springclient.client;

import com.coindcx.springclient.config.MetaTraderFeignConfig;
import com.coindcx.springclient.model.metatrader.ClosePositionRequest;
import com.coindcx.springclient.model.metatrader.ClosePositionResponse;
import com.coindcx.springclient.model.metatrader.OpenPositionRequest;
import com.coindcx.springclient.model.metatrader.OpenPositionResponse;
import com.coindcx.springclient.model.metatrader.TpSlRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * OpenFeign declarative REST client for the MetaTrader 5 REST Gateway.
 *
 * <p>The base URL is configured via {@code metatrader.api.base-url} in
 * {@code application.properties} (default: {@code http://localhost:5000}).
 *
 * <p>All endpoints from the MT5 Swagger spec are mapped here:
 * <ul>
 *   <li>Utility  : GET /health</li>
 *   <li>Account  : GET /account, /account/balance, /account/leverage</li>
 *   <li>Market   : GET /symbols, /ticks, /candlesticks</li>
 *   <li>Trading  : GET /positions, /orders | POST /trade/open, /trade/close, /trade/tpsl</li>
 *   <li>History  : GET /history/trades, /history/pnl</li>
 * </ul>
 */
@FeignClient(
        name = "metatrader-gateway",
        url = "${metatrader.api.base-url}",
        configuration = MetaTraderFeignConfig.class)
public interface MetaTraderClient {

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Liveness check – GET /health
     */
    @GetMapping("/health")
    Map<String, Object> health();

    // -------------------------------------------------------------------------
    // Account
    // -------------------------------------------------------------------------

    /**
     * Full MT5 account snapshot – GET /account
     */
    @GetMapping("/account")
    Map<String, Object> getAccount();

    /**
     * Wallet balance details – GET /account/balance
     */
    @GetMapping("/account/balance")
    Map<String, Object> getBalance();

    /**
     * Account leverage – GET /account/leverage
     */
    @GetMapping("/account/leverage")
    Map<String, Object> getLeverage();

    // -------------------------------------------------------------------------
    // Market Data
    // -------------------------------------------------------------------------

    /**
     * All tradable symbols – GET /symbols
     *
     * @param search optional case-insensitive substring filter (e.g. "XAU")
     */
    @GetMapping("/symbols")
    Map<String, Object> getSymbols(@RequestParam(value = "search", required = false) String search);

    /**
     * Raw tick data – GET /ticks
     *
     * @param symbol    symbol name, e.g. XAUUSD (required)
     * @param count     number of ticks (default 100, max 10000)
     * @param flags     tick type filter: all | info | trade (default all)
     * @param from      start timestamp YYYY-MM-DDTHH:MM:SS
     * @param to        end timestamp YYYY-MM-DDTHH:MM:SS
     */
    @GetMapping("/ticks")
    Map<String, Object> getTicks(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "count", required = false) Integer count,
            @RequestParam(value = "flags", required = false) String flags,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to);

    /**
     * OHLCV candlestick bars – GET /candlesticks
     *
     * @param symbol    symbol name, e.g. XAUUSD (required)
     * @param timeframe timeframe (default H1); M1..MN1
     * @param count     number of bars (default 100, max 5000)
     * @param from      start date YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS
     * @param to        end date YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS
     */
    @GetMapping("/candlesticks")
    Map<String, Object> getCandlesticks(
            @RequestParam("symbol") String symbol,
            @RequestParam(value = "timeframe", required = false) String timeframe,
            @RequestParam(value = "count", required = false) Integer count,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to);

    // -------------------------------------------------------------------------
    // Trading
    // -------------------------------------------------------------------------

    /**
     * All open positions – GET /positions
     *
     * @param symbol optional symbol filter, e.g. XAUUSD
     */
    @GetMapping("/positions")
    Map<String, Object> getPositions(@RequestParam(value = "symbol", required = false) String symbol);

    /**
     * All pending orders – GET /orders
     *
     * @param symbol optional symbol filter, e.g. XAUUSD
     */
    @GetMapping("/orders")
    Map<String, Object> getOrders(@RequestParam(value = "symbol", required = false) String symbol);

    /**
     * Open a market position – POST /trade/open
     *
     * @param request body with symbol, direction (long|short), margin_usdt
     */
    @PostMapping("/trade/open")
    OpenPositionResponse openPosition(@RequestBody OpenPositionRequest request);

    /**
     * Close an open position – POST /trade/close
     *
     * @param request body with ticket of the position to close
     */
    @PostMapping("/trade/close")
    ClosePositionResponse closePosition(@RequestBody ClosePositionRequest request);

    /**
     * Set or update TP/SL – POST /trade/tpsl
     *
     * @param request body with ticket, tp (optional), sl (optional)
     */
    @PostMapping("/trade/tpsl")
    Map<String, Object> setTpSl(@RequestBody TpSlRequest request);

    /**
     * Legacy endpoint – POST /trade/open-long-xauusd
     *
     * @param body optional map with margin_usdt
     */
    @PostMapping("/trade/open-long-xauusd")
    Map<String, Object> openLongXauUsd(@RequestBody(required = false) Map<String, Object> body);

    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------

    /**
     * Executed deal log – GET /history/trades
     *
     * @param from   start date (default 30 days ago)
     * @param to     end date (default now)
     * @param symbol optional symbol filter
     */
    @GetMapping("/history/trades")
    Map<String, Object> getHistoryTrades(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "symbol", required = false) String symbol);

    /**
     * Closed-position PnL summary – GET /history/pnl
     *
     * @param from   start date (default 30 days ago)
     * @param to     end date (default now)
     * @param symbol optional symbol filter
     */
    @GetMapping("/history/pnl")
    Map<String, Object> getHistoryPnl(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "symbol", required = false) String symbol);
}
