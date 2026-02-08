package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for storing WebSocket Futures Position Update data
 * Channel: coindcx
 * Event: df-position-update
 * Description: Real-time updates for futures positions including margin, PnL, and position details
 */
@Entity
@Table(name = "websocket_futures_position_update_data", indexes = {
    @Index(name = "idx_position_id", columnList = "positionId"),
    @Index(name = "idx_pair", columnList = "pair"),
    @Index(name = "idx_side", columnList = "side"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_timestamp", columnList = "updateTimestamp"),
    @Index(name = "idx_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_position_timestamp", columnList = "positionId, updateTimestamp")
})
public class WebSocketFuturesPositionUpdateData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Position ID
     */
    @Column(name = "position_id")
    private String positionId;

    /**
     * Trading pair (e.g., BTCUSDT)
     */
    @Column(name = "pair")
    private String pair;

    /**
     * Position side (long/short)
     */
    @Column(name = "side")
    private String side;

    /**
     * Position status (active/closed/liquidated)
     */
    @Column(name = "status")
    private String status;

    /**
     * Entry price
     */
    @Column(name = "entry_price")
    private Double entryPrice;

    /**
     * Current/Mark price
     */
    @Column(name = "current_price")
    private Double currentPrice;

    /**
     * Liquidation price
     */
    @Column(name = "liquidation_price")
    private Double liquidationPrice;

    /**
     * Position quantity/size
     */
    @Column(name = "quantity")
    private Double quantity;

    /**
     * Leverage
     */
    @Column(name = "leverage")
    private Double leverage;

    /**
     * Margin amount
     */
    @Column(name = "margin")
    private Double margin;

    /**
     * Initial margin
     */
    @Column(name = "initial_margin")
    private Double initialMargin;

    /**
     * Maintenance margin
     */
    @Column(name = "maintenance_margin")
    private Double maintenanceMargin;

    /**
     * Unrealized PnL
     */
    @Column(name = "unrealized_pnl")
    private Double unrealizedPnl;

    /**
     * Realized PnL
     */
    @Column(name = "realized_pnl")
    private Double realizedPnl;

    /**
     * Total PnL
     */
    @Column(name = "total_pnl")
    private Double totalPnl;

    /**
     * Margin currency (e.g., USDT, INR)
     */
    @Column(name = "margin_currency")
    private String marginCurrency;

    /**
     * Position margin type (isolated/cross)
     */
    @Column(name = "position_margin_type")
    private String positionMarginType;

    /**
     * ROI (Return on Investment) percentage
     */
    @Column(name = "roi")
    private Double roi;

    /**
     * Update timestamp from the event
     */
    @Column(name = "update_timestamp")
    private Long updateTimestamp;

    /**
     * Channel name
     */
    @Column(name = "channel_name")
    private String channelName;

    /**
     * Complete JSON data
     */
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    /**
     * Record creation timestamp
     */
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

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
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

    public Double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(Double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getLiquidationPrice() {
        return liquidationPrice;
    }

    public void setLiquidationPrice(Double liquidationPrice) {
        this.liquidationPrice = liquidationPrice;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getLeverage() {
        return leverage;
    }

    public void setLeverage(Double leverage) {
        this.leverage = leverage;
    }

    public Double getMargin() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public Double getInitialMargin() {
        return initialMargin;
    }

    public void setInitialMargin(Double initialMargin) {
        this.initialMargin = initialMargin;
    }

    public Double getMaintenanceMargin() {
        return maintenanceMargin;
    }

    public void setMaintenanceMargin(Double maintenanceMargin) {
        this.maintenanceMargin = maintenanceMargin;
    }

    public Double getUnrealizedPnl() {
        return unrealizedPnl;
    }

    public void setUnrealizedPnl(Double unrealizedPnl) {
        this.unrealizedPnl = unrealizedPnl;
    }

    public Double getRealizedPnl() {
        return realizedPnl;
    }

    public void setRealizedPnl(Double realizedPnl) {
        this.realizedPnl = realizedPnl;
    }

    public Double getTotalPnl() {
        return totalPnl;
    }

    public void setTotalPnl(Double totalPnl) {
        this.totalPnl = totalPnl;
    }

    public String getMarginCurrency() {
        return marginCurrency;
    }

    public void setMarginCurrency(String marginCurrency) {
        this.marginCurrency = marginCurrency;
    }

    public String getPositionMarginType() {
        return positionMarginType;
    }

    public void setPositionMarginType(String positionMarginType) {
        this.positionMarginType = positionMarginType;
    }

    public Double getRoi() {
        return roi;
    }

    public void setRoi(Double roi) {
        this.roi = roi;
    }

    public Long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
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

    @Override
    public String toString() {
        return "WebSocketFuturesPositionUpdateData{" +
                "id=" + id +
                ", positionId='" + positionId + '\'' +
                ", pair='" + pair + '\'' +
                ", side='" + side + '\'' +
                ", status='" + status + '\'' +
                ", entryPrice=" + entryPrice +
                ", currentPrice=" + currentPrice +
                ", quantity=" + quantity +
                ", leverage=" + leverage +
                ", margin=" + margin +
                ", unrealizedPnl=" + unrealizedPnl +
                ", realizedPnl=" + realizedPnl +
                ", totalPnl=" + totalPnl +
                ", roi=" + roi +
                ", updateTimestamp=" + updateTimestamp +
                ", recordTimestamp=" + recordTimestamp +
                '}';
    }
}
