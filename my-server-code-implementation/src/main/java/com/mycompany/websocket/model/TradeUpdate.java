package com.mycompany.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Trade Update WebSocket event model
 * Event: trade-update
 * Channel: coindcx (Private)
 */
public class TradeUpdate {
    
    @JsonProperty("o")
    private String orderId;
    
    @JsonProperty("c")
    private String clientOrderId;
    
    @JsonProperty("t")
    private String tradeId;
    
    @JsonProperty("s")
    private String symbol;
    
    @JsonProperty("p")
    private String price;
    
    @JsonProperty("q")
    private String quantity;
    
    @JsonProperty("T")
    private Double timestamp;
    
    @JsonProperty("m")
    private Boolean isBuyerMaker;
    
    @JsonProperty("f")
    private String feeAmount;
    
    @JsonProperty("e")
    private String exchangeIdentifier;
    
    @JsonProperty("x")
    private String status;

    public TradeUpdate() {
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

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getBuyerMaker() {
        return isBuyerMaker;
    }

    public void setBuyerMaker(Boolean buyerMaker) {
        isBuyerMaker = buyerMaker;
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

    @Override
    public String toString() {
        return "TradeUpdate{" +
                "orderId='" + orderId + '\'' +
                ", clientOrderId='" + clientOrderId + '\'' +
                ", tradeId='" + tradeId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}
