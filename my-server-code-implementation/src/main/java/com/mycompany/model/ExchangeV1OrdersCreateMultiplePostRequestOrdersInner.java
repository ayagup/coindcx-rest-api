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
 * ExchangeV1OrdersCreateMultiplePostRequestOrdersInner
 */

@JsonTypeName("_exchange_v1_orders_create_multiple_post_request_orders_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersCreateMultiplePostRequestOrdersInner {

  private String market;

  private BigDecimal totalQuantity;

  private @Nullable BigDecimal pricePerUnit;

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

  private SideEnum side;

  /**
   * Gets or Sets orderType
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

  private String ecode;

  private @Nullable String clientOrderId;

  private Long timestamp;

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner(String market, BigDecimal totalQuantity, SideEnum side, OrderTypeEnum orderType, String ecode, Long timestamp) {
    this.market = market;
    this.totalQuantity = totalQuantity;
    this.side = side;
    this.orderType = orderType;
    this.ecode = ecode;
    this.timestamp = timestamp;
  }

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner market(String market) {
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

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner totalQuantity(BigDecimal totalQuantity) {
    this.totalQuantity = totalQuantity;
    return this;
  }

  /**
   * Get totalQuantity
   * @return totalQuantity
   */
  @NotNull @Valid 
  @Schema(name = "total_quantity", example = "1.101", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total_quantity")
  public BigDecimal getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalQuantity(BigDecimal totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner pricePerUnit(@Nullable BigDecimal pricePerUnit) {
    this.pricePerUnit = pricePerUnit;
    return this;
  }

  /**
   * Get pricePerUnit
   * @return pricePerUnit
   */
  @Valid 
  @Schema(name = "price_per_unit", example = "0.082", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price_per_unit")
  public @Nullable BigDecimal getPricePerUnit() {
    return pricePerUnit;
  }

  public void setPricePerUnit(@Nullable BigDecimal pricePerUnit) {
    this.pricePerUnit = pricePerUnit;
  }

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner side(SideEnum side) {
    this.side = side;
    return this;
  }

  /**
   * Get side
   * @return side
   */
  @NotNull 
  @Schema(name = "side", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("side")
  public SideEnum getSide() {
    return side;
  }

  public void setSide(SideEnum side) {
    this.side = side;
  }

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner orderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
    return this;
  }

  /**
   * Get orderType
   * @return orderType
   */
  @NotNull 
  @Schema(name = "order_type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("order_type")
  public OrderTypeEnum getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
  }

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner ecode(String ecode) {
    this.ecode = ecode;
    return this;
  }

  /**
   * Exchange code
   * @return ecode
   */
  @NotNull 
  @Schema(name = "ecode", example = "I", description = "Exchange code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ecode")
  public String getEcode() {
    return ecode;
  }

  public void setEcode(String ecode) {
    this.ecode = ecode;
  }

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner clientOrderId(@Nullable String clientOrderId) {
    this.clientOrderId = clientOrderId;
    return this;
  }

  /**
   * Get clientOrderId
   * @return clientOrderId
   */
  
  @Schema(name = "client_order_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_order_id")
  public @Nullable String getClientOrderId() {
    return clientOrderId;
  }

  public void setClientOrderId(@Nullable String clientOrderId) {
    this.clientOrderId = clientOrderId;
  }

  public ExchangeV1OrdersCreateMultiplePostRequestOrdersInner timestamp(Long timestamp) {
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
    ExchangeV1OrdersCreateMultiplePostRequestOrdersInner exchangeV1OrdersCreateMultiplePostRequestOrdersInner = (ExchangeV1OrdersCreateMultiplePostRequestOrdersInner) o;
    return Objects.equals(this.market, exchangeV1OrdersCreateMultiplePostRequestOrdersInner.market) &&
        Objects.equals(this.totalQuantity, exchangeV1OrdersCreateMultiplePostRequestOrdersInner.totalQuantity) &&
        Objects.equals(this.pricePerUnit, exchangeV1OrdersCreateMultiplePostRequestOrdersInner.pricePerUnit) &&
        Objects.equals(this.side, exchangeV1OrdersCreateMultiplePostRequestOrdersInner.side) &&
        Objects.equals(this.orderType, exchangeV1OrdersCreateMultiplePostRequestOrdersInner.orderType) &&
        Objects.equals(this.ecode, exchangeV1OrdersCreateMultiplePostRequestOrdersInner.ecode) &&
        Objects.equals(this.clientOrderId, exchangeV1OrdersCreateMultiplePostRequestOrdersInner.clientOrderId) &&
        Objects.equals(this.timestamp, exchangeV1OrdersCreateMultiplePostRequestOrdersInner.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(market, totalQuantity, pricePerUnit, side, orderType, ecode, clientOrderId, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersCreateMultiplePostRequestOrdersInner {\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    totalQuantity: ").append(toIndentedString(totalQuantity)).append("\n");
    sb.append("    pricePerUnit: ").append(toIndentedString(pricePerUnit)).append("\n");
    sb.append("    side: ").append(toIndentedString(side)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
    sb.append("    ecode: ").append(toIndentedString(ecode)).append("\n");
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

