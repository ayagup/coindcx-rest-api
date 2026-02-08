package com.coindcx.springclient.controller;

import com.coindcx.springclient.service.CoinDCXWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for managing WebSocket channel subscriptions
 */
@RestController
@RequestMapping("/api/websocket/subscriptions")
public class WebSocketSubscriptionController {
    
    private final CoinDCXWebSocketService webSocketService;
    
    @Autowired
    public WebSocketSubscriptionController(CoinDCXWebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
    
    /**
     * Subscribe to a public channel
     * POST /api/websocket/subscriptions/public
     * 
     * Request Body Example:
     * {
     *   "channelName": "B-BTC_USDT@candles_1h@rt",
     *   "eventName": "candlestick"
     * }
     */
    @PostMapping("/public")
    public ResponseEntity<Map<String, Object>> subscribeToPublicChannel(
            @RequestBody SubscriptionRequest request) {
        
        try {
            webSocketService.subscribeToPublicChannel(
                request.getChannelName(),
                request.getEventName(),
                true  // persist to database
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully subscribed to channel");
            response.put("channelName", request.getChannelName());
            response.put("eventName", request.getEventName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to subscribe: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Subscribe to a private channel
     * POST /api/websocket/subscriptions/private
     * 
     * Request Body Example:
     * {
     *   "channelName": "coindcx",
     *   "eventName": "balance-update"
     * }
     */
    @PostMapping("/private")
    public ResponseEntity<Map<String, Object>> subscribeToPrivateChannel(
            @RequestBody SubscriptionRequest request) {
        
        try {
            webSocketService.subscribeToPrivateChannel(
                request.getChannelName(),
                request.getEventName(),
                true  // persist to database
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully subscribed to private channel");
            response.put("channelName", request.getChannelName());
            response.put("eventName", request.getEventName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to subscribe: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Unsubscribe from a channel
     * DELETE /api/websocket/subscriptions
     * 
     * Request Body Example:
     * {
     *   "channelName": "B-BTC_USDT@candles_1h@rt"
     * }
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> unsubscribe(
            @RequestBody SubscriptionRequest request) {
        
        try {
            webSocketService.unsubscribeFromChannel(request.getChannelName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully unsubscribed from channel");
            response.put("channelName", request.getChannelName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to unsubscribe: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get list of currently subscribed channels
     * GET /api/websocket/subscriptions
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSubscriptions() {
        Map<String, Object> response = new HashMap<>();
        response.put("subscribedChannels", webSocketService.getSubscribedChannels());
        response.put("connected", webSocketService.isConnected());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Quick subscribe endpoints for common channels
     */
    
    /**
     * Subscribe to BTC/USDT spot candlestick (1h)
     */
    @PostMapping("/quick/spot-btc-candlestick")
    public ResponseEntity<Map<String, Object>> subscribeSpotBtcCandlestick() {
        try {
            webSocketService.subscribeToPublicChannel("B-BTC_USDT@candles_1h@rt", "candlestick", true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Subscribed to BTC/USDT spot candlestick"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Subscribe to BTC/USDT futures candlestick (1h)
     */
    @PostMapping("/quick/futures-btc-candlestick")
    public ResponseEntity<Map<String, Object>> subscribeFuturesBtcCandlestick() {
        try {
            webSocketService.subscribeToPublicChannel("B-BTC_USDT@candles_1h@rt", "futures-candlestick", true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Subscribed to BTC/USDT futures candlestick"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Subscribe to BTC/USDT spot orderbook
     */
    @PostMapping("/quick/spot-btc-orderbook")
    public ResponseEntity<Map<String, Object>> subscribeSpotBtcOrderbook() {
        try {
            webSocketService.subscribeToPublicChannel("B-BTC_USDT@depth@rt", "depth-update", true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Subscribed to BTC/USDT spot orderbook"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Subscribe to BTC/USDT futures orderbook
     */
    @PostMapping("/quick/futures-btc-orderbook")
    public ResponseEntity<Map<String, Object>> subscribeFuturesBtcOrderbook() {
        try {
            webSocketService.subscribeToPublicChannel("B-BTC_USDT@depth-futures@rt", "futures-depth-update", true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Subscribed to BTC/USDT futures orderbook"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Subscribe to spot current prices
     */
    @PostMapping("/quick/spot-current-prices")
    public ResponseEntity<Map<String, Object>> subscribeSpotCurrentPrices() {
        try {
            webSocketService.subscribeToPublicChannel("currentPrices@rt", "currentPrices#update", true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Subscribed to spot current prices"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Subscribe to futures current prices
     */
    @PostMapping("/quick/futures-current-prices")
    public ResponseEntity<Map<String, Object>> subscribeFuturesCurrentPrices() {
        try {
            webSocketService.subscribeToPublicChannel("currentPrices@futures@rt", "currentPrices@futures#update", true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Subscribed to futures current prices"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Subscribe to BTC/USDT spot trades
     */
    @PostMapping("/quick/spot-btc-trades")
    public ResponseEntity<Map<String, Object>> subscribeSpotBtcTrades() {
        try {
            webSocketService.subscribeToPublicChannel("B-BTC_USDT", "new-trade", true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Subscribed to BTC/USDT spot trades"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Subscribe to BTC/USDT futures trades
     */
    @PostMapping("/quick/futures-btc-trades")
    public ResponseEntity<Map<String, Object>> subscribeFuturesBtcTrades() {
        try {
            webSocketService.subscribeToPublicChannel("B-BTC_USDT@trades-futures", "new-trade", true);
            return ResponseEntity.ok(Map.of("success", true, "message", "Subscribed to BTC/USDT futures trades"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * Request/Response DTOs
     */
    public static class SubscriptionRequest {
        private String channelName;
        private String eventName;
        
        public String getChannelName() {
            return channelName;
        }
        
        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }
        
        public String getEventName() {
            return eventName;
        }
        
        public void setEventName(String eventName) {
            this.eventName = eventName;
        }
    }
}
