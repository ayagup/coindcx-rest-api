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
 * ExchangeV1MarginCreatePostRequest
 */

@JsonTypeName("_exchange_v1_margin_create_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarginCreatePostRequest {

  private String market;

  private BigDecimal quantity;

  private @Nullable BigDecimal price;

  private BigDecimal leverage = new BigDecimal("1.0");

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

  private @Nullable BigDecimal stopPrice;

  /**
   * Order Type
   */
  public enum OrderTypeEnum {
    MARKET_ORDER("market_order"),
    
    LIMIT_ORDER("limit_order"),
    
    STOP_LIMIT("stop_limit"),
    
    TAKE_PROFIT("take_profit");

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

  private @Nullable Boolean trailingSl;

  private @Nullable BigDecimal targetPrice;

  private @Nullable BigDecimal slPrice;

  private String ecode = "B";

  private Integer timestamp;

  public ExchangeV1MarginCreatePostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1MarginCreatePostRequest(String market, BigDecimal quantity, SideEnum side, OrderTypeEnum orderType, String ecode, Integer timestamp) {
    this.market = market;
    this.quantity = quantity;
    this.side = side;
    this.orderType = orderType;
    this.ecode = ecode;
    this.timestamp = timestamp;
  }

  public ExchangeV1MarginCreatePostRequest market(String market) {
    this.market = market;
    return this;
  }

  /**
   * The trading pair
   * @return market
   */
  @NotNull 
  @Schema(name = "market", example = "XRPBTC", description = "The trading pair", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("market")
  public String getMarket() {
    return market;
  }

  public void setMarket(String market) {
    this.market = market;
  }

  public ExchangeV1MarginCreatePostRequest quantity(BigDecimal quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Quantity to trade
   * @return quantity
   */
  @NotNull @Valid 
  @Schema(name = "quantity", example = "90", description = "Quantity to trade", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("quantity")
  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public ExchangeV1MarginCreatePostRequest price(@Nullable BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Price per unit (not required for market order, mandatory for rest)
   * @return price
   */
  @Valid 
  @Schema(name = "price", example = "0.000025", description = "Price per unit (not required for market order, mandatory for rest)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price")
  public @Nullable BigDecimal getPrice() {
    return price;
  }

  public void setPrice(@Nullable BigDecimal price) {
    this.price = price;
  }

  public ExchangeV1MarginCreatePostRequest leverage(BigDecimal leverage) {
    this.leverage = leverage;
    return this;
  }

  /**
   * Borrowed capital to increase the potential returns
   * @return leverage
   */
  @Valid 
  @Schema(name = "leverage", example = "1.0", description = "Borrowed capital to increase the potential returns", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leverage")
  public BigDecimal getLeverage() {
    return leverage;
  }

  public void setLeverage(BigDecimal leverage) {
    this.leverage = leverage;
  }

  public ExchangeV1MarginCreatePostRequest side(SideEnum side) {
    this.side = side;
    return this;
  }

  /**
   * Specify buy or sell
   * @return side
   */
  @NotNull 
  @Schema(name = "side", example = "buy", description = "Specify buy or sell", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("side")
  public SideEnum getSide() {
    return side;
  }

  public void setSide(SideEnum side) {
    this.side = side;
  }

  public ExchangeV1MarginCreatePostRequest stopPrice(@Nullable BigDecimal stopPrice) {
    this.stopPrice = stopPrice;
    return this;
  }

  /**
   * Price to stop the order at (mandatory in case of stop_limit & take_profit)
   * @return stopPrice
   */
  @Valid 
  @Schema(name = "stop_price", example = "0.082", description = "Price to stop the order at (mandatory in case of stop_limit & take_profit)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stop_price")
  public @Nullable BigDecimal getStopPrice() {
    return stopPrice;
  }

  public void setStopPrice(@Nullable BigDecimal stopPrice) {
    this.stopPrice = stopPrice;
  }

  public ExchangeV1MarginCreatePostRequest orderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
    return this;
  }

  /**
   * Order Type
   * @return orderType
   */
  @NotNull 
  @Schema(name = "order_type", example = "limit_order", description = "Order Type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("order_type")
  public OrderTypeEnum getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
  }

  public ExchangeV1MarginCreatePostRequest trailingSl(@Nullable Boolean trailingSl) {
    this.trailingSl = trailingSl;
    return this;
  }

  /**
   * To place order with Trailing Stop Loss
   * @return trailingSl
   */
  
  @Schema(name = "trailing_sl", example = "false", description = "To place order with Trailing Stop Loss", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("trailing_sl")
  public @Nullable Boolean getTrailingSl() {
    return trailingSl;
  }

  public void setTrailingSl(@Nullable Boolean trailingSl) {
    this.trailingSl = trailingSl;
  }

  public ExchangeV1MarginCreatePostRequest targetPrice(@Nullable BigDecimal targetPrice) {
    this.targetPrice = targetPrice;
    return this;
  }

  /**
   * The price to buy/sell or close the order position
   * @return targetPrice
   */
  @Valid 
  @Schema(name = "target_price", example = "0.082", description = "The price to buy/sell or close the order position", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("target_price")
  public @Nullable BigDecimal getTargetPrice() {
    return targetPrice;
  }

  public void setTargetPrice(@Nullable BigDecimal targetPrice) {
    this.targetPrice = targetPrice;
  }

  public ExchangeV1MarginCreatePostRequest slPrice(@Nullable BigDecimal slPrice) {
    this.slPrice = slPrice;
    return this;
  }

  /**
   * Stop loss price
   * @return slPrice
   */
  @Valid 
  @Schema(name = "sl_price", example = "0.00005005", description = "Stop loss price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sl_price")
  public @Nullable BigDecimal getSlPrice() {
    return slPrice;
  }

  public void setSlPrice(@Nullable BigDecimal slPrice) {
    this.slPrice = slPrice;
  }

  public ExchangeV1MarginCreatePostRequest ecode(String ecode) {
    this.ecode = ecode;
    return this;
  }

  /**
   * Exchange code in which the order will be placed (always 'B')
   * @return ecode
   */
  @NotNull 
  @Schema(name = "ecode", example = "B", description = "Exchange code in which the order will be placed (always 'B')", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ecode")
  public String getEcode() {
    return ecode;
  }

  public void setEcode(String ecode) {
    this.ecode = ecode;
  }

  public ExchangeV1MarginCreatePostRequest timestamp(Integer timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * EPOCH timestamp in milliseconds
   * @return timestamp
   */
  @NotNull 
  @Schema(name = "timestamp", example = "1524211224000", description = "EPOCH timestamp in milliseconds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Integer timestamp) {
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
    ExchangeV1MarginCreatePostRequest exchangeV1MarginCreatePostRequest = (ExchangeV1MarginCreatePostRequest) o;
    return Objects.equals(this.market, exchangeV1MarginCreatePostRequest.market) &&
        Objects.equals(this.quantity, exchangeV1MarginCreatePostRequest.quantity) &&
        Objects.equals(this.price, exchangeV1MarginCreatePostRequest.price) &&
        Objects.equals(this.leverage, exchangeV1MarginCreatePostRequest.leverage) &&
        Objects.equals(this.side, exchangeV1MarginCreatePostRequest.side) &&
        Objects.equals(this.stopPrice, exchangeV1MarginCreatePostRequest.stopPrice) &&
        Objects.equals(this.orderType, exchangeV1MarginCreatePostRequest.orderType) &&
        Objects.equals(this.trailingSl, exchangeV1MarginCreatePostRequest.trailingSl) &&
        Objects.equals(this.targetPrice, exchangeV1MarginCreatePostRequest.targetPrice) &&
        Objects.equals(this.slPrice, exchangeV1MarginCreatePostRequest.slPrice) &&
        Objects.equals(this.ecode, exchangeV1MarginCreatePostRequest.ecode) &&
        Objects.equals(this.timestamp, exchangeV1MarginCreatePostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(market, quantity, price, leverage, side, stopPrice, orderType, trailingSl, targetPrice, slPrice, ecode, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarginCreatePostRequest {\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    leverage: ").append(toIndentedString(leverage)).append("\n");
    sb.append("    side: ").append(toIndentedString(side)).append("\n");
    sb.append("    stopPrice: ").append(toIndentedString(stopPrice)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
    sb.append("    trailingSl: ").append(toIndentedString(trailingSl)).append("\n");
    sb.append("    targetPrice: ").append(toIndentedString(targetPrice)).append("\n");
    sb.append("    slPrice: ").append(toIndentedString(slPrice)).append("\n");
    sb.append("    ecode: ").append(toIndentedString(ecode)).append("\n");
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

