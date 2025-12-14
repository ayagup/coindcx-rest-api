package com.coindcx.springclient.model.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * Model for new trade data from WebSocket
 */
public class NewTradeData {
    
    @SerializedName("T")
    private String timestamp;
    
    @SerializedName("p")
    private String price;
    
    @SerializedName("q")
    private String quantity;
    
    @SerializedName("m")
    private Integer isBuyerMaker; // 0 = buyer is not maker, 1 = buyer is maker
    
    @SerializedName("s")
    private String symbol;
    
    @SerializedName("pr")
    private String product;

    // Getters and Setters
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    
    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    
    public Integer getIsBuyerMaker() { return isBuyerMaker; }
    public void setIsBuyerMaker(Integer isBuyerMaker) { this.isBuyerMaker = isBuyerMaker; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
}
