package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity to store real-time Spot market WebSocket data
 */
@Entity
@Table(name = "websocket_spot_data", indexes = {
    @Index(name = "idx_market_pair", columnList = "market_pair"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_event_type", columnList = "event_type")
})
public class WebSocketSpotData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market_pair", nullable = false, length = 50)
    private String marketPair; // e.g., B-BTC_USDT, BTCINR

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // e.g., price-change, new-trade, depth-update, candlestick

    @Column(name = "channel_name", length = 100)
    private String channelName;

    // Price information
    @Column(name = "price", precision = 20, scale = 8)
    private BigDecimal price;

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

    // Trade information
    @Column(name = "trade_id", length = 100)
    private String tradeId;

    @Column(name = "side", length = 10)
    private String side; // buy, sell

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

    // Raw data
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "exchange_timestamp")
    private Long exchangeTimestamp; // Unix timestamp from exchange

    public WebSocketSpotData() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketPair() {
        return marketPair;
    }

    public void setMarketPair(String marketPair) {
        this.marketPair = marketPair;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
        return "WebSocketSpotData{" +
                "id=" + id +
                ", marketPair='" + marketPair + '\'' +
                ", eventType='" + eventType + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", timestamp=" + timestamp +
                '}';
    }
}
