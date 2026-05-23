package com.coindcx.springclient.controller;

import com.coindcx.springclient.service.CoinDCXWebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebSocketSubscriptionController.class)
@DisplayName("WebSocketSubscriptionController Tests")
class WebSocketSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoinDCXWebSocketService webSocketService;

    private Map<String, String> subscriptionBody(String channelName, String eventName) {
        return Map.of("channelName", channelName, "eventName", eventName);
    }

    // ── POST /public ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/websocket/subscriptions/public - 200 on success")
    void subscribePublic_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                subscriptionBody("B-BTC_USDT@candles_1h@rt", "candlestick"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/public - 400 on exception")
    void subscribePublic_failure() throws Exception {
        doThrow(new RuntimeException("Connection refused"))
                .when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                subscriptionBody("BAD_CHANNEL", "event"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ── POST /private ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/websocket/subscriptions/private - 200 on success")
    void subscribePrivate_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPrivateChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                subscriptionBody("coindcx", "balance-update"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/private - 400 on exception")
    void subscribePrivate_failure() throws Exception {
        doThrow(new RuntimeException("Auth failed"))
                .when(webSocketService).subscribeToPrivateChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                subscriptionBody("coindcx", "balance-update"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ── DELETE / (unsubscribe) ───────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/websocket/subscriptions - 200 on success")
    void unsubscribe_success() throws Exception {
        doNothing().when(webSocketService).unsubscribeFromChannel(anyString());

        mockMvc.perform(delete("/api/websocket/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("channelName", "B-BTC_USDT@candles_1h@rt"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/websocket/subscriptions - 400 on exception")
    void unsubscribe_failure() throws Exception {
        doThrow(new RuntimeException("Channel not found"))
                .when(webSocketService).unsubscribeFromChannel(anyString());

        mockMvc.perform(delete("/api/websocket/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("channelName", "NONEXISTENT"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ── GET / (list subscriptions) ───────────────────────────────────────────

    @Test
    @DisplayName("GET /api/websocket/subscriptions - 200 returns subscription list")
    void getSubscriptions_connected() throws Exception {
        when(webSocketService.getSubscribedChannels()).thenReturn(Map.of("channel1", "event1", "channel2", "event2"));
        when(webSocketService.isConnected()).thenReturn(true);

        mockMvc.perform(get("/api/websocket/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.connected").value(true));
    }

    @Test
    @DisplayName("GET /api/websocket/subscriptions - 200 when disconnected")
    void getSubscriptions_disconnected() throws Exception {
        when(webSocketService.getSubscribedChannels()).thenReturn(java.util.Collections.emptyMap());
        when(webSocketService.isConnected()).thenReturn(false);

        mockMvc.perform(get("/api/websocket/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.connected").value(false));
    }

    // ── Quick subscribe endpoints ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/spot-btc-candlestick - 200 on success")
    void quickSpotBtcCandlestick_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/spot-btc-candlestick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/futures-btc-candlestick - 200 on success")
    void quickFuturesBtcCandlestick_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/futures-btc-candlestick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/spot-btc-orderbook - 200 on success")
    void quickSpotBtcOrderbook_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/spot-btc-orderbook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/futures-btc-orderbook - 200 on success")
    void quickFuturesBtcOrderbook_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/futures-btc-orderbook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/spot-current-prices - 200 on success")
    void quickSpotCurrentPrices_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/spot-current-prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/futures-current-prices - 200 on success")
    void quickFuturesCurrentPrices_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/futures-current-prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/spot-btc-trades - 200 on success")
    void quickSpotBtcTrades_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/spot-btc-trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/futures-btc-trades - 200 on success")
    void quickFuturesBtcTrades_success() throws Exception {
        doNothing().when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/futures-btc-trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ── Error path for quick endpoints ───────────────────────────────────────

    @Test
    @DisplayName("POST /api/websocket/subscriptions/quick/spot-btc-candlestick - 400 on exception")
    void quickSpotBtcCandlestick_failure() throws Exception {
        doThrow(new RuntimeException("WS disconnected"))
                .when(webSocketService).subscribeToPublicChannel(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/websocket/subscriptions/quick/spot-btc-candlestick"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
