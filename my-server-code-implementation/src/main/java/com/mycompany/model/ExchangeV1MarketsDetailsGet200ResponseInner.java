package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
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
 * ExchangeV1MarketsDetailsGet200ResponseInner
 */

@JsonTypeName("_exchange_v1_markets_details_get_200_response_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarketsDetailsGet200ResponseInner {

  private @Nullable String coindcxName;

  private @Nullable String baseCurrencyShortName;

  private @Nullable String targetCurrencyShortName;

  private @Nullable String targetCurrencyName;

  private @Nullable String baseCurrencyName;

  private @Nullable BigDecimal minQuantity;

  private @Nullable BigDecimal maxQuantity;

  private @Nullable BigDecimal minPrice;

  private @Nullable BigDecimal maxPrice;

  private @Nullable BigDecimal minNotional;

  private @Nullable Integer baseCurrencyPrecision;

  private @Nullable Integer targetCurrencyPrecision;

  private @Nullable BigDecimal step;

  @Valid
  private List<String> orderTypes = new ArrayList<>();

  private @Nullable String symbol;

  private @Nullable String ecode;

  private @Nullable BigDecimal maxLeverage;

  private @Nullable BigDecimal maxLeverageShort;

  private @Nullable String pair;

  private @Nullable String status;

  public ExchangeV1MarketsDetailsGet200ResponseInner coindcxName(@Nullable String coindcxName) {
    this.coindcxName = coindcxName;
    return this;
  }

  /**
   * Get coindcxName
   * @return coindcxName
   */
  
  @Schema(name = "coindcx_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("coindcx_name")
  public @Nullable String getCoindcxName() {
    return coindcxName;
  }

  public void setCoindcxName(@Nullable String coindcxName) {
    this.coindcxName = coindcxName;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner baseCurrencyShortName(@Nullable String baseCurrencyShortName) {
    this.baseCurrencyShortName = baseCurrencyShortName;
    return this;
  }

  /**
   * Get baseCurrencyShortName
   * @return baseCurrencyShortName
   */
  
  @Schema(name = "base_currency_short_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("base_currency_short_name")
  public @Nullable String getBaseCurrencyShortName() {
    return baseCurrencyShortName;
  }

  public void setBaseCurrencyShortName(@Nullable String baseCurrencyShortName) {
    this.baseCurrencyShortName = baseCurrencyShortName;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner targetCurrencyShortName(@Nullable String targetCurrencyShortName) {
    this.targetCurrencyShortName = targetCurrencyShortName;
    return this;
  }

  /**
   * Get targetCurrencyShortName
   * @return targetCurrencyShortName
   */
  
  @Schema(name = "target_currency_short_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("target_currency_short_name")
  public @Nullable String getTargetCurrencyShortName() {
    return targetCurrencyShortName;
  }

  public void setTargetCurrencyShortName(@Nullable String targetCurrencyShortName) {
    this.targetCurrencyShortName = targetCurrencyShortName;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner targetCurrencyName(@Nullable String targetCurrencyName) {
    this.targetCurrencyName = targetCurrencyName;
    return this;
  }

  /**
   * Get targetCurrencyName
   * @return targetCurrencyName
   */
  
  @Schema(name = "target_currency_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("target_currency_name")
  public @Nullable String getTargetCurrencyName() {
    return targetCurrencyName;
  }

  public void setTargetCurrencyName(@Nullable String targetCurrencyName) {
    this.targetCurrencyName = targetCurrencyName;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner baseCurrencyName(@Nullable String baseCurrencyName) {
    this.baseCurrencyName = baseCurrencyName;
    return this;
  }

  /**
   * Get baseCurrencyName
   * @return baseCurrencyName
   */
  
  @Schema(name = "base_currency_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("base_currency_name")
  public @Nullable String getBaseCurrencyName() {
    return baseCurrencyName;
  }

  public void setBaseCurrencyName(@Nullable String baseCurrencyName) {
    this.baseCurrencyName = baseCurrencyName;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner minQuantity(@Nullable BigDecimal minQuantity) {
    this.minQuantity = minQuantity;
    return this;
  }

  /**
   * Minimum quantity of target currency for which an order may be placed
   * @return minQuantity
   */
  @Valid 
  @Schema(name = "min_quantity", description = "Minimum quantity of target currency for which an order may be placed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min_quantity")
  public @Nullable BigDecimal getMinQuantity() {
    return minQuantity;
  }

  public void setMinQuantity(@Nullable BigDecimal minQuantity) {
    this.minQuantity = minQuantity;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner maxQuantity(@Nullable BigDecimal maxQuantity) {
    this.maxQuantity = maxQuantity;
    return this;
  }

  /**
   * Maximum quantity of target currency for which an order may be placed
   * @return maxQuantity
   */
  @Valid 
  @Schema(name = "max_quantity", description = "Maximum quantity of target currency for which an order may be placed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("max_quantity")
  public @Nullable BigDecimal getMaxQuantity() {
    return maxQuantity;
  }

  public void setMaxQuantity(@Nullable BigDecimal maxQuantity) {
    this.maxQuantity = maxQuantity;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner minPrice(@Nullable BigDecimal minPrice) {
    this.minPrice = minPrice;
    return this;
  }

  /**
   * Minimum price per unit
   * @return minPrice
   */
  @Valid 
  @Schema(name = "min_price", description = "Minimum price per unit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min_price")
  public @Nullable BigDecimal getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(@Nullable BigDecimal minPrice) {
    this.minPrice = minPrice;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner maxPrice(@Nullable BigDecimal maxPrice) {
    this.maxPrice = maxPrice;
    return this;
  }

  /**
   * Maximum price per unit
   * @return maxPrice
   */
  @Valid 
  @Schema(name = "max_price", description = "Maximum price per unit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("max_price")
  public @Nullable BigDecimal getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(@Nullable BigDecimal maxPrice) {
    this.maxPrice = maxPrice;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner minNotional(@Nullable BigDecimal minNotional) {
    this.minNotional = minNotional;
    return this;
  }

  /**
   * Minimum notional value
   * @return minNotional
   */
  @Valid 
  @Schema(name = "min_notional", description = "Minimum notional value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("min_notional")
  public @Nullable BigDecimal getMinNotional() {
    return minNotional;
  }

  public void setMinNotional(@Nullable BigDecimal minNotional) {
    this.minNotional = minNotional;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner baseCurrencyPrecision(@Nullable Integer baseCurrencyPrecision) {
    this.baseCurrencyPrecision = baseCurrencyPrecision;
    return this;
  }

  /**
   * Get baseCurrencyPrecision
   * @return baseCurrencyPrecision
   */
  
  @Schema(name = "base_currency_precision", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("base_currency_precision")
  public @Nullable Integer getBaseCurrencyPrecision() {
    return baseCurrencyPrecision;
  }

  public void setBaseCurrencyPrecision(@Nullable Integer baseCurrencyPrecision) {
    this.baseCurrencyPrecision = baseCurrencyPrecision;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner targetCurrencyPrecision(@Nullable Integer targetCurrencyPrecision) {
    this.targetCurrencyPrecision = targetCurrencyPrecision;
    return this;
  }

  /**
   * Get targetCurrencyPrecision
   * @return targetCurrencyPrecision
   */
  
  @Schema(name = "target_currency_precision", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("target_currency_precision")
  public @Nullable Integer getTargetCurrencyPrecision() {
    return targetCurrencyPrecision;
  }

  public void setTargetCurrencyPrecision(@Nullable Integer targetCurrencyPrecision) {
    this.targetCurrencyPrecision = targetCurrencyPrecision;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner step(@Nullable BigDecimal step) {
    this.step = step;
    return this;
  }

  /**
   * Price precision
   * @return step
   */
  @Valid 
  @Schema(name = "step", description = "Price precision", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("step")
  public @Nullable BigDecimal getStep() {
    return step;
  }

  public void setStep(@Nullable BigDecimal step) {
    this.step = step;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner orderTypes(List<String> orderTypes) {
    this.orderTypes = orderTypes;
    return this;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner addOrderTypesItem(String orderTypesItem) {
    if (this.orderTypes == null) {
      this.orderTypes = new ArrayList<>();
    }
    this.orderTypes.add(orderTypesItem);
    return this;
  }

  /**
   * Get orderTypes
   * @return orderTypes
   */
  
  @Schema(name = "order_types", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("order_types")
  public List<String> getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(List<String> orderTypes) {
    this.orderTypes = orderTypes;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner symbol(@Nullable String symbol) {
    this.symbol = symbol;
    return this;
  }

  /**
   * Get symbol
   * @return symbol
   */
  
  @Schema(name = "symbol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("symbol")
  public @Nullable String getSymbol() {
    return symbol;
  }

  public void setSymbol(@Nullable String symbol) {
    this.symbol = symbol;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner ecode(@Nullable String ecode) {
    this.ecode = ecode;
    return this;
  }

  /**
   * Exchange code
   * @return ecode
   */
  
  @Schema(name = "ecode", description = "Exchange code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ecode")
  public @Nullable String getEcode() {
    return ecode;
  }

  public void setEcode(@Nullable String ecode) {
    this.ecode = ecode;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner maxLeverage(@Nullable BigDecimal maxLeverage) {
    this.maxLeverage = maxLeverage;
    return this;
  }

  /**
   * Get maxLeverage
   * @return maxLeverage
   */
  @Valid 
  @Schema(name = "max_leverage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("max_leverage")
  public @Nullable BigDecimal getMaxLeverage() {
    return maxLeverage;
  }

  public void setMaxLeverage(@Nullable BigDecimal maxLeverage) {
    this.maxLeverage = maxLeverage;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner maxLeverageShort(@Nullable BigDecimal maxLeverageShort) {
    this.maxLeverageShort = maxLeverageShort;
    return this;
  }

  /**
   * Get maxLeverageShort
   * @return maxLeverageShort
   */
  @Valid 
  @Schema(name = "max_leverage_short", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("max_leverage_short")
  public @Nullable BigDecimal getMaxLeverageShort() {
    return maxLeverageShort;
  }

  public void setMaxLeverageShort(@Nullable BigDecimal maxLeverageShort) {
    this.maxLeverageShort = maxLeverageShort;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner pair(@Nullable String pair) {
    this.pair = pair;
    return this;
  }

  /**
   * String created by (ecode, target_currency_short_name, base_currency_short_name)
   * @return pair
   */
  
  @Schema(name = "pair", description = "String created by (ecode, target_currency_short_name, base_currency_short_name)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pair")
  public @Nullable String getPair() {
    return pair;
  }

  public void setPair(@Nullable String pair) {
    this.pair = pair;
  }

  public ExchangeV1MarketsDetailsGet200ResponseInner status(@Nullable String status) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1MarketsDetailsGet200ResponseInner exchangeV1MarketsDetailsGet200ResponseInner = (ExchangeV1MarketsDetailsGet200ResponseInner) o;
    return Objects.equals(this.coindcxName, exchangeV1MarketsDetailsGet200ResponseInner.coindcxName) &&
        Objects.equals(this.baseCurrencyShortName, exchangeV1MarketsDetailsGet200ResponseInner.baseCurrencyShortName) &&
        Objects.equals(this.targetCurrencyShortName, exchangeV1MarketsDetailsGet200ResponseInner.targetCurrencyShortName) &&
        Objects.equals(this.targetCurrencyName, exchangeV1MarketsDetailsGet200ResponseInner.targetCurrencyName) &&
        Objects.equals(this.baseCurrencyName, exchangeV1MarketsDetailsGet200ResponseInner.baseCurrencyName) &&
        Objects.equals(this.minQuantity, exchangeV1MarketsDetailsGet200ResponseInner.minQuantity) &&
        Objects.equals(this.maxQuantity, exchangeV1MarketsDetailsGet200ResponseInner.maxQuantity) &&
        Objects.equals(this.minPrice, exchangeV1MarketsDetailsGet200ResponseInner.minPrice) &&
        Objects.equals(this.maxPrice, exchangeV1MarketsDetailsGet200ResponseInner.maxPrice) &&
        Objects.equals(this.minNotional, exchangeV1MarketsDetailsGet200ResponseInner.minNotional) &&
        Objects.equals(this.baseCurrencyPrecision, exchangeV1MarketsDetailsGet200ResponseInner.baseCurrencyPrecision) &&
        Objects.equals(this.targetCurrencyPrecision, exchangeV1MarketsDetailsGet200ResponseInner.targetCurrencyPrecision) &&
        Objects.equals(this.step, exchangeV1MarketsDetailsGet200ResponseInner.step) &&
        Objects.equals(this.orderTypes, exchangeV1MarketsDetailsGet200ResponseInner.orderTypes) &&
        Objects.equals(this.symbol, exchangeV1MarketsDetailsGet200ResponseInner.symbol) &&
        Objects.equals(this.ecode, exchangeV1MarketsDetailsGet200ResponseInner.ecode) &&
        Objects.equals(this.maxLeverage, exchangeV1MarketsDetailsGet200ResponseInner.maxLeverage) &&
        Objects.equals(this.maxLeverageShort, exchangeV1MarketsDetailsGet200ResponseInner.maxLeverageShort) &&
        Objects.equals(this.pair, exchangeV1MarketsDetailsGet200ResponseInner.pair) &&
        Objects.equals(this.status, exchangeV1MarketsDetailsGet200ResponseInner.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coindcxName, baseCurrencyShortName, targetCurrencyShortName, targetCurrencyName, baseCurrencyName, minQuantity, maxQuantity, minPrice, maxPrice, minNotional, baseCurrencyPrecision, targetCurrencyPrecision, step, orderTypes, symbol, ecode, maxLeverage, maxLeverageShort, pair, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarketsDetailsGet200ResponseInner {\n");
    sb.append("    coindcxName: ").append(toIndentedString(coindcxName)).append("\n");
    sb.append("    baseCurrencyShortName: ").append(toIndentedString(baseCurrencyShortName)).append("\n");
    sb.append("    targetCurrencyShortName: ").append(toIndentedString(targetCurrencyShortName)).append("\n");
    sb.append("    targetCurrencyName: ").append(toIndentedString(targetCurrencyName)).append("\n");
    sb.append("    baseCurrencyName: ").append(toIndentedString(baseCurrencyName)).append("\n");
    sb.append("    minQuantity: ").append(toIndentedString(minQuantity)).append("\n");
    sb.append("    maxQuantity: ").append(toIndentedString(maxQuantity)).append("\n");
    sb.append("    minPrice: ").append(toIndentedString(minPrice)).append("\n");
    sb.append("    maxPrice: ").append(toIndentedString(maxPrice)).append("\n");
    sb.append("    minNotional: ").append(toIndentedString(minNotional)).append("\n");
    sb.append("    baseCurrencyPrecision: ").append(toIndentedString(baseCurrencyPrecision)).append("\n");
    sb.append("    targetCurrencyPrecision: ").append(toIndentedString(targetCurrencyPrecision)).append("\n");
    sb.append("    step: ").append(toIndentedString(step)).append("\n");
    sb.append("    orderTypes: ").append(toIndentedString(orderTypes)).append("\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    ecode: ").append(toIndentedString(ecode)).append("\n");
    sb.append("    maxLeverage: ").append(toIndentedString(maxLeverage)).append("\n");
    sb.append("    maxLeverageShort: ").append(toIndentedString(maxLeverageShort)).append("\n");
    sb.append("    pair: ").append(toIndentedString(pair)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

