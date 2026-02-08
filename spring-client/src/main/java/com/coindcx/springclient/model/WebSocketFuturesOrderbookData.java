package com.coindcx.springclient.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for storing futures orderbook snapshot data
 * Channel: [instrument_name]@orderbook@{depth}-futures
 * Event: depth-snapshot
 */
@Entity
@Table(name = "websocket_futures_orderbook_data", indexes = {
    @Index(name = "idx_futures_orderbook_timestamp", columnList = "timestamp"),
    @Index(name = "idx_futures_orderbook_channel", columnList = "channelName"),
    @Index(name = "idx_futures_orderbook_record_ts", columnList = "recordTimestamp"),
    @Index(name = "idx_futures_orderbook_vs", columnList = "versionSequence"),
    @Index(name = "idx_futures_orderbook_channel_ts", columnList = "channelName, timestamp")
})
public class WebSocketFuturesOrderbookData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Timestamp from response (ts field)
     */
    @Column(name = "timestamp")
    private Long timestamp;
    
    /**
     * Version sequence from response (vs field)
     */
    @Column(name = "version_sequence")
    private Long versionSequence;
    
    /**
     * Asks data (price -> quantity mapping) stored as JSON
     */
    @Column(name = "asks", columnDefinition = "TEXT")
    private String asks;
    
    /**
     * Bids data (price -> quantity mapping) stored as JSON
     */
    @Column(name = "bids", columnDefinition = "TEXT")
    private String bids;
    
    /**
     * Product type (pr field - should be "futures")
     */
    @Column(name = "product_type", length = 50)
    private String productType;
    
    /**
     * Channel name (e.g., "B-ID_USDT@orderbook@50-futures")
     */
    @Column(name = "channel_name", length = 200)
    private String channelName;
    
    /**
     * Orderbook depth (10, 20, or 50)
     */
    @Column(name = "depth")
    private Integer depth;
    
    /**
     * Instrument name extracted from channel (e.g., "B-ID_USDT")
     */
    @Column(name = "instrument_name", length = 100)
    private String instrumentName;
    
    /**
     * Raw JSON data
     */
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
    
    /**
     * Record timestamp (when this record was created)
     */
    @Column(name = "record_timestamp", nullable = false)
    private LocalDateTime recordTimestamp;

    // Constructors
    public WebSocketFuturesOrderbookData() {
        this.recordTimestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getVersionSequence() {
        return versionSequence;
    }

    public void setVersionSequence(Long versionSequence) {
        this.versionSequence = versionSequence;
    }

    public String getAsks() {
        return asks;
    }

    public void setAsks(String asks) {
        this.asks = asks;
    }

    public String getBids() {
        return bids;
    }

    public void setBids(String bids) {
        this.bids = bids;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
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
        return "WebSocketFuturesOrderbookData{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", versionSequence=" + versionSequence +
                ", channelName='" + channelName + '\'' +
                ", instrumentName='" + instrumentName + '\'' +
                ", depth=" + depth +
                ", productType='" + productType + '\'' +
                ", recordTimestamp=" + recordTimestamp +
                '}';
    }
}
