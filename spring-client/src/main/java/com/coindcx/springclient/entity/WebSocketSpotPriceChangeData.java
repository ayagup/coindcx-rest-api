package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to store WebSocket Spot Price Change data (Spot-LTP)
 * Channel: {pair}@prices (e.g., B-BTC_USDT@prices)
 * Event: price-change
 * Description: Latest price info when there is a price change
 */
@Entity
@Table(name = "websocket_spot_price_change_data", indexes = {
    @Index(name = "idx_price_change_pair", columnList = "pair"),
    @Index(name = "idx_price_change_product", columnList = "product"),
    @Index(name = "idx_price_change_timestamp", columnList = "tradeTimestamp"),
    @Index(name = "idx_price_change_price", columnList = "tradePrice"),
    @Index(name = "idx_price_change_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_price_change_pair_timestamp", columnList = "pair, tradeTimestamp")
})
public class WebSocketSpotPriceChangeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Trade price
     * Field from response: p
     */
    @Column(name = "tradePrice")
    private Double tradePrice;

    /**
     * Timestamp of trade
     * Field from response: T
     */
    @Column(name = "tradeTimestamp")
    private Long tradeTimestamp;

    /**
     * Product (spot)
     * Field from response: pr
     */
    @Column(name = "product", length = 50)
    private String product;

    /**
     * Trading pair (extracted from channel name)
     * e.g., B-BTC_USDT from channel B-BTC_USDT@prices
     */
    @Column(name = "pair", length = 50)
    private String pair;

    /**
     * Channel name (e.g., B-BTC_USDT@prices)
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
    public WebSocketSpotPriceChangeData() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public Long getTradeTimestamp() {
        return tradeTimestamp;
    }

    public void setTradeTimestamp(Long tradeTimestamp) {
        this.tradeTimestamp = tradeTimestamp;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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
        return "WebSocketSpotPriceChangeData{" +
                "id=" + id +
                ", tradePrice=" + tradePrice +
                ", tradeTimestamp=" + tradeTimestamp +
                ", product='" + product + '\'' +
                ", pair='" + pair + '\'' +
                ", channelName='" + channelName + '\'' +
                ", recordTimestamp=" + recordTimestamp +
                '}';
    }
}
