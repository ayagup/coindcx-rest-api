package com.coindcx.springclient.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Append-only futures trade lifecycle log.
 * Records API requests and websocket-confirmed lifecycle events such as
 * position opens/closes, TP/SL placement, and triggered orders.
 */
@Entity
@Table(name = "futures_trade_log", indexes = {
    @Index(name = "idx_futures_trade_log_position_id", columnList = "position_id"),
    @Index(name = "idx_futures_trade_log_order_id", columnList = "order_id"),
    @Index(name = "idx_futures_trade_log_pair", columnList = "pair"),
    @Index(name = "idx_futures_trade_log_event_type", columnList = "event_type"),
    @Index(name = "idx_futures_trade_log_source", columnList = "source"),
    @Index(name = "idx_futures_trade_log_event_timestamp", columnList = "event_timestamp"),
    @Index(name = "idx_futures_trade_log_record_timestamp", columnList = "record_timestamp")
})
public class FuturesTradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source", nullable = false, length = 30)
    private String source;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @Column(name = "pair", length = 50)
    private String pair;

    @Column(name = "position_id", length = 120)
    private String positionId;

    @Column(name = "order_id", length = 120)
    private String orderId;

    @Column(name = "group_id", length = 120)
    private String groupId;

    @Column(name = "side", length = 20)
    private String side;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "order_type", length = 50)
    private String orderType;

    @Column(name = "trigger_type", length = 50)
    private String triggerType;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "trigger_price")
    private Double triggerPrice;

    @Column(name = "event_timestamp")
    private Long eventTimestamp;

    @Column(name = "channel_name", length = 120)
    private String channelName;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "record_timestamp", nullable = false)
    private LocalDateTime recordTimestamp;

    @PrePersist
    protected void onCreate() {
        if (recordTimestamp == null) {
            recordTimestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(Double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public Long getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
