package com.mycompany.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;

/**
 * Depth Snapshot (Order Book) WebSocket event model
 * Event: depth-snapshot
 * Channel: {pair}@orderbook@{depth} (Public)
 * Example: B-BTC_USDT@orderbook@20
 */
public class DepthSnapshot {
    
    @JsonProperty("ts")
    private Long timestamp;
    
    @JsonProperty("vs")
    private Long versionSequence;
    
    @JsonProperty("asks")
    private Map<String, String> asks;
    
    @JsonProperty("bids")
    private Map<String, String> bids;

    public DepthSnapshot() {
    }

    public DepthSnapshot(Long timestamp, Long versionSequence, Map<String, String> asks, Map<String, String> bids) {
        this.timestamp = timestamp;
        this.versionSequence = versionSequence;
        this.asks = asks;
        this.bids = bids;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getVersionSequence() {
        return versionSequence;
    }

    public void setVersionSequence(Long versionSequence) {
        this.versionSequence = versionSequence;
    }

    public Map<String, String> getAsks() {
        return asks;
    }

    public void setAsks(Map<String, String> asks) {
        this.asks = asks;
    }

    public Map<String, String> getBids() {
        return bids;
    }

    public void setBids(Map<String, String> bids) {
        this.bids = bids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepthSnapshot that = (DepthSnapshot) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(versionSequence, that.versionSequence) &&
                Objects.equals(asks, that.asks) &&
                Objects.equals(bids, that.bids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, versionSequence, asks, bids);
    }

    @Override
    public String toString() {
        return "DepthSnapshot{" +
                "timestamp=" + timestamp +
                ", versionSequence=" + versionSequence +
                ", asksSize=" + (asks != null ? asks.size() : 0) +
                ", bidsSize=" + (bids != null ? bids.size() : 0) +
                '}';
    }
}
