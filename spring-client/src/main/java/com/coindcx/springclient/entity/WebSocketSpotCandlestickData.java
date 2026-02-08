package com.coindcx.springclient.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to store spot candlestick data from WebSocket
 */
@Entity
@Table(name = "websocket_spot_candlestick_data", indexes = {
    @Index(name = "idx_spot_candle_symbol", columnList = "symbol"),
    @Index(name = "idx_spot_candle_interval", columnList = "candle_interval"),
    @Index(name = "idx_spot_candle_start_timestamp", columnList = "start_timestamp"),
    @Index(name = "idx_spot_candle_close_timestamp", columnList = "close_timestamp"),
    @Index(name = "idx_spot_candle_symbol_interval", columnList = "symbol, candle_interval"),
    @Index(name = "idx_spot_candle_is_completed", columnList = "is_completed"),
    @Index(name = "idx_spot_candle_timestamp", columnList = "timestamp")
})
public class WebSocketSpotCandlestickData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "start_timestamp", nullable = false)
    private Long startTimestamp;
    
    @Column(name = "close_timestamp", nullable = false)
    private Long closeTimestamp;
    
    @Column(name = "symbol", nullable = false, length = 50)
    private String symbol;
    
    @Column(name = "candle_interval", nullable = false, length = 10)
    private String candleInterval;
    
    @Column(name = "first_trade_id", length = 100)
    private String firstTradeId;
    
    @Column(name = "last_trade_id", length = 100)
    private String lastTradeId;
    
    @Column(name = "open_price", precision = 30, scale = 10)
    private String openPrice;
    
    @Column(name = "close_price", precision = 30, scale = 10)
    private String closePrice;
    
    @Column(name = "high_price", precision = 30, scale = 10)
    private String highPrice;
    
    @Column(name = "low_price", precision = 30, scale = 10)
    private String lowPrice;
    
    @Column(name = "base_asset_volume", precision = 30, scale = 10)
    private String baseAssetVolume;
    
    @Column(name = "number_of_trades")
    private Long numberOfTrades;
    
    @Column(name = "is_completed")
    private Boolean isCompleted;
    
    @Column(name = "quote_asset_volume", precision = 30, scale = 10)
    private String quoteAssetVolume;
    
    @Column(name = "taker_buy_base_asset_volume", precision = 30, scale = 10)
    private String takerBuyBaseAssetVolume;
    
    @Column(name = "taker_buy_quote_asset_volume", precision = 30, scale = 10)
    private String takerBuyQuoteAssetVolume;
    
    @Column(name = "first_trade_id_b", length = 100)
    private String firstTradeIdB;
    
    @Column(name = "exchange_code", length = 50)
    private String exchangeCode;
    
    @Column(name = "channel_name", length = 100)
    private String channelName;
    
    @Column(name = "product", length = 50)
    private String product;
    
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Constructors
    public WebSocketSpotCandlestickData() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getStartTimestamp() {
        return startTimestamp;
    }
    
    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }
    
    public Long getCloseTimestamp() {
        return closeTimestamp;
    }
    
    public void setCloseTimestamp(Long closeTimestamp) {
        this.closeTimestamp = closeTimestamp;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getCandleInterval() {
        return candleInterval;
    }
    
    public void setCandleInterval(String candleInterval) {
        this.candleInterval = candleInterval;
    }
    
    public String getFirstTradeId() {
        return firstTradeId;
    }
    
    public void setFirstTradeId(String firstTradeId) {
        this.firstTradeId = firstTradeId;
    }
    
    public String getLastTradeId() {
        return lastTradeId;
    }
    
    public void setLastTradeId(String lastTradeId) {
        this.lastTradeId = lastTradeId;
    }
    
    public String getOpenPrice() {
        return openPrice;
    }
    
    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }
    
    public String getClosePrice() {
        return closePrice;
    }
    
    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }
    
    public String getHighPrice() {
        return highPrice;
    }
    
    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }
    
    public String getLowPrice() {
        return lowPrice;
    }
    
    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }
    
    public String getBaseAssetVolume() {
        return baseAssetVolume;
    }
    
    public void setBaseAssetVolume(String baseAssetVolume) {
        this.baseAssetVolume = baseAssetVolume;
    }
    
    public Long getNumberOfTrades() {
        return numberOfTrades;
    }
    
    public void setNumberOfTrades(Long numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }
    
    public Boolean getIsCompleted() {
        return isCompleted;
    }
    
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    
    public String getQuoteAssetVolume() {
        return quoteAssetVolume;
    }
    
    public void setQuoteAssetVolume(String quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }
    
    public String getTakerBuyBaseAssetVolume() {
        return takerBuyBaseAssetVolume;
    }
    
    public void setTakerBuyBaseAssetVolume(String takerBuyBaseAssetVolume) {
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
    }
    
    public String getTakerBuyQuoteAssetVolume() {
        return takerBuyQuoteAssetVolume;
    }
    
    public void setTakerBuyQuoteAssetVolume(String takerBuyQuoteAssetVolume) {
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
    }
    
    public String getFirstTradeIdB() {
        return firstTradeIdB;
    }
    
    public void setFirstTradeIdB(String firstTradeIdB) {
        this.firstTradeIdB = firstTradeIdB;
    }
    
    public String getExchangeCode() {
        return exchangeCode;
    }
    
    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }
    
    public String getChannelName() {
        return channelName;
    }
    
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    
    public String getProduct() {
        return product;
    }
    
    public void setProduct(String product) {
        this.product = product;
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
    
    @Override
    public String toString() {
        return "WebSocketSpotCandlestickData{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", interval='" + candleInterval + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", closeTimestamp=" + closeTimestamp +
                ", open=" + openPrice +
                ", close=" + closePrice +
                ", high=" + highPrice +
                ", low=" + lowPrice +
                ", volume=" + baseAssetVolume +
                ", trades=" + numberOfTrades +
                ", completed=" + isCompleted +
                ", timestamp=" + timestamp +
                '}';
    }
}
