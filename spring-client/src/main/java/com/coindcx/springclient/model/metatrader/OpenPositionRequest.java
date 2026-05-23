package com.coindcx.springclient.model.metatrader;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for POST /trade/open
 */
public class OpenPositionRequest {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("direction")
    private String direction = "long";

    @JsonProperty(value = "margin_usdt", required = true)
    private Double marginUsdt;

    public OpenPositionRequest() {}

    public OpenPositionRequest(String symbol, String direction, Double marginUsdt) {
        this.symbol = symbol;
        this.direction = direction;
        this.marginUsdt = marginUsdt;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getMarginUsdt() {
        return marginUsdt;
    }

    public void setMarginUsdt(Double marginUsdt) {
        this.marginUsdt = marginUsdt;
    }
}
