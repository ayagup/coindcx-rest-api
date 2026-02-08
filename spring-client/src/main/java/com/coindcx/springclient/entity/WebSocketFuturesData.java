package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity to store real-time Futures market WebSocket data
 */
@Entity
@Table(name = "websocket_futures_data", indexes = {
    @Index(name = "idx_contract_symbol", columnList = "contract_symbol"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_event_type", columnList = "event_type")
})
public class WebSocketFuturesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_symbol", nullable = false, length = 50)
    private String contractSymbol; // e.g., BTCPERP, ETHUSDT

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // e.g., price-change, position-update, order-update

    @Column(name = "channel_name", length = 100)
    private String channelName;

    // Price information
    @Column(name = "mark_price", precision = 20, scale = 8)
    private BigDecimal markPrice;

    @Column(name = "index_price", precision = 20, scale = 8)
    private BigDecimal indexPrice;

    @Column(name = "last_price", precision = 20, scale = 8)
    private BigDecimal lastPrice;

    @Column(name = "volume", precision = 20, scale = 8)
    private BigDecimal volume;

    @Column(name = "high", precision = 20, scale = 8)
    private BigDecimal high;

    @Column(name = "low", precision = 20, scale = 8)
    private BigDecimal low;

    @Column(name = "open", precision = 20, scale = 8)
    private BigDecimal open;

    @Column(name = "close", precision = 20, scale = 8)
    private BigDecimal close;

    // Funding rate information
    @Column(name = "funding_rate", precision = 10, scale = 6)
    private BigDecimal fundingRate;

    @Column(name = "next_funding_time")
    private LocalDateTime nextFundingTime;

    // Position information
    @Column(name = "position_size", precision = 20, scale = 8)
    private BigDecimal positionSize;

    @Column(name = "leverage", precision = 5, scale = 2)
    private BigDecimal leverage;

    @Column(name = "unrealized_pnl", precision = 20, scale = 8)
    private BigDecimal unrealizedPnl;

    @Column(name = "liquidation_price", precision = 20, scale = 8)
    private BigDecimal liquidationPrice;

    // Trade information
    @Column(name = "trade_id", length = 100)
    private String tradeId;

    @Column(name = "side", length = 10)
    private String side; // long, short, buy, sell

    @Column(name = "quantity", precision = 20, scale = 8)
    private BigDecimal quantity;

    // Orderbook information
    @Column(name = "bid_price", precision = 20, scale = 8)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", precision = 20, scale = 8)
    private BigDecimal askPrice;

    @Column(name = "bid_quantity", precision = 20, scale = 8)
    private BigDecimal bidQuantity;

    @Column(name = "ask_quantity", precision = 20, scale = 8)
    private BigDecimal askQuantity;

    // Open Interest
    @Column(name = "open_interest", precision = 20, scale = 8)
    private BigDecimal openInterest;

    // Raw data
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "exchange_timestamp")
    private Long exchangeTimestamp; // Unix timestamp from exchange

    public WebSocketFuturesData() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractSymbol() {
        return contractSymbol;
    }

    public void setContractSymbol(String contractSymbol) {
        this.contractSymbol = contractSymbol;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public BigDecimal getMarkPrice() {
        return markPrice;
    }

    public void setMarkPrice(BigDecimal markPrice) {
        this.markPrice = markPrice;
    }

    public BigDecimal getIndexPrice() {
        return indexPrice;
    }

    public void setIndexPrice(BigDecimal indexPrice) {
        this.indexPrice = indexPrice;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getFundingRate() {
        return fundingRate;
    }

    public void setFundingRate(BigDecimal fundingRate) {
        this.fundingRate = fundingRate;
    }

    public LocalDateTime getNextFundingTime() {
        return nextFundingTime;
    }

    public void setNextFundingTime(LocalDateTime nextFundingTime) {
        this.nextFundingTime = nextFundingTime;
    }

    public BigDecimal getPositionSize() {
        return positionSize;
    }

    public void setPositionSize(BigDecimal positionSize) {
        this.positionSize = positionSize;
    }

    public BigDecimal getLeverage() {
        return leverage;
    }

    public void setLeverage(BigDecimal leverage) {
        this.leverage = leverage;
    }

    public BigDecimal getUnrealizedPnl() {
        return unrealizedPnl;
    }

    public void setUnrealizedPnl(BigDecimal unrealizedPnl) {
        this.unrealizedPnl = unrealizedPnl;
    }

    public BigDecimal getLiquidationPrice() {
        return liquidationPrice;
    }

    public void setLiquidationPrice(BigDecimal liquidationPrice) {
        this.liquidationPrice = liquidationPrice;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(BigDecimal askPrice) {
        this.askPrice = askPrice;
    }

    public BigDecimal getBidQuantity() {
        return bidQuantity;
    }

    public void setBidQuantity(BigDecimal bidQuantity) {
        this.bidQuantity = bidQuantity;
    }

    public BigDecimal getAskQuantity() {
        return askQuantity;
    }

    public void setAskQuantity(BigDecimal askQuantity) {
        this.askQuantity = askQuantity;
    }

    public BigDecimal getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(BigDecimal openInterest) {
        this.openInterest = openInterest;
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

    @Override
    public String toString() {
        return "WebSocketFuturesData{" +
                "id=" + id +
                ", contractSymbol='" + contractSymbol + '\'' +
                ", eventType='" + eventType + '\'' +
                ", markPrice=" + markPrice +
                ", lastPrice=" + lastPrice +
                ", timestamp=" + timestamp +
                '}';
    }
}
