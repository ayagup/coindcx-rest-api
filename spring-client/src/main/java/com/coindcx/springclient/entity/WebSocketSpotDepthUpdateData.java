package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for WebSocket Spot Depth Update (Order Book Changes) Data
 * Channel: {pair}@orderbook@{depth} where depth can be 10, 20, or 50
 * Event: depth-update
 * Description: Captures changes in the orderbook data when depth changes
 */
@Entity
@Table(name = "websocket_spot_depth_update_data", indexes = {
    @Index(name = "idx_symbol", columnList = "symbol"),
    @Index(name = "idx_version", columnList = "version"),
    @Index(name = "idx_event_time", columnList = "eventTime"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_symbol_timestamp", columnList = "symbol,timestamp"),
    @Index(name = "idx_symbol_event_time", columnList = "symbol,eventTime")
})
public class WebSocketSpotDepthUpdateData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Order book version number
     */
    @Column(name = "version")
    private Long version;

    /**
     * Timestamp from the WebSocket message
     */
    @Column(name = "timestamp")
    private Long timestamp;

    /**
     * Product type
     */
    @Column(name = "product", length = 50)
    private String product;

    /**
     * Event time - when the orderbook change occurred
     */
    @Column(name = "eventTime")
    private Long eventTime;

    /**
     * Trading pair symbol (e.g., "B-BTC_USDT")
     */
    @Column(name = "symbol", length = 50, nullable = false)
    private String symbol;

    /**
     * Depth level (10, 20, or 50) - extracted from channel name
     */
    @Column(name = "depthLevel")
    private Integer depthLevel;

    /**
     * Channel name that produced this data
     */
    @Column(name = "channelName", length = 100)
    private String channelName;

    /**
     * Raw JSON data from WebSocket
     */
    @Column(name = "rawData", columnDefinition = "TEXT")
    private String rawData;

    /**
     * Record creation timestamp
     */
    @Column(name = "recordTimestamp", nullable = false, updatable = false)
    private LocalDateTime recordTimestamp;

    @PrePersist
    protected void onCreate() {
        recordTimestamp = LocalDateTime.now();
    }

    // Constructors
    public WebSocketSpotDepthUpdateData() {
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public LocalDateTime getRecordTimestamp() {
        return recordTimestamp;
    }

    public void setRecordTimestamp(LocalDateTime recordTimestamp) {
        this.recordTimestamp = recordTimestamp;
    }

    @Override
    public String toString() {
        return "WebSocketSpotDepthUpdateData{" +
                "id=" + id +
                ", version=" + version +
                ", timestamp=" + timestamp +
                ", product='" + product + '\'' +
                ", eventTime=" + eventTime +
                ", symbol='" + symbol + '\'' +
                ", depthLevel=" + depthLevel +
                ", channelName='" + channelName + '\'' +
                ", recordTimestamp=" + recordTimestamp +
                '}';
    }
}
