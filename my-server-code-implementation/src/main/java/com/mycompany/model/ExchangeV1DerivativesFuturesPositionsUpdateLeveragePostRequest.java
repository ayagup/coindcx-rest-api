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
 * ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_positions_update_leverage_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest {

  private Long timestamp;

  private String leverage;

  private @Nullable String pair;

  private @Nullable String id;

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

  private MarginCurrencyShortNameEnum marginCurrencyShortName = MarginCurrencyShortNameEnum.USDT;

  public ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest(Long timestamp, String leverage, MarginCurrencyShortNameEnum marginCurrencyShortName) {
    this.timestamp = timestamp;
    this.leverage = leverage;
    this.marginCurrencyShortName = marginCurrencyShortName;
  }

  public ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest timestamp(Long timestamp) {
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

  public ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest leverage(String leverage) {
    this.leverage = leverage;
    return this;
  }

  /**
   * Leverage value
   * @return leverage
   */
  @NotNull 
  @Schema(name = "leverage", example = "5", description = "Leverage value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("leverage")
  public String getLeverage() {
    return leverage;
  }

  public void setLeverage(String leverage) {
    this.leverage = leverage;
  }

  public ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest pair(@Nullable String pair) {
    this.pair = pair;
    return this;
  }

  /**
   * Instrument pair (use either pair or id)
   * @return pair
   */
  
  @Schema(name = "pair", description = "Instrument pair (use either pair or id)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pair")
  public @Nullable String getPair() {
    return pair;
  }

  public void setPair(@Nullable String pair) {
    this.pair = pair;
  }

  public ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Position ID (use either pair or id)
   * @return id
   */
  
  @Schema(name = "id", description = "Position ID (use either pair or id)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  public ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest marginCurrencyShortName(MarginCurrencyShortNameEnum marginCurrencyShortName) {
    this.marginCurrencyShortName = marginCurrencyShortName;
    return this;
  }

  /**
   * Get marginCurrencyShortName
   * @return marginCurrencyShortName
   */
  @NotNull 
  @Schema(name = "margin_currency_short_name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("margin_currency_short_name")
  public MarginCurrencyShortNameEnum getMarginCurrencyShortName() {
    return marginCurrencyShortName;
  }

  public void setMarginCurrencyShortName(MarginCurrencyShortNameEnum marginCurrencyShortName) {
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
    ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest = (ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest.timestamp) &&
        Objects.equals(this.leverage, exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest.leverage) &&
        Objects.equals(this.pair, exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest.pair) &&
        Objects.equals(this.id, exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest.id) &&
        Objects.equals(this.marginCurrencyShortName, exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest.marginCurrencyShortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, leverage, pair, id, marginCurrencyShortName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    leverage: ").append(toIndentedString(leverage)).append("\n");
    sb.append("    pair: ").append(toIndentedString(pair)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

