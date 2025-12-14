package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * ExchangeV1OrdersCreatePostRequest
 */

@JsonTypeName("_exchange_v1_orders_create_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersCreatePostRequest {

  private String market;

  private BigDecimal totalQuantity;

  private @Nullable BigDecimal pricePerUnit;

  /**
   * Specify buy or sell
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

  private SideEnum side;

  /**
   * Order Type
   */
  public enum OrderTypeEnum {
    MARKET_ORDER("market_order"),
    
    LIMIT_ORDER("limit_order");

    private final String value;

    OrderTypeEnum(String value) {
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
    public static OrderTypeEnum fromValue(String value) {
      for (OrderTypeEnum b : OrderTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private OrderTypeEnum orderType;

  private @Nullable String clientOrderId;

  private Long timestamp;

  public ExchangeV1OrdersCreatePostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1OrdersCreatePostRequest(String market, BigDecimal totalQuantity, SideEnum side, OrderTypeEnum orderType, Long timestamp) {
    this.market = market;
    this.totalQuantity = totalQuantity;
    this.side = side;
    this.orderType = orderType;
    this.timestamp = timestamp;
  }

  public ExchangeV1OrdersCreatePostRequest market(String market) {
    this.market = market;
    return this;
  }

  /**
   * The trading pair
   * @return market
   */
  @NotNull 
  @Schema(name = "market", example = "SNTBTC", description = "The trading pair", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("market")
  public String getMarket() {
    return market;
  }

  public void setMarket(String market) {
    this.market = market;
  }

  public ExchangeV1OrdersCreatePostRequest totalQuantity(BigDecimal totalQuantity) {
    this.totalQuantity = totalQuantity;
    return this;
  }

  /**
   * Quantity to trade
   * @return totalQuantity
   */
  @NotNull @Valid 
  @Schema(name = "total_quantity", example = "1.101", description = "Quantity to trade", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total_quantity")
  public BigDecimal getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalQuantity(BigDecimal totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public ExchangeV1OrdersCreatePostRequest pricePerUnit(@Nullable BigDecimal pricePerUnit) {
    this.pricePerUnit = pricePerUnit;
    return this;
  }

  /**
   * Price per unit (not required for market order)
   * @return pricePerUnit
   */
  @Valid 
  @Schema(name = "price_per_unit", example = "0.082", description = "Price per unit (not required for market order)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price_per_unit")
  public @Nullable BigDecimal getPricePerUnit() {
    return pricePerUnit;
  }

  public void setPricePerUnit(@Nullable BigDecimal pricePerUnit) {
    this.pricePerUnit = pricePerUnit;
  }

  public ExchangeV1OrdersCreatePostRequest side(SideEnum side) {
    this.side = side;
    return this;
  }

  /**
   * Specify buy or sell
   * @return side
   */
  @NotNull 
  @Schema(name = "side", description = "Specify buy or sell", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("side")
  public SideEnum getSide() {
    return side;
  }

  public void setSide(SideEnum side) {
    this.side = side;
  }

  public ExchangeV1OrdersCreatePostRequest orderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
    return this;
  }

  /**
   * Order Type
   * @return orderType
   */
  @NotNull 
  @Schema(name = "order_type", description = "Order Type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("order_type")
  public OrderTypeEnum getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
  }

  public ExchangeV1OrdersCreatePostRequest clientOrderId(@Nullable String clientOrderId) {
    this.clientOrderId = clientOrderId;
    return this;
  }

  /**
   * Client order id of the order
   * @return clientOrderId
   */
  
  @Schema(name = "client_order_id", example = "2022.02.14-btcinr_1", description = "Client order id of the order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_order_id")
  public @Nullable String getClientOrderId() {
    return clientOrderId;
  }

  public void setClientOrderId(@Nullable String clientOrderId) {
    this.clientOrderId = clientOrderId;
  }

  public ExchangeV1OrdersCreatePostRequest timestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Current timestamp in milliseconds
   * @return timestamp
   */
  @NotNull 
  @Schema(name = "timestamp", description = "Current timestamp in milliseconds", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ExchangeV1OrdersCreatePostRequest exchangeV1OrdersCreatePostRequest = (ExchangeV1OrdersCreatePostRequest) o;
    return Objects.equals(this.market, exchangeV1OrdersCreatePostRequest.market) &&
        Objects.equals(this.totalQuantity, exchangeV1OrdersCreatePostRequest.totalQuantity) &&
        Objects.equals(this.pricePerUnit, exchangeV1OrdersCreatePostRequest.pricePerUnit) &&
        Objects.equals(this.side, exchangeV1OrdersCreatePostRequest.side) &&
        Objects.equals(this.orderType, exchangeV1OrdersCreatePostRequest.orderType) &&
        Objects.equals(this.clientOrderId, exchangeV1OrdersCreatePostRequest.clientOrderId) &&
        Objects.equals(this.timestamp, exchangeV1OrdersCreatePostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(market, totalQuantity, pricePerUnit, side, orderType, clientOrderId, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersCreatePostRequest {\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    totalQuantity: ").append(toIndentedString(totalQuantity)).append("\n");
    sb.append("    pricePerUnit: ").append(toIndentedString(pricePerUnit)).append("\n");
    sb.append("    side: ").append(toIndentedString(side)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
    sb.append("    clientOrderId: ").append(toIndentedString(clientOrderId)).append("\n");
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

