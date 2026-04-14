package com.coindcx.springclient.model.metatrader;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for POST /trade/tpsl
 */
public class TpSlRequest {

    @JsonProperty("ticket")
    private Long ticket;

    @JsonProperty("tp")
    private Double tp;

    @JsonProperty("sl")
    private Double sl;

    public TpSlRequest() {}

    public TpSlRequest(Long ticket, Double tp, Double sl) {
        this.ticket = ticket;
        this.tp = tp;
        this.sl = sl;
    }

    public Long getTicket() {
        return ticket;
    }

    public void setTicket(Long ticket) {
        this.ticket = ticket;
    }

    public Double getTp() {
        return tp;
    }

    public void setTp(Double tp) {
        this.tp = tp;
    }

    public Double getSl() {
        return sl;
    }

    public void setSl(Double sl) {
        this.sl = sl;
    }
}
