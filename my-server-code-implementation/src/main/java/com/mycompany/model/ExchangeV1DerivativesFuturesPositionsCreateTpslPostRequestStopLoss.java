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
 * ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss
 */

@JsonTypeName("_exchange_v1_derivatives_futures_positions_create_tpsl_post_request_stop_loss")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss {

  private String stopPrice;

  private @Nullable String limitPrice;

  /**
   * Only stop_market is currently supported
   */
  public enum OrderTypeEnum {
    STOP_MARKET("stop_market");

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

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss(String stopPrice, OrderTypeEnum orderType) {
    this.stopPrice = stopPrice;
    this.orderType = orderType;
  }

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss stopPrice(String stopPrice) {
    this.stopPrice = stopPrice;
    return this;
  }

  /**
   * Stop price (trigger price)
   * @return stopPrice
   */
  @NotNull 
  @Schema(name = "stop_price", description = "Stop price (trigger price)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("stop_price")
  public String getStopPrice() {
    return stopPrice;
  }

  public void setStopPrice(String stopPrice) {
    this.stopPrice = stopPrice;
  }

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss limitPrice(@Nullable String limitPrice) {
    this.limitPrice = limitPrice;
    return this;
  }

  /**
   * Limit price (not currently supported)
   * @return limitPrice
   */
  
  @Schema(name = "limit_price", description = "Limit price (not currently supported)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("limit_price")
  public @Nullable String getLimitPrice() {
    return limitPrice;
  }

  public void setLimitPrice(@Nullable String limitPrice) {
    this.limitPrice = limitPrice;
  }

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss orderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
    return this;
  }

  /**
   * Only stop_market is currently supported
   * @return orderType
   */
  @NotNull 
  @Schema(name = "order_type", description = "Only stop_market is currently supported", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("order_type")
  public OrderTypeEnum getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss exchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss = (ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss) o;
    return Objects.equals(this.stopPrice, exchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss.stopPrice) &&
        Objects.equals(this.limitPrice, exchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss.limitPrice) &&
        Objects.equals(this.orderType, exchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss.orderType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stopPrice, limitPrice, orderType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss {\n");
    sb.append("    stopPrice: ").append(toIndentedString(stopPrice)).append("\n");
    sb.append("    limitPrice: ").append(toIndentedString(limitPrice)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
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

