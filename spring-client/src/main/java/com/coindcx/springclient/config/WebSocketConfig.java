package com.coindcx.springclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for CoinDCX WebSocket connection
 */
@Configuration
public class WebSocketConfig {
    
    @Value("${coindcx.websocket.endpoint:wss://stream.coindcx.com}")
    private String websocketEndpoint;
    
    @Value("${coindcx.api.key:}")
    private String apiKey;
    
    @Value("${coindcx.api.secret:}")
    private String apiSecret;
    
    public String getWebsocketEndpoint() {
        return websocketEndpoint;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getApiSecret() {
        return apiSecret;
    }
}
