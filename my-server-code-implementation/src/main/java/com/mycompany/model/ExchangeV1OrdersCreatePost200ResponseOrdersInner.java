package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExchangeV1OrdersCreatePost200ResponseOrdersInner
 */

@JsonTypeName("_exchange_v1_orders_create_post_200_response_orders_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersCreatePost200ResponseOrdersInner {

  private @Nullable String id;

  private @Nullable String market;

  private @Nullable String orderType;

  private @Nullable String side;

  private @Nullable String status;

  private @Nullable BigDecimal feeAmount;

  private @Nullable BigDecimal fee;

  private @Nullable BigDecimal totalQuantity;

  private @Nullable BigDecimal remainingQuantity;

  private @Nullable BigDecimal avgPrice;

  private @Nullable BigDecimal pricePerUnit;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime updatedAt;

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner market(@Nullable String market) {
    this.market = market;
    return this;
  }

  /**
   * Get market
   * @return market
   */
  
  @Schema(name = "market", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("market")
  public @Nullable String getMarket() {
    return market;
  }

  public void setMarket(@Nullable String market) {
    this.market = market;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner orderType(@Nullable String orderType) {
    this.orderType = orderType;
    return this;
  }

  /**
   * Get orderType
   * @return orderType
   */
  
  @Schema(name = "order_type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("order_type")
  public @Nullable String getOrderType() {
    return orderType;
  }

  public void setOrderType(@Nullable String orderType) {
    this.orderType = orderType;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner side(@Nullable String side) {
    this.side = side;
    return this;
  }

  /**
   * Get side
   * @return side
   */
  
  @Schema(name = "side", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("side")
  public @Nullable String getSide() {
    return side;
  }

  public void setSide(@Nullable String side) {
    this.side = side;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner feeAmount(@Nullable BigDecimal feeAmount) {
    this.feeAmount = feeAmount;
    return this;
  }

  /**
   * Get feeAmount
   * @return feeAmount
   */
  @Valid 
  @Schema(name = "fee_amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fee_amount")
  public @Nullable BigDecimal getFeeAmount() {
    return feeAmount;
  }

  public void setFeeAmount(@Nullable BigDecimal feeAmount) {
    this.feeAmount = feeAmount;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner fee(@Nullable BigDecimal fee) {
    this.fee = fee;
    return this;
  }

  /**
   * Get fee
   * @return fee
   */
  @Valid 
  @Schema(name = "fee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fee")
  public @Nullable BigDecimal getFee() {
    return fee;
  }

  public void setFee(@Nullable BigDecimal fee) {
    this.fee = fee;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner totalQuantity(@Nullable BigDecimal totalQuantity) {
    this.totalQuantity = totalQuantity;
    return this;
  }

  /**
   * Get totalQuantity
   * @return totalQuantity
   */
  @Valid 
  @Schema(name = "total_quantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total_quantity")
  public @Nullable BigDecimal getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalQuantity(@Nullable BigDecimal totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner remainingQuantity(@Nullable BigDecimal remainingQuantity) {
    this.remainingQuantity = remainingQuantity;
    return this;
  }

  /**
   * Get remainingQuantity
   * @return remainingQuantity
   */
  @Valid 
  @Schema(name = "remaining_quantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("remaining_quantity")
  public @Nullable BigDecimal getRemainingQuantity() {
    return remainingQuantity;
  }

  public void setRemainingQuantity(@Nullable BigDecimal remainingQuantity) {
    this.remainingQuantity = remainingQuantity;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner avgPrice(@Nullable BigDecimal avgPrice) {
    this.avgPrice = avgPrice;
    return this;
  }

  /**
   * Get avgPrice
   * @return avgPrice
   */
  @Valid 
  @Schema(name = "avg_price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avg_price")
  public @Nullable BigDecimal getAvgPrice() {
    return avgPrice;
  }

  public void setAvgPrice(@Nullable BigDecimal avgPrice) {
    this.avgPrice = avgPrice;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner pricePerUnit(@Nullable BigDecimal pricePerUnit) {
    this.pricePerUnit = pricePerUnit;
    return this;
  }

  /**
   * Get pricePerUnit
   * @return pricePerUnit
   */
  @Valid 
  @Schema(name = "price_per_unit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price_per_unit")
  public @Nullable BigDecimal getPricePerUnit() {
    return pricePerUnit;
  }

  public void setPricePerUnit(@Nullable BigDecimal pricePerUnit) {
    this.pricePerUnit = pricePerUnit;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner createdAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @Valid 
  @Schema(name = "created_at", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("created_at")
  public @Nullable OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(@Nullable OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public ExchangeV1OrdersCreatePost200ResponseOrdersInner updatedAt(@Nullable OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  @Valid 
  @Schema(name = "updated_at", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("updated_at")
  public @Nullable OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(@Nullable OffsetDateTime updatedAt) {
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
    ExchangeV1OrdersCreatePost200ResponseOrdersInner exchangeV1OrdersCreatePost200ResponseOrdersInner = (ExchangeV1OrdersCreatePost200ResponseOrdersInner) o;
    return Objects.equals(this.id, exchangeV1OrdersCreatePost200ResponseOrdersInner.id) &&
        Objects.equals(this.market, exchangeV1OrdersCreatePost200ResponseOrdersInner.market) &&
        Objects.equals(this.orderType, exchangeV1OrdersCreatePost200ResponseOrdersInner.orderType) &&
        Objects.equals(this.side, exchangeV1OrdersCreatePost200ResponseOrdersInner.side) &&
        Objects.equals(this.status, exchangeV1OrdersCreatePost200ResponseOrdersInner.status) &&
        Objects.equals(this.feeAmount, exchangeV1OrdersCreatePost200ResponseOrdersInner.feeAmount) &&
        Objects.equals(this.fee, exchangeV1OrdersCreatePost200ResponseOrdersInner.fee) &&
        Objects.equals(this.totalQuantity, exchangeV1OrdersCreatePost200ResponseOrdersInner.totalQuantity) &&
        Objects.equals(this.remainingQuantity, exchangeV1OrdersCreatePost200ResponseOrdersInner.remainingQuantity) &&
        Objects.equals(this.avgPrice, exchangeV1OrdersCreatePost200ResponseOrdersInner.avgPrice) &&
        Objects.equals(this.pricePerUnit, exchangeV1OrdersCreatePost200ResponseOrdersInner.pricePerUnit) &&
        Objects.equals(this.createdAt, exchangeV1OrdersCreatePost200ResponseOrdersInner.createdAt) &&
        Objects.equals(this.updatedAt, exchangeV1OrdersCreatePost200ResponseOrdersInner.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, market, orderType, side, status, feeAmount, fee, totalQuantity, remainingQuantity, avgPrice, pricePerUnit, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersCreatePost200ResponseOrdersInner {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
    sb.append("    side: ").append(toIndentedString(side)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    feeAmount: ").append(toIndentedString(feeAmount)).append("\n");
    sb.append("    fee: ").append(toIndentedString(fee)).append("\n");
    sb.append("    totalQuantity: ").append(toIndentedString(totalQuantity)).append("\n");
    sb.append("    remainingQuantity: ").append(toIndentedString(remainingQuantity)).append("\n");
    sb.append("    avgPrice: ").append(toIndentedString(avgPrice)).append("\n");
    sb.append("    pricePerUnit: ").append(toIndentedString(pricePerUnit)).append("\n");
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

