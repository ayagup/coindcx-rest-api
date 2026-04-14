package com.coindcx.springclient.controller;

import com.coindcx.springclient.dto.ApiCallStatistics;
import com.coindcx.springclient.entity.ApiCallLog;
import com.coindcx.springclient.service.ApiCallLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiCallLogController.class)
@DisplayName("ApiCallLogController Tests")
class ApiCallLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiCallLogService apiCallLogService;

    // ── GET / (paginated) ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs - 200 returns paginated logs (defaults)")
    void getAllLogs_defaults() throws Exception {
        Page<ApiCallLog> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0);
        when(apiCallLogService.getAllLogs(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/logs - 200 with explicit pagination params")
    void getAllLogs_explicitParams() throws Exception {
        Page<ApiCallLog> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 10), 0);
        when(apiCallLogService.getAllLogs(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/logs")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "timestamp", "asc"))
                .andExpect(status().isOk());
    }

    // ── GET /service/{serviceName} ───────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs/service/{serviceName} - 200 returns list")
    void getLogsByService_success() throws Exception {
        when(apiCallLogService.getLogsByService(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/logs/service/OrderService"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /service/{serviceName}/method/{methodName} ───────────────────────

    @Test
    @DisplayName("GET /api/logs/service/{s}/method/{m} - 200 returns list")
    void getLogsByServiceAndMethod_success() throws Exception {
        when(apiCallLogService.getLogsByServiceAndMethod(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/logs/service/OrderService/method/createOrder"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /range ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs/range - 200 with valid date range")
    void getLogsByTimeRange_success() throws Exception {
        when(apiCallLogService.getLogsByTimeRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/logs/range")
                        .param("start", "2024-01-01T00:00:00")
                        .param("end", "2024-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /failed ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs/failed - 200 returns list")
    void getFailedCalls_success() throws Exception {
        when(apiCallLogService.getFailedCalls()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/logs/failed"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /slow ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs/slow - 200 with threshold")
    void getSlowCalls_success() throws Exception {
        when(apiCallLogService.getSlowCalls(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/logs/slow").param("threshold", "5000"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── GET /statistics ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs/statistics - 200 returns statistics list")
    void getStatistics_success() throws Exception {
        when(apiCallLogService.getStatistics()).thenReturn(List.of(new ApiCallStatistics()));

        mockMvc.perform(get("/api/logs/statistics"))
                .andExpect(status().isOk());
    }

    // ── GET /statistics/{serviceName} ────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs/statistics/{service} - 200 returns service stats")
    void getStatisticsForService_success() throws Exception {
        when(apiCallLogService.getStatisticsForService(anyString())).thenReturn(new ApiCallStatistics());

        mockMvc.perform(get("/api/logs/statistics/OrderService"))
                .andExpect(status().isOk());
    }

    // ── GET /recent ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs/recent - 200 with default limit")
    void getRecentLogs_defaultLimit() throws Exception {
        when(apiCallLogService.getRecentLogs(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/logs/recent"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/logs/recent - 200 with explicit limit")
    void getRecentLogs_explicitLimit() throws Exception {
        when(apiCallLogService.getRecentLogs(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/logs/recent").param("limit", "100"))
                .andExpect(status().isOk());
    }

    // ── GET /count/status ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/logs/count/status - 200 returns status counts")
    void getCountByStatus_success() throws Exception {
        when(apiCallLogService.getCountByStatus())
                .thenReturn(Map.of("SUCCESS", 42L, "FAILED", 5L));

        mockMvc.perform(get("/api/logs/count/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.SUCCESS").value(42));
    }
}
