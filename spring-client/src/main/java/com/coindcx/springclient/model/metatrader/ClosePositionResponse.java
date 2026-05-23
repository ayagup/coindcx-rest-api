package com.coindcx.springclient.model.metatrader;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response from POST /trade/close
 */
public class ClosePositionResponse {

    @JsonProperty("ok")
    private Boolean ok;

    @JsonProperty("ticket")
    private Long ticket;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("volume")
    private Double volume;

    @JsonProperty("close_price")
    private Double closePrice;

    @JsonProperty("orders_cancelled")
    private List<Long> ordersCancelled;

    public ClosePositionResponse() {}

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public Long getTicket() {
        return ticket;
    }

    public void setTicket(Long ticket) {
        this.ticket = ticket;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
    }

    public List<Long> getOrdersCancelled() {
        return ordersCancelled;
    }

    public void setOrdersCancelled(List<Long> ordersCancelled) {
        this.ordersCancelled = ordersCancelled;
    }
}
