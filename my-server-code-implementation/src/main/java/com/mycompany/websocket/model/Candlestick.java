package com.mycompany.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Candlestick WebSocket event model
 * Event: candlestick
 * Channel: {pair}_{interval} (Public)
 * Example: B-BTC_USDT_1m
 */
public class Candlestick {
    
    @JsonProperty("t")
    private Long startTimestamp;
    
    @JsonProperty("T")
    private Long closeTimestamp;
    
    @JsonProperty("s")
    private String symbol;
    
    @JsonProperty("i")
    private String interval;
    
    @JsonProperty("f")
    private Long firstTradeId;
    
    @JsonProperty("L")
    private Long lastTradeId;
    
    @JsonProperty("o")
    private String open;
    
    @JsonProperty("c")
    private String close;
    
    @JsonProperty("h")
    private String high;
    
    @JsonProperty("l")
    private String low;
    
    @JsonProperty("v")
    private String baseAssetVolume;
    
    @JsonProperty("n")
    private Integer numberOfTrades;
    
    @JsonProperty("x")
    private Boolean isClosed;
    
    @JsonProperty("q")
    private String quoteAssetVolume;
    
    @JsonProperty("V")
    private String takerBuyBaseAssetVolume;
    
    @JsonProperty("Q")
    private String takerBuyQuoteAssetVolume;
    
    @JsonProperty("B")
    private String ignore;
    
    @JsonProperty("ecode")
    private String exchangeCode;
    
    @JsonProperty("channel")
    private String channel;
    
    @JsonProperty("pr")
    private String product;

    public Candlestick() {
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

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Long getFirstTradeId() {
        return firstTradeId;
    }

    public void setFirstTradeId(Long firstTradeId) {
        this.firstTradeId = firstTradeId;
    }

    public Long getLastTradeId() {
        return lastTradeId;
    }

    public void setLastTradeId(Long lastTradeId) {
        this.lastTradeId = lastTradeId;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getBaseAssetVolume() {
        return baseAssetVolume;
    }

    public void setBaseAssetVolume(String baseAssetVolume) {
        this.baseAssetVolume = baseAssetVolume;
    }

    public Integer getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(Integer numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
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

    public String getIgnore() {
        return ignore;
    }

    public void setIgnore(String ignore) {
        this.ignore = ignore;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Candlestick{" +
                "symbol='" + symbol + '\'' +
                ", interval='" + interval + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", open='" + open + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", close='" + close + '\'' +
                ", volume='" + baseAssetVolume + '\'' +
                ", isClosed=" + isClosed +
                '}';
    }
}
