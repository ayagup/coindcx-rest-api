package com.coindcx.springclient.controller;

import com.coindcx.springclient.entity.FuturesTradeLog;
import com.coindcx.springclient.model.FuturesTradeLogWithDetailsDTO;
import com.coindcx.springclient.repository.FuturesTradeLogRepository;
import com.coindcx.springclient.repository.WebSocketFuturesOrderUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesPositionUpdateDataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FuturesTradeLogController.class)
@DisplayName("FuturesTradeLogController Tests")
class FuturesTradeLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FuturesTradeLogRepository repository;

    @MockBean
    private WebSocketFuturesOrderUpdateDataRepository orderRepo;

    @MockBean
    private WebSocketFuturesPositionUpdateDataRepository positionRepo;

    // ── GET /recent ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/trade-log/recent - 200 with default limit")
    void getRecent_defaultLimit() throws Exception {
        when(repository.findRecentLogs(50)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/recent"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/futures/trade-log/recent - 200 with explicit limit capped at 500")
    void getRecent_limitCapped() throws Exception {
        when(repository.findRecentLogs(500)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/recent").param("limit", "9999"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/futures/trade-log/recent - 200 with limit=10")
    void getRecent_explicitLimit() throws Exception {
        when(repository.findRecentLogs(10)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/recent").param("limit", "10"))
                .andExpect(status().isOk());
    }

    // ── GET /all ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/trade-log/all - 200 returns all entries")
    void getAll_success() throws Exception {
        when(repository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/all"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /position/{positionId} ───────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/trade-log/position/{positionId} - 200 returns list")
    void getByPositionId_success() throws Exception {
        when(repository.findByPositionIdOrderByRecordTimestampDesc(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/position/pos-abc-123"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /order/{orderId} ─────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/trade-log/order/{orderId} - 200 returns list")
    void getByOrderId_success() throws Exception {
        when(repository.findByOrderIdOrderByRecordTimestampDesc(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/order/order-xyz-456"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /pair/{pair} ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/trade-log/pair/{pair} - 200 returns list")
    void getByPair_success() throws Exception {
        when(repository.findByPairOrderByRecordTimestampDesc(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/pair/BTCUSDT"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /event-type/{eventType} ──────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/trade-log/event-type/{eventType} - 200 returns list")
    void getByEventType_success() throws Exception {
        when(repository.findByEventTypeOrderByRecordTimestampDesc(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/event-type/ORDER_SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /{id} ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/trade-log/{id} - 200 when found")
    void getById_found() throws Exception {
        FuturesTradeLog log = new FuturesTradeLog();
        when(repository.findById(1L)).thenReturn(Optional.of(log));
        when(orderRepo.findFirstByOrderIdOrderByUpdatedAtDesc(any())).thenReturn(Optional.empty());
        when(positionRepo.findFirstByPositionIdOrderByUpdateTimestampDesc(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/futures/trade-log/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/futures/trade-log/{id} - 404 when not found")
    void getById_notFound() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/futures/trade-log/999"))
                .andExpect(status().isNotFound());
    }

    // ── GET /stats ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/futures/trade-log/stats - 200 returns stats map")
    void getStats_success() throws Exception {
        when(repository.count()).thenReturn(0L);
        when(repository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/futures/trade-log/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(0));
    }

    @Test
    @DisplayName("GET /api/futures/trade-log/stats - 200 with data")
    void getStats_withData() throws Exception {
        FuturesTradeLog log = new FuturesTradeLog();
        log.setSource("COINDCX");
        log.setEventType("ORDER_SUBMITTED");
        log.setPair("BTCUSDT");

        when(repository.count()).thenReturn(1L);
        when(repository.findAll()).thenReturn(java.util.List.of(log));

        mockMvc.perform(get("/api/futures/trade-log/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntries").value(1))
                .andExpect(jsonPath("$.uniquePairs").value(1));
    }
}
