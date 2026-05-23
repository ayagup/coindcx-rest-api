package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.service.MetaTraderService;
import com.coindcx.springclient.service.MetaTraderSymbolService;
import com.coindcx.springclient.service.PublicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicController.class)
@DisplayName("PublicController Tests")
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PublicService publicService;

    @MockBean
    private MetaTraderService metaTraderService;

    @MockBean
    private MetaTraderSymbolService metaTraderSymbolService;

    // ── GET /ticker ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/public/ticker - 200 returns list")
    void getTicker_success() throws Exception {
        when(publicService.getTicker()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/public/ticker"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/public/ticker - ApiException propagated")
    void getTicker_apiException() throws Exception {
        when(publicService.getTicker()).thenThrow(new ApiException(503, "Unavailable"));

        mockMvc.perform(get("/api/public/ticker"))
                .andExpect(status().isServiceUnavailable());
    }

    // ── GET /markets/details ─────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/public/markets/details - 200 returns list")
    void getMarketDetails_success() throws Exception {
        when(publicService.getMarketDetails()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/public/markets/details"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/public/markets/details - ApiException propagated")
    void getMarketDetails_apiException() throws Exception {
        when(publicService.getMarketDetails()).thenThrow(new ApiException(500, "Internal error"));

        mockMvc.perform(get("/api/public/markets/details"))
                .andExpect(status().isInternalServerError());
    }

    // ── GET /markets ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/public/markets - 200 on success")
    void getMarkets_success() throws Exception {
        when(publicService.getMarkets()).thenReturn(Map.of());

        mockMvc.perform(get("/api/public/markets"))
                .andExpect(status().isOk());
    }

    // ── GET /candles ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/public/candles - 200 returns list")
    void getCandles_success() throws Exception {
        when(publicService.getCandles(anyString(), anyString(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/public/candles")
                        .param("pair", "BTCINR")
                        .param("interval", "1h"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/public/candles - with optional params")
    void getCandles_withOptionalParams() throws Exception {
        when(publicService.getCandles(anyString(), anyString(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/public/candles")
                        .param("pair", "BTCINR")
                        .param("interval", "1d")
                        .param("startTime", "1700000000000")
                        .param("endTime", "1710000000000")
                        .param("limit", "100"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/public/candles - ApiException propagated")
    void getCandles_apiException() throws Exception {
        when(publicService.getCandles(anyString(), anyString(), any(), any(), any()))
                .thenThrow(new ApiException(404, "Not found"));

        mockMvc.perform(get("/api/public/candles")
                        .param("pair", "INVALID")
                        .param("interval", "1h"))
                .andExpect(status().isNotFound());
    }

    // ── GET /orderbook ───────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/public/orderbook - 200 on success")
    void getOrderbook_success() throws Exception {
        when(publicService.getOrderbook(anyString())).thenReturn(new MarketDataOrderbookGet200Response());

        mockMvc.perform(get("/api/public/orderbook").param("pair", "BTCINR"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/public/orderbook - ApiException propagated")
    void getOrderbook_apiException() throws Exception {
        when(publicService.getOrderbook(anyString())).thenThrow(new ApiException(400, "Bad request"));

        mockMvc.perform(get("/api/public/orderbook").param("pair", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    // ── GET /trade-history ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/public/trade-history - 200 returns list")
    void getTradeHistory_success() throws Exception {
        when(publicService.getTradeHistory(anyString(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/public/trade-history").param("pair", "BTCINR"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/public/trade-history - with limit param")
    void getTradeHistory_withLimit() throws Exception {
        when(publicService.getTradeHistory(anyString(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/public/trade-history")
                        .param("pair", "BTCINR")
                        .param("limit", "50"))
                .andExpect(status().isOk());
    }

    // ── GET /futures/depth ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/public/futures/depth - 200 on success")
    void getFuturesDepth_success() throws Exception {
        when(publicService.getFuturesDepth(anyString(), anyString()))
                .thenReturn(new MarketDataV3OrderbookInstrumentFuturesDepthGet200Response());

        mockMvc.perform(get("/api/public/futures/depth")
                        .param("instrument", "BTCUSDT")
                        .param("depth", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/public/futures/depth - ApiException propagated")
    void getFuturesDepth_apiException() throws Exception {
        when(publicService.getFuturesDepth(anyString(), anyString()))
                .thenThrow(new ApiException(503, "Service unavailable"));

        mockMvc.perform(get("/api/public/futures/depth")
                        .param("instrument", "BTCUSDT")
                        .param("depth", "5"))
                .andExpect(status().isServiceUnavailable());
    }
}
