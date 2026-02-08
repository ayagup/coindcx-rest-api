package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for WebSocket Spot Current Prices Data
 * Channel: currentPrices@spot@{interval} where interval can be 1s or 10s
 * Event: currentPrices@spot#update
 * Description: Current prices of pairs whose price got updated in the last 1s/10s
 */
@Entity
@Table(name = "websocket_spot_current_price_data", indexes = {
    @Index(name = "idx_version", columnList = "version"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_interval", columnList = "priceInterval"),
    @Index(name = "idx_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_interval_timestamp", columnList = "priceInterval,timestamp")
})
public class WebSocketSpotCurrentPriceData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Version number
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
     * Price update interval (1s or 10s) - extracted from channel name
     */
    @Column(name = "priceInterval", length = 10)
    private String priceInterval;

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
    public WebSocketSpotCurrentPriceData() {
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

    public String getPriceInterval() {
        return priceInterval;
    }

    public void setPriceInterval(String priceInterval) {
        this.priceInterval = priceInterval;
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
        return "WebSocketSpotCurrentPriceData{" +
                "id=" + id +
                ", version=" + version +
                ", timestamp=" + timestamp +
                ", product='" + product + '\'' +
                ", priceInterval='" + priceInterval + '\'' +
                ", channelName='" + channelName + '\'' +
                ", recordTimestamp=" + recordTimestamp +
                '}';
    }
}
