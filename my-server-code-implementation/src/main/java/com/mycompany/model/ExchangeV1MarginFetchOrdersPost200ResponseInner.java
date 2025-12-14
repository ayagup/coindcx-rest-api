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
 * ExchangeV1MarginFetchOrdersPost200ResponseInner
 */

@JsonTypeName("_exchange_v1_margin_fetch_orders_post_200_response_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarginFetchOrdersPost200ResponseInner {

  private @Nullable String id;

  private @Nullable String side;

  private @Nullable String status;

  private @Nullable String market;

  private @Nullable String orderType;

  private @Nullable BigDecimal quantity;

  private @Nullable BigDecimal price;

  private @Nullable BigDecimal leverage;

  private @Nullable BigDecimal pnl;

  private @Nullable BigDecimal initialMargin;

  private @Nullable Integer createdAt;

  private @Nullable Integer updatedAt;

  public ExchangeV1MarginFetchOrdersPost200ResponseInner id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", example = "30b5002f-d9c1-413d-8a8d-0fd32b054c9c", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner side(@Nullable String side) {
    this.side = side;
    return this;
  }

  /**
   * Get side
   * @return side
   */
  
  @Schema(name = "side", example = "sell", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("side")
  public @Nullable String getSide() {
    return side;
  }

  public void setSide(@Nullable String side) {
    this.side = side;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", example = "rejected", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner market(@Nullable String market) {
    this.market = market;
    return this;
  }

  /**
   * Get market
   * @return market
   */
  
  @Schema(name = "market", example = "XRPBTC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("market")
  public @Nullable String getMarket() {
    return market;
  }

  public void setMarket(@Nullable String market) {
    this.market = market;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner orderType(@Nullable String orderType) {
    this.orderType = orderType;
    return this;
  }

  /**
   * Get orderType
   * @return orderType
   */
  
  @Schema(name = "order_type", example = "limit_order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("order_type")
  public @Nullable String getOrderType() {
    return orderType;
  }

  public void setOrderType(@Nullable String orderType) {
    this.orderType = orderType;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner quantity(@Nullable BigDecimal quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Get quantity
   * @return quantity
   */
  @Valid 
  @Schema(name = "quantity", example = "200", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quantity")
  public @Nullable BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(@Nullable BigDecimal quantity) {
    this.quantity = quantity;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner price(@Nullable BigDecimal price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price
   */
  @Valid 
  @Schema(name = "price", example = "0.000026", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price")
  public @Nullable BigDecimal getPrice() {
    return price;
  }

  public void setPrice(@Nullable BigDecimal price) {
    this.price = price;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner leverage(@Nullable BigDecimal leverage) {
    this.leverage = leverage;
    return this;
  }

  /**
   * Get leverage
   * @return leverage
   */
  @Valid 
  @Schema(name = "leverage", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leverage")
  public @Nullable BigDecimal getLeverage() {
    return leverage;
  }

  public void setLeverage(@Nullable BigDecimal leverage) {
    this.leverage = leverage;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner pnl(@Nullable BigDecimal pnl) {
    this.pnl = pnl;
    return this;
  }

  /**
   * Get pnl
   * @return pnl
   */
  @Valid 
  @Schema(name = "pnl", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pnl")
  public @Nullable BigDecimal getPnl() {
    return pnl;
  }

  public void setPnl(@Nullable BigDecimal pnl) {
    this.pnl = pnl;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner initialMargin(@Nullable BigDecimal initialMargin) {
    this.initialMargin = initialMargin;
    return this;
  }

  /**
   * Get initialMargin
   * @return initialMargin
   */
  @Valid 
  @Schema(name = "initial_margin", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("initial_margin")
  public @Nullable BigDecimal getInitialMargin() {
    return initialMargin;
  }

  public void setInitialMargin(@Nullable BigDecimal initialMargin) {
    this.initialMargin = initialMargin;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner createdAt(@Nullable Integer createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  
  @Schema(name = "created_at", example = "1568122929782", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created_at")
  public @Nullable Integer getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(@Nullable Integer createdAt) {
    this.createdAt = createdAt;
  }

  public ExchangeV1MarginFetchOrdersPost200ResponseInner updatedAt(@Nullable Integer updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  
  @Schema(name = "updated_at", example = "1568122930404", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updated_at")
  public @Nullable Integer getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(@Nullable Integer updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1MarginFetchOrdersPost200ResponseInner exchangeV1MarginFetchOrdersPost200ResponseInner = (ExchangeV1MarginFetchOrdersPost200ResponseInner) o;
    return Objects.equals(this.id, exchangeV1MarginFetchOrdersPost200ResponseInner.id) &&
        Objects.equals(this.side, exchangeV1MarginFetchOrdersPost200ResponseInner.side) &&
        Objects.equals(this.status, exchangeV1MarginFetchOrdersPost200ResponseInner.status) &&
        Objects.equals(this.market, exchangeV1MarginFetchOrdersPost200ResponseInner.market) &&
        Objects.equals(this.orderType, exchangeV1MarginFetchOrdersPost200ResponseInner.orderType) &&
        Objects.equals(this.quantity, exchangeV1MarginFetchOrdersPost200ResponseInner.quantity) &&
        Objects.equals(this.price, exchangeV1MarginFetchOrdersPost200ResponseInner.price) &&
        Objects.equals(this.leverage, exchangeV1MarginFetchOrdersPost200ResponseInner.leverage) &&
        Objects.equals(this.pnl, exchangeV1MarginFetchOrdersPost200ResponseInner.pnl) &&
        Objects.equals(this.initialMargin, exchangeV1MarginFetchOrdersPost200ResponseInner.initialMargin) &&
        Objects.equals(this.createdAt, exchangeV1MarginFetchOrdersPost200ResponseInner.createdAt) &&
        Objects.equals(this.updatedAt, exchangeV1MarginFetchOrdersPost200ResponseInner.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, side, status, market, orderType, quantity, price, leverage, pnl, initialMargin, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarginFetchOrdersPost200ResponseInner {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    side: ").append(toIndentedString(side)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    leverage: ").append(toIndentedString(leverage)).append("\n");
    sb.append("    pnl: ").append(toIndentedString(pnl)).append("\n");
    sb.append("    initialMargin: ").append(toIndentedString(initialMargin)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

