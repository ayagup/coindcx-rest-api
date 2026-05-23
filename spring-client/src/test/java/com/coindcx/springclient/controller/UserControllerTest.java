package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1UsersBalancesPost200ResponseInner;
import com.coindcx.springclient.model.ExchangeV1UsersBalancesPostRequest;
import com.coindcx.springclient.model.ExchangeV1UsersInfoPost200Response;
import com.coindcx.springclient.service.MetaTraderService;
import com.coindcx.springclient.service.UserService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private MetaTraderService metaTraderService;

    // ── POST /balances ───────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/user/balances - 200 returns balance list")
    void getBalances_success() throws Exception {
        when(userService.getBalances(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/user/balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1UsersBalancesPostRequest())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("POST /api/user/balances - 200 returns populated list")
    void getBalances_populated() throws Exception {
        ExchangeV1UsersBalancesPost200ResponseInner item = new ExchangeV1UsersBalancesPost200ResponseInner();
        when(userService.getBalances(any())).thenReturn(java.util.List.of(item));

        mockMvc.perform(post("/api/user/balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1UsersBalancesPostRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/user/balances - ApiException propagated")
    void getBalances_apiException() throws Exception {
        when(userService.getBalances(any())).thenThrow(new ApiException(401, "Unauthorized"));

        mockMvc.perform(post("/api/user/balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1UsersBalancesPostRequest())))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /info ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/user/info - success merges CoinDCX and MT5")
    void getUserInfo_success() throws Exception {
        when(userService.getUserInfo(any())).thenReturn(new ExchangeV1UsersInfoPost200Response());
        when(metaTraderService.getAccount()).thenReturn(java.util.Map.of("balance", 1000.0));

        mockMvc.perform(post("/api/user/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1UsersBalancesPostRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coindcx").exists())
                .andExpect(jsonPath("$.mt5").exists());
    }

    @Test
    @DisplayName("POST /api/user/info - CoinDCX error included, MT5 still returned")
    void getUserInfo_coindcxError() throws Exception {
        when(userService.getUserInfo(any())).thenThrow(new ApiException(403, "Forbidden"));
        when(metaTraderService.getAccount()).thenReturn(java.util.Map.of("balance", 1000.0));

        mockMvc.perform(post("/api/user/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1UsersBalancesPostRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coindcx").exists())
                .andExpect(jsonPath("$.mt5").exists())
                .andExpect(jsonPath("$.coindcx_error").exists());
    }

    @Test
    @DisplayName("POST /api/user/info - MT5 error included, CoinDCX still returned")
    void getUserInfo_mt5Error() throws Exception {
        when(userService.getUserInfo(any())).thenReturn(new ExchangeV1UsersInfoPost200Response());
        when(metaTraderService.getAccount()).thenThrow(new RuntimeException("MT5 unavailable"));

        mockMvc.perform(post("/api/user/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExchangeV1UsersBalancesPostRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coindcx").exists())
                .andExpect(jsonPath("$.mt5").exists())
                .andExpect(jsonPath("$.mt5_error").value("MT5 unavailable"));
    }
}
