package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to store spot order update data from WebSocket
 */
@Entity
@Table(name = "websocket_spot_order_update_data", indexes = {
    @Index(name = "idx_spot_order_id", columnList = "order_id"),
    @Index(name = "idx_spot_order_timestamp", columnList = "timestamp"),
    @Index(name = "idx_spot_order_status", columnList = "status"),
    @Index(name = "idx_spot_order_market", columnList = "market"),
    @Index(name = "idx_spot_order_client_id", columnList = "client_order_id")
})
public class WebSocketSpotOrderUpdateData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;
    
    @Column(name = "client_order_id", length = 100)
    private String clientOrderId;
    
    @Column(name = "order_type", length = 50)
    private String orderType;
    
    @Column(name = "side", length = 20)
    private String side;
    
    @Column(name = "status", length = 50)
    private String status;
    
    @Column(name = "fee_amount", precision = 30, scale = 10)
    private String feeAmount;
    
    @Column(name = "maker_fee", precision = 10, scale = 6)
    private String makerFee;
    
    @Column(name = "taker_fee", precision = 10, scale = 6)
    private String takerFee;
    
    @Column(name = "total_quantity", precision = 30, scale = 10)
    private String totalQuantity;
    
    @Column(name = "remaining_quantity", precision = 30, scale = 10)
    private String remainingQuantity;
    
    @Column(name = "avg_price", precision = 30, scale = 10)
    private String avgPrice;
    
    @Column(name = "price_per_unit", precision = 30, scale = 10)
    private String pricePerUnit;
    
    @Column(name = "stop_price", precision = 30, scale = 10)
    private String stopPrice;
    
    @Column(name = "market", length = 50)
    private String market;
    
    @Column(name = "time_in_force", length = 50)
    private String timeInForce;
    
    @Column(name = "created_at")
    private Long createdAt;
    
    @Column(name = "updated_at")
    private Long updatedAt;
    
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Constructors
    public WebSocketSpotOrderUpdateData() {
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
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public String getSide() {
        return side;
    }
    
    public void setSide(String side) {
        this.side = side;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getFeeAmount() {
        return feeAmount;
    }
    
    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }
    
    public String getMakerFee() {
        return makerFee;
    }
    
    public void setMakerFee(String makerFee) {
        this.makerFee = makerFee;
    }
    
    public String getTakerFee() {
        return takerFee;
    }
    
    public void setTakerFee(String takerFee) {
        this.takerFee = takerFee;
    }
    
    public String getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public String getRemainingQuantity() {
        return remainingQuantity;
    }
    
    public void setRemainingQuantity(String remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }
    
    public String getAvgPrice() {
        return avgPrice;
    }
    
    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }
    
    public String getPricePerUnit() {
        return pricePerUnit;
    }
    
    public void setPricePerUnit(String pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
    
    public String getStopPrice() {
        return stopPrice;
    }
    
    public void setStopPrice(String stopPrice) {
        this.stopPrice = stopPrice;
    }
    
    public String getMarket() {
        return market;
    }
    
    public void setMarket(String market) {
        this.market = market;
    }
    
    public String getTimeInForce() {
        return timeInForce;
    }
    
    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }
    
    public Long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
    
    public Long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
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
