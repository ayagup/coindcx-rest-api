package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to store spot trade update data from WebSocket
 */
@Entity
@Table(name = "websocket_spot_trade_update_data", indexes = {
    @Index(name = "idx_spot_trade_order_id", columnList = "order_id"),
    @Index(name = "idx_spot_trade_id", columnList = "trade_id"),
    @Index(name = "idx_spot_trade_timestamp", columnList = "timestamp"),
    @Index(name = "idx_spot_trade_symbol", columnList = "symbol"),
    @Index(name = "idx_spot_trade_client_order_id", columnList = "client_order_id"),
    @Index(name = "idx_spot_trade_exchange_timestamp", columnList = "exchange_timestamp")
})
public class WebSocketSpotTradeUpdateData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;
    
    @Column(name = "client_order_id", length = 100)
    private String clientOrderId;
    
    @Column(name = "trade_id", nullable = false, length = 100)
    private String tradeId;
    
    @Column(name = "symbol", nullable = false, length = 50)
    private String symbol;
    
    @Column(name = "price", precision = 30, scale = 10)
    private String price;
    
    @Column(name = "quantity", precision = 30, scale = 10)
    private String quantity;
    
    @Column(name = "exchange_timestamp")
    private Long exchangeTimestamp;
    
    @Column(name = "is_buyer_maker")
    private Boolean isBuyerMaker;
    
    @Column(name = "fee_amount", precision = 30, scale = 10)
    private String feeAmount;
    
    @Column(name = "exchange_identifier", length = 50)
    private String exchangeIdentifier;
    
    @Column(name = "status", length = 50)
    private String status;
    
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Constructors
    public WebSocketSpotTradeUpdateData() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getClientOrderId() {
        return clientOrderId;
    }
    
    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }
    
    public String getTradeId() {
        return tradeId;
    }
    
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public Long getExchangeTimestamp() {
        return exchangeTimestamp;
    }
    
    public void setExchangeTimestamp(Long exchangeTimestamp) {
        this.exchangeTimestamp = exchangeTimestamp;
    }
    
    public Boolean getIsBuyerMaker() {
        return isBuyerMaker;
    }
    
    public void setIsBuyerMaker(Boolean isBuyerMaker) {
        this.isBuyerMaker = isBuyerMaker;
    }
    
    public String getFeeAmount() {
        return feeAmount;
    }
    
    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }
    
    public String getExchangeIdentifier() {
        return exchangeIdentifier;
    }
    
    public void setExchangeIdentifier(String exchangeIdentifier) {
        this.exchangeIdentifier = exchangeIdentifier;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
}
