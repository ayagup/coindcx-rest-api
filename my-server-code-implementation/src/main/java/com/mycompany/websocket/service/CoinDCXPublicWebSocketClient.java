package com.mycompany.websocket.service;

import com.google.gson.Gson;
import com.mycompany.config.WebSocketConfig;
import com.mycompany.websocket.model.Candlestick;
import com.mycompany.websocket.model.DepthSnapshot;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * WebSocket client service for CoinDCX public channels
 * Handles connections to public data streams like candlesticks and order book
 */
@Service
public class CoinDCXPublicWebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(CoinDCXPublicWebSocketClient.class);

    @Autowired
    private WebSocketConfig.WebSocketProperties webSocketProperties;

    private Socket socket;
    private final Gson gson = new Gson();
    private final Map<String, List<Consumer<Candlestick>>> candlestickListeners = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<DepthSnapshot>>> depthSnapshotListeners = new ConcurrentHashMap<>();
    private final Set<String> subscribedChannels = ConcurrentHashMap.newKeySet();

    /**
     * Initialize WebSocket connection
     */
    @PostConstruct
    public void initialize() {
        try {
            connectToWebSocket();
        } catch (URISyntaxException e) {
            logger.error("Failed to initialize WebSocket connection", e);
        }
    }

    /**
     * Connect to CoinDCX WebSocket server
     */
    private void connectToWebSocket() throws URISyntaxException {
        IO.Options options = new IO.Options();
        options.transports = new String[]{"websocket"};
        options.reconnection = webSocketProperties.isAutoReconnect();
        options.reconnectionDelay = webSocketProperties.getReconnectDelay();

        socket = IO.socket(webSocketProperties.getSocketEndpoint(), options);

        // Connection event handlers
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.info("Connected to CoinDCX WebSocket");
                resubscribeToChannels();
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.warn("Disconnected from CoinDCX WebSocket");
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.error("Connection error: {}", args.length > 0 ? args[0] : "Unknown error");
            }
        });

        // Event listeners for data streams
        socket.on("candlestick", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleCandlestickUpdate(args);
            }
        });

        socket.on("depth-snapshot", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleDepthSnapshotUpdate(args);
            }
        });

        socket.connect();
    }

    /**
     * Subscribe to candlestick channel
     * @param pair Trading pair (e.g., "BTC_USDT")
     * @param interval Candlestick interval (e.g., "1m", "5m", "15m", "30m", "1h", "4h", "8h", "1d", "3d", "1w", "1M")
     * @param listener Callback function to handle candlestick updates
     */
    public void subscribeToCandlestick(String pair, String interval, Consumer<Candlestick> listener) {
        String channelName = "B-" + pair + "_" + interval;
        
        // Add listener
        candlestickListeners.computeIfAbsent(channelName, k -> new ArrayList<>()).add(listener);
        
        // Join channel
        joinChannel(channelName);
        
        logger.info("Subscribed to candlestick channel: {}", channelName);
    }

    /**
     * Subscribe to depth snapshot (order book) channel
     * @param pair Trading pair (e.g., "BTC_USDT")
     * @param depth Depth level (e.g., "20")
     * @param listener Callback function to handle depth snapshot updates
     */
    public void subscribeToDepthSnapshot(String pair, String depth, Consumer<DepthSnapshot> listener) {
        String channelName = "B-" + pair + "@orderbook@" + depth;
        
        // Add listener
        depthSnapshotListeners.computeIfAbsent(channelName, k -> new ArrayList<>()).add(listener);
        
        // Join channel
        joinChannel(channelName);
        
        logger.info("Subscribed to depth snapshot channel: {}", channelName);
    }

    /**
     * Unsubscribe from a channel
     * @param channelName Channel name to unsubscribe from
     */
    public void unsubscribeFromChannel(String channelName) {
        leaveChannel(channelName);
        candlestickListeners.remove(channelName);
        depthSnapshotListeners.remove(channelName);
        subscribedChannels.remove(channelName);
        logger.info("Unsubscribed from channel: {}", channelName);
    }

    /**
     * Join a WebSocket channel
     */
    private void joinChannel(String channelName) {
        if (socket != null && socket.connected()) {
            Map<String, String> payload = new HashMap<>();
            payload.put("channelName", channelName);
            socket.emit("join", gson.toJson(payload));
            subscribedChannels.add(channelName);
        } else {
            logger.warn("Socket not connected, cannot join channel: {}", channelName);
        }
    }

    /**
     * Leave a WebSocket channel
     */
    private void leaveChannel(String channelName) {
        if (socket != null && socket.connected()) {
            Map<String, String> payload = new HashMap<>();
            payload.put("channelName", channelName);
            socket.emit("leave", gson.toJson(payload));
        }
    }

    /**
     * Resubscribe to all channels after reconnection
     */
    private void resubscribeToChannels() {
        for (String channelName : subscribedChannels) {
            joinChannel(channelName);
            logger.info("Resubscribed to channel: {}", channelName);
        }
    }

    /**
     * Handle candlestick update events
     */
    private void handleCandlestickUpdate(Object... args) {
        try {
            if (args.length > 0) {
                String jsonData = args[0].toString();
                Candlestick candlestick = gson.fromJson(jsonData, Candlestick.class);
                
                // Notify all listeners for this channel
                String channelName = candlestick.getChannel();
                List<Consumer<Candlestick>> listeners = candlestickListeners.get(channelName);
                if (listeners != null) {
                    for (Consumer<Candlestick> listener : listeners) {
                        try {
                            listener.accept(candlestick);
                        } catch (Exception e) {
                            logger.error("Error in candlestick listener", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling candlestick update", e);
        }
    }

    /**
     * Handle depth snapshot update events
     */
    private void handleDepthSnapshotUpdate(Object... args) {
        try {
            if (args.length > 0) {
                String jsonData = args[0].toString();
                DepthSnapshot depthSnapshot = gson.fromJson(jsonData, DepthSnapshot.class);
                
                // Find matching channel and notify listeners
                for (Map.Entry<String, List<Consumer<DepthSnapshot>>> entry : depthSnapshotListeners.entrySet()) {
                    for (Consumer<DepthSnapshot> listener : entry.getValue()) {
                        try {
                            listener.accept(depthSnapshot);
                        } catch (Exception e) {
                            logger.error("Error in depth snapshot listener", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling depth snapshot update", e);
        }
    }

    /**
     * Check if connected to WebSocket
     */
    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    /**
     * Get list of subscribed channels
     */
    public Set<String> getSubscribedChannels() {
        return new HashSet<>(subscribedChannels);
    }

    /**
     * Disconnect and cleanup
     */
    @PreDestroy
    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.close();
            logger.info("Disconnected from CoinDCX WebSocket");
        }
        candlestickListeners.clear();
        depthSnapshotListeners.clear();
        subscribedChannels.clear();
    }
}
