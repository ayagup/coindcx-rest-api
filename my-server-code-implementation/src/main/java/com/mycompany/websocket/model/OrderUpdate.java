package com.mycompany.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Order Update WebSocket event model
 * Event: order-update
 * Channel: coindcx (Private)
 */
public class OrderUpdate {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("client_order_id")
    private String clientOrderId;
    
    @JsonProperty("order_type")
    private String orderType;
    
    @JsonProperty("side")
    private String side;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("fee_amount")
    private Double feeAmount;
    
    @JsonProperty("fee")
    private Double fee;
    
    @JsonProperty("maker_fee")
    private Double makerFee;
    
    @JsonProperty("taker_fee")
    private Double takerFee;
    
    @JsonProperty("total_quantity")
    private Double totalQuantity;
    
    @JsonProperty("remaining_quantity")
    private Double remainingQuantity;
    
    @JsonProperty("source")
    private String source;
    
    @JsonProperty("base_currency_name")
    private String baseCurrencyName;
    
    @JsonProperty("target_currency_name")
    private String targetCurrencyName;
    
    @JsonProperty("base_currency_short_name")
    private String baseCurrencyShortName;
    
    @JsonProperty("target_currency_short_name")
    private String targetCurrencyShortName;
    
    @JsonProperty("base_currency_precision")
    private Integer baseCurrencyPrecision;
    
    @JsonProperty("target_currency_precision")
    private Integer targetCurrencyPrecision;
    
    @JsonProperty("avg_price")
    private Double avgPrice;
    
    @JsonProperty("price_per_unit")
    private Double pricePerUnit;
    
    @JsonProperty("stop_price")
    private Double stopPrice;
    
    @JsonProperty("market")
    private String market;
    
    @JsonProperty("time_in_force")
    private String timeInForce;
    
    @JsonProperty("created_at")
    private Long createdAt;
    
    @JsonProperty("updated_at")
    private Long updatedAt;

    // Constructors, getters, setters
    public OrderUpdate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getMakerFee() {
        return makerFee;
    }

    public void setMakerFee(Double makerFee) {
        this.makerFee = makerFee;
    }

    public Double getTakerFee() {
        return takerFee;
    }

    public void setTakerFee(Double takerFee) {
        this.takerFee = takerFee;
    }

    public Double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Double remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {
        this.baseCurrencyName = baseCurrencyName;
    }

    public String getTargetCurrencyName() {
        return targetCurrencyName;
    }

    public void setTargetCurrencyName(String targetCurrencyName) {
        this.targetCurrencyName = targetCurrencyName;
    }

    public String getBaseCurrencyShortName() {
        return baseCurrencyShortName;
    }

    public void setBaseCurrencyShortName(String baseCurrencyShortName) {
        this.baseCurrencyShortName = baseCurrencyShortName;
    }

    public String getTargetCurrencyShortName() {
        return targetCurrencyShortName;
    }

    public void setTargetCurrencyShortName(String targetCurrencyShortName) {
        this.targetCurrencyShortName = targetCurrencyShortName;
    }

    public Integer getBaseCurrencyPrecision() {
        return baseCurrencyPrecision;
    }

    public void setBaseCurrencyPrecision(Integer baseCurrencyPrecision) {
        this.baseCurrencyPrecision = baseCurrencyPrecision;
    }

    public Integer getTargetCurrencyPrecision() {
        return targetCurrencyPrecision;
    }

    public void setTargetCurrencyPrecision(Integer targetCurrencyPrecision) {
        this.targetCurrencyPrecision = targetCurrencyPrecision;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(Double stopPrice) {
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

    @Override
    public String toString() {
        return "OrderUpdate{" +
                "id='" + id + '\'' +
                ", clientOrderId='" + clientOrderId + '\'' +
                ", orderType='" + orderType + '\'' +
                ", side='" + side + '\'' +
                ", status='" + status + '\'' +
                ", market='" + market + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", remainingQuantity=" + remainingQuantity +
                ", avgPrice=" + avgPrice +
                ", pricePerUnit=" + pricePerUnit +
                '}';
    }
}
