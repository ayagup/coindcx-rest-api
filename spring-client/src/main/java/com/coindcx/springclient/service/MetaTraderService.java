package com.coindcx.springclient.service;

import com.coindcx.springclient.client.MetaTraderClient;
import com.coindcx.springclient.model.metatrader.ClosePositionRequest;
import com.coindcx.springclient.model.metatrader.ClosePositionResponse;
import com.coindcx.springclient.model.metatrader.OpenPositionRequest;
import com.coindcx.springclient.model.metatrader.OpenPositionResponse;
import com.coindcx.springclient.model.metatrader.TpSlRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Service layer for the MetaTrader 5 REST Gateway.
 *
 * <p>Delegates all calls to {@link MetaTraderClient} (OpenFeign). Logging of
 * every call is handled automatically by {@code ApiCallLoggingAspect}.
 *
 * <p>Before forwarding an open-position request the symbol is validated against
 * the local {@link MetaTraderSymbolService} registry.  Only symbols with
 * {@code enabled = true} are allowed through.
 */
@Service
public class MetaTraderService {

    private final MetaTraderClient         metaTraderClient;
    private final MetaTraderSymbolService  symbolService;

    public MetaTraderService(MetaTraderClient metaTraderClient,
                             MetaTraderSymbolService symbolService) {
        this.metaTraderClient = metaTraderClient;
        this.symbolService    = symbolService;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    public Map<String, Object> health() {
        return metaTraderClient.health();
    }

    // -------------------------------------------------------------------------
    // Account
    // -------------------------------------------------------------------------

    public Map<String, Object> getAccount() {
        return metaTraderClient.getAccount();
    }

    public Map<String, Object> getBalance() {
        return metaTraderClient.getBalance();
    }

    public Map<String, Object> getLeverage() {
        return metaTraderClient.getLeverage();
    }

    // -------------------------------------------------------------------------
    // Market Data
    // -------------------------------------------------------------------------

    public List<Object> getSymbols(String search) {
        return metaTraderClient.getSymbols(search);
    }

    public List<Object> getTicks(String symbol, Integer count, String flags, String from, String to) {
        return metaTraderClient.getTicks(symbol, count, flags, from, to);
    }

    public List<Object> getCandlesticks(String symbol, String timeframe, Integer count, String from, String to) {
        return metaTraderClient.getCandlesticks(symbol, timeframe, count, from, to);
    }

    // -------------------------------------------------------------------------
    // Trading
    // -------------------------------------------------------------------------

    public List<Object> getPositions(String symbol) {
        return metaTraderClient.getPositions(symbol);
    }

    public List<Object> getOrders(String symbol) {
        return metaTraderClient.getOrders(symbol);
    }

    /**
     * Opens a new MT5 position.  The symbol is first validated against the
     * local registry; a 400 Bad Request is returned if the symbol is unknown
     * or disabled.
     */
    public OpenPositionResponse openPosition(OpenPositionRequest request) {
        String symbol = request.getSymbol();
        if (symbol == null || symbol.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "symbol is required");
        }
        if (!symbolService.isSymbolEnabled(symbol)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Symbol '" + symbol + "' is not enabled for trading. "
                    + "Use PATCH /api/metatrader/registry/symbols/" + symbol + "/enable "
                    + "to enable it, or check GET /api/metatrader/registry/symbols/" + symbol
            );
        }
        return metaTraderClient.openPosition(request);
    }

    public ClosePositionResponse closePosition(Long ticket) {
        return metaTraderClient.closePosition(new ClosePositionRequest(ticket));
    }

    public Map<String, Object> setTpSl(TpSlRequest request) {
        return metaTraderClient.setTpSl(request);
    }

    public Map<String, Object> openLongXauUsd(Double marginUsdt) {
        Map<String, Object> body = marginUsdt != null
                ? Map.of("margin_usdt", marginUsdt)
                : Map.of();
        return metaTraderClient.openLongXauUsd(body);
    }

    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------

    public List<Object> getHistoryTrades(String from, String to, String symbol) {
        return metaTraderClient.getHistoryTrades(from, to, symbol);
    }

    public List<Object> getHistoryPnl(String from, String to, String symbol) {
        return metaTraderClient.getHistoryPnl(from, to, symbol);
    }
}
