package com.coindcx.springclient.controller;

import com.coindcx.springclient.service.CoinDCXWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for WebSocket operations
 * Note: Subscribe/Unsubscribe functionality has been removed.
 * Use direct service calls or programmatic subscription for WebSocket channels.
 */
@RestController
@RequestMapping("/api/websocket")
public class WebSocketController {
    
    private final CoinDCXWebSocketService webSocketService;
    
    @Autowired
    public WebSocketController(CoinDCXWebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
    
    /**
     * Get WebSocket connection status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connected", webSocketService.isConnected());
        status.put("status", webSocketService.getConnectionStatus());
        status.put("subscribedChannels", webSocketService.getSubscribedChannels());
        status.put("storageStats", webSocketService.getStorageStats());
        return ResponseEntity.ok(status);
    }
    
    /**
     * Get received messages for a specific event
     * Example: /api/websocket/messages?event=candlestick
     */
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(@RequestParam String event) {
        
        List<Object> messages = webSocketService.getMessages(event);
        
        Map<String, Object> response = new HashMap<>();
        response.put("event", event);
        response.put("count", messages.size());
        response.put("messages", messages);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Clear stored messages for a specific event
     * Example: /api/websocket/messages/clear?event=candlestick
     */
    @DeleteMapping("/messages/clear")
    public ResponseEntity<Map<String, String>> clearMessages(@RequestParam String event) {
        
        webSocketService.clearMessages(event);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "cleared");
        response.put("event", event);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Clear all stored messages
     */
    @DeleteMapping("/messages/clear-all")
    public ResponseEntity<Map<String, String>> clearAllMessages() {
        
        webSocketService.clearAllMessages();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "all messages cleared");
        
        return ResponseEntity.ok(response);
    }
}
