package com.coindcx.springclient.service;

import com.coindcx.springclient.config.WebSocketConfig;
import com.coindcx.springclient.constants.WebSocketChannels;
import com.google.gson.Gson;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing CoinDCX WebSocket connections
 */
@Service
public class CoinDCXWebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(CoinDCXWebSocketService.class);
    
    private final WebSocketConfig config;
    private final Gson gson;
    private Socket socket;
    
    // Store received messages by event type
    private final Map<String, List<Object>> messageStore = new ConcurrentHashMap<>();
    
    @Autowired
    public CoinDCXWebSocketService(WebSocketConfig config) {
        this.config = config;
        this.gson = new Gson();
    }
    
    @PostConstruct
    public void initialize() {
        try {
            logger.info("Initializing WebSocket connection to {}", config.getWebsocketEndpoint());
            
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};
            options.reconnection = true;
            options.reconnectionAttempts = 5;
            options.reconnectionDelay = 1000;
            
            socket = IO.socket(config.getWebsocketEndpoint(), options);
            
            // Setup event listeners
            setupEventListeners();
            
            // Connect to the WebSocket
            socket.connect();
            
        } catch (URISyntaxException e) {
            logger.error("Invalid WebSocket URI: {}", config.getWebsocketEndpoint(), e);
        }
    }
    
    private void setupEventListeners() {
        socket.on(WebSocketChannels.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.info("WebSocket connected successfully");
            }
        });
        
        socket.on(WebSocketChannels.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.error("WebSocket connection error: {}", args.length > 0 ? args[0] : "Unknown error");
            }
        });
        
        socket.on(WebSocketChannels.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.info("WebSocket disconnected");
            }
        });
    }
    
    /**
     * Subscribe to a public channel (no authentication required)
     */
    public void subscribeToPublicChannel(String channelName, String eventName) {
        if (!isConnected()) {
            logger.warn("Cannot subscribe: WebSocket not connected");
            return;
        }
        
        logger.info("Subscribing to channel: {} for event: {}", channelName, eventName);
        
        // Setup listener for the specific event
        socket.on(eventName, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0) {
                    Object data = args[0];
                    logger.debug("Received {} event: {}", eventName, data);
                    storeMessage(eventName, data);
                }
            }
        });
        
        // Join the channel
        Map<String, String> joinData = new HashMap<>();
        joinData.put("channelName", channelName);
        socket.emit(WebSocketChannels.ACTION_JOIN, gson.toJson(joinData));
        
        logger.info("Joined channel: {}", channelName);
    }
    
    /**
     * Subscribe to a private channel (requires authentication)
     */
    public void subscribeToPrivateChannel(String channelName, String eventName) {
        if (!isConnected()) {
            logger.warn("Cannot subscribe: WebSocket not connected");
            return;
        }
        
        String apiKey = config.getApiKey();
        String apiSecret = config.getApiSecret();
        
        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            logger.error("Cannot subscribe to private channel: API credentials not configured");
            return;
        }
        
        logger.info("Subscribing to private channel: {} for event: {}", channelName, eventName);
        
        // Setup listener for the specific event
        socket.on(eventName, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0) {
                    Object data = args[0];
                    logger.debug("Received {} event: {}", eventName, data);
                    storeMessage(eventName, data);
                }
            }
        });
        
        // Generate authentication signature
        String signature = generateSignature(channelName, apiSecret);
        
        // Join the channel with authentication
        Map<String, String> joinData = new HashMap<>();
        joinData.put("channelName", channelName);
        joinData.put("authSignature", signature);
        joinData.put("apiKey", apiKey);
        socket.emit(WebSocketChannels.ACTION_JOIN, gson.toJson(joinData));
        
        logger.info("Joined private channel: {}", channelName);
    }
    
    /**
     * Unsubscribe from a channel
     */
    public void unsubscribeFromChannel(String channelName) {
        if (!isConnected()) {
            logger.warn("Cannot unsubscribe: WebSocket not connected");
            return;
        }
        
        logger.info("Unsubscribing from channel: {}", channelName);
        
        Map<String, String> leaveData = new HashMap<>();
        leaveData.put("channelName", channelName);
        socket.emit(WebSocketChannels.ACTION_LEAVE, gson.toJson(leaveData));
        
        logger.info("Left channel: {}", channelName);
    }
    
    /**
     * Get received messages for a specific event
     */
    public List<Object> getMessages(String eventName) {
        return messageStore.getOrDefault(eventName, new ArrayList<>());
    }
    
    /**
     * Clear stored messages for a specific event
     */
    public void clearMessages(String eventName) {
        messageStore.remove(eventName);
    }
    
    /**
     * Clear all stored messages
     */
    public void clearAllMessages() {
        messageStore.clear();
    }
    
    /**
     * Check if WebSocket is connected
     */
    public boolean isConnected() {
        return socket != null && socket.connected();
    }
    
    /**
     * Get connection status
     */
    public String getConnectionStatus() {
        if (socket == null) {
            return "NOT_INITIALIZED";
        }
        return socket.connected() ? "CONNECTED" : "DISCONNECTED";
    }
    
    /**
     * Store a message in memory
     */
    private void storeMessage(String eventName, Object data) {
        messageStore.computeIfAbsent(eventName, k -> Collections.synchronizedList(new ArrayList<>()))
                   .add(data);
        
        // Keep only last 100 messages per event to prevent memory issues
        List<Object> messages = messageStore.get(eventName);
        if (messages.size() > 100) {
            messages.remove(0);
        }
    }
    
    /**
     * Generate HMAC-SHA256 signature for authentication
     */
    private String generateSignature(String channelName, String secret) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("channel", channelName);
            String jsonBody = gson.toJson(body);
            
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            
            byte[] hash = sha256_HMAC.doFinal(jsonBody.getBytes());
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (Exception e) {
            logger.error("Error generating signature", e);
            return null;
        }
    }
    
    @PreDestroy
    public void cleanup() {
        if (socket != null) {
            logger.info("Closing WebSocket connection");
            socket.disconnect();
            socket.close();
        }
    }
}
