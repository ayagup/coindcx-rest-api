package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to store WebSocket Spot New Trade data (latest trade information)
 * Channel: {pair}@trades (e.g., B-BTC_USDT@trades)
 * Event: new-trade
 */
@Entity
@Table(name = "websocket_spot_new_trade_data", indexes = {
    @Index(name = "idx_new_trade_symbol", columnList = "symbol"),
    @Index(name = "idx_new_trade_pair", columnList = "pair"),
    @Index(name = "idx_new_trade_timestamp", columnList = "tradeTimestamp"),
    @Index(name = "idx_new_trade_price", columnList = "tradePrice"),
    @Index(name = "idx_new_trade_quantity", columnList = "quantity"),
    @Index(name = "idx_new_trade_market_maker", columnList = "isBuyerMarketMaker"),
    @Index(name = "idx_new_trade_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_new_trade_pair_timestamp", columnList = "pair, tradeTimestamp")
})
public class WebSocketSpotNewTradeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Whether the buyer is market maker or not
     * Field from response: m
     */
    @Column(name = "isBuyerMarketMaker")
    private Boolean isBuyerMarketMaker;

    /**
     * Trade price
     * Field from response: p
     */
    @Column(name = "tradePrice")
    private Double tradePrice;

    /**
     * Quantity
     * Field from response: q
     */
    @Column(name = "quantity")
    private Double quantity;

    /**
     * Timestamp of trade
     * Field from response: T
     */
    @Column(name = "tradeTimestamp")
    private Long tradeTimestamp;

    /**
     * Symbol (currency)
     * Field from response: s
     */
    @Column(name = "symbol", length = 50)
    private String symbol;

    /**
     * Trading pair (extracted from channel name)
     * e.g., B-BTC_USDT from channel B-BTC_USDT@trades
     */
    @Column(name = "pair", length = 50)
    private String pair;

    /**
     * Channel name (e.g., B-BTC_USDT@trades)
     */
    @Column(name = "channelName", length = 100)
    private String channelName;

    /**
     * Raw JSON data received
     */
    @Column(name = "rawData", columnDefinition = "TEXT")
    private String rawData;

    /**
     * Record creation timestamp
     */
    @Column(name = "recordTimestamp")
    private LocalDateTime recordTimestamp;

    /**
     * Auto-set record timestamp before persisting
     */
    @PrePersist
    protected void onCreate() {
        this.recordTimestamp = LocalDateTime.now();
    }

    // Constructors
    public WebSocketSpotNewTradeData() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsBuyerMarketMaker() {
        return isBuyerMarketMaker;
    }

    public void setIsBuyerMarketMaker(Boolean isBuyerMarketMaker) {
        this.isBuyerMarketMaker = isBuyerMarketMaker;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Long getTradeTimestamp() {
        return tradeTimestamp;
    }

    public void setTradeTimestamp(Long tradeTimestamp) {
        this.tradeTimestamp = tradeTimestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
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
        return "WebSocketSpotNewTradeData{" +
                "id=" + id +
                ", isBuyerMarketMaker=" + isBuyerMarketMaker +
                ", tradePrice=" + tradePrice +
                ", quantity=" + quantity +
                ", tradeTimestamp=" + tradeTimestamp +
                ", symbol='" + symbol + '\'' +
                ", pair='" + pair + '\'' +
                ", channelName='" + channelName + '\'' +
                ", recordTimestamp=" + recordTimestamp +
                '}';
    }
}
