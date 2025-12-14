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
 * ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_positions_cancel_all_open_orders_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest {

  private Long timestamp;

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

  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest(Long timestamp) {
    this.timestamp = timestamp;
  }

  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest timestamp(Long timestamp) {
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

  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest marginCurrencyShortName(List<MarginCurrencyShortNameEnum> marginCurrencyShortName) {
    this.marginCurrencyShortName = marginCurrencyShortName;
    return this;
  }

  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest addMarginCurrencyShortNameItem(MarginCurrencyShortNameEnum marginCurrencyShortNameItem) {
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
    ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest = (ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest.timestamp) &&
        Objects.equals(this.marginCurrencyShortName, exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest.marginCurrencyShortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, marginCurrencyShortName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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

