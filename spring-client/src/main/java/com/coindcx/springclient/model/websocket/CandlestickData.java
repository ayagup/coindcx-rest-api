package com.coindcx.springclient.model.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * Model for candlestick data from WebSocket
 */
public class CandlestickData {
    
    @SerializedName("t")
    private Long startTime;
    
    @SerializedName("T")
    private Long closeTime;
    
    @SerializedName("s")
    private String symbol;
    
    @SerializedName("i")
    private String interval;
    
    @SerializedName("f")
    private Long firstTradeId;
    
    @SerializedName("L")
    private Long lastTradeId;
    
    @SerializedName("o")
    private String open;
    
    @SerializedName("c")
    private String close;
    
    @SerializedName("h")
    private String high;
    
    @SerializedName("l")
    private String low;
    
    @SerializedName("v")
    private String volume;
    
    @SerializedName("n")
    private Integer numberOfTrades;
    
    @SerializedName("x")
    private Boolean isClosed;
    
    @SerializedName("q")
    private String quoteVolume;
    
    @SerializedName("V")
    private String takerBuyVolume;
    
    @SerializedName("Q")
    private String takerBuyQuoteVolume;
    
    @SerializedName("B")
    private String ignore;
    
    @SerializedName("ecode")
    private String exchangeCode;
    
    @SerializedName("channel")
    private String channel;
    
    @SerializedName("pr")
    private String product;

    // Getters and Setters
    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }
    
    public Long getCloseTime() { return closeTime; }
    public void setCloseTime(Long closeTime) { this.closeTime = closeTime; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getInterval() { return interval; }
    public void setInterval(String interval) { this.interval = interval; }
    
    public Long getFirstTradeId() { return firstTradeId; }
    public void setFirstTradeId(Long firstTradeId) { this.firstTradeId = firstTradeId; }
    
    public Long getLastTradeId() { return lastTradeId; }
    public void setLastTradeId(Long lastTradeId) { this.lastTradeId = lastTradeId; }
    
    public String getOpen() { return open; }
    public void setOpen(String open) { this.open = open; }
    
    public String getClose() { return close; }
    public void setClose(String close) { this.close = close; }
    
    public String getHigh() { return high; }
    public void setHigh(String high) { this.high = high; }
    
    public String getLow() { return low; }
    public void setLow(String low) { this.low = low; }
    
    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }
    
    public Integer getNumberOfTrades() { return numberOfTrades; }
    public void setNumberOfTrades(Integer numberOfTrades) { this.numberOfTrades = numberOfTrades; }
    
    public Boolean getIsClosed() { return isClosed; }
    public void setIsClosed(Boolean isClosed) { this.isClosed = isClosed; }
    
    public String getQuoteVolume() { return quoteVolume; }
    public void setQuoteVolume(String quoteVolume) { this.quoteVolume = quoteVolume; }
    
    public String getTakerBuyVolume() { return takerBuyVolume; }
    public void setTakerBuyVolume(String takerBuyVolume) { this.takerBuyVolume = takerBuyVolume; }
    
    public String getTakerBuyQuoteVolume() { return takerBuyQuoteVolume; }
    public void setTakerBuyQuoteVolume(String takerBuyQuoteVolume) { this.takerBuyQuoteVolume = takerBuyQuoteVolume; }
    
    public String getExchangeCode() { return exchangeCode; }
    public void setExchangeCode(String exchangeCode) { this.exchangeCode = exchangeCode; }
    
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
}
