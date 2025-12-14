package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * MarketDataV3OrderbookInstrumentFuturesDepthGet200Response
 */

@JsonTypeName("_market_data_v3_orderbook__instrument__futures__depth__get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class MarketDataV3OrderbookInstrumentFuturesDepthGet200Response {

  @Valid
  private List<List<String>> bids = new ArrayList<>();

  @Valid
  private List<List<String>> asks = new ArrayList<>();

  public MarketDataV3OrderbookInstrumentFuturesDepthGet200Response bids(List<List<String>> bids) {
    this.bids = bids;
    return this;
  }

  public MarketDataV3OrderbookInstrumentFuturesDepthGet200Response addBidsItem(List<String> bidsItem) {
    if (this.bids == null) {
      this.bids = new ArrayList<>();
    }
    this.bids.add(bidsItem);
    return this;
  }

  /**
   * Buy orders
   * @return bids
   */
  @Valid 
  @Schema(name = "bids", description = "Buy orders", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bids")
  public List<List<String>> getBids() {
    return bids;
  }

  public void setBids(List<List<String>> bids) {
    this.bids = bids;
  }

  public MarketDataV3OrderbookInstrumentFuturesDepthGet200Response asks(List<List<String>> asks) {
    this.asks = asks;
    return this;
  }

  public MarketDataV3OrderbookInstrumentFuturesDepthGet200Response addAsksItem(List<String> asksItem) {
    if (this.asks == null) {
      this.asks = new ArrayList<>();
    }
    this.asks.add(asksItem);
    return this;
  }

  /**
   * Sell orders
   * @return asks
   */
  @Valid 
  @Schema(name = "asks", description = "Sell orders", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("asks")
  public List<List<String>> getAsks() {
    return asks;
  }

  public void setAsks(List<List<String>> asks) {
    this.asks = asks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarketDataV3OrderbookInstrumentFuturesDepthGet200Response marketDataV3OrderbookInstrumentFuturesDepthGet200Response = (MarketDataV3OrderbookInstrumentFuturesDepthGet200Response) o;
    return Objects.equals(this.bids, marketDataV3OrderbookInstrumentFuturesDepthGet200Response.bids) &&
        Objects.equals(this.asks, marketDataV3OrderbookInstrumentFuturesDepthGet200Response.asks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bids, asks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarketDataV3OrderbookInstrumentFuturesDepthGet200Response {\n");
    sb.append("    bids: ").append(toIndentedString(bids)).append("\n");
    sb.append("    asks: ").append(toIndentedString(asks)).append("\n");
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

