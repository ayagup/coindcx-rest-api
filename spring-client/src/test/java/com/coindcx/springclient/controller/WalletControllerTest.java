package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1WalletsSubAccountTransferPost200Response;
import com.coindcx.springclient.model.ExchangeV1WalletsSubAccountTransferPostRequest;
import com.coindcx.springclient.model.ExchangeV1WalletsTransferPostRequest;
import com.coindcx.springclient.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@DisplayName("WalletController Tests")
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    // ── POST /sub-account/transfer ───────────────────────────────────────────

    @Test
    @DisplayName("POST /api/wallet/sub-account/transfer - 200 on success")
    void subAccountTransfer_success() throws Exception {
        when(walletService.subAccountTransfer(any()))
                .thenReturn(new ExchangeV1WalletsSubAccountTransferPost200Response());

        mockMvc.perform(post("/api/wallet/sub-account/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1WalletsSubAccountTransferPostRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/wallet/sub-account/transfer - ApiException propagated")
    void subAccountTransfer_apiException() throws Exception {
        when(walletService.subAccountTransfer(any())).thenThrow(new ApiException(422, "Unprocessable"));

        mockMvc.perform(post("/api/wallet/sub-account/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1WalletsSubAccountTransferPostRequest())))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── POST /transfer ───────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/wallet/transfer - 200 on success")
    void walletTransfer_success() throws Exception {
        when(walletService.walletTransfer(any()))
                .thenReturn(new ExchangeV1WalletsSubAccountTransferPost200Response());

        mockMvc.perform(post("/api/wallet/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1WalletsTransferPostRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/wallet/transfer - ApiException propagated")
    void walletTransfer_apiException() throws Exception {
        when(walletService.walletTransfer(any())).thenThrow(new ApiException(400, "Bad request"));

        mockMvc.perform(post("/api/wallet/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1WalletsTransferPostRequest())))
                .andExpect(status().isBadRequest());
    }
}
