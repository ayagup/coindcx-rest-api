package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * MarketDataOrderbookGet200Response
 */

@JsonTypeName("_market_data_orderbook_get_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class MarketDataOrderbookGet200Response {

  private @Nullable Object bids;

  private @Nullable Object asks;

  public MarketDataOrderbookGet200Response bids(@Nullable Object bids) {
    this.bids = bids;
    return this;
  }

  /**
   * Buy orders sorted in descending order
   * @return bids
   */
  
  @Schema(name = "bids", description = "Buy orders sorted in descending order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bids")
  public @Nullable Object getBids() {
    return bids;
  }

  public void setBids(@Nullable Object bids) {
    this.bids = bids;
  }

  public MarketDataOrderbookGet200Response asks(@Nullable Object asks) {
    this.asks = asks;
    return this;
  }

  /**
   * Sell orders sorted in ascending order
   * @return asks
   */
  
  @Schema(name = "asks", description = "Sell orders sorted in ascending order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("asks")
  public @Nullable Object getAsks() {
    return asks;
  }

  public void setAsks(@Nullable Object asks) {
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
    MarketDataOrderbookGet200Response marketDataOrderbookGet200Response = (MarketDataOrderbookGet200Response) o;
    return Objects.equals(this.bids, marketDataOrderbookGet200Response.bids) &&
        Objects.equals(this.asks, marketDataOrderbookGet200Response.asks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bids, asks);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarketDataOrderbookGet200Response {\n");
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

