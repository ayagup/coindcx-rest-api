package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to store WebSocket Futures Order Update Data from coindcx channel (df-order-update event)
 */
@Entity
@Table(name = "websocket_futures_order_update_data", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_pair", columnList = "pair"),
    @Index(name = "idx_side", columnList = "side"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_order_type", columnList = "order_type"),
    @Index(name = "idx_order_category", columnList = "order_category"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_updated_at", columnList = "updated_at"),
    @Index(name = "idx_record_timestamp", columnList = "record_timestamp"),
    @Index(name = "idx_order_id_updated_at", columnList = "order_id, updated_at")
})
public class WebSocketFuturesOrderUpdateData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", length = 100)
    private String orderId;

    @Column(name = "pair", length = 50)
    private String pair;

    @Column(name = "side", length = 20)
    private String side;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "order_type", length = 50)
    private String orderType;

    @Column(name = "stop_trigger_instruction", length = 50)
    private String stopTriggerInstruction;

    @Column(name = "notification", length = 50)
    private String notification;

    @Column(name = "leverage")
    private Double leverage;

    @Column(name = "maker_fee")
    private Double makerFee;

    @Column(name = "taker_fee")
    private Double takerFee;

    @Column(name = "fee_amount")
    private Double feeAmount;

    @Column(name = "price")
    private Double price;

    @Column(name = "stop_price")
    private Double stopPrice;

    @Column(name = "avg_price")
    private Double avgPrice;

    @Column(name = "take_profit_price")
    private Double takeProfitPrice;

    @Column(name = "stop_loss_price")
    private Double stopLossPrice;

    @Column(name = "total_quantity")
    private Double totalQuantity;

    @Column(name = "remaining_quantity")
    private Double remainingQuantity;

    @Column(name = "cancelled_quantity")
    private Double cancelledQuantity;

    @Column(name = "filled_quantity")
    private Double filledQuantity;

    @Column(name = "ideal_margin")
    private Double idealMargin;

    @Column(name = "order_category", length = 50)
    private String orderCategory;

    @Column(name = "stage", length = 50)
    private String stage;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "display_message", length = 500)
    private String displayMessage;

    @Column(name = "group_status", length = 50)
    private String groupStatus;

    @Column(name = "group_id", length = 100)
    private String groupId;

    @Column(name = "metatags", length = 500)
    private String metatags;

    @Column(name = "margin_currency_short_name", length = 20)
    private String marginCurrencyShortName;

    @Column(name = "settlement_currency_conversion_price")
    private Double settlementCurrencyConversionPrice;

    @Column(name = "trade_count")
    private Integer tradeCount;

    @Column(name = "channel_name", length = 50)
    private String channelName;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "record_timestamp")
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStopTriggerInstruction() {
        return stopTriggerInstruction;
    }

    public void setStopTriggerInstruction(String stopTriggerInstruction) {
        this.stopTriggerInstruction = stopTriggerInstruction;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Double getLeverage() {
        return leverage;
    }

    public void setLeverage(Double leverage) {
        this.leverage = leverage;
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

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(Double stopPrice) {
        this.stopPrice = stopPrice;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Double getTakeProfitPrice() {
        return takeProfitPrice;
    }

    public void setTakeProfitPrice(Double takeProfitPrice) {
        this.takeProfitPrice = takeProfitPrice;
    }

    public Double getStopLossPrice() {
        return stopLossPrice;
    }

    public void setStopLossPrice(Double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
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

    public Double getCancelledQuantity() {
        return cancelledQuantity;
    }

    public void setCancelledQuantity(Double cancelledQuantity) {
        this.cancelledQuantity = cancelledQuantity;
    }

    public Double getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(Double filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public Double getIdealMargin() {
        return idealMargin;
    }

    public void setIdealMargin(Double idealMargin) {
        this.idealMargin = idealMargin;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
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

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMetatags() {
        return metatags;
    }

    public void setMetatags(String metatags) {
        this.metatags = metatags;
    }

    public String getMarginCurrencyShortName() {
        return marginCurrencyShortName;
    }

    public void setMarginCurrencyShortName(String marginCurrencyShortName) {
        this.marginCurrencyShortName = marginCurrencyShortName;
    }

    public Double getSettlementCurrencyConversionPrice() {
        return settlementCurrencyConversionPrice;
    }

    public void setSettlementCurrencyConversionPrice(Double settlementCurrencyConversionPrice) {
        this.settlementCurrencyConversionPrice = settlementCurrencyConversionPrice;
    }

    public Integer getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(Integer tradeCount) {
        this.tradeCount = tradeCount;
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
