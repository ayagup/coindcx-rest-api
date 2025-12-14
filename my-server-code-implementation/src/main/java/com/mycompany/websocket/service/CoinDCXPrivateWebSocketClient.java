package com.mycompany.websocket.service;

import com.google.gson.Gson;
import com.mycompany.config.WebSocketConfig;
import com.mycompany.websocket.model.BalanceUpdate;
import com.mycompany.websocket.model.OrderUpdate;
import com.mycompany.websocket.model.TradeUpdate;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * WebSocket client service for CoinDCX private (authenticated) channels
 * Handles connections to private data streams like balance updates, order updates, and trade updates
 * Requires API key and secret for HMAC authentication
 */
@Service
public class CoinDCXPrivateWebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(CoinDCXPrivateWebSocketClient.class);
    private static final String CHANNEL_NAME = "coindcx";

    @Autowired
    private WebSocketConfig.WebSocketProperties webSocketProperties;

    @Value("${coindcx.api.key:}")
    private String apiKey;

    @Value("${coindcx.api.secret:}")
    private String apiSecret;

    private Socket socket;
    private final Gson gson = new Gson();
    private final List<Consumer<BalanceUpdate>> balanceUpdateListeners = new ArrayList<>();
    private final List<Consumer<OrderUpdate>> orderUpdateListeners = new ArrayList<>();
    private final List<Consumer<TradeUpdate>> tradeUpdateListeners = new ArrayList<>();
    private boolean isSubscribed = false;

    /**
     * Connect to CoinDCX private WebSocket channels
     * Requires API key and secret to be configured
     */
    public void connect() throws URISyntaxException {
        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            throw new IllegalStateException("API key and secret must be configured to use private WebSocket channels");
        }

        IO.Options options = new IO.Options();
        options.transports = new String[]{"websocket"};
        options.reconnection = webSocketProperties.isAutoReconnect();
        options.reconnectionDelay = webSocketProperties.getReconnectDelay();

        socket = IO.socket(webSocketProperties.getSocketEndpoint(), options);

        // Connection event handlers
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.info("Connected to CoinDCX Private WebSocket");
                authenticateAndJoin();
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.warn("Disconnected from CoinDCX Private WebSocket");
                isSubscribed = false;
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.error("Private WebSocket connection error: {}", args.length > 0 ? args[0] : "Unknown error");
            }
        });

        // Event listeners for private data streams
        socket.on("balance-update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleBalanceUpdate(args);
            }
        });

        socket.on("order-update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleOrderUpdate(args);
            }
        });

        socket.on("trade-update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleTradeUpdate(args);
            }
        });

        socket.connect();
    }

    /**
     * Authenticate and join the private channel using HMAC signature
     */
    private void authenticateAndJoin() {
        try {
            String signature = generateHmacSignature();
            
            Map<String, String> payload = new HashMap<>();
            payload.put("channelName", CHANNEL_NAME);
            payload.put("authSignature", signature);
            payload.put("apiKey", apiKey);
            
            socket.emit("join", gson.toJson(payload));
            isSubscribed = true;
            logger.info("Authenticated and joined private channel: {}", CHANNEL_NAME);
        } catch (Exception e) {
            logger.error("Failed to authenticate and join private channel", e);
        }
    }

    /**
     * Generate HMAC SHA256 signature for authentication
     */
    private String generateHmacSignature() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("channel", CHANNEL_NAME);
        String jsonBody = gson.toJson(body);
        
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKeySpec);
        byte[] hash = hmacSha256.doFinal(jsonBody.getBytes(StandardCharsets.UTF_8));
        
        // Convert to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Subscribe to balance updates
     * @param listener Callback function to handle balance update events
     */
    public void subscribeToBalanceUpdates(Consumer<BalanceUpdate> listener) {
        balanceUpdateListeners.add(listener);
        logger.info("Added listener for balance updates");
    }

    /**
     * Subscribe to order updates
     * @param listener Callback function to handle order update events
     */
    public void subscribeToOrderUpdates(Consumer<OrderUpdate> listener) {
        orderUpdateListeners.add(listener);
        logger.info("Added listener for order updates");
    }

    /**
     * Subscribe to trade updates
     * @param listener Callback function to handle trade update events
     */
    public void subscribeToTradeUpdates(Consumer<TradeUpdate> listener) {
        tradeUpdateListeners.add(listener);
        logger.info("Added listener for trade updates");
    }

    /**
     * Handle balance update events
     */
    private void handleBalanceUpdate(Object... args) {
        try {
            if (args.length > 0) {
                String jsonData = args[0].toString();
                // Response is an array
                BalanceUpdate[] balanceUpdates = gson.fromJson(jsonData, BalanceUpdate[].class);
                
                for (BalanceUpdate update : balanceUpdates) {
                    for (Consumer<BalanceUpdate> listener : balanceUpdateListeners) {
                        try {
                            listener.accept(update);
                        } catch (Exception e) {
                            logger.error("Error in balance update listener", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling balance update", e);
        }
    }

    /**
     * Handle order update events
     */
    private void handleOrderUpdate(Object... args) {
        try {
            if (args.length > 0) {
                String jsonData = args[0].toString();
                // Response is an array
                OrderUpdate[] orderUpdates = gson.fromJson(jsonData, OrderUpdate[].class);
                
                for (OrderUpdate update : orderUpdates) {
                    for (Consumer<OrderUpdate> listener : orderUpdateListeners) {
                        try {
                            listener.accept(update);
                        } catch (Exception e) {
                            logger.error("Error in order update listener", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling order update", e);
        }
    }

    /**
     * Handle trade update events
     */
    private void handleTradeUpdate(Object... args) {
        try {
            if (args.length > 0) {
                String jsonData = args[0].toString();
                // Response is an array
                TradeUpdate[] tradeUpdates = gson.fromJson(jsonData, TradeUpdate[].class);
                
                for (TradeUpdate update : tradeUpdates) {
                    for (Consumer<TradeUpdate> listener : tradeUpdateListeners) {
                        try {
                            listener.accept(update);
                        } catch (Exception e) {
                            logger.error("Error in trade update listener", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling trade update", e);
        }
    }

    /**
     * Check if connected and subscribed to private WebSocket
     */
    public boolean isConnected() {
        return socket != null && socket.connected() && isSubscribed;
    }

    /**
     * Leave the private channel
     */
    public void leaveChannel() {
        if (socket != null && socket.connected()) {
            Map<String, String> payload = new HashMap<>();
            payload.put("channelName", CHANNEL_NAME);
            socket.emit("leave", gson.toJson(payload));
            isSubscribed = false;
            logger.info("Left private channel: {}", CHANNEL_NAME);
        }
    }

    /**
     * Disconnect and cleanup
     */
    @PreDestroy
    public void disconnect() {
        if (socket != null) {
            leaveChannel();
            socket.disconnect();
            socket.close();
            logger.info("Disconnected from CoinDCX Private WebSocket");
        }
        balanceUpdateListeners.clear();
        orderUpdateListeners.clear();
        tradeUpdateListeners.clear();
    }
}
