package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to store spot balance update data from WebSocket
 */
@Entity
@Table(name = "websocket_spot_balance_data", indexes = {
    @Index(name = "idx_spot_currency", columnList = "currency"),
    @Index(name = "idx_spot_timestamp", columnList = "timestamp"),
    @Index(name = "idx_spot_user_id", columnList = "user_id")
})
public class WebSocketSpotBalanceData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", length = 100)
    private String userId;
    
    @Column(name = "currency", nullable = false, length = 20)
    private String currency;
    
    @Column(name = "balance", precision = 30, scale = 10)
    private String balance;
    
    @Column(name = "locked_balance", precision = 30, scale = 10)
    private String lockedBalance;
    
    @Column(name = "available_balance", precision = 30, scale = 10)
    private String availableBalance;
    
    @Column(name = "total_balance", precision = 30, scale = 10)
    private String totalBalance;
    
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "exchange_timestamp")
    private Long exchangeTimestamp;
    
    // Constructors
    public WebSocketSpotBalanceData() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getBalance() {
        return balance;
    }
    
    public void setBalance(String balance) {
        this.balance = balance;
    }
    
    public String getLockedBalance() {
        return lockedBalance;
    }
    
    public void setLockedBalance(String lockedBalance) {
        this.lockedBalance = lockedBalance;
    }
    
    public String getAvailableBalance() {
        return availableBalance;
    }
    
    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }
    
    public String getTotalBalance() {
        return totalBalance;
    }
    
    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
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
    
    public Long getExchangeTimestamp() {
        return exchangeTimestamp;
    }
    
    public void setExchangeTimestamp(Long exchangeTimestamp) {
        this.exchangeTimestamp = exchangeTimestamp;
    }
}
