package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExchangeTickerGet200ResponseInner
 */

@JsonTypeName("_exchange_ticker_get_200_response_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeTickerGet200ResponseInner {

  private @Nullable String market;

  private @Nullable BigDecimal bid;

  private @Nullable BigDecimal ask;

  private @Nullable BigDecimal high;

  private @Nullable BigDecimal low;

  private @Nullable BigDecimal volume;

  private @Nullable Integer timestamp;

  public ExchangeTickerGet200ResponseInner market(@Nullable String market) {
    this.market = market;
    return this;
  }

  /**
   * Get market
   * @return market
   */
  
  @Schema(name = "market", example = "SNTBTC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("market")
  public @Nullable String getMarket() {
    return market;
  }

  public void setMarket(@Nullable String market) {
    this.market = market;
  }

  public ExchangeTickerGet200ResponseInner bid(@Nullable BigDecimal bid) {
    this.bid = bid;
    return this;
  }

  /**
   * Highest bid offer in the orderbook
   * @return bid
   */
  @Valid 
  @Schema(name = "bid", description = "Highest bid offer in the orderbook", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bid")
  public @Nullable BigDecimal getBid() {
    return bid;
  }

  public void setBid(@Nullable BigDecimal bid) {
    this.bid = bid;
  }

  public ExchangeTickerGet200ResponseInner ask(@Nullable BigDecimal ask) {
    this.ask = ask;
    return this;
  }

  /**
   * Lowest ask offer in the orderbook
   * @return ask
   */
  @Valid 
  @Schema(name = "ask", description = "Lowest ask offer in the orderbook", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ask")
  public @Nullable BigDecimal getAsk() {
    return ask;
  }

  public void setAsk(@Nullable BigDecimal ask) {
    this.ask = ask;
  }

  public ExchangeTickerGet200ResponseInner high(@Nullable BigDecimal high) {
    this.high = high;
    return this;
  }

  /**
   * 24 hour high
   * @return high
   */
  @Valid 
  @Schema(name = "high", description = "24 hour high", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("high")
  public @Nullable BigDecimal getHigh() {
    return high;
  }

  public void setHigh(@Nullable BigDecimal high) {
    this.high = high;
  }

  public ExchangeTickerGet200ResponseInner low(@Nullable BigDecimal low) {
    this.low = low;
    return this;
  }

  /**
   * 24 hour low
   * @return low
   */
  @Valid 
  @Schema(name = "low", description = "24 hour low", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("low")
  public @Nullable BigDecimal getLow() {
    return low;
  }

  public void setLow(@Nullable BigDecimal low) {
    this.low = low;
  }

  public ExchangeTickerGet200ResponseInner volume(@Nullable BigDecimal volume) {
    this.volume = volume;
    return this;
  }

  /**
   * 24 hour volume
   * @return volume
   */
  @Valid 
  @Schema(name = "volume", description = "24 hour volume", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("volume")
  public @Nullable BigDecimal getVolume() {
    return volume;
  }

  public void setVolume(@Nullable BigDecimal volume) {
    this.volume = volume;
  }

  public ExchangeTickerGet200ResponseInner timestamp(@Nullable Integer timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Time when this ticker was generated
   * @return timestamp
   */
  
  @Schema(name = "timestamp", description = "Time when this ticker was generated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestamp")
  public @Nullable Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(@Nullable Integer timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeTickerGet200ResponseInner exchangeTickerGet200ResponseInner = (ExchangeTickerGet200ResponseInner) o;
    return Objects.equals(this.market, exchangeTickerGet200ResponseInner.market) &&
        Objects.equals(this.bid, exchangeTickerGet200ResponseInner.bid) &&
        Objects.equals(this.ask, exchangeTickerGet200ResponseInner.ask) &&
        Objects.equals(this.high, exchangeTickerGet200ResponseInner.high) &&
        Objects.equals(this.low, exchangeTickerGet200ResponseInner.low) &&
        Objects.equals(this.volume, exchangeTickerGet200ResponseInner.volume) &&
        Objects.equals(this.timestamp, exchangeTickerGet200ResponseInner.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(market, bid, ask, high, low, volume, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeTickerGet200ResponseInner {\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    bid: ").append(toIndentedString(bid)).append("\n");
    sb.append("    ask: ").append(toIndentedString(ask)).append("\n");
    sb.append("    high: ").append(toIndentedString(high)).append("\n");
    sb.append("    low: ").append(toIndentedString(low)).append("\n");
    sb.append("    volume: ").append(toIndentedString(volume)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

