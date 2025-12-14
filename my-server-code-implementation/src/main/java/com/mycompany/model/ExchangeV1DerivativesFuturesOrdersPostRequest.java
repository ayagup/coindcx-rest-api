package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * ExchangeV1DerivativesFuturesOrdersPostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_orders_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesOrdersPostRequest {

  private Long timestamp;

  private String page;

  private String size;

  private @Nullable String pairs;

  private @Nullable String orderIds;

  /**
   * Gets or Sets marginCurrencyShortName
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

  @Valid
  private List<MarginCurrencyShortNameEnum> marginCurrencyShortName = new ArrayList<>(Arrays.asList(MarginCurrencyShortNameEnum.USDT));

  public ExchangeV1DerivativesFuturesOrdersPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesOrdersPostRequest(Long timestamp, String page, String size) {
    this.timestamp = timestamp;
    this.page = page;
    this.size = size;
  }

  public ExchangeV1DerivativesFuturesOrdersPostRequest timestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * EPOCH timestamp in milliseconds
   * @return timestamp
   */
  @NotNull 
  @Schema(name = "timestamp", description = "EPOCH timestamp in milliseconds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public ExchangeV1DerivativesFuturesOrdersPostRequest page(String page) {
    this.page = page;
    return this;
  }

  /**
   * Page number
   * @return page
   */
  @NotNull 
  @Schema(name = "page", example = "1", description = "Page number", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("page")
  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public ExchangeV1DerivativesFuturesOrdersPostRequest size(String size) {
    this.size = size;
    return this;
  }

  /**
   * Number of records per page
   * @return size
   */
  @NotNull 
  @Schema(name = "size", example = "10", description = "Number of records per page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("size")
  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public ExchangeV1DerivativesFuturesOrdersPostRequest pairs(@Nullable String pairs) {
    this.pairs = pairs;
    return this;
  }

  /**
   * Comma-separated list of pairs (optional)
   * @return pairs
   */
  
  @Schema(name = "pairs", example = "B-BTC_USDT,B-ETH_USDT", description = "Comma-separated list of pairs (optional)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pairs")
  public @Nullable String getPairs() {
    return pairs;
  }

  public void setPairs(@Nullable String pairs) {
    this.pairs = pairs;
  }

  public ExchangeV1DerivativesFuturesOrdersPostRequest orderIds(@Nullable String orderIds) {
    this.orderIds = orderIds;
    return this;
  }

  /**
   * Comma-separated list of order IDs (optional)
   * @return orderIds
   */
  
  @Schema(name = "order_ids", description = "Comma-separated list of order IDs (optional)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("order_ids")
  public @Nullable String getOrderIds() {
    return orderIds;
  }

  public void setOrderIds(@Nullable String orderIds) {
    this.orderIds = orderIds;
  }

  public ExchangeV1DerivativesFuturesOrdersPostRequest marginCurrencyShortName(List<MarginCurrencyShortNameEnum> marginCurrencyShortName) {
    this.marginCurrencyShortName = marginCurrencyShortName;
    return this;
  }

  public ExchangeV1DerivativesFuturesOrdersPostRequest addMarginCurrencyShortNameItem(MarginCurrencyShortNameEnum marginCurrencyShortNameItem) {
    if (this.marginCurrencyShortName == null) {
      this.marginCurrencyShortName = new ArrayList<>(Arrays.asList(MarginCurrencyShortNameEnum.USDT));
    }
    this.marginCurrencyShortName.add(marginCurrencyShortNameItem);
    return this;
  }

  /**
   * Futures margin mode
   * @return marginCurrencyShortName
   */
  
  @Schema(name = "margin_currency_short_name", description = "Futures margin mode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("margin_currency_short_name")
  public List<MarginCurrencyShortNameEnum> getMarginCurrencyShortName() {
    return marginCurrencyShortName;
  }

  public void setMarginCurrencyShortName(List<MarginCurrencyShortNameEnum> marginCurrencyShortName) {
    this.marginCurrencyShortName = marginCurrencyShortName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1DerivativesFuturesOrdersPostRequest exchangeV1DerivativesFuturesOrdersPostRequest = (ExchangeV1DerivativesFuturesOrdersPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesOrdersPostRequest.timestamp) &&
        Objects.equals(this.page, exchangeV1DerivativesFuturesOrdersPostRequest.page) &&
        Objects.equals(this.size, exchangeV1DerivativesFuturesOrdersPostRequest.size) &&
        Objects.equals(this.pairs, exchangeV1DerivativesFuturesOrdersPostRequest.pairs) &&
        Objects.equals(this.orderIds, exchangeV1DerivativesFuturesOrdersPostRequest.orderIds) &&
        Objects.equals(this.marginCurrencyShortName, exchangeV1DerivativesFuturesOrdersPostRequest.marginCurrencyShortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, page, size, pairs, orderIds, marginCurrencyShortName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesOrdersPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    pairs: ").append(toIndentedString(pairs)).append("\n");
    sb.append("    orderIds: ").append(toIndentedString(orderIds)).append("\n");
    sb.append("    marginCurrencyShortName: ").append(toIndentedString(marginCurrencyShortName)).append("\n");
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

