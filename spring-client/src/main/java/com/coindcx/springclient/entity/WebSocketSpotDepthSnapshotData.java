package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to store spot depth snapshot (order book) data from WebSocket
 * Note: bids and asks are stored as JSON strings due to their array nature
 */
@Entity
@Table(name = "websocket_spot_depth_snapshot_data", indexes = {
    @Index(name = "idx_spot_depth_symbol", columnList = "symbol"),
    @Index(name = "idx_spot_depth_version", columnList = "version"),
    @Index(name = "idx_spot_depth_snapshot_timestamp", columnList = "snapshot_timestamp"),
    @Index(name = "idx_spot_depth_timestamp", columnList = "timestamp"),
    @Index(name = "idx_spot_depth_symbol_timestamp", columnList = "symbol, timestamp")
})
public class WebSocketSpotDepthSnapshotData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "version", nullable = false)
    private Long version;
    
    @Column(name = "snapshot_timestamp", nullable = false)
    private Long snapshotTimestamp;
    
    @Column(name = "product", length = 50)
    private String product;
    
    @Column(name = "symbol", nullable = false, length = 50)
    private String symbol;
    
    @Column(name = "bids", columnDefinition = "TEXT")
    private String bids;
    
    @Column(name = "asks", columnDefinition = "TEXT")
    private String asks;
    
    @Column(name = "depth_level")
    private Integer depthLevel;
    
    @Column(name = "channel_name", length = 100)
    private String channelName;
    
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Constructors
    public WebSocketSpotDepthSnapshotData() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    public Long getSnapshotTimestamp() {
        return snapshotTimestamp;
    }
    
    public void setSnapshotTimestamp(Long snapshotTimestamp) {
        this.snapshotTimestamp = snapshotTimestamp;
    }
    
    public String getProduct() {
        return product;
    }
    
    public void setProduct(String product) {
        this.product = product;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getBids() {
        return bids;
    }
    
    public void setBids(String bids) {
        this.bids = bids;
    }
    
    public String getAsks() {
        return asks;
    }
    
    public void setAsks(String asks) {
        this.asks = asks;
    }
    
    public Integer getDepthLevel() {
        return depthLevel;
    }
    
    public void setDepthLevel(Integer depthLevel) {
        this.depthLevel = depthLevel;
    }
    
    public String getChannelName() {
        return channelName;
    }
    
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    
    public String getRawData() {
        return rawData;
    }
    
    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "WebSocketSpotDepthSnapshotData{" +
                "id=" + id +
                ", version=" + version +
                ", symbol='" + symbol + '\'' +
                ", snapshotTimestamp=" + snapshotTimestamp +
                ", depthLevel=" + depthLevel +
                ", timestamp=" + timestamp +
                '}';
    }
}
