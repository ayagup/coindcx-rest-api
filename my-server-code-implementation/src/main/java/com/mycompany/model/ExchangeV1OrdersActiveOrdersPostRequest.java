package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExchangeV1OrdersActiveOrdersPostRequest
 */

@JsonTypeName("_exchange_v1_orders_active_orders_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersActiveOrdersPostRequest {

  private String market;

  /**
   * Gets or Sets side
   */
  public enum SideEnum {
    BUY("buy"),
    
    SELL("sell");

    private final String value;

    SideEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SideEnum fromValue(String value) {
      for (SideEnum b : SideEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable SideEnum side;

  private Long timestamp;

  public ExchangeV1OrdersActiveOrdersPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1OrdersActiveOrdersPostRequest(String market, Long timestamp) {
    this.market = market;
    this.timestamp = timestamp;
  }

  public ExchangeV1OrdersActiveOrdersPostRequest market(String market) {
    this.market = market;
    return this;
  }

  /**
   * Get market
   * @return market
   */
  @NotNull 
  @Schema(name = "market", example = "SNTBTC", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("market")
  public String getMarket() {
    return market;
  }

  public void setMarket(String market) {
    this.market = market;
  }

  public ExchangeV1OrdersActiveOrdersPostRequest side(@Nullable SideEnum side) {
    this.side = side;
    return this;
  }

  /**
   * Get side
   * @return side
   */
  
  @Schema(name = "side", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("side")
  public @Nullable SideEnum getSide() {
    return side;
  }

  public void setSide(@Nullable SideEnum side) {
    this.side = side;
  }

  public ExchangeV1OrdersActiveOrdersPostRequest timestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
   */
  @NotNull 
  @Schema(name = "timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
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
    ExchangeV1OrdersActiveOrdersPostRequest exchangeV1OrdersActiveOrdersPostRequest = (ExchangeV1OrdersActiveOrdersPostRequest) o;
    return Objects.equals(this.market, exchangeV1OrdersActiveOrdersPostRequest.market) &&
        Objects.equals(this.side, exchangeV1OrdersActiveOrdersPostRequest.side) &&
        Objects.equals(this.timestamp, exchangeV1OrdersActiveOrdersPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(market, side, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersActiveOrdersPostRequest {\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    side: ").append(toIndentedString(side)).append("\n");
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

