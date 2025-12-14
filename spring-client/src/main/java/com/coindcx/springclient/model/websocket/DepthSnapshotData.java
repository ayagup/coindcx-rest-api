package com.coindcx.springclient.model.websocket;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Model for orderbook depth snapshot from WebSocket
 */
public class DepthSnapshotData {
    
    @SerializedName("ts")
    private Long timestamp;
    
    @SerializedName("vs")
    private Long version;
    
    @SerializedName("asks")
    private Map<String, String> asks; // price -> quantity
    
    @SerializedName("bids")
    private Map<String, String> bids; // price -> quantity
    
    @SerializedName("pr")
    private String product;
    
    @SerializedName("s")
    private String symbol;

    // Getters and Setters
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
    public Map<String, String> getAsks() { return asks; }
    public void setAsks(Map<String, String> asks) { this.asks = asks; }
    
    public Map<String, String> getBids() { return bids; }
    public void setBids(Map<String, String> bids) { this.bids = bids; }
    
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
}
