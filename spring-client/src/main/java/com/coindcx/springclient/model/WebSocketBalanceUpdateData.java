package com.coindcx.springclient.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "websocket_balance_update_data", indexes = {
    @Index(name = "idx_balance_id", columnList = "balanceId"),
    @Index(name = "idx_currency_short_name", columnList = "currencyShortName"),
    @Index(name = "idx_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_balance_id_timestamp", columnList = "balanceId,recordTimestamp"),
    @Index(name = "idx_currency_timestamp", columnList = "currencyShortName,recordTimestamp")
})
public class WebSocketBalanceUpdateData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String balanceId; // from "id" field
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal balance;
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal lockedBalance;
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal availableBalance; // calculated: balance - lockedBalance
    
    @Column(nullable = false, length = 100)
    private String currencyId;
    
    @Column(nullable = false, length = 20)
    private String currencyShortName;
    
    @Column(nullable = false, length = 50)
    private String channelName;
    
    @Column(columnDefinition = "TEXT")
    private String rawData;
    
    @Column(nullable = false)
    private LocalDateTime recordTimestamp;
    
    public WebSocketBalanceUpdateData() {
        this.recordTimestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBalanceId() {
        return balanceId;
    }
    
    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public BigDecimal getLockedBalance() {
        return lockedBalance;
    }
    
    public void setLockedBalance(BigDecimal lockedBalance) {
        this.lockedBalance = lockedBalance;
    }
    
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
    
    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }
    
    public String getCurrencyId() {
        return currencyId;
    }
    
    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
    
    public String getCurrencyShortName() {
        return currencyShortName;
    }
    
    public void setCurrencyShortName(String currencyShortName) {
        this.currencyShortName = currencyShortName;
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
