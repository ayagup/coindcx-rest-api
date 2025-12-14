package com.mycompany.websocket.example;

import com.mycompany.websocket.model.Candlestick;
import com.mycompany.websocket.model.DepthSnapshot;
import com.mycompany.websocket.service.CoinDCXPublicWebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Example usage of CoinDCX WebSocket implementation
 * This class demonstrates how to subscribe to public channels programmatically
 * 
 * To enable this example, uncomment the @Component annotation
 */
// @Component
public class WebSocketUsageExample implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketUsageExample.class);

    @Autowired
    private CoinDCXPublicWebSocketClient publicWebSocketClient;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting WebSocket example...");

        // Example 1: Subscribe to BTC/USDT 1-minute candlesticks
        publicWebSocketClient.subscribeToCandlestick("BTC_USDT", "1m", this::handleCandlestick);

        // Example 2: Subscribe to ETH/USDT 5-minute candlesticks
        publicWebSocketClient.subscribeToCandlestick("ETH_USDT", "5m", this::handleCandlestick);

        // Example 3: Subscribe to BTC/USDT order book (20 levels)
        publicWebSocketClient.subscribeToDepthSnapshot("BTC_USDT", "20", this::handleDepthSnapshot);

        logger.info("WebSocket subscriptions active. Press Ctrl+C to exit.");
    }

    /**
     * Handle candlestick updates
     */
    private void handleCandlestick(Candlestick candlestick) {
        logger.info("Candlestick Update - Symbol: {}, Interval: {}, Open: {}, High: {}, Low: {}, Close: {}, Volume: {}, Closed: {}",
                candlestick.getSymbol(),
                candlestick.getInterval(),
                candlestick.getOpen(),
                candlestick.getHigh(),
                candlestick.getLow(),
                candlestick.getClose(),
                candlestick.getBaseAssetVolume(),
                candlestick.getClosed());
    }

    /**
     * Handle order book updates
     */
    private void handleDepthSnapshot(DepthSnapshot depthSnapshot) {
        logger.info("Depth Snapshot Update - Timestamp: {}, Asks: {}, Bids: {}",
                depthSnapshot.getTimestamp(),
                depthSnapshot.getAsks() != null ? depthSnapshot.getAsks().size() : 0,
                depthSnapshot.getBids() != null ? depthSnapshot.getBids().size() : 0);
        
        // Print top 5 asks and bids
        if (depthSnapshot.getAsks() != null && !depthSnapshot.getAsks().isEmpty()) {
            logger.info("Top Asks:");
            depthSnapshot.getAsks().entrySet().stream()
                    .limit(5)
                    .forEach(entry -> logger.info("  Price: {}, Quantity: {}", entry.getKey(), entry.getValue()));
        }
        
        if (depthSnapshot.getBids() != null && !depthSnapshot.getBids().isEmpty()) {
            logger.info("Top Bids:");
            depthSnapshot.getBids().entrySet().stream()
                    .limit(5)
                    .forEach(entry -> logger.info("  Price: {}, Quantity: {}", entry.getKey(), entry.getValue()));
        }
    }
}
