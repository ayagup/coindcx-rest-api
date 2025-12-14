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
 * ExchangeV1DerivativesFuturesOrdersCreatePostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_orders_create_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesOrdersCreatePostRequest {

  private Long timestamp;

  /**
   * Order side
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

  private String pair;

  /**
   * Order type
   */
  public enum OrderTypeEnum {
    MARKET("market"),
    
    LIMIT("limit"),
    
    STOP_LIMIT("stop_limit"),
    
    STOP_MARKET("stop_market"),
    
    TAKE_PROFIT_LIMIT("take_profit_limit"),
    
    TAKE_PROFIT_MARKET("take_profit_market");

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

  private @Nullable BigDecimal price;

  private @Nullable BigDecimal stopPrice;

  private BigDecimal totalQuantity;

  private @Nullable BigDecimal leverage;

  /**
   * Email notification preference
   */
  public enum NotificationEnum {
    NO_NOTIFICATION("no_notification"),
    
    EMAIL_NOTIFICATION("email_notification");

    private final String value;

    NotificationEnum(String value) {
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
    public static NotificationEnum fromValue(String value) {
      for (NotificationEnum b : NotificationEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private NotificationEnum notification;

  /**
   * Time in force (null for market orders)
   */
  public enum TimeInForceEnum {
    GOOD_TILL_CANCEL("good_till_cancel"),
    
    FILL_OR_KILL("fill_or_kill"),
    
    IMMEDIATE_OR_CANCEL("immediate_or_cancel");

    private final String value;

    TimeInForceEnum(String value) {
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
    public static TimeInForceEnum fromValue(String value) {
      for (TimeInForceEnum b : TimeInForceEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TimeInForceEnum timeInForce = TimeInForceEnum.GOOD_TILL_CANCEL;

  private @Nullable Boolean hidden;

  private @Nullable Boolean postOnly;

  /**
   * Futures margin mode
   */
  public enum MarginCurrencyShortNameEnum {
    INR("INR"),
    
    USDT("USDT");

    private final String value;

    MarginCurrencyShortNameEnum(String value) {
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
    public static MarginCurrencyShortNameEnum fromValue(String value) {
      for (MarginCurrencyShortNameEnum b : MarginCurrencyShortNameEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private MarginCurrencyShortNameEnum marginCurrencyShortName = MarginCurrencyShortNameEnum.USDT;

  /**
   * Position margin type
   */
  public enum PositionMarginTypeEnum {
    ISOLATED("isolated"),
    
    CROSSED("crossed");

    private final String value;

    PositionMarginTypeEnum(String value) {
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
    public static PositionMarginTypeEnum fromValue(String value) {
      for (PositionMarginTypeEnum b : PositionMarginTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable PositionMarginTypeEnum positionMarginType;

  private @Nullable BigDecimal takeProfitPrice;

  private @Nullable BigDecimal stopLossPrice;

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest(Long timestamp, SideEnum side, String pair, OrderTypeEnum orderType, BigDecimal totalQuantity, NotificationEnum notification) {
    this.timestamp = timestamp;
    this.side = side;
    this.pair = pair;
    this.orderType = orderType;
    this.totalQuantity = totalQuantity;
    this.notification = notification;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest timestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * EPOCH timestamp in milliseconds. Orders with delay > 10 seconds will be rejected
   * @return timestamp
   */
  @NotNull 
  @Schema(name = "timestamp", description = "EPOCH timestamp in milliseconds. Orders with delay > 10 seconds will be rejected", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest side(SideEnum side) {
    this.side = side;
    return this;
  }

  /**
   * Order side
   * @return side
   */
  @NotNull 
  @Schema(name = "side", description = "Order side", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("side")
  public SideEnum getSide() {
    return side;
  }

  public void setSide(SideEnum side) {
    this.side = side;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest pair(String pair) {
    this.pair = pair;
    return this;
  }

  /**
   * Pair name (format B-ETH_USDT)
   * @return pair
   */
  @NotNull 
  @Schema(name = "pair", example = "B-ETH_USDT", description = "Pair name (format B-ETH_USDT)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pair")
  public String getPair() {
    return pair;
  }

  public void setPair(String pair) {
    this.pair = pair;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest orderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
    return this;
  }

  /**
   * Order type
   * @return orderType
   */
  @NotNull 
  @Schema(name = "order_type", description = "Order type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("order_type")
  public OrderTypeEnum getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest price(@Nullable BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Order Price (limit price for limit, stop limit, and take profit limit orders). NULL for market orders
   * @return price
   */
  @Valid 
  @Schema(name = "price", description = "Order Price (limit price for limit, stop limit, and take profit limit orders). NULL for market orders", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price")
  public @Nullable BigDecimal getPrice() {
    return price;
  }

  public void setPrice(@Nullable BigDecimal price) {
    this.price = price;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest stopPrice(@Nullable BigDecimal stopPrice) {
    this.stopPrice = stopPrice;
    return this;
  }

  /**
   * Stop Price (for stop_limit, stop_market, take_profit_limit, take_profit_market orders). Trigger price
   * @return stopPrice
   */
  @Valid 
  @Schema(name = "stop_price", description = "Stop Price (for stop_limit, stop_market, take_profit_limit, take_profit_market orders). Trigger price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stop_price")
  public @Nullable BigDecimal getStopPrice() {
    return stopPrice;
  }

  public void setStopPrice(@Nullable BigDecimal stopPrice) {
    this.stopPrice = stopPrice;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest totalQuantity(BigDecimal totalQuantity) {
    this.totalQuantity = totalQuantity;
    return this;
  }

  /**
   * Order total quantity
   * @return totalQuantity
   */
  @NotNull @Valid 
  @Schema(name = "total_quantity", description = "Order total quantity", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total_quantity")
  public BigDecimal getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalQuantity(BigDecimal totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest leverage(@Nullable BigDecimal leverage) {
    this.leverage = leverage;
    return this;
  }

  /**
   * Leverage at which to take position. Should match position leverage
   * @return leverage
   */
  @Valid 
  @Schema(name = "leverage", description = "Leverage at which to take position. Should match position leverage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leverage")
  public @Nullable BigDecimal getLeverage() {
    return leverage;
  }

  public void setLeverage(@Nullable BigDecimal leverage) {
    this.leverage = leverage;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest notification(NotificationEnum notification) {
    this.notification = notification;
    return this;
  }

  /**
   * Email notification preference
   * @return notification
   */
  @NotNull 
  @Schema(name = "notification", description = "Email notification preference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("notification")
  public NotificationEnum getNotification() {
    return notification;
  }

  public void setNotification(NotificationEnum notification) {
    this.notification = notification;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest timeInForce(TimeInForceEnum timeInForce) {
    this.timeInForce = timeInForce;
    return this;
  }

  /**
   * Time in force (null for market orders)
   * @return timeInForce
   */
  
  @Schema(name = "time_in_force", description = "Time in force (null for market orders)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("time_in_force")
  public TimeInForceEnum getTimeInForce() {
    return timeInForce;
  }

  public void setTimeInForce(TimeInForceEnum timeInForce) {
    this.timeInForce = timeInForce;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest hidden(@Nullable Boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  /**
   * Not supported at the moment
   * @return hidden
   */
  
  @Schema(name = "hidden", description = "Not supported at the moment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hidden")
  public @Nullable Boolean getHidden() {
    return hidden;
  }

  public void setHidden(@Nullable Boolean hidden) {
    this.hidden = hidden;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest postOnly(@Nullable Boolean postOnly) {
    this.postOnly = postOnly;
    return this;
  }

  /**
   * Not supported at the moment
   * @return postOnly
   */
  
  @Schema(name = "post_only", description = "Not supported at the moment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("post_only")
  public @Nullable Boolean getPostOnly() {
    return postOnly;
  }

  public void setPostOnly(@Nullable Boolean postOnly) {
    this.postOnly = postOnly;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest marginCurrencyShortName(MarginCurrencyShortNameEnum marginCurrencyShortName) {
    this.marginCurrencyShortName = marginCurrencyShortName;
    return this;
  }

  /**
   * Futures margin mode
   * @return marginCurrencyShortName
   */
  
  @Schema(name = "margin_currency_short_name", description = "Futures margin mode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("margin_currency_short_name")
  public MarginCurrencyShortNameEnum getMarginCurrencyShortName() {
    return marginCurrencyShortName;
  }

  public void setMarginCurrencyShortName(MarginCurrencyShortNameEnum marginCurrencyShortName) {
    this.marginCurrencyShortName = marginCurrencyShortName;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest positionMarginType(@Nullable PositionMarginTypeEnum positionMarginType) {
    this.positionMarginType = positionMarginType;
    return this;
  }

  /**
   * Position margin type
   * @return positionMarginType
   */
  
  @Schema(name = "position_margin_type", description = "Position margin type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("position_margin_type")
  public @Nullable PositionMarginTypeEnum getPositionMarginType() {
    return positionMarginType;
  }

  public void setPositionMarginType(@Nullable PositionMarginTypeEnum positionMarginType) {
    this.positionMarginType = positionMarginType;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest takeProfitPrice(@Nullable BigDecimal takeProfitPrice) {
    this.takeProfitPrice = takeProfitPrice;
    return this;
  }

  /**
   * Take profit trigger price (only for market_order, limit_order)
   * @return takeProfitPrice
   */
  @Valid 
  @Schema(name = "take_profit_price", description = "Take profit trigger price (only for market_order, limit_order)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("take_profit_price")
  public @Nullable BigDecimal getTakeProfitPrice() {
    return takeProfitPrice;
  }

  public void setTakeProfitPrice(@Nullable BigDecimal takeProfitPrice) {
    this.takeProfitPrice = takeProfitPrice;
  }

  public ExchangeV1DerivativesFuturesOrdersCreatePostRequest stopLossPrice(@Nullable BigDecimal stopLossPrice) {
    this.stopLossPrice = stopLossPrice;
    return this;
  }

  /**
   * Stop loss trigger price (only for market_order, limit_order)
   * @return stopLossPrice
   */
  @Valid 
  @Schema(name = "stop_loss_price", description = "Stop loss trigger price (only for market_order, limit_order)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stop_loss_price")
  public @Nullable BigDecimal getStopLossPrice() {
    return stopLossPrice;
  }

  public void setStopLossPrice(@Nullable BigDecimal stopLossPrice) {
    this.stopLossPrice = stopLossPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1DerivativesFuturesOrdersCreatePostRequest exchangeV1DerivativesFuturesOrdersCreatePostRequest = (ExchangeV1DerivativesFuturesOrdersCreatePostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesOrdersCreatePostRequest.timestamp) &&
        Objects.equals(this.side, exchangeV1DerivativesFuturesOrdersCreatePostRequest.side) &&
        Objects.equals(this.pair, exchangeV1DerivativesFuturesOrdersCreatePostRequest.pair) &&
        Objects.equals(this.orderType, exchangeV1DerivativesFuturesOrdersCreatePostRequest.orderType) &&
        Objects.equals(this.price, exchangeV1DerivativesFuturesOrdersCreatePostRequest.price) &&
        Objects.equals(this.stopPrice, exchangeV1DerivativesFuturesOrdersCreatePostRequest.stopPrice) &&
        Objects.equals(this.totalQuantity, exchangeV1DerivativesFuturesOrdersCreatePostRequest.totalQuantity) &&
        Objects.equals(this.leverage, exchangeV1DerivativesFuturesOrdersCreatePostRequest.leverage) &&
        Objects.equals(this.notification, exchangeV1DerivativesFuturesOrdersCreatePostRequest.notification) &&
        Objects.equals(this.timeInForce, exchangeV1DerivativesFuturesOrdersCreatePostRequest.timeInForce) &&
        Objects.equals(this.hidden, exchangeV1DerivativesFuturesOrdersCreatePostRequest.hidden) &&
        Objects.equals(this.postOnly, exchangeV1DerivativesFuturesOrdersCreatePostRequest.postOnly) &&
        Objects.equals(this.marginCurrencyShortName, exchangeV1DerivativesFuturesOrdersCreatePostRequest.marginCurrencyShortName) &&
        Objects.equals(this.positionMarginType, exchangeV1DerivativesFuturesOrdersCreatePostRequest.positionMarginType) &&
        Objects.equals(this.takeProfitPrice, exchangeV1DerivativesFuturesOrdersCreatePostRequest.takeProfitPrice) &&
        Objects.equals(this.stopLossPrice, exchangeV1DerivativesFuturesOrdersCreatePostRequest.stopLossPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, side, pair, orderType, price, stopPrice, totalQuantity, leverage, notification, timeInForce, hidden, postOnly, marginCurrencyShortName, positionMarginType, takeProfitPrice, stopLossPrice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesOrdersCreatePostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    side: ").append(toIndentedString(side)).append("\n");
    sb.append("    pair: ").append(toIndentedString(pair)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    stopPrice: ").append(toIndentedString(stopPrice)).append("\n");
    sb.append("    totalQuantity: ").append(toIndentedString(totalQuantity)).append("\n");
    sb.append("    leverage: ").append(toIndentedString(leverage)).append("\n");
    sb.append("    notification: ").append(toIndentedString(notification)).append("\n");
    sb.append("    timeInForce: ").append(toIndentedString(timeInForce)).append("\n");
    sb.append("    hidden: ").append(toIndentedString(hidden)).append("\n");
    sb.append("    postOnly: ").append(toIndentedString(postOnly)).append("\n");
    sb.append("    marginCurrencyShortName: ").append(toIndentedString(marginCurrencyShortName)).append("\n");
    sb.append("    positionMarginType: ").append(toIndentedString(positionMarginType)).append("\n");
    sb.append("    takeProfitPrice: ").append(toIndentedString(takeProfitPrice)).append("\n");
    sb.append("    stopLossPrice: ").append(toIndentedString(stopLossPrice)).append("\n");
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

