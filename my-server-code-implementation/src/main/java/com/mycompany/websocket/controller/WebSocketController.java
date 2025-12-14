package com.mycompany.websocket.controller;

import com.mycompany.websocket.model.*;
import com.mycompany.websocket.service.CoinDCXPrivateWebSocketClient;
import com.mycompany.websocket.service.CoinDCXPublicWebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for managing CoinDCX WebSocket connections
 * Provides endpoints to subscribe/unsubscribe to various WebSocket channels
 */
@RestController
@RequestMapping("/api/websocket")
public class WebSocketController {

    @Autowired
    private CoinDCXPublicWebSocketClient publicWebSocketClient;

    @Autowired
    private CoinDCXPrivateWebSocketClient privateWebSocketClient;

    // In-memory storage for demonstration purposes
    // In production, consider using a message broker or database
    private final List<Candlestick> latestCandlesticks = Collections.synchronizedList(new ArrayList<>());
    private final List<DepthSnapshot> latestDepthSnapshots = Collections.synchronizedList(new ArrayList<>());
    private final List<BalanceUpdate> latestBalanceUpdates = Collections.synchronizedList(new ArrayList<>());
    private final List<OrderUpdate> latestOrderUpdates = Collections.synchronizedList(new ArrayList<>());
    private final List<TradeUpdate> latestTradeUpdates = Collections.synchronizedList(new ArrayList<>());

