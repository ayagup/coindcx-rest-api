package com.coindcx.springclient.model.metatrader;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for POST /trade/close
 */
public class ClosePositionRequest {

    @JsonProperty("ticket")
    private Long ticket;

    public ClosePositionRequest() {}

    public ClosePositionRequest(Long ticket) {
        this.ticket = ticket;
    }

    public Long getTicket() {
        return ticket;
    }

    public void setTicket(Long ticket) {
        this.ticket = ticket;
    }
}
