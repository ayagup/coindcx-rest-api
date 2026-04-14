package com.coindcx.springclient.controller;

import com.coindcx.springclient.service.CoinDCXWebSocketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebSocketController.class)
@DisplayName("WebSocketController Tests")
class WebSocketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoinDCXWebSocketService webSocketService;

    // ── GET /status ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/websocket/status - 200 returns connection status")
    void getStatus_connected() throws Exception {
        when(webSocketService.isConnected()).thenReturn(true);
        when(webSocketService.getConnectionStatus()).thenReturn("CONNECTED");
        when(webSocketService.getSubscribedChannels()).thenReturn(Map.of("B-BTC_USDT@candles_1h@rt", "candlestick"));
        when(webSocketService.getStorageStats()).thenReturn("Stats: 1 spot, 0 futures records");

        mockMvc.perform(get("/api/websocket/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.connected").value(true))
                .andExpect(jsonPath("$.status").value("CONNECTED"));
    }

    @Test
    @DisplayName("GET /api/websocket/status - 200 returns disconnected status")
    void getStatus_disconnected() throws Exception {
        when(webSocketService.isConnected()).thenReturn(false);
        when(webSocketService.getConnectionStatus()).thenReturn("DISCONNECTED");
        when(webSocketService.getSubscribedChannels()).thenReturn(Collections.emptyMap());
        when(webSocketService.getStorageStats()).thenReturn("");

        mockMvc.perform(get("/api/websocket/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.connected").value(false));
    }

    // ── GET /messages ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/websocket/messages?event=candlestick - 200 returns messages")
    void getMessages_success() throws Exception {
        when(webSocketService.getMessages("candlestick")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/websocket/messages").param("event", "candlestick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event").value("candlestick"))
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @DisplayName("GET /api/websocket/messages - 200 with non-empty messages")
    void getMessages_nonEmpty() throws Exception {
        when(webSocketService.getMessages("ticker")).thenReturn(List.of("msg1", "msg2"));

        mockMvc.perform(get("/api/websocket/messages").param("event", "ticker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2));
    }

    // ── DELETE /messages/clear ─────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/websocket/messages/clear?event=candlestick - 200 clears messages")
    void clearMessages_success() throws Exception {
        doNothing().when(webSocketService).clearMessages(anyString());

        mockMvc.perform(delete("/api/websocket/messages/clear").param("event", "candlestick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cleared"))
                .andExpect(jsonPath("$.event").value("candlestick"));

        verify(webSocketService).clearMessages("candlestick");
    }

    // ── DELETE /messages/clear-all ────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/websocket/messages/clear-all - 200 clears all messages")
    void clearAllMessages_success() throws Exception {
        doNothing().when(webSocketService).clearAllMessages();

        mockMvc.perform(delete("/api/websocket/messages/clear-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("all messages cleared"));

        verify(webSocketService).clearAllMessages();
    }
}
