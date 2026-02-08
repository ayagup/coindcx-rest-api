import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CoinDCXWebSocketClient {
    
    private static final String SOCKET_ENDPOINT = "wss://stream.coindcx.com";
    private static final String API_KEY = "8fb0800e8afad00a68f3d54fd48d73f82c09718b1b496f4e";
    private static final String API_SECRET = "21b43bc57a67a4ff05f3b0def38d6b3385d894cac1ae7e089d0c54a4c7f85ee1";
    
    private Socket socket;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SHORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // Known event types for catch-all filtering
    private static final Set<String> KNOWN_EVENTS = new HashSet<>(Arrays.asList(
        "balance-update", "order-update", "trade-update",
        "candlestick", "new-trade", "price-change", "depth-update",
        "currentPrices@spot#update"
    ));
    
    public CoinDCXWebSocketClient() throws URISyntaxException {
        // Configure Socket.IO client with WebSocket transport only
        IO.Options options = IO.Options.builder()
            .setTransports(new String[] {"websocket"})
            .setReconnection(true)
            .setReconnectionDelay(1000)
            .setReconnectionDelayMax(5000)
            .setForceNew(false)
            .build();
        
        socket = IO.socket(SOCKET_ENDPOINT, options);
        setupEventHandlers();
    }
    
    /**
     * Create HMAC-SHA256 signature for authenticated channel subscription
     */
    private String createSignature(String channelName) {
        try {
            JSONObject body = new JSONObject();
            body.put("channel", channelName);
            String jsonBody = body.toString();
            
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                API_SECRET.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
            );
            sha256Hmac.init(secretKey);
            
            byte[] hash = sha256Hmac.doFinal(jsonBody.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating signature", e);
        }
    }
    
    /**
     * Convert byte array to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * Join authenticated channel
     */
    private void joinAuthChannel(String channelName) {
        String signature = createSignature(channelName);
        
        JSONObject joinData = new JSONObject();
        joinData.put("channelName", channelName);
        joinData.put("authSignature", signature);
        joinData.put("apiKey", API_KEY);
        
        System.out.println("\n📡 Joining channel: " + channelName);
        System.out.println("   Signature: " + signature.substring(0, 16) + "...");
        socket.emit("join", joinData);
    }
    
    /**
     * Join public channel (no authentication)
     */
    private void joinPublicChannel(String channelName) {
        JSONObject joinData = new JSONObject();
        joinData.put("channelName", channelName);
        socket.emit("join", joinData);
    }
    
    /**
     * Setup all event handlers
     */
    private void setupEventHandlers() {
        
        // Connection event
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("✅ Connected to CoinDCX WebSocket!");
                LocalDateTime now = LocalDateTime.now();
                System.out.println("   Time: " + now.format(TIME_FORMATTER));
                System.out.println("=".repeat(80));
                
                // Subscribe to PRIVATE channel (commented out like in Python)
                System.out.println("🔐 Subscribing to PRIVATE channel: coindcx");
                // joinAuthChannel("coindcx");
                
                // Subscribe to PUBLIC channels
                System.out.println("📊 Subscribing to PUBLIC channels:");
                
                System.out.println("   1. B-BTC_USDT_1m (candlestick)");
                // joinAuthChannel("B-BTC_USDT_1m-futures");
                // joinPublicChannel("B-BTC_USDT_1m");
                
                System.out.println("   2. B-BTC_USDT@trades (new-trade)");
                // joinAuthChannel("B-BTC_USDT@trades-futures");
                
                System.out.println("   3. B-BTC_USDT@orderbook@20 (depth-update)");
                // joinAuthChannel("B-BTC_USDT@orderbook@20-futures");
                
                System.out.println("   4. currentPrices@spot@10s (current prices)");
                joinAuthChannel("currentPrices@futures@rt");
                // joinPublicChannel("currentPrices@spot@10s");
                
                System.out.println("\n⏳ Waiting for events...");
                System.out.println("=".repeat(80));
            }
        });
        
        // Disconnection event
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("\n❌ Disconnected from CoinDCX WebSocket");
            }
        });
        
        // Connection error event
        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("❌ Connection error: " + (args.length > 0 ? args[0] : "Unknown"));
            }
        });
        
        // PRIVATE CHANNEL EVENTS
        
        socket.on("balance-update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("💳 BALANCE UPDATE", args);
            }
        });
        
        socket.on("order-update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("📝 ORDER UPDATE", args);
            }
        });
        
        socket.on("trade-update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("🔄 TRADE UPDATE", args);
            }
        });
        
        // PUBLIC CHANNEL EVENTS
        
        socket.on("candlestick", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("🕯️  CANDLESTICK", args);
            }
        });
        
        socket.on("depth-update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("📊 DEPTH UPDATE", args);
            }
        });
        
        socket.on("new-trade", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("💱 NEW TRADE", args);
            }
        });
        
        socket.on("currentPrices@spot#update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("💰 CURRENT PRICES", args);
            }
        });
        
        socket.on("price-change", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("💵 PRICE CHANGE", args);
            }
        });
        
        // Additional event handlers for debugging and futures
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("📨 MESSAGE", args);
            }
        });
        
        socket.on("joined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("✅ JOINED CHANNEL", args);
            }
        });
        
        socket.on("error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("❌ ERROR", args);
            }
        });
        
        socket.on("subscription-error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("❌ SUBSCRIPTION ERROR", args);
            }
        });
        
        // Additional futures-specific events
        socket.on("ticker", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("📈 TICKER", args);
            }
        });
        
        socket.on("update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("🔄 UPDATE", args);
            }
        });
        
        socket.on("data", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleEvent("📊 DATA", args);
            }
        });
    }
    
    /**
     * Generic event handler for formatting output
     */
    private void handleEvent(String eventName, Object... args) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n" + eventName + " - " + now.format(SHORT_TIME_FORMATTER));
        
        if (args.length > 0) {
            try {
                if (args[0] instanceof JSONObject) {
                    JSONObject response = (JSONObject) args[0];
                    
                    // Check if there's a nested "data" field with JSON string
                    if (response.has("data")) {
                        String dataStr = response.optString("data");
                        if (dataStr != null && dataStr.startsWith("{")) {
                            // Parse the nested JSON string
                            try {
                                JSONObject nestedData = new JSONObject(dataStr);
                                System.out.println("Data: " + nestedData.toString(2));
                            } catch (Exception e) {
                                // If parsing fails, show the original
                                System.out.println("Data: " + response.toString(2));
                            }
                        } else {
                            System.out.println("Data: " + response.toString(2));
                        }
                    } else {
                        System.out.println("Data: " + response.toString(2));
                    }
                } else {
                    System.out.println("Data: " + args[0]);
                }
            } catch (Exception e) {
                System.out.println("Data: " + args[0]);
            }
        }
        
        System.out.println("-".repeat(80));
    }
    
    /**
     * Connect to the WebSocket
     */
    public void connect() {
        System.out.println("🔌 Connecting to " + SOCKET_ENDPOINT + "...");
        socket.connect();
    }
    
    /**
     * Disconnect from the WebSocket
     */
    public void disconnect() {
        System.out.println("\n🛑 Stopping...");
        socket.disconnect();
        System.out.println("✅ Disconnected successfully");
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return socket.connected();
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        System.out.println("Starting CoinDCX WebSocket Client...");
        System.out.flush();
        
        try {
            CoinDCXWebSocketClient client = new CoinDCXWebSocketClient();
            
            // Add shutdown hook for graceful disconnection
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (client.isConnected()) {
                    client.disconnect();
                }
            }));
            
            // Connect
            client.connect();
            
            System.out.println("\n💡 Press Ctrl+C to stop\n");
            
            // Keep the application running
            Thread.currentThread().join();
            
        } catch (URISyntaxException e) {
            System.err.println("❌ Invalid socket endpoint URI: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("\n🛑 Application interrupted");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
