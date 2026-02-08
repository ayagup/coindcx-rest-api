//package com.coindcx.springclient.config;
//
//import com.coindcx.springclient.service.CoinDCXWebSocketService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
///**
// * Auto-subscription configuration for WebSocket channels
// * ENABLED - Automatically subscribes to SPOT and FUTURES market channels on startup
// *
// * Total 8 channels:
// * - 4 SPOT channels (candlestick, orderbook, prices, trades)
// * - 4 FUTURES channels (candlestick, orderbook, prices, trades)
// */
//@Component
//public class WebSocketAutoSubscriptionConfig {
//
//    private static final Logger logger = LoggerFactory.getLogger(WebSocketAutoSubscriptionConfig.class);
//
//    private final CoinDCXWebSocketService webSocketService;
//
//    @Autowired
//    public WebSocketAutoSubscriptionConfig(CoinDCXWebSocketService webSocketService) {
//        this.webSocketService = webSocketService;
//    }
//
//    /**
//     * Auto-subscription to SPOT and FUTURES market channels after application is ready
//     *
//     * SPOT Market Channels:
//     * - BTC/USDT Candlestick (1m interval)
//     * - BTC/USDT Orderbook Depth (20 levels)
//     * - Spot Current Prices (10s updates)
//     * - BTC/USDT New Trades
//     *
//     * FUTURES Market Channels:
//     * - BTC/USDT Candlestick (1m interval)
//     * - BTC/USDT Orderbook Depth (20 levels)
//     * - Futures Current Prices (real-time)
//     * - BTC/USDT New Trades
//     *
//     * All channels persist data to database automatically.
//     *
//     * To change markets or disable auto-subscription:
//     *   - Modify this method or comment out subscriptions
//     *   - Use /api/websocket/market/enable/{spot|futures} endpoints for runtime control
//     *   - Use /api/websocket/market/disable to stop subscriptions
//     */
//    @EventListener(ApplicationReadyEvent.class)
//    public void autoSubscribeToChannels() {
//        logger.info("🚀 Starting auto-subscription to SPOT and FUTURES market WebSocket channels...");
//
//        try {
//            // Wait for application to fully initialize
//            Thread.sleep(1000);
//
//            // // ==================== SPOT MARKET CHANNELS ====================
//            // logger.info("📊 Subscribing to SPOT market channels...");
//
//            // // Spot Candlestick - BTC/USDT (1 minute interval)
//            // webSocketService.subscribeToPublicChannel(
//            //     "B-BTC_USDT_1m",
//            //     "candlestick",
//            //     true  // persist to database
//            // );
//            // logger.info("  ✓ Subscribed to spot candlestick (B-BTC_USDT_1m)");
//
//            // // Spot Orderbook Depth Update
//            // webSocketService.subscribeToPublicChannel(
//            //     "B-BTC_USDT@orderbook@20",
//            //     "depth-update",
//            //     true
//            // );
//            // logger.info("  ✓ Subscribed to spot depth update (B-BTC_USDT@orderbook@20)");
//
//            // // Spot Current Prices
//            // webSocketService.subscribeToPublicChannel(
//            //     "currentPrices@spot@10s",
//            //     "currentPrices@spot#update",
//            //     true
//            // );
//            // logger.info("  ✓ Subscribed to spot current prices (10s updates)");
//
//            // // Spot New Trade
//            // webSocketService.subscribeToPublicChannel(
//            //     "B-BTC_USDT@trades",
//            //     "new-trade",
//            //     true
//            // );
//            // logger.info("  ✓ Subscribed to spot new trade (B-BTC_USDT@trades)");
//
//            // logger.info("✅ SPOT market: 4 channels subscribed");
//
//            // // Small delay between spot and futures subscriptions
//            // Thread.sleep(500);
//
//            // ==================== FUTURES MARKET CHANNELS ====================
//            logger.info("📊 Subscribing to FUTURES market channels...");
//
//            // Futures Candlestick - BTC/USDT (1 minute interval)
//            webSocketService.subscribeToPrivateChannel(
//                "B-BTC_USDT_1m-futures",
//                "candlestick",
//                true
//            );
//            logger.info("  ✓ Subscribed to futures candlestick (B-BTC_USDT_1m-futures)");
//
//            // Futures Orderbook Depth
//            webSocketService.subscribeToPrivateChannel(
//                "B-BTC_USDT@orderbook@20-futures",
//                "depth-update",
//                true
//            );
//            logger.info("  ✓ Subscribed to futures depth update (B-BTC_USDT@orderbook@20-futures)");
//
//            // Futures Current Prices
//            webSocketService.subscribeToPrivateChannel(
//                "currentPrices@futures@rt",
//                "currentPrices@futures#update",
//                true
//            );
//            logger.info("  ✓ Subscribed to futures current prices (real-time)");
//
//            // Futures New Trade
//            webSocketService.subscribeToPrivateChannel(
//                "B-BTC_USDT@trades-futures",
//                "new-trade",
//                true
//            );
//            logger.info("  ✓ Subscribed to futures new trade (B-BTC_USDT@trades-futures)");
//
//            logger.info("✅ FUTURES market: 4 channels subscribed");
//            logger.info("🎉 Auto-subscription completed - SPOT + FUTURES markets active with 8 total channels");
//            logger.info("📊 All data is being persisted to MySQL database automatically");
//
//        } catch (InterruptedException e) {
//            logger.error("❌ Auto-subscription interrupted", e);
//            Thread.currentThread().interrupt();
//        } catch (Exception e) {
//            logger.error("❌ Failed to auto-subscribe to channels", e);
//        }
//    }
//}
