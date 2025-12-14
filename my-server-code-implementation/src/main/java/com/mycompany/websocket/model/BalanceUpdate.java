package com.mycompany.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Balance Update WebSocket event model
 * Event: balance-update
 * Channel: coindcx (Private)
 */
public class BalanceUpdate {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("balance")
    private String balance;
    
    @JsonProperty("locked_balance")
    private String lockedBalance;
    
    @JsonProperty("currency_id")
    private String currencyId;
    
    @JsonProperty("currency_short_name")
    private String currencyShortName;

    public BalanceUpdate() {
    }

    public BalanceUpdate(String id, String balance, String lockedBalance, String currencyId, String currencyShortName) {
        this.id = id;
        this.balance = balance;
        this.lockedBalance = lockedBalance;
        this.currencyId = currencyId;
        this.currencyShortName = currencyShortName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLockedBalance() {
        return lockedBalance;
    }

    public void setLockedBalance(String lockedBalance) {
        this.lockedBalance = lockedBalance;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyShortName() {
        return currencyShortName;
    }

    public void setCurrencyShortName(String currencyShortName) {
        this.currencyShortName = currencyShortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BalanceUpdate that = (BalanceUpdate) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(lockedBalance, that.lockedBalance) &&
                Objects.equals(currencyId, that.currencyId) &&
                Objects.equals(currencyShortName, that.currencyShortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, lockedBalance, currencyId, currencyShortName);
    }

    @Override
    public String toString() {
        return "BalanceUpdate{" +
                "id='" + id + '\'' +
                ", balance='" + balance + '\'' +
                ", lockedBalance='" + lockedBalance + '\'' +
                ", currencyId='" + currencyId + '\'' +
                ", currencyShortName='" + currencyShortName + '\'' +
                '}';
    }
}
