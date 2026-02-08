package com.coindcx.springclient.service;

import com.coindcx.springclient.config.WebSocketConfig;
import com.coindcx.springclient.constants.WebSocketChannels;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service for managing CoinDCX WebSocket connections with database persistence
 * Using Socket.IO client for WebSocket support
 */
@Service
public class CoinDCXWebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(CoinDCXWebSocketService.class);

    private final WebSocketConfig config;
    private final WebSocketDataPersistenceService persistenceService;
    private final Gson gson;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Socket socket;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    // Store received messages by event type
    private final Map<String, List<Object>> messageStore = new ConcurrentHashMap<>();

    // Track subscribed channels
    private final Map<String, String> subscribedChannels = new ConcurrentHashMap<>();

    // Track event handlers
    private final Map<String, EventHandler> eventHandlers = new ConcurrentHashMap<>();

    @Autowired
    public CoinDCXWebSocketService(
            WebSocketConfig config,
            WebSocketDataPersistenceService persistenceService) {
        this.config = config;
        this.persistenceService = persistenceService;
        this.gson = new Gson();
    }

    @FunctionalInterface
    private interface EventHandler {
        void handle(Object data, String channelName, boolean persistToDatabase);
    }

    @PostConstruct
    public void initialize() {
        logger.info("Initializing Socket.IO client for CoinDCX");

        // Connect asynchronously to not block application startup
        scheduler.execute(() -> {
            try {
                connectWithRetry();
            } catch (Exception e) {
                logger.error("Socket.IO initialization failed: {}", e.getMessage());
                logger.warn("⚠️ Application will continue without WebSocket connection");
                logger.warn("💡 You can use REST API endpoints for market data");
            }
        });

        logger.info("Socket.IO connection scheduled - application startup will continue");
    }

    /**
     * Connect to WebSocket with retry logic
     */
    private void connectWithRetry() {
        int maxRetries = 3;
        int retryDelay = 3000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("Connection attempt {}/{} to {}", attempt, maxRetries, config.getWebsocketEndpoint());

                if (connectToWebSocket()) {
                    logger.info("✅ Socket.IO connection established successfully on attempt {}", attempt);
                    return;
                } else {
                    logger.warn("❌ Connection attempt {} failed", attempt);
                }
            } catch (Exception e) {
                logger.error("Connection attempt {} failed: {}", attempt, e.getMessage());
            }

            if (attempt < maxRetries) {
                logger.info("Waiting {}ms before retry...", retryDelay);
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                retryDelay += 2000;
            }
        }

        // Failed after all retries
        logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.error("❌ SOCKET.IO CONNECTION FAILED");
        logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        logger.error("Failed to establish Socket.IO connection after {} attempts", maxRetries);
        logger.error("");
        logger.error("📋 Endpoint: {}", config.getWebsocketEndpoint());
        logger.error("");
        logger.error("💡 POSSIBLE CAUSES:");
        logger.error("   1. CoinDCX WebSocket server is down for maintenance");
        logger.error("   2. The endpoint URL may have changed");
        logger.error("   3. Your IP address may be rate-limited or blocked");
        logger.error("   4. Network/firewall blocking WebSocket connections");
        logger.error("");
        logger.error("✅ WHAT TO DO:");
        logger.error("   1. Check CoinDCX status: https://coindcx.com");
        logger.error("   2. Verify endpoint: https://docs.coindcx.com");
        logger.error("   3. Contact support: support@coindcx.com");
        logger.error("   4. Try again in 30-60 minutes");
        logger.error("");
        logger.error("ℹ️  APPLICATION STATUS:");
        logger.error("   • REST API endpoints: ✅ AVAILABLE");
        logger.error("   • WebSocket real-time data: ❌ UNAVAILABLE");
        logger.error("   • Database operations: ✅ AVAILABLE");
        logger.error("");
        logger.error("🔄 Auto-reconnect will retry in background every 60 seconds");
        logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        schedulePeriodicReconnect();
    }

    /**
     * Schedule periodic reconnection attempts in background
     */
    private void schedulePeriodicReconnect() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!isConnected()) {
                logger.info("🔄 Attempting background reconnection...");
                try {
                    if (connectToWebSocket()) {
                        logger.info("✅ Background reconnection successful!");
                    }
                } catch (Exception e) {
                    logger.debug("Background reconnection failed: {}", e.getMessage());
                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * Connect to WebSocket server using Socket.IO
     */
    private boolean connectToWebSocket() {
        try {
            URI uri = new URI(config.getWebsocketEndpoint());

            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};
            options.reconnection = false; // We handle reconnection manually
            options.timeout = 10000; // 10 second timeout

            socket = IO.socket(uri, options);

            setupEventListeners();

            // Connect and wait for connection
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean connectionSuccess = new AtomicBoolean(false);

            socket.once(Socket.EVENT_CONNECT, args -> {
                connectionSuccess.set(true);
                latch.countDown();
            });

            socket.once(Socket.EVENT_CONNECT_ERROR, args -> {
                logger.error("Connection error: {}", args.length > 0 ? args[0] : "Unknown");
                latch.countDown();
            });

            socket.connect();

            // Wait for connection or timeout
            boolean completed = latch.await(10, TimeUnit.SECONDS);

            if (completed && connectionSuccess.get()) {
                connected.set(true);
                logger.info("✅ Socket.IO connected successfully");
                return true;
            } else {
                logger.error("Connection timeout or failed");
                if (socket != null) {
                    socket.close();
                }
                return false;
            }

        } catch (Exception e) {
            logger.error("Connection failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Setup Socket.IO event listeners
     */
    private void setupEventListeners() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            logger.info("✅ Socket.IO connected - Session established");
            connected.set(true);

            // Auto-subscribe to channels if API credentials configured
            if (config.getApiKey() != null && !config.getApiKey().isEmpty()) {
                logger.info("Auto-subscribing to futures channels...");

                subscribeToPrivateChannel(
                    WebSocketChannels.CHANNEL_PRIVATE_FUTURES_BTC_NEW_TRADES,
                    WebSocketChannels.EVENT_NEW_TRADE,
                    true
                );

                subscribeToPrivateChannel(
                    WebSocketChannels.CHANNEL_PRIVATE_FUTURES_BTC_PRICE_1M,
                    WebSocketChannels.EVENT_CANDLESTICK,
                    true
                );

                subscribeToPrivateChannel(
                    WebSocketChannels.CHANNEL_PRIVATE_FUTURES_BTC_DEPTH_UPDATE,
                    WebSocketChannels.EVENT_DEPTH_UPDATE,
                    true
                );

                subscribeToPrivateChannel(
                    WebSocketChannels.CHANNEL_PRIVATE_FUTURES_CURRENT_PRICES,
                    WebSocketChannels.EVENT_FUTURES_CURRENT_PRICES_UPDATE,
                    true
                );
            } else {
                logger.warn("API credentials not configured - skipping private channel subscriptions");
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            logger.info("Socket.IO disconnected: {}", args.length > 0 ? args[0] : "Unknown reason");
            connected.set(false);

            // Schedule reconnection
            scheduler.schedule(this::connectWithRetry, 10, TimeUnit.SECONDS);
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            logger.error("❌ Socket.IO connection error: {}", args.length > 0 ? args[0] : "Unknown error");
            connected.set(false);
        });
    }

    /**
     * Subscribe to a public channel (no authentication required)
     */
    public void subscribeToPublicChannel(String channelName, String eventName) {
        subscribeToPublicChannel(channelName, eventName, true);
    }

    /**
     * Subscribe to a public channel with option to persist data
     */
    public void subscribeToPublicChannel(String channelName, String eventName, boolean persistToDatabase) {
        if (!isConnected()) {
            logger.warn("Cannot subscribe: Socket.IO not connected");
            return;
        }

        logger.info("🔌 Subscribing to PUBLIC channel: '{}' for event: '{}' (persist={})", channelName, eventName, persistToDatabase);

        // Track subscription
        subscribedChannels.put(channelName, eventName);

        // Register event handler for message processing
        registerEventHandler(channelName, eventName, persistToDatabase);

        // Setup Socket.IO event listener
        socket.on(eventName, args -> {
            if (args.length > 0) {
                Object rawData = args[0];
                logger.debug("📨 Raw Socket.IO data: {}", rawData.toString().length() > 200 ? rawData.toString().substring(0, 200) + "..." : rawData);

                // Extract the actual data from Socket.IO message format for display
                Object actualData = extractDataFromSocketIOMessage(rawData);

                logger.debug("✅ Received {} event from channel '{}': {}", eventName, channelName,
                           actualData.toString().length() > 200 ? actualData.toString().substring(0, 200) + "..." : actualData);

                // Store extracted data in memory for backward compatibility
                storeMessage(eventName, actualData);

                // Call registered event handler for persistence - pass rawData for proper parsing
                String handlerKey = channelName + ":" + eventName;
                EventHandler handler = eventHandlers.get(handlerKey);
                if (handler != null) {
                    // Pass rawData instead of actualData so persistence service gets the full Socket.IO format
                    handler.handle(rawData, channelName, persistToDatabase);
                }
            }
        });

        // Send join message (public channels don't need authentication)
        Map<String, String> joinData = new HashMap<>();
        joinData.put("channelName", channelName);

        socket.emit(WebSocketChannels.ACTION_JOIN, joinData);

        logger.info("✅ Joined public channel: '{}'", channelName);
    }

    /**
     * Subscribe to a private channel (requires authentication)
     */
    public void subscribeToPrivateChannel(String channelName, String eventName) {
        subscribeToPrivateChannel(channelName, eventName, true);
    }

    /**
     * Subscribe to a private channel with option to persist data
     */
    public void subscribeToPrivateChannel(String channelName, String eventName, boolean persistToDatabase) {
        if (!isConnected()) {
            logger.warn("Cannot subscribe: Socket.IO not connected");
            return;
        }

        String apiKey = config.getApiKey();
        String apiSecret = config.getApiSecret();

        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            logger.error("Cannot subscribe to private channel: API credentials not configured");
            return;
        }

        logger.info("🔐 Subscribing to PRIVATE channel: '{}' for event: '{}' (persist={})", channelName, eventName, persistToDatabase);

        // Track subscription
        subscribedChannels.put(channelName, eventName);

        // Register event handler for message processing
        registerEventHandler(channelName, eventName, persistToDatabase);

        // Setup Socket.IO event listener
        socket.on(eventName, args -> {
            if (args.length > 0) {
                Object rawData = args[0];
                logger.debug("📨 Raw Socket.IO data: {}", rawData.toString().length() > 200 ? rawData.toString().substring(0, 200) + "..." : rawData);

                // Extract the actual data from Socket.IO message format for display
                Object actualData = extractDataFromSocketIOMessage(rawData);

                logger.debug("✅ Received {} event from channel '{}': {}", eventName, channelName,
                           actualData.toString().length() > 200 ? actualData.toString().substring(0, 200) + "..." : actualData);

                // Store extracted data in memory for backward compatibility
                storeMessage(eventName, actualData);

                // Call registered event handler for persistence - pass rawData for proper parsing
                String handlerKey = channelName + ":" + eventName;
                EventHandler handler = eventHandlers.get(handlerKey);
                if (handler != null) {
                    // Pass rawData instead of actualData so persistence service gets the full Socket.IO format
                    handler.handle(rawData, channelName, persistToDatabase);
                }
            }
        });

        // Generate authentication signature
        String signature = generateSignature(channelName, apiSecret);

        if (signature != null && signature.length() > 10) {
            logger.debug("Generated signature for channel '{}': {}...", channelName, signature.substring(0, 10));
        }

        // Send join message with authentication
        Map<String, String> joinData = new HashMap<>();
        joinData.put("channelName", channelName);
        joinData.put("authSignature", signature != null ? signature : "");
        joinData.put("apiKey", apiKey);

        socket.emit(WebSocketChannels.ACTION_JOIN, joinData);

        logger.info("✅ Joined private channel: '{}'", channelName);
    }

    /**
     * Register event handler for a channel and event combination
     */
    private void registerEventHandler(String channelName, String eventName, boolean persistToDatabase) {
        String handlerKey = channelName + ":" + eventName;

        EventHandler handler = (data, channel, persist) -> {
            logger.debug("💾 Processing event '{}' from channel '{}', data type: {}", eventName, channel, data.getClass().getSimpleName());

            if (!persist || !persistToDatabase) {
                logger.debug("Skipping persistence for event: {}", eventName);
                return;
            }

            // Persist to database based on event type
            try {
                if (WebSocketChannels.EVENT_CANDLESTICK.equals(eventName)) {
                    if (channel != null && channel.contains("-futures")) {
                        persistenceService.saveFuturesCandlestickData(data, channel);
                    } else {
                        persistenceService.saveSpotCandlestickData(data);
                    }
                } else if (WebSocketChannels.EVENT_DEPTH_UPDATE.equals(eventName)) {
                    if (channel != null && channel.endsWith("-futures")) {
                        persistenceService.saveFuturesOrderbookData(data);
                    } else {
                        persistenceService.saveSpotDepthUpdateData(data, channel);
                    }
                } else if (WebSocketChannels.EVENT_DEPTH_SNAPSHOT.equals(eventName)) {
                    if (channel != null && channel.endsWith("-futures")) {
                        persistenceService.saveFuturesOrderbookData(data);
                    } else {
                        persistenceService.saveSpotDepthSnapshotData(data, channel);
                    }
                } else if (WebSocketChannels.EVENT_NEW_TRADE.equals(eventName)) {
                    if (channel != null && channel.endsWith("@trades-futures")) {
                        persistenceService.saveFuturesNewTradeData(data, channel);
                    } else {
                        persistenceService.saveSpotNewTradeData(data, channel);
                    }
                } else if (WebSocketChannels.EVENT_FUTURES_CURRENT_PRICES_UPDATE.equals(eventName)) {
                    persistenceService.saveFuturesCurrentPricesData(data);
                } else if (WebSocketChannels.EVENT_CURRENT_PRICES_UPDATE.equals(eventName)) {
                    persistenceService.saveSpotCurrentPriceData(data, channel);
                } else if (WebSocketChannels.EVENT_PRICE_STATS_UPDATE.equals(eventName)) {
                    persistenceService.saveSpotPriceStatsData(data, channel);
                } else if (WebSocketChannels.EVENT_PRICE_CHANGE.equals(eventName)) {
                    persistenceService.saveSpotPriceChangeData(data, channel);
                } else if (WebSocketChannels.EVENT_BALANCE_UPDATE.equals(eventName)) {
                    persistenceService.saveSpotBalanceData(data);
                } else if (WebSocketChannels.EVENT_ORDER_UPDATE.equals(eventName)) {
                    persistenceService.saveSpotOrderUpdateData(data);
                } else if (WebSocketChannels.EVENT_TRADE_UPDATE.equals(eventName)) {
                    persistenceService.saveSpotTradeUpdateData(data);
                } else if (WebSocketChannels.EVENT_FUTURES_POSITION_UPDATE.equals(eventName)) {
                    persistenceService.saveFuturesPositionUpdateData(data, channel);
                } else if (WebSocketChannels.EVENT_FUTURES_ORDER_UPDATE.equals(eventName)) {
                    persistenceService.saveFuturesOrderUpdateData(data, channel);
                } else {
                    // Generic fallback
                    String marketPair = extractMarketPair(channel);
                    boolean isFutures = isFuturesMarket(marketPair);

                    if (isFutures) {
                        persistenceService.saveFuturesData(marketPair, eventName, channel, data);
                    } else {
                        persistenceService.saveSpotData(marketPair, eventName, channel, data);
                    }
                }
            } catch (Exception e) {
                logger.error("Error persisting data for event '{}': {}", eventName, e.getMessage(), e);
            }
        };

        eventHandlers.put(handlerKey, handler);
        logger.debug("Registered event handler for key: {}", handlerKey);
    }

    /**
     * Unsubscribe from a channel
     */
    public void unsubscribeFromChannel(String channelName) {
        if (!isConnected()) {
            logger.warn("Cannot unsubscribe: Socket.IO not connected");
            return;
        }

        logger.info("Unsubscribing from channel: {}", channelName);

        Map<String, String> leaveData = new HashMap<>();
        leaveData.put("channelName", channelName);

        socket.emit(WebSocketChannels.ACTION_LEAVE, leaveData);

        // Remove from subscribed channels tracking
        subscribedChannels.remove(channelName);

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
     * Check if Socket.IO is connected
     */
    public boolean isConnected() {
        return connected.get() && socket != null && socket.connected();
    }

    /**
     * Get connection status
     */
    public String getConnectionStatus() {
        if (socket == null) {
            return "NOT_INITIALIZED";
        }
        if (socket.connected()) {
            return "CONNECTED";
        }
        return "DISCONNECTED";
    }

    /**
     * Extract actual data from Socket.IO message format
     * Socket.IO sends: {"event":"...", "data":"{...JSON string...}"}
     * We need to parse the inner "data" field which is a JSON string and convert it to a Map
     */
    private Object extractDataFromSocketIOMessage(Object rawData) {
        try {
            // Socket.IO sends data as a Map object: {event="...", data="..."}
            // We need to extract the "data" field directly from the Map

            if (rawData instanceof org.json.JSONObject) {
                // Handle org.json.JSONObject (Socket.IO client library uses this)
                org.json.JSONObject jsonObject = (org.json.JSONObject) rawData;

                if (jsonObject.has("data")) {
                    Object dataValue = jsonObject.get("data");

                    // The "data" value is a JSON string, parse it to a Map
                    if (dataValue instanceof String) {
                        String innerDataString = (String) dataValue;
                        logger.debug("✅ Extracted inner data string from JSONObject (first 100 chars): {}",
                                    innerDataString.length() > 100 ? innerDataString.substring(0, 100) + "..." : innerDataString);

                        // Parse the JSON string to a Map so persistence service can extract fields properly
                        try {
                            JsonElement parsed = JsonParser.parseString(innerDataString);
                            if (parsed.isJsonObject()) {
                                // Convert JsonObject to Map for easier processing
                                Map<String, Object> resultMap = gson.fromJson(parsed, Map.class);
                                logger.debug("✅ Parsed JSON data to Map with {} keys", resultMap.size());
                                return resultMap;
                            } else {
                                logger.debug("⚠️ Parsed data is not a JSON object, returning string");
                                return innerDataString;
                            }
                        } catch (Exception parseEx) {
                            logger.warn("Failed to parse inner data as JSON, returning string: {}", parseEx.getMessage());
                            return innerDataString;
                        }
                    } else {
                        // If data is not a string, it might already be parsed
                        logger.debug("⚠️ Data field in JSONObject is not a string, type: {}", dataValue.getClass());
                        return dataValue;
                    }
                } else {
                    logger.debug("No 'data' field found in JSONObject, returning original");
                    return rawData;
                }
            } else if (rawData instanceof java.util.Map) {
                java.util.Map<?, ?> dataMap = (java.util.Map<?, ?>) rawData;

                if (dataMap.containsKey("data")) {
                    Object dataValue = dataMap.get("data");

                    // The "data" value is a JSON string, parse it to a Map
                    if (dataValue instanceof String) {
                        String innerDataString = (String) dataValue;
                        logger.debug("✅ Extracted inner data string (first 100 chars): {}",
                                    innerDataString.length() > 100 ? innerDataString.substring(0, 100) + "..." : innerDataString);

                        // Parse the JSON string to a Map so persistence service can extract fields properly
                        try {
                            JsonElement parsed = JsonParser.parseString(innerDataString);
                            if (parsed.isJsonObject()) {
                                // Convert JsonObject to Map for easier processing
                                Map<String, Object> resultMap = gson.fromJson(parsed, Map.class);
                                logger.debug("✅ Parsed JSON data to Map with {} keys", resultMap.size());
                                return resultMap;
                            } else {
                                logger.debug("⚠️ Parsed data is not a JSON object, returning string");
                                return innerDataString;
                            }
                        } catch (Exception parseEx) {
                            logger.warn("Failed to parse inner data as JSON, returning string: {}", parseEx.getMessage());
                            return innerDataString;
                        }
                    } else {
                        // If data is not a string, it might already be parsed
                        logger.debug("⚠️ Data field is not a string, type: {}", dataValue.getClass());
                        return dataValue;
                    }
                } else {
                    logger.debug("No 'data' field found in message, returning original");
                    return rawData;
                }
            } else if (rawData instanceof String) {
                // If it's already a string, parse it as JSON
                String rawJson = (String) rawData;
                JsonObject outerJson = JsonParser.parseString(rawJson).getAsJsonObject();

                if (outerJson.has("data")) {
                    String innerDataString = outerJson.get("data").getAsString();
                    logger.debug("✅ Extracted inner data string from JSON (first 100 chars): {}",
                                innerDataString.length() > 100 ? innerDataString.substring(0, 100) + "..." : innerDataString);

                    // Parse the JSON string to a Map
                    try {
                        JsonElement parsed = JsonParser.parseString(innerDataString);
                        if (parsed.isJsonObject()) {
                            Map<String, Object> resultMap = gson.fromJson(parsed, Map.class);
                            logger.debug("✅ Parsed JSON data to Map with {} keys", resultMap.size());
                            return resultMap;
                        } else {
                            return innerDataString;
                        }
                    } catch (Exception parseEx) {
                        logger.warn("Failed to parse inner data as JSON: {}", parseEx.getMessage());
                        return innerDataString;
                    }
                } else {
                    logger.debug("⚠️ No 'data' field found in JSON string, returning original");
                    return rawData;
                }
            } else {
                // Unknown type, return as-is
                logger.warn("⚠️ Unknown data type: {}, returning original", rawData.getClass());
                return rawData;
            }
        } catch (Exception e) {
            logger.error("❌ Error extracting data from Socket.IO message: {}", e.getMessage(), e);
            // Return original data if parsing fails
            return rawData;
        }
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
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(jsonBody.getBytes(StandardCharsets.UTF_8));

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

    /**
     * Extract market pair from channel name
     */
    private String extractMarketPair(String channelName) {
        if (channelName == null) {
            return "unknown";
        }

        String cleaned = channelName.split("@")[0];

        if (cleaned.contains("_") && cleaned.matches(".*_\\d+[smhd]$")) {
            int lastUnderscore = cleaned.lastIndexOf('_');
            cleaned = cleaned.substring(0, lastUnderscore);
        }

        return cleaned;
    }

    /**
     * Determine if market pair is a futures market
     */
    private boolean isFuturesMarket(String marketPair) {
        if (marketPair == null) {
            return false;
        }

        return marketPair.contains("PERP") ||
               marketPair.endsWith("_USDT") ||
               marketPair.matches(".*\\d{6}$");
    }

    /**
     * Get storage statistics
     */
    public String getStorageStats() {
        return persistenceService.getStorageStatistics();
    }

    /**
     * Get list of subscribed channels
     */
    public Map<String, String> getSubscribedChannels() {
        return new HashMap<>(subscribedChannels);
    }

    @PreDestroy
    public void cleanup() {
        logger.info("Shutting down Socket.IO service");

        // Shutdown scheduler
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Close Socket.IO connection
        if (socket != null && socket.connected()) {
            logger.info("Closing Socket.IO connection");
            socket.close();
        }

        connected.set(false);
        logger.info("Socket.IO service shutdown complete");
    }
}