    /**
     * Get connection status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("publicConnected", publicWebSocketClient.isConnected());
        status.put("privateConnected", privateWebSocketClient.isConnected());
        status.put("subscribedChannels", publicWebSocketClient.getSubscribedChannels());
        return ResponseEntity.ok(status);
    }

    /**
     * Subscribe to candlestick data for a trading pair
     * 
     * @param pair Trading pair (e.g., "BTC_USDT")
     * @param interval Candlestick interval: 1m, 5m, 15m, 30m, 1h, 4h, 8h, 1d, 3d, 1w, 1M
     */
    @PostMapping("/subscribe/candlestick")
    public ResponseEntity<Map<String, String>> subscribeToCandlestick(
            @RequestParam String pair,
            @RequestParam(defaultValue = "1m") String interval) {
        try {
            publicWebSocketClient.subscribeToCandlestick(pair, interval, candlestick -> {
                // Store latest candlestick (keep last 100)
                synchronized (latestCandlesticks) {
                    latestCandlesticks.add(candlestick);
                    if (latestCandlesticks.size() > 100) {
                        latestCandlesticks.remove(0);
                    }
                }
            });
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Subscribed to candlestick channel: " + pair + "_" + interval);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Subscribe to order book (depth snapshot) for a trading pair
     * 
     * @param pair Trading pair (e.g., "BTC_USDT")
     * @param depth Depth level (e.g., "20")
     */
    @PostMapping("/subscribe/orderbook")
    public ResponseEntity<Map<String, String>> subscribeToOrderBook(
            @RequestParam String pair,
            @RequestParam(defaultValue = "20") String depth) {
        try {
            publicWebSocketClient.subscribeToDepthSnapshot(pair, depth, depthSnapshot -> {
                // Store latest depth snapshot (keep last 50)
                synchronized (latestDepthSnapshots) {
                    latestDepthSnapshots.add(depthSnapshot);
                    if (latestDepthSnapshots.size() > 50) {
                        latestDepthSnapshots.remove(0);
                    }
                }
            });
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Subscribed to order book channel: " + pair + "@orderbook@" + depth);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Unsubscribe from a specific channel
     * 
     * @param channelName Full channel name (e.g., "B-BTC_USDT_1m")
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestParam String channelName) {
        try {
            publicWebSocketClient.unsubscribeFromChannel(channelName);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Unsubscribed from channel: " + channelName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Connect to private WebSocket and subscribe to updates
     * Requires API key and secret to be configured
     */
    @PostMapping("/subscribe/private")
    public ResponseEntity<Map<String, String>> subscribeToPrivateChannels() {
        try {
            // Connect to private WebSocket
            privateWebSocketClient.connect();
            
            // Subscribe to balance updates
            privateWebSocketClient.subscribeToBalanceUpdates(balanceUpdate -> {
                synchronized (latestBalanceUpdates) {
                    latestBalanceUpdates.add(balanceUpdate);
                    if (latestBalanceUpdates.size() > 100) {
                        latestBalanceUpdates.remove(0);
                    }
                }
            });
            
            // Subscribe to order updates
            privateWebSocketClient.subscribeToOrderUpdates(orderUpdate -> {
                synchronized (latestOrderUpdates) {
                    latestOrderUpdates.add(orderUpdate);
                    if (latestOrderUpdates.size() > 100) {
                        latestOrderUpdates.remove(0);
                    }
                }
            });
            
            // Subscribe to trade updates
            privateWebSocketClient.subscribeToTradeUpdates(tradeUpdate -> {
                synchronized (latestTradeUpdates) {
                    latestTradeUpdates.add(tradeUpdate);
                    if (latestTradeUpdates.size() > 100) {
                        latestTradeUpdates.remove(0);
                    }
                }
            });
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Connected to private WebSocket channels");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Disconnect from private WebSocket
     */
    @PostMapping("/unsubscribe/private")
    public ResponseEntity<Map<String, String>> unsubscribeFromPrivateChannels() {
        try {
            privateWebSocketClient.disconnect();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Disconnected from private WebSocket");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get latest candlestick data
     */
    @GetMapping("/data/candlesticks")
    public ResponseEntity<List<Candlestick>> getLatestCandlesticks(
            @RequestParam(defaultValue = "10") int limit) {
        synchronized (latestCandlesticks) {
            int size = latestCandlesticks.size();
            int fromIndex = Math.max(0, size - limit);
            return ResponseEntity.ok(new ArrayList<>(latestCandlesticks.subList(fromIndex, size)));
        }
    }

    /**
     * Get latest order book data
     */
    @GetMapping("/data/orderbook")
    public ResponseEntity<List<DepthSnapshot>> getLatestOrderBook(
            @RequestParam(defaultValue = "10") int limit) {
        synchronized (latestDepthSnapshots) {
            int size = latestDepthSnapshots.size();
            int fromIndex = Math.max(0, size - limit);
            return ResponseEntity.ok(new ArrayList<>(latestDepthSnapshots.subList(fromIndex, size)));
        }
    }

    /**
     * Get latest balance updates
     */
    @GetMapping("/data/balances")
    public ResponseEntity<List<BalanceUpdate>> getLatestBalances(
            @RequestParam(defaultValue = "10") int limit) {
        synchronized (latestBalanceUpdates) {
            int size = latestBalanceUpdates.size();
            int fromIndex = Math.max(0, size - limit);
            return ResponseEntity.ok(new ArrayList<>(latestBalanceUpdates.subList(fromIndex, size)));
        }
    }

    /**
     * Get latest order updates
     */
    @GetMapping("/data/orders")
    public ResponseEntity<List<OrderUpdate>> getLatestOrders(
            @RequestParam(defaultValue = "10") int limit) {
        synchronized (latestOrderUpdates) {
            int size = latestOrderUpdates.size();
            int fromIndex = Math.max(0, size - limit);
            return ResponseEntity.ok(new ArrayList<>(latestOrderUpdates.subList(fromIndex, size)));
        }
    }

    /**
     * Get latest trade updates
     */
    @GetMapping("/data/trades")
    public ResponseEntity<List<TradeUpdate>> getLatestTrades(
            @RequestParam(defaultValue = "10") int limit) {
        synchronized (latestTradeUpdates) {
            int size = latestTradeUpdates.size();
            int fromIndex = Math.max(0, size - limit);
            return ResponseEntity.ok(new ArrayList<>(latestTradeUpdates.subList(fromIndex, size)));
        }
    }
}
