package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for storing individual futures instrument prices
 * Parsed from the prices object in currentPrices@futures@rt channel
 * 
 * Each record represents a single instrument's price data:
 * {
 *   "mp": 0.22990213,           // mark price (optional)
 *   "bmST": 1770541732000,      // benchmark timestamp
 *   "cmRT": 1770541732148       // calculated mark timestamp
 * }
 */
@Entity
@Table(name = "websocket_futures_instrument_price", indexes = {
    @Index(name = "idx_futures_instrument", columnList = "instrument"),
    @Index(name = "idx_futures_instrument_timestamp", columnList = "instrument, eventTimestamp"),
    @Index(name = "idx_futures_parent_timestamp", columnList = "parentTimestamp"),
    @Index(name = "idx_futures_instrument_record_timestamp", columnList = "recordTimestamp")
})
public class WebSocketFuturesInstrumentPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Instrument symbol (e.g., "B-AVA_USDT", "B-WLD_USDT")
     */
    @Column(nullable = false, length = 50)
    private String instrument;
    
    /**
     * Mark price (mp field - optional, some instruments don't have it)
     */
    private Double markPrice;
    
    /**
     * Benchmark timestamp (bmST field)
     */
    private Long benchmarkTimestamp;
    
    /**
     * Calculated mark timestamp (cmRT field)
     */
    private Long calculatedMarkTimestamp;
    
    /**
     * Event timestamp from parent WebSocketFuturesCurrentPricesData record
     */
    @Column(nullable = false)
    private Long eventTimestamp;
    
    /**
     * Parent record's timestamp for reference
     * Links to WebSocketFuturesCurrentPricesData.timestamp
     */
    @Column(nullable = false)
    private Long parentTimestamp;
    
    /**
     * Version sequence from parent record
     */
    private Long versionSequence;
    
    /**
     * Product type from parent record (should be "futures")
     */
    @Column(length = 50)
    private String productType;
    
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
    
    public String getInstrument() {
        return instrument;
    }
    
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }
    
    public Double getMarkPrice() {
        return markPrice;
    }
    
    public void setMarkPrice(Double markPrice) {
        this.markPrice = markPrice;
    }
    
    public Long getBenchmarkTimestamp() {
        return benchmarkTimestamp;
    }
    
    public void setBenchmarkTimestamp(Long benchmarkTimestamp) {
        this.benchmarkTimestamp = benchmarkTimestamp;
    }
    
    public Long getCalculatedMarkTimestamp() {
        return calculatedMarkTimestamp;
    }
    
    public void setCalculatedMarkTimestamp(Long calculatedMarkTimestamp) {
        this.calculatedMarkTimestamp = calculatedMarkTimestamp;
    }
    
    public Long getEventTimestamp() {
        return eventTimestamp;
    }
    
    public void setEventTimestamp(Long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
    
    public Long getParentTimestamp() {
        return parentTimestamp;
    }
    
    public void setParentTimestamp(Long parentTimestamp) {
        this.parentTimestamp = parentTimestamp;
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
    
    public LocalDateTime getRecordTimestamp() {
        return recordTimestamp;
    }
    
    public void setRecordTimestamp(LocalDateTime recordTimestamp) {
        this.recordTimestamp = recordTimestamp;
    }
}
