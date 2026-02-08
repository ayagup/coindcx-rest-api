package com.coindcx.springclient.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for storing futures new trade data
 * Channel: [instrument_name]@trades-futures
 * Event: new-trade
 * 
 * Sample Response:
 * {
 *   "T":1705516361108,
 *   "RT":1705516416271.6133,
 *   "p":"0.3473",
 *   "q":"40",
 *   "m":1,
 *   "s":"B-ID_USDT",
 *   "pr":"f"
 * }
 */
@Entity
@Table(name = "websocket_futures_new_trade_data", indexes = {
    @Index(name = "idx_futures_new_trade_timestamp", columnList = "tradeTimestamp"),
    @Index(name = "idx_futures_new_trade_symbol", columnList = "symbol"),
    @Index(name = "idx_futures_new_trade_channel", columnList = "channelName"),
    @Index(name = "idx_futures_new_trade_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_futures_new_trade_buyer_maker", columnList = "isBuyerMaker"),
    @Index(name = "idx_futures_new_trade_symbol_timestamp", columnList = "symbol, tradeTimestamp")
})
public class WebSocketFuturesNewTradeData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Trade timestamp from the exchange (T field)
     */
    @Column(nullable = false)
    private Long tradeTimestamp;
    
    /**
     * Received timestamp (RT field)
     */
    @Column(precision = 20, scale = 4)
    private BigDecimal receivedTimestamp;
    
    /**
     * Trade price (p field)
     */
    @Column(precision = 30, scale = 10)
    private BigDecimal price;
    
    /**
     * Trade quantity (q field)
     */
    @Column(precision = 30, scale = 10)
    private BigDecimal quantity;
    
    /**
     * Is buyer maker flag (m field)
     * 1 = buyer is maker (sell trade), 0 = buyer is taker (buy trade)
     */
    private Integer isBuyerMaker;
    
    /**
     * Trading symbol/instrument (s field)
     * Example: B-ID_USDT
     */
    @Column(length = 50)
    private String symbol;
    
    /**
     * Product type (pr field)
     * "f" for futures
     */
    @Column(length = 10)
    private String productType;
    
    /**
     * Channel name
     * Format: [instrument_name]@trades-futures
     */
    @Column(length = 100)
    private String channelName;
    
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
    
    public Long getTradeTimestamp() {
        return tradeTimestamp;
    }
    
    public void setTradeTimestamp(Long tradeTimestamp) {
        this.tradeTimestamp = tradeTimestamp;
    }
    
    public BigDecimal getReceivedTimestamp() {
        return receivedTimestamp;
    }
    
    public void setReceivedTimestamp(BigDecimal receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public Integer getIsBuyerMaker() {
        return isBuyerMaker;
    }
    
    public void setIsBuyerMaker(Integer isBuyerMaker) {
        this.isBuyerMaker = isBuyerMaker;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
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
