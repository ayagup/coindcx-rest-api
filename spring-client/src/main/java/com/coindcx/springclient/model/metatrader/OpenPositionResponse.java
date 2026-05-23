package com.coindcx.springclient.model.metatrader;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response from POST /trade/open
 */
public class OpenPositionResponse {

    @JsonProperty("ok")
    private Boolean ok;

    @JsonProperty("ticket")
    private Long ticket;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("lot")
    private Double lot;

    public OpenPositionResponse() {}

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getLot() {
        return lot;
    }

    public void setLot(Double lot) {
        this.lot = lot;
    }
}
