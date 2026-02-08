package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for WebSocket Spot Price Stats Data
 * Channel: priceStats@spot@{interval} where interval is 60s
 * Event: priceStats@spot#update
 * Description: 24hrs price change of pairs whose price got updated in the last 60s
 */
@Entity
@Table(name = "websocket_spot_price_stats_data", indexes = {
    @Index(name = "idx_version", columnList = "version"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_interval", columnList = "statsInterval"),
    @Index(name = "idx_price_change", columnList = "priceChangePercent"),
    @Index(name = "idx_volume", columnList = "volume24h"),
    @Index(name = "idx_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_interval_timestamp", columnList = "statsInterval,timestamp")
})
public class WebSocketSpotPriceStatsData {

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
     * Price change percentage (24 hours)
     */
    @Column(name = "priceChangePercent")
    private Double priceChangePercent;

    /**
     * Volume in 24 hours
     */
    @Column(name = "volume24h")
    private Double volume24h;

    /**
     * Stats update interval (e.g., 60s) - extracted from channel name
     */
    @Column(name = "statsInterval", length = 10)
    private String statsInterval;

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
    public WebSocketSpotPriceStatsData() {
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

    public Double getPriceChangePercent() {
        return priceChangePercent;
    }

    public void setPriceChangePercent(Double priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }

    public Double getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(Double volume24h) {
        this.volume24h = volume24h;
    }

    public String getStatsInterval() {
        return statsInterval;
    }

    public void setStatsInterval(String statsInterval) {
        this.statsInterval = statsInterval;
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
        return "WebSocketSpotPriceStatsData{" +
                "id=" + id +
                ", version=" + version +
                ", timestamp=" + timestamp +
                ", product='" + product + '\'' +
                ", priceChangePercent=" + priceChangePercent +
                ", volume24h=" + volume24h +
                ", statsInterval='" + statsInterval + '\'' +
                ", channelName='" + channelName + '\'' +
                ", recordTimestamp=" + recordTimestamp +
                '}';
    }
}
