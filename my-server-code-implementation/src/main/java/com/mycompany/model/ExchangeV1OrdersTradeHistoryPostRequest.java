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
 * ExchangeV1OrdersTradeHistoryPostRequest
 */

@JsonTypeName("_exchange_v1_orders_trade_history_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersTradeHistoryPostRequest {

  private Integer limit = 500;

  private @Nullable Integer fromId;

  /**
   * Sort order
   */
  public enum SortEnum {
    ASC("asc"),
    
    DESC("desc");

    private final String value;

    SortEnum(String value) {
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
    public static SortEnum fromValue(String value) {
      for (SortEnum b : SortEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private SortEnum sort = SortEnum.ASC;

  private Long timestamp;

  public ExchangeV1OrdersTradeHistoryPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1OrdersTradeHistoryPostRequest(Long timestamp) {
    this.timestamp = timestamp;
  }

  public ExchangeV1OrdersTradeHistoryPostRequest limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Number of trades to return
   * minimum: 1
   * maximum: 5000
   * @return limit
   */
  @Min(value = 1) @Max(value = 5000) 
  @Schema(name = "limit", description = "Number of trades to return", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("limit")
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public ExchangeV1OrdersTradeHistoryPostRequest fromId(@Nullable Integer fromId) {
    this.fromId = fromId;
    return this;
  }

  /**
   * Trade ID after which you want the data
   * @return fromId
   */
  
  @Schema(name = "from_id", example = "28473", description = "Trade ID after which you want the data", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("from_id")
  public @Nullable Integer getFromId() {
    return fromId;
  }

  public void setFromId(@Nullable Integer fromId) {
    this.fromId = fromId;
  }

  public ExchangeV1OrdersTradeHistoryPostRequest sort(SortEnum sort) {
    this.sort = sort;
    return this;
  }

  /**
   * Sort order
   * @return sort
   */
  
  @Schema(name = "sort", description = "Sort order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sort")
  public SortEnum getSort() {
    return sort;
  }

  public void setSort(SortEnum sort) {
    this.sort = sort;
  }

  public ExchangeV1OrdersTradeHistoryPostRequest timestamp(Long timestamp) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1OrdersTradeHistoryPostRequest exchangeV1OrdersTradeHistoryPostRequest = (ExchangeV1OrdersTradeHistoryPostRequest) o;
    return Objects.equals(this.limit, exchangeV1OrdersTradeHistoryPostRequest.limit) &&
        Objects.equals(this.fromId, exchangeV1OrdersTradeHistoryPostRequest.fromId) &&
        Objects.equals(this.sort, exchangeV1OrdersTradeHistoryPostRequest.sort) &&
        Objects.equals(this.timestamp, exchangeV1OrdersTradeHistoryPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(limit, fromId, sort, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersTradeHistoryPostRequest {\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    fromId: ").append(toIndentedString(fromId)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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

