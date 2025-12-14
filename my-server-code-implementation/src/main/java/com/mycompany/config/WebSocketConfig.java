package com.mycompany.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration for CoinDCX API
 * Enables WebSocket support for real-time streaming data
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket endpoints can be registered here if needed
        // Currently using Socket.IO client for external connections
    }
    
    /**
     * Bean for WebSocket connection properties
     */
    @Bean
    public WebSocketProperties webSocketProperties() {
        return new WebSocketProperties();
    }
    
    /**
     * Properties class for WebSocket configuration
     */
    public static class WebSocketProperties {
        private String socketEndpoint = "wss://stream.coindcx.com";
        private int reconnectDelay = 5000; // milliseconds
        private boolean autoReconnect = true;
        
        public String getSocketEndpoint() {
            return socketEndpoint;
        }
        
        public void setSocketEndpoint(String socketEndpoint) {
            this.socketEndpoint = socketEndpoint;
        }
        
        public int getReconnectDelay() {
            return reconnectDelay;
        }
        
        public void setReconnectDelay(int reconnectDelay) {
            this.reconnectDelay = reconnectDelay;
        }
        
        public boolean isAutoReconnect() {
            return autoReconnect;
        }
        
        public void setAutoReconnect(boolean autoReconnect) {
            this.autoReconnect = autoReconnect;
        }
    }
}
