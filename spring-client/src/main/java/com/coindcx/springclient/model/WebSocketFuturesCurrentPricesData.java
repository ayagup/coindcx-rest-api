package com.coindcx.springclient.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for storing futures current prices data
 * Channel: currentPrices@futures@rt
 * Event: currentPrices@futures#update
 * 
 * Sample Response:
 * {
 *   "vs":29358821,
 *   "ts":1707384027242,
 *   "pr":"futures",
 *   "pST":1707384027230,
 *   "prices":{
 *     "B-UNI_USDT":{"bmST":1707384027000,"cmRT":1707384027149},
 *     "B-LDO_USDT":{"mp":2.87559482,"bmST":1707384027000,"cmRT":1707384027149}
 *   }
 * }
 */
@Entity
@Table(name = "websocket_futures_current_prices_data", indexes = {
    @Index(name = "idx_futures_current_prices_timestamp", columnList = "timestamp"),
    @Index(name = "idx_futures_current_prices_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_futures_current_prices_version_sequence", columnList = "versionSequence"),
    @Index(name = "idx_futures_current_prices_product_type", columnList = "productType")
})
public class WebSocketFuturesCurrentPricesData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Event timestamp from the exchange (ts field)
     */
    @Column(nullable = false)
    private Long timestamp;
    
    /**
     * Version sequence for tracking updates (vs field)
     */
    @Column(name = "versionSequence")
    private Long versionSequence;
    
    /**
     * Product type (pr field - should be "futures")
     */
    @Column(length = 50)
    private String productType;
    
    /**
     * Prices sent timestamp (pST field)
     */
    @Column(name = "pricesSentTimestamp")
    private Long pricesSentTimestamp;
    
    /**
     * Prices data as JSON string
     * Map of instrument -> {mp: mark_price, bmST: benchmark_timestamp, cmRT: calculated_mark_timestamp}
     * Example: {"B-UNI_USDT":{"bmST":1707384027000,"cmRT":1707384027149},"B-LDO_USDT":{"mp":2.87559482,...}}
     */
    @Column(columnDefinition = "TEXT")
    private String prices;
    
    /**
     * Complete raw JSON data
     */
    @Column(columnDefinition = "TEXT")
    private String rawData;
    
    /**
     * Record creation timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime recordTimestamp;
    
    @PrePersist
    protected void onCreate() {
        recordTimestamp = LocalDateTime.now();
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
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public Long getPricesSentTimestamp() {
        return pricesSentTimestamp;
    }
    
    public void setPricesSentTimestamp(Long pricesSentTimestamp) {
        this.pricesSentTimestamp = pricesSentTimestamp;
    }
    
    public String getPrices() {
        return prices;
    }
    
    public void setPrices(String prices) {
        this.prices = prices;
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
}
