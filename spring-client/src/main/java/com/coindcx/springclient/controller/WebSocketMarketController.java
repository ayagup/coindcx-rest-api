package com.coindcx.springclient.controller;

import com.coindcx.springclient.service.CoinDCXWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing WebSocket market subscriptions
 * Allows enabling/disabling spot and futures markets separately
 */
@RestController
@RequestMapping("/api/websocket/market")
public class WebSocketMarketController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketMarketController.class);
    
    private final CoinDCXWebSocketService webSocketService;
    
    // Track which market is currently enabled
    private volatile String enabledMarket = "none"; // "spot", "futures", or "none"
    
    @Autowired
    public WebSocketMarketController(CoinDCXWebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
    
    /**
     * Enable spot market (disables futures if active)
     * GET /api/websocket/market/enable/spot
     */
    @GetMapping("/enable/spot")
    public ResponseEntity<Map<String, Object>> enableSpotMarket() {
        logger.info("📊 Enabling SPOT market...");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Unsubscribe from futures if active
            if ("futures".equals(enabledMarket)) {
                logger.info("Unsubscribing from FUTURES market first...");
                unsubscribeFuturesChannels();
            }
            
            // Subscribe to spot channels
            subscribeSpotChannels();
            
            enabledMarket = "spot";
            response.put("success", true);
            response.put("message", "SPOT market enabled successfully");
            response.put("enabledMarket", "spot");
            response.put("subscribedChannels", 4);
            
            logger.info("✅ SPOT market enabled successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Failed to enable SPOT market", e);
            response.put("success", false);
            response.put("message", "Failed to enable SPOT market: " + e.getMessage());
            response.put("enabledMarket", enabledMarket);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Enable futures market (disables spot if active)
     * GET /api/websocket/market/enable/futures
     */
    @GetMapping("/enable/futures")
    public ResponseEntity<Map<String, Object>> enableFuturesMarket() {
        logger.info("📊 Enabling FUTURES market...");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Unsubscribe from spot if active
            if ("spot".equals(enabledMarket)) {
                logger.info("Unsubscribing from SPOT market first...");
                unsubscribeSpotChannels();
            }
            
            // Subscribe to futures channels
            subscribeFuturesChannels();
            
            enabledMarket = "futures";
            response.put("success", true);
            response.put("message", "FUTURES market enabled successfully");
            response.put("enabledMarket", "futures");
            response.put("subscribedChannels", 4);
            
            logger.info("✅ FUTURES market enabled successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Failed to enable FUTURES market", e);
            response.put("success", false);
            response.put("message", "Failed to enable FUTURES market: " + e.getMessage());
            response.put("enabledMarket", enabledMarket);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Disable both markets
     * GET /api/websocket/market/disable
     */
    @GetMapping("/disable")
    public ResponseEntity<Map<String, Object>> disableAllMarkets() {
        logger.info("🛑 Disabling all markets...");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if ("spot".equals(enabledMarket)) {
                unsubscribeSpotChannels();
            } else if ("futures".equals(enabledMarket)) {
                unsubscribeFuturesChannels();
            }
            
            enabledMarket = "none";
            response.put("success", true);
            response.put("message", "All markets disabled successfully");
            response.put("enabledMarket", "none");
            
            logger.info("✅ All markets disabled successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Failed to disable markets", e);
            response.put("success", false);
            response.put("message", "Failed to disable markets: " + e.getMessage());
            response.put("enabledMarket", enabledMarket);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get current market status
     * GET /api/websocket/market/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMarketStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("enabledMarket", enabledMarket);
        response.put("spotEnabled", "spot".equals(enabledMarket));
        response.put("futuresEnabled", "futures".equals(enabledMarket));
        response.put("websocketConnected", webSocketService.isConnected());
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== Helper Methods ====================
    
    private void subscribeSpotChannels() throws InterruptedException {
        logger.info("Subscribing to SPOT channels...");
        
        // Wait a bit to ensure previous unsubscriptions are processed
        Thread.sleep(500);
        
        // Spot Candlestick - BTC/USDT (1 minute interval)
        webSocketService.subscribeToPublicChannel(
            "B-BTC_USDT_1m",
            "candlestick",
            true
        );
        logger.info("  ✓ Subscribed to spot candlestick");
        
        // Spot Orderbook Depth Update
        webSocketService.subscribeToPublicChannel(
            "B-BTC_USDT@orderbook@20",
            "depth-update",
            true
        );
        logger.info("  ✓ Subscribed to spot depth update");
        
        // Spot Current Prices
        webSocketService.subscribeToPublicChannel(
            "currentPrices@spot@10s",
            "currentPrices@spot#update",
            true
        );
        logger.info("  ✓ Subscribed to spot current prices");
        
        // Spot New Trade
        webSocketService.subscribeToPublicChannel(
            "B-BTC_USDT@trades",
            "new-trade",
            true
        );
        logger.info("  ✓ Subscribed to spot new trade");
        
        logger.info("✅ All SPOT channels subscribed");
    }
    
    private void subscribeFuturesChannels() throws InterruptedException {
        logger.info("Subscribing to FUTURES channels...");
        
        // Wait a bit to ensure previous unsubscriptions are processed
        Thread.sleep(500);
        
        // Futures Candlestick - BTC/USDT (1 minute interval)
        // Note: Using different channel pattern to distinguish from spot
        webSocketService.subscribeToPublicChannel(
            "BTCUSDT_1m-futures",  // Futures might use different pair format
            "candlestick",
            true
        );
        logger.info("  ✓ Subscribed to futures candlestick");
        
        // Futures Orderbook Depth
        webSocketService.subscribeToPublicChannel(
            "B-BTC_USDT@orderbook@20-futures",
            "depth-update",
            true
        );
        logger.info("  ✓ Subscribed to futures depth update");
        
        // Futures Current Prices
        webSocketService.subscribeToPublicChannel(
            "currentPrices@futures@rt",
            "currentPrices@futures#update",
            true
        );
        logger.info("  ✓ Subscribed to futures current prices");
        
        // Futures New Trade
        webSocketService.subscribeToPublicChannel(
            "B-BTC_USDT@trades-futures",
            "new-trade",
            true
        );
        logger.info("  ✓ Subscribed to futures new trade");
        
        logger.info("✅ All FUTURES channels subscribed");
    }
    
    private void unsubscribeSpotChannels() {
        logger.info("Unsubscribing from SPOT channels...");
        
        webSocketService.unsubscribeFromChannel("B-BTC_USDT_1m");
        webSocketService.unsubscribeFromChannel("B-BTC_USDT@orderbook@20");
        webSocketService.unsubscribeFromChannel("currentPrices@spot@10s");
        webSocketService.unsubscribeFromChannel("B-BTC_USDT@trades");
        
        logger.info("✅ Unsubscribed from all SPOT channels");
    }
    
    private void unsubscribeFuturesChannels() {
        logger.info("Unsubscribing from FUTURES channels...");
        
        webSocketService.unsubscribeFromChannel("BTCUSDT_1m");
        webSocketService.unsubscribeFromChannel("B-BTC_USDT@depth-futures@rt");
        webSocketService.unsubscribeFromChannel("currentPrices@futures@10s");
        webSocketService.unsubscribeFromChannel("B-BTC_USDT@trades-futures");
        
        logger.info("✅ Unsubscribed from all FUTURES channels");
    }
}
