package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.service.MarginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarginController.class)
@DisplayName("MarginController Tests")
class MarginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MarginService marginService;

    // ── POST /add-margin ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/add-margin - 200 on success")
    void addMargin_success() throws Exception {
        when(marginService.addMargin(any())).thenReturn(new ExchangeV1MarginAddMarginPost200Response());

        mockMvc.perform(post("/api/margin/add-margin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginAddMarginPostRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/margin/add-margin - ApiException propagated")
    void addMargin_apiException() throws Exception {
        when(marginService.addMargin(any())).thenThrow(new ApiException(400, "Bad request"));

        mockMvc.perform(post("/api/margin/add-margin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginAddMarginPostRequest())))
                .andExpect(status().isBadRequest());
    }

    // ── POST /cancel ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/cancel - 200 on success")
    void cancelOrder_success() throws Exception {
        doNothing().when(marginService).cancelOrder(any());

        mockMvc.perform(post("/api/margin/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginCancelPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Margin order cancelled successfully"));
    }

    @Test
    @DisplayName("POST /api/margin/cancel - ApiException propagated")
    void cancelOrder_apiException() throws Exception {
        doThrow(new ApiException(404, "Not found")).when(marginService).cancelOrder(any());

        mockMvc.perform(post("/api/margin/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginCancelPostRequest())))
                .andExpect(status().isNotFound());
    }

    // ── POST /create ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/create - 200 returns order list")
    void createOrders_success() throws Exception {
        when(marginService.createOrders(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/margin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginCreatePostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── POST /edit-target-price ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/edit-target-price - 200 on success")
    void editPriceOfTargetOrder_success() throws Exception {
        doNothing().when(marginService).editPriceOfTargetOrder(any());

        mockMvc.perform(post("/api/margin/edit-target-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginEditPriceOfTargetOrderPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Target price updated successfully"));
    }

    // ── POST /edit-sl ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/edit-sl - 200 on success")
    void editStopLoss_success() throws Exception {
        doNothing().when(marginService).editStopLoss(any());

        mockMvc.perform(post("/api/margin/edit-sl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginEditSlPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Stop loss updated successfully"));
    }

    // ── POST /edit-target ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/edit-target - 200 on success")
    void editTarget_success() throws Exception {
        doNothing().when(marginService).editTarget(any());

        mockMvc.perform(post("/api/margin/edit-target")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginEditTargetPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Target updated successfully"));
    }

    // ── POST /edit-trailing-sl ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/edit-trailing-sl - 200 on success")
    void editTrailingStopLoss_success() throws Exception {
        doNothing().when(marginService).editTrailingStopLoss(any());

        mockMvc.perform(post("/api/margin/edit-trailing-sl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginEditTrailingSlPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Trailing stop loss updated successfully"));
    }

    // ── POST /exit ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/exit - 200 on success")
    void exitPosition_success() throws Exception {
        doNothing().when(marginService).exitPosition(any());

        mockMvc.perform(post("/api/margin/exit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginExitPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Position exited successfully"));
    }

    // ── POST /orders ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/orders - 200 returns order list")
    void fetchOrders_success() throws Exception {
        when(marginService.fetchOrders(any())).thenReturn(List.of());

        mockMvc.perform(post("/api/margin/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginFetchOrdersPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── POST /order-status ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/order-status - 200 on success")
    void getOrderStatus_success() throws Exception {
        when(marginService.getOrderStatus(any())).thenReturn(new ExchangeV1MarginFetchOrdersPost200ResponseInner());

        mockMvc.perform(post("/api/margin/order-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginOrderPostRequest())))
                .andExpect(status().isOk());
    }

    // ── POST /remove-margin ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/margin/remove-margin - 200 on success")
    void removeMargin_success() throws Exception {
        when(marginService.removeMargin(any())).thenReturn(new ExchangeV1MarginRemoveMarginPost200Response());

        mockMvc.perform(post("/api/margin/remove-margin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginRemoveMarginPostRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/margin/remove-margin - ApiException propagated")
    void removeMargin_apiException() throws Exception {
        when(marginService.removeMargin(any())).thenThrow(new ApiException(503, "Service unavailable"));

        mockMvc.perform(post("/api/margin/remove-margin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1MarginRemoveMarginPostRequest())))
                .andExpect(status().isServiceUnavailable());
    }
}
