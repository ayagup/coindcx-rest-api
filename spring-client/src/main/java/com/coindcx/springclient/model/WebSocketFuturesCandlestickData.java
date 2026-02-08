package com.coindcx.springclient.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "websocket_futures_candlestick_data", indexes = {
    @Index(name = "idx_pair", columnList = "pair"),
    @Index(name = "idx_duration", columnList = "duration"),
    @Index(name = "idx_symbol", columnList = "symbol"),
    @Index(name = "idx_open_time", columnList = "openTime"),
    @Index(name = "idx_close_time", columnList = "closeTime"),
    @Index(name = "idx_record_timestamp", columnList = "recordTimestamp"),
    @Index(name = "idx_pair_duration", columnList = "pair,duration"),
    @Index(name = "idx_pair_open_time", columnList = "pair,openTime"),
    @Index(name = "idx_symbol_duration", columnList = "symbol,duration")
})
public class WebSocketFuturesCandlestickData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal open;
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal close;
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal high;
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal low;
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal volume;
    
    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal quoteVolume;
    
    @Column(nullable = false)
    private Long openTime; // epoch timestamp in seconds
    
    @Column(nullable = false)
    private Double closeTime; // epoch timestamp with milliseconds (e.g., 1705517999.999)
    
    @Column(nullable = false, length = 50)
    private String pair;
    
    @Column(nullable = false, length = 10)
    private String duration; // 1m, 5m, 15m, 30m, 1h, 4h, 8h, 1d, 3d, 1w, 1M
    
    @Column(nullable = false, length = 50)
    private String symbol;
    
    @Column(nullable = false)
    private Long ets; // Event timestamp
    
    @Column(name = "`interval`", nullable = false, length = 10)
    private String interval; // from "i" field (e.g., "1h") - backticks escape reserved keyword
    
    @Column(nullable = false, length = 100)
    private String channelName;
    
    @Column(nullable = false, length = 20)
    private String product; // "futures"
    
    @Column(columnDefinition = "TEXT")
    private String rawData;
    
    @Column(nullable = false)
    private LocalDateTime recordTimestamp;
    
    public WebSocketFuturesCandlestickData() {
        this.recordTimestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public BigDecimal getVolume() {
        return volume;
    }
    
    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
    
    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }
    
    public void setQuoteVolume(BigDecimal quoteVolume) {
        this.quoteVolume = quoteVolume;
    }
    
    public Long getOpenTime() {
        return openTime;
    }
    
    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }
    
    public Double getCloseTime() {
        return closeTime;
    }
    
    public void setCloseTime(Double closeTime) {
        this.closeTime = closeTime;
    }
    
    public String getPair() {
        return pair;
    }
    
    public void setPair(String pair) {
        this.pair = pair;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public Long getEts() {
        return ets;
    }
    
    public void setEts(Long ets) {
        this.ets = ets;
    }
    
    public String getInterval() {
        return interval;
    }
    
    public void setInterval(String interval) {
        this.interval = interval;
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
    
    public LocalDateTime getRecordTimestamp() {
        return recordTimestamp;
    }
    
    public void setRecordTimestamp(LocalDateTime recordTimestamp) {
        this.recordTimestamp = recordTimestamp;
    }
}
