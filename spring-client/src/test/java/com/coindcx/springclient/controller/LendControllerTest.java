package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1FundingFetchOrdersPost200ResponseInner;
import com.coindcx.springclient.model.ExchangeV1FundingFetchOrdersPostRequest;
import com.coindcx.springclient.model.ExchangeV1FundingLendPostRequest;
import com.coindcx.springclient.service.LendService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LendController.class)
@DisplayName("LendController Tests")
class LendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LendService lendService;

    // ── POST /orders/fetch ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/lending/orders/fetch - 200 returns list")
    void fetchOrders_success() throws Exception {
        when(lendService.fetchOrders(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/lending/orders/fetch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1FundingFetchOrdersPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("POST /api/lending/orders/fetch - 200 returns populated list")
    void fetchOrders_populated() throws Exception {
        when(lendService.fetchOrders(any())).thenReturn(
                java.util.List.of(new ExchangeV1FundingFetchOrdersPost200ResponseInner()));

        mockMvc.perform(post("/api/lending/orders/fetch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1FundingFetchOrdersPostRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/lending/orders/fetch - ApiException propagated")
    void fetchOrders_apiException() throws Exception {
        when(lendService.fetchOrders(any())).thenThrow(new ApiException(401, "Unauthorized"));

        mockMvc.perform(post("/api/lending/orders/fetch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1FundingFetchOrdersPostRequest())))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /orders/create ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/lending/orders/create - 200 on success")
    void createLendOrder_success() throws Exception {
        doNothing().when(lendService).createLendOrder(any());

        mockMvc.perform(post("/api/lending/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1FundingLendPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Lending order created successfully"));
    }

    @Test
    @DisplayName("POST /api/lending/orders/create - ApiException propagated")
    void createLendOrder_apiException() throws Exception {
        doThrow(new ApiException(400, "Bad request")).when(lendService).createLendOrder(any());

        mockMvc.perform(post("/api/lending/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1FundingLendPostRequest())))
                .andExpect(status().isBadRequest());
    }
}
