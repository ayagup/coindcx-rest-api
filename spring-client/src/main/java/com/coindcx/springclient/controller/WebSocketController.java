package com.coindcx.springclient.controller;

import com.coindcx.springclient.constants.WebSocketChannels;
import com.coindcx.springclient.service.CoinDCXWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for WebSocket operations
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
        return ResponseEntity.ok(status);
    }
    
    /**
     * Subscribe to candlestick data
     * Example: /api/websocket/candlestick/subscribe?pair=B-BTC_USDT&interval=1m
     */
    @PostMapping("/candlestick/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToCandlestick(
            @RequestParam String pair,
            @RequestParam(defaultValue = "1m") String interval) {
        
        String channelName = WebSocketChannels.buildCandlestickChannel(pair, interval);
        webSocketService.subscribeToPublicChannel(channelName, WebSocketChannels.EVENT_CANDLESTICK);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", channelName);
        response.put("event", WebSocketChannels.EVENT_CANDLESTICK);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Subscribe to orderbook data
     * Example: /api/websocket/orderbook/subscribe?pair=B-BTC_USDT&depth=20
     */
    @PostMapping("/orderbook/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToOrderbook(
            @RequestParam String pair,
            @RequestParam(defaultValue = "20") int depth) {
        
        String channelName = WebSocketChannels.buildOrderbookChannel(pair, depth);
        
        // Subscribe to both snapshot and update events
        webSocketService.subscribeToPublicChannel(channelName, WebSocketChannels.EVENT_DEPTH_SNAPSHOT);
        webSocketService.subscribeToPublicChannel(channelName, WebSocketChannels.EVENT_DEPTH_UPDATE);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", channelName);
        response.put("events", WebSocketChannels.EVENT_DEPTH_SNAPSHOT + ", " + WebSocketChannels.EVENT_DEPTH_UPDATE);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Subscribe to new trades
     * Example: /api/websocket/trades/subscribe?pair=B-BTC_USDT
     */
    @PostMapping("/trades/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToTrades(@RequestParam String pair) {
        
        String channelName = WebSocketChannels.buildTradesChannel(pair);
        webSocketService.subscribeToPublicChannel(channelName, WebSocketChannels.EVENT_NEW_TRADE);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", channelName);
        response.put("event", WebSocketChannels.EVENT_NEW_TRADE);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Subscribe to price changes
     * Example: /api/websocket/prices/subscribe?pair=B-BTC_USDT
     */
    @PostMapping("/prices/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToPrices(@RequestParam String pair) {
        
        String channelName = WebSocketChannels.buildPricesChannel(pair);
        webSocketService.subscribeToPublicChannel(channelName, WebSocketChannels.EVENT_PRICE_CHANGE);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", channelName);
        response.put("event", WebSocketChannels.EVENT_PRICE_CHANGE);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Subscribe to current prices (all markets)
     * Example: /api/websocket/current-prices/subscribe?interval=10s
     */
    @PostMapping("/current-prices/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToCurrentPrices(
            @RequestParam(defaultValue = "10s") String interval) {
        
        String channelName = WebSocketChannels.buildCurrentPricesChannel(interval);
        webSocketService.subscribeToPublicChannel(channelName, WebSocketChannels.EVENT_CURRENT_PRICES_UPDATE);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", channelName);
        response.put("event", WebSocketChannels.EVENT_CURRENT_PRICES_UPDATE);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Subscribe to price statistics
     * Example: /api/websocket/price-stats/subscribe?interval=60s
     */
    @PostMapping("/price-stats/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToPriceStats(
            @RequestParam(defaultValue = "60s") String interval) {
        
        String channelName = WebSocketChannels.buildPriceStatsChannel(interval);
        webSocketService.subscribeToPublicChannel(channelName, WebSocketChannels.EVENT_PRICE_STATS_UPDATE);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", channelName);
        response.put("event", WebSocketChannels.EVENT_PRICE_STATS_UPDATE);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Unsubscribe from a channel
     * Example: /api/websocket/unsubscribe?channel=B-BTC_USDT_1m
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestParam String channel) {
        
        webSocketService.unsubscribeFromChannel(channel);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "unsubscribed");
        response.put("channel", channel);
        
        return ResponseEntity.ok(response);
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
    
    /**
     * Subscribe to private balance updates (requires API credentials in config)
     */
    @PostMapping("/balance/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToBalance() {
        
        webSocketService.subscribeToPrivateChannel(
            WebSocketChannels.CHANNEL_PRIVATE_COINDCX,
            WebSocketChannels.EVENT_BALANCE_UPDATE
        );
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", WebSocketChannels.CHANNEL_PRIVATE_COINDCX);
        response.put("event", WebSocketChannels.EVENT_BALANCE_UPDATE);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Subscribe to private order updates (requires API credentials in config)
     */
    @PostMapping("/orders/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToOrders() {
        
        webSocketService.subscribeToPrivateChannel(
            WebSocketChannels.CHANNEL_PRIVATE_COINDCX,
            WebSocketChannels.EVENT_ORDER_UPDATE
        );
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", WebSocketChannels.CHANNEL_PRIVATE_COINDCX);
        response.put("event", WebSocketChannels.EVENT_ORDER_UPDATE);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Subscribe to private trade updates (requires API credentials in config)
     */
    @PostMapping("/user-trades/subscribe")
    public ResponseEntity<Map<String, String>> subscribeToUserTrades() {
        
        webSocketService.subscribeToPrivateChannel(
            WebSocketChannels.CHANNEL_PRIVATE_COINDCX,
            WebSocketChannels.EVENT_TRADE_UPDATE
        );
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "subscribed");
        response.put("channel", WebSocketChannels.CHANNEL_PRIVATE_COINDCX);
        response.put("event", WebSocketChannels.EVENT_TRADE_UPDATE);
        
        return ResponseEntity.ok(response);
    }
}
