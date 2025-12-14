package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * ExchangeV1DerivativesFuturesTradesPostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_trades_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesTradesPostRequest {

  private Long timestamp;

  private @Nullable String pair;

  private @Nullable String orderId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate fromDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate toDate;

  private String page;

  private String size;

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

  public ExchangeV1DerivativesFuturesTradesPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesTradesPostRequest(Long timestamp, String page, String size) {
    this.timestamp = timestamp;
    this.page = page;
    this.size = size;
  }

  public ExchangeV1DerivativesFuturesTradesPostRequest timestamp(Long timestamp) {
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

  public ExchangeV1DerivativesFuturesTradesPostRequest pair(@Nullable String pair) {
    this.pair = pair;
    return this;
  }

  /**
   * Instrument pair (optional)
   * @return pair
   */
  
  @Schema(name = "pair", example = "B-BTC_USDT", description = "Instrument pair (optional)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pair")
  public @Nullable String getPair() {
    return pair;
  }

  public void setPair(@Nullable String pair) {
    this.pair = pair;
  }

  public ExchangeV1DerivativesFuturesTradesPostRequest orderId(@Nullable String orderId) {
    this.orderId = orderId;
    return this;
  }

  /**
   * Order ID (optional)
   * @return orderId
   */
  
  @Schema(name = "order_id", description = "Order ID (optional)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("order_id")
  public @Nullable String getOrderId() {
    return orderId;
  }

  public void setOrderId(@Nullable String orderId) {
    this.orderId = orderId;
  }

  public ExchangeV1DerivativesFuturesTradesPostRequest fromDate(@Nullable LocalDate fromDate) {
    this.fromDate = fromDate;
    return this;
  }

  /**
   * Start date (YYYY-MM-DD)
   * @return fromDate
   */
  @Valid 
  @Schema(name = "from_date", example = "2024-01-01", description = "Start date (YYYY-MM-DD)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("from_date")
  public @Nullable LocalDate getFromDate() {
    return fromDate;
  }

  public void setFromDate(@Nullable LocalDate fromDate) {
    this.fromDate = fromDate;
  }

  public ExchangeV1DerivativesFuturesTradesPostRequest toDate(@Nullable LocalDate toDate) {
    this.toDate = toDate;
    return this;
  }

  /**
   * End date (YYYY-MM-DD)
   * @return toDate
   */
  @Valid 
  @Schema(name = "to_date", example = "2024-01-31", description = "End date (YYYY-MM-DD)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("to_date")
  public @Nullable LocalDate getToDate() {
    return toDate;
  }

  public void setToDate(@Nullable LocalDate toDate) {
    this.toDate = toDate;
  }

  public ExchangeV1DerivativesFuturesTradesPostRequest page(String page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
   */
  @NotNull 
  @Schema(name = "page", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("page")
  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public ExchangeV1DerivativesFuturesTradesPostRequest size(String size) {
    this.size = size;
    return this;
  }

  /**
   * Get size
   * @return size
   */
  @NotNull 
  @Schema(name = "size", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("size")
  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public ExchangeV1DerivativesFuturesTradesPostRequest marginCurrencyShortName(List<MarginCurrencyShortNameEnum> marginCurrencyShortName) {
    this.marginCurrencyShortName = marginCurrencyShortName;
    return this;
  }

  public ExchangeV1DerivativesFuturesTradesPostRequest addMarginCurrencyShortNameItem(MarginCurrencyShortNameEnum marginCurrencyShortNameItem) {
    if (this.marginCurrencyShortName == null) {
      this.marginCurrencyShortName = new ArrayList<>(Arrays.asList(MarginCurrencyShortNameEnum.USDT));
    }
    this.marginCurrencyShortName.add(marginCurrencyShortNameItem);
    return this;
  }

  /**
   * Get marginCurrencyShortName
   * @return marginCurrencyShortName
   */
  
  @Schema(name = "margin_currency_short_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    ExchangeV1DerivativesFuturesTradesPostRequest exchangeV1DerivativesFuturesTradesPostRequest = (ExchangeV1DerivativesFuturesTradesPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesTradesPostRequest.timestamp) &&
        Objects.equals(this.pair, exchangeV1DerivativesFuturesTradesPostRequest.pair) &&
        Objects.equals(this.orderId, exchangeV1DerivativesFuturesTradesPostRequest.orderId) &&
        Objects.equals(this.fromDate, exchangeV1DerivativesFuturesTradesPostRequest.fromDate) &&
        Objects.equals(this.toDate, exchangeV1DerivativesFuturesTradesPostRequest.toDate) &&
        Objects.equals(this.page, exchangeV1DerivativesFuturesTradesPostRequest.page) &&
        Objects.equals(this.size, exchangeV1DerivativesFuturesTradesPostRequest.size) &&
        Objects.equals(this.marginCurrencyShortName, exchangeV1DerivativesFuturesTradesPostRequest.marginCurrencyShortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, pair, orderId, fromDate, toDate, page, size, marginCurrencyShortName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesTradesPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    pair: ").append(toIndentedString(pair)).append("\n");
    sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
    sb.append("    fromDate: ").append(toIndentedString(fromDate)).append("\n");
    sb.append("    toDate: ").append(toIndentedString(toDate)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
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

