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
 * ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_positions_transactions_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest {

  private Long timestamp;

  /**
   * Transaction stage filter
   */
  public enum StageEnum {
    ALL("all"),
    
    DEFAULT("default"),
    
    FUNDING("funding"),
    
    EXIT("exit"),
    
    TPSL_EXIT("tpsl_exit"),
    
    LIQUIDATION("liquidation");

    private final String value;

    StageEnum(String value) {
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
    public static StageEnum fromValue(String value) {
      for (StageEnum b : StageEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StageEnum stage;

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

  public ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest(Long timestamp, StageEnum stage, String page, String size) {
    this.timestamp = timestamp;
    this.stage = stage;
    this.page = page;
    this.size = size;
  }

  public ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest timestamp(Long timestamp) {
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

  public ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest stage(StageEnum stage) {
    this.stage = stage;
    return this;
  }

  /**
   * Transaction stage filter
   * @return stage
   */
  @NotNull 
  @Schema(name = "stage", description = "Transaction stage filter", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("stage")
  public StageEnum getStage() {
    return stage;
  }

  public void setStage(StageEnum stage) {
    this.stage = stage;
  }

  public ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest page(String page) {
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

  public ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest size(String size) {
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

  public ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest marginCurrencyShortName(List<MarginCurrencyShortNameEnum> marginCurrencyShortName) {
    this.marginCurrencyShortName = marginCurrencyShortName;
    return this;
  }

  public ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest addMarginCurrencyShortNameItem(MarginCurrencyShortNameEnum marginCurrencyShortNameItem) {
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
    ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest exchangeV1DerivativesFuturesPositionsTransactionsPostRequest = (ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesPositionsTransactionsPostRequest.timestamp) &&
        Objects.equals(this.stage, exchangeV1DerivativesFuturesPositionsTransactionsPostRequest.stage) &&
        Objects.equals(this.page, exchangeV1DerivativesFuturesPositionsTransactionsPostRequest.page) &&
        Objects.equals(this.size, exchangeV1DerivativesFuturesPositionsTransactionsPostRequest.size) &&
        Objects.equals(this.marginCurrencyShortName, exchangeV1DerivativesFuturesPositionsTransactionsPostRequest.marginCurrencyShortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, stage, page, size, marginCurrencyShortName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    stage: ").append(toIndentedString(stage)).append("\n");
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

