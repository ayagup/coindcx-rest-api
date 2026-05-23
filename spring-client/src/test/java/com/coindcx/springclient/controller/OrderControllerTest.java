package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.service.MetaTraderService;
import com.coindcx.springclient.service.MetaTraderSymbolService;
import com.coindcx.springclient.service.OrderService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private MetaTraderService metaTraderService;

    @MockBean
    private MetaTraderSymbolService metaTraderSymbolService;

    // ── GET active orders count ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/active/count - returns count on success")
    void getActiveOrdersCount_success() throws Exception {
        ExchangeV1OrdersActiveOrdersCountPost200Response resp =
                new ExchangeV1OrdersActiveOrdersCountPost200Response();
        when(orderService.getActiveOrdersCount(any())).thenReturn(resp);

        mockMvc.perform(post("/api/orders/active/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersActiveOrdersPostRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/orders/active/count - propagates ApiException status")
    void getActiveOrdersCount_apiException() throws Exception {
        when(orderService.getActiveOrdersCount(any())).thenThrow(new ApiException(503, "Service unavailable"));

        mockMvc.perform(post("/api/orders/active/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersActiveOrdersPostRequest())))
                .andExpect(status().isServiceUnavailable());
    }

    // ── GET active orders ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/active - returns list on success")
    void getActiveOrders_success() throws Exception {
        when(orderService.getActiveOrders(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/orders/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersActiveOrdersPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("POST /api/orders/active - propagates ApiException status")
    void getActiveOrders_apiException() throws Exception {
        when(orderService.getActiveOrders(any())).thenThrow(new ApiException(401, "Unauthorized"));

        mockMvc.perform(post("/api/orders/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersActiveOrdersPostRequest())))
                .andExpect(status().isUnauthorized());
    }

    // ── Cancel all orders ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/cancel-all - 200 on success")
    void cancelAllOrders_success() throws Exception {
        doNothing().when(orderService).cancelAllOrders(any());

        mockMvc.perform(post("/api/orders/cancel-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersCancelAllPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("All orders cancelled successfully"));
    }

    @Test
    @DisplayName("POST /api/orders/cancel-all - error message on ApiException")
    void cancelAllOrders_apiException() throws Exception {
        doThrow(new ApiException(400, "Bad request")).when(orderService).cancelAllOrders(any());

        mockMvc.perform(post("/api/orders/cancel-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersCancelAllPostRequest())))
                .andExpect(status().isBadRequest());
    }

    // ── Cancel orders by IDs ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/cancel-by-ids - 200 on success")
    void cancelOrdersByIds_success() throws Exception {
        doNothing().when(orderService).cancelOrdersByIds(any());

        mockMvc.perform(post("/api/orders/cancel-by-ids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersCancelByIdsPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Orders cancelled by IDs successfully"));
    }

    // ── Cancel single order ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/cancel - 200 on success")
    void cancelOrder_success() throws Exception {
        doNothing().when(orderService).cancelOrder(any());

        mockMvc.perform(post("/api/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersCancelPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Order cancelled successfully"));
    }

    // ── Create multiple orders ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/create-multiple - 200 on success")
    void createMultipleOrders_success() throws Exception {
        ExchangeV1OrdersCreateMultiplePost200Response resp = new ExchangeV1OrdersCreateMultiplePost200Response();
        when(orderService.createMultipleOrders(any())).thenReturn(resp);

        mockMvc.perform(post("/api/orders/create-multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersCreateMultiplePostRequest())))
                .andExpect(status().isOk());
    }

    // ── Create single order ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/create - 200 on success (CoinDCX path)")
    void createOrder_success_coindcx() throws Exception {
        when(metaTraderSymbolService.isSymbolEnabled(any())).thenReturn(false);
        when(orderService.createOrder(any())).thenReturn(new ExchangeV1OrdersCreatePost200Response());

        ExchangeV1OrdersCreatePostRequest req = new ExchangeV1OrdersCreatePostRequest();
        req.setMarket("BTCINR");

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/orders/create - ApiException propagated")
    void createOrder_apiException() throws Exception {
        when(metaTraderSymbolService.isSymbolEnabled(any())).thenReturn(false);
        when(orderService.createOrder(any())).thenThrow(new ApiException(503, "Gateway timeout"));

        ExchangeV1OrdersCreatePostRequest req = new ExchangeV1OrdersCreatePostRequest();
        req.setMarket("BTCINR");

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isServiceUnavailable());
    }

    // ── Edit order ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/edit - 200 on success")
    void editOrder_success() throws Exception {
        when(orderService.editOrder(any())).thenReturn(Map.of());

        mockMvc.perform(post("/api/orders/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersEditPostRequest())))
                .andExpect(status().isOk());
    }

    // ── Order status multiple ────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/status/multiple - 200 on success")
    void getMultipleOrdersStatus_success() throws Exception {
        when(orderService.getMultipleOrdersStatus(any())).thenReturn(List.of());

        mockMvc.perform(post("/api/orders/status/multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersStatusMultiplePostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ── Order status single ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/status - 200 on success")
    void getOrderStatus_success() throws Exception {
        when(orderService.getOrderStatus(any())).thenReturn(new ExchangeV1OrdersCreatePost200ResponseOrdersInner());

        mockMvc.perform(post("/api/orders/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersStatusPostRequest())))
                .andExpect(status().isOk());
    }

    // ── Trade history ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders/trade-history - 200 returns list")
    void getTradeHistory_success() throws Exception {
        when(orderService.getTradeHistory(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/orders/trade-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersTradeHistoryPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("POST /api/orders/trade-history - ApiException propagated")
    void getTradeHistory_apiException() throws Exception {
        when(orderService.getTradeHistory(any())).thenThrow(new ApiException(422, "Unprocessable entity"));

        mockMvc.perform(post("/api/orders/trade-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1OrdersTradeHistoryPostRequest())))
                .andExpect(status().isUnprocessableEntity());
    }
}
