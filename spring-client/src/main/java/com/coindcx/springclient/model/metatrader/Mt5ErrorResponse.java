package com.coindcx.springclient.model.metatrader;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Standard error response from the MetaTrader 5 REST Gateway
 */
public class Mt5ErrorResponse {

    @JsonProperty("ok")
    private Boolean ok;

    @JsonProperty("error")
    private String error;

    public Mt5ErrorResponse() {}

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
