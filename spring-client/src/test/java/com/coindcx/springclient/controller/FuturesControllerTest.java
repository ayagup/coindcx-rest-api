package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.service.FuturesService;
import com.coindcx.springclient.service.MetaTraderService;
import com.coindcx.springclient.service.MetaTraderSymbolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FuturesController.class)
@DisplayName("FuturesController Tests")
class FuturesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FuturesService futuresService;

    @MockBean
    private MetaTraderService metaTraderService;

    @MockBean
    private MetaTraderSymbolService metaTraderSymbolService;

    // ── GET active instruments ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/instruments/active - success merges futures and MT5 symbols")
    void getActiveInstruments_coindcx() throws Exception {
        when(futuresService.getActiveInstruments(anyString())).thenReturn(List.of());
        when(metaTraderService.getSymbols(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/futures/instruments/active")
                        .param("marginCurrencyShortName", "USDT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").isArray())
                .andExpect(jsonPath("$.mt5").isArray());
    }

    @Test
    @DisplayName("GET /api/futures/instruments/active - futures error included, MT5 still returned")
    void getActiveInstruments_apiException() throws Exception {
        when(futuresService.getActiveInstruments(anyString())).thenThrow(new ApiException(503, "Unavailable"));
        when(metaTraderService.getSymbols(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/futures/instruments/active")
                        .param("marginCurrencyShortName", "USDT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").isArray())
                .andExpect(jsonPath("$.mt5").isArray())
                .andExpect(jsonPath("$.futures_error").exists());
    }

    // ── POST orders/create ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/orders/create - success CoinDCX path")
    void createOrder_success() throws Exception {
        when(metaTraderSymbolService.isSymbolEnabled(anyString())).thenReturn(false);
        when(futuresService.createOrder(any())).thenReturn("{\"id\":\"abc\"}");

        Map<String, Object> body = Map.of(
                "market", "BTCUSDT",
                "side", "buy",
                "order_type", "market",
                "quantity", "0.01"
        );

        mockMvc.perform(post("/api/futures/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/futures/orders/create - missing side returns 400")
    void createOrder_missingSide() throws Exception {
        when(metaTraderSymbolService.isSymbolEnabled(anyString())).thenReturn(false);

        Map<String, Object> body = Map.of(
                "market", "BTCUSDT",
                "order_type", "market",
                "quantity", "0.01"
        );

        mockMvc.perform(post("/api/futures/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // ── POST orders/cancel ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/orders/cancel - success")
    void cancelOrder_success() throws Exception {
        when(futuresService.cancelOrder(any())).thenReturn(new ExchangeV1DerivativesFuturesOrdersCancelPost200Response());

        mockMvc.perform(post("/api/futures/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1DerivativesFuturesOrdersCancelPostRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/futures/orders/cancel - ApiException propagated")
    void cancelOrder_apiException() throws Exception {
        when(futuresService.cancelOrder(any())).thenThrow(new ApiException(404, "Not found"));

        mockMvc.perform(post("/api/futures/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1DerivativesFuturesOrdersCancelPostRequest())))
                .andExpect(status().isNotFound());
    }

    // ── POST orders ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/orders - success CoinDCX path")
    void getOrders_success() throws Exception {
        when(metaTraderSymbolService.isSymbolEnabled(any())).thenReturn(false);
        doNothing().when(futuresService).getOrders(any());

        mockMvc.perform(post("/api/futures/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pair\":\"BTCUSDT\"}"))
                .andExpect(status().isOk());
    }

    // ── POST positions ───────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/positions - success CoinDCX path")
    void getPositions_success() throws Exception {
        when(metaTraderSymbolService.isSymbolEnabled(any())).thenReturn(false);
        doNothing().when(futuresService).getPositions(any());

        mockMvc.perform(post("/api/futures/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pair\":\"BTCUSDT\"}"))
                .andExpect(status().isOk());
    }

    // ── POST positions/add-margin ────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/positions/add-margin - success")
    void addMargin_success() throws Exception {
        doNothing().when(futuresService).addMargin(any());

        mockMvc.perform(post("/api/futures/positions/add-margin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Margin added successfully"));
    }

    // ── POST positions/remove-margin ─────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/positions/remove-margin - success")
    void removeMargin_success() throws Exception {
        doNothing().when(futuresService).removeMargin(any());

        mockMvc.perform(post("/api/futures/positions/remove-margin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Margin removed successfully"));
    }

    // ── POST positions/update-leverage ───────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/positions/update-leverage - success")
    void updateLeverage_success() throws Exception {
        doNothing().when(futuresService).updateLeverage(any());

        mockMvc.perform(post("/api/futures/positions/update-leverage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Leverage updated successfully"));
    }

    // ── POST positions/cancel-all-orders ─────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/positions/cancel-all-orders - success")
    void cancelAllOpenOrdersForPosition_success() throws Exception {
        doNothing().when(futuresService).cancelAllOpenOrdersForPosition(any());

        mockMvc.perform(post("/api/futures/positions/cancel-all-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("All open orders cancelled successfully"));
    }

    // ── POST orders/cancel-all ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/orders/cancel-all - success")
    void cancelAllOpenOrders_success() throws Exception {
        doNothing().when(futuresService).cancelAllOpenOrders(any());

        mockMvc.perform(post("/api/futures/orders/cancel-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("All orders cancelled successfully"));
    }

    // ── POST positions/transactions ──────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/positions/transactions - success merges futures and MT5 PnL")
    void getPositionTransactions_success() throws Exception {
        when(futuresService.getPositionTransactions(any())).thenReturn("[]");
        when(metaTraderService.getHistoryPnl(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(post("/api/futures/positions/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").isArray())
                .andExpect(jsonPath("$.mt5").isArray());
    }

    // ── POST trades ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/trades - success merges futures and MT5")
    void getTrades_success() throws Exception {
        when(futuresService.getTrades(any())).thenReturn("[]");
        when(metaTraderService.getHistoryTrades(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(post("/api/futures/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1DerivativesFuturesTradesPostRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").isArray())
                .andExpect(jsonPath("$.mt5").isArray());
    }

    // ── GET depth ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/depth - success")
    void getDepth_success() throws Exception {
        when(futuresService.getDepth(anyString(), anyString()))
                .thenReturn(new MarketDataV3OrderbookInstrumentFuturesDepthGet200Response());

        mockMvc.perform(get("/api/futures/depth")
                        .param("instrument", "BTCUSDT")
                        .param("depth", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/futures/depth - ApiException propagated")
    void getDepth_apiException() throws Exception {
        when(futuresService.getDepth(anyString(), anyString())).thenThrow(new ApiException(503, "Unavailable"));

        mockMvc.perform(get("/api/futures/depth")
                        .param("instrument", "BTCUSDT")
                        .param("depth", "5"))
                .andExpect(status().isServiceUnavailable());
    }

    // ── GET wallets ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/wallets - success merges futures and MT5")
    void getWallets_success() throws Exception {
        when(futuresService.getWallets()).thenReturn("{\"usdt\":100}");
        when(metaTraderService.getBalance()).thenReturn(java.util.Map.of("balance", 500.0));

        mockMvc.perform(get("/api/futures/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").exists())
                .andExpect(jsonPath("$.mt5").exists());
    }

    @Test
    @DisplayName("GET /api/futures/wallets - futures error included, MT5 still returned")
    void getWallets_apiException() throws Exception {
        when(futuresService.getWallets()).thenThrow(new ApiException(401, "Unauthorized"));
        when(metaTraderService.getBalance()).thenReturn(java.util.Map.of("balance", 500.0));

        mockMvc.perform(get("/api/futures/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").exists())
                .andExpect(jsonPath("$.mt5").exists())
                .andExpect(jsonPath("$.futures_error").exists());
    }

    // ── POST positions/exit ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/positions/exit - missing position returns 400")
    void exitPosition_noActivePosition() throws Exception {
        when(metaTraderSymbolService.isSymbolEnabled(any())).thenReturn(false);
        when(futuresService.fetchActivePositionId(anyString())).thenReturn(null);

        Map<String, Object> body = Map.of("market", "BTCUSDT");

        mockMvc.perform(post("/api/futures/positions/exit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // ── GET leverage ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/leverage - success returns futures note and MT5 leverage")
    void getLeverage_success() throws Exception {
        when(metaTraderService.getLeverage()).thenReturn(Map.of("leverage", 500, "display", "1:500"));

        mockMvc.perform(get("/api/futures/leverage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").exists())
                .andExpect(jsonPath("$.mt5").exists())
                .andExpect(jsonPath("$.mt5.leverage").value(500));
    }

    @Test
    @DisplayName("GET /api/futures/leverage - MT5 error included, futures note still returned")
    void getLeverage_mt5Error() throws Exception {
        when(metaTraderService.getLeverage()).thenThrow(new RuntimeException("MT5 unavailable"));

        mockMvc.perform(get("/api/futures/leverage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").exists())
                .andExpect(jsonPath("$.mt5").exists())
                .andExpect(jsonPath("$.mt5_error").value("MT5 unavailable"));
    }

    // ── GET candles ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/candles - success merges futures and MT5")
    void getCandlesticks_success() throws Exception {
        when(futuresService.getCandlesticks(anyString(), any(), any(), anyString()))
                .thenReturn(List.of(Map.of("open", 50000, "close", 51000)));
        when(metaTraderService.getCandlesticks(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of(Map.of("open", 1900.0, "close", 1910.0)));

        mockMvc.perform(get("/api/futures/candles").param("pair", "BTCUSDT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").isArray())
                .andExpect(jsonPath("$.mt5").isArray())
                .andExpect(jsonPath("$.futures[0].open").value(50000))
                .andExpect(jsonPath("$.mt5[0].open").value(1900.0));
    }

    @Test
    @DisplayName("GET /api/futures/candles - futures error included, MT5 still returned")
    void getCandlesticks_futuresError() throws Exception {
        when(futuresService.getCandlesticks(anyString(), any(), any(), anyString()))
                .thenThrow(new ApiException(400, "Missing required param"));
        when(metaTraderService.getCandlesticks(anyString(), any(), any(), any(), any()))
                .thenReturn(List.of(Map.of("open", 1900.0)));

        mockMvc.perform(get("/api/futures/candles").param("pair", "XAUUSD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.futures").isArray())
                .andExpect(jsonPath("$.mt5").isArray())
                .andExpect(jsonPath("$.futures_error").exists());
    }

    // ── POST positions/create-tpsl ───────────────────────────────────────────

    @Test
    @DisplayName("POST /api/futures/positions/create-tpsl - no TP or SL returns 400")
    void createTpSl_noTpOrSl() throws Exception {
        when(metaTraderSymbolService.isSymbolEnabled(any())).thenReturn(false);
        when(futuresService.fetchActivePositionId(anyString())).thenReturn("pos-123");

        Map<String, Object> body = Map.of("market", "BTCUSDT");  // no TP or SL fields

        mockMvc.perform(post("/api/futures/positions/create-tpsl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
