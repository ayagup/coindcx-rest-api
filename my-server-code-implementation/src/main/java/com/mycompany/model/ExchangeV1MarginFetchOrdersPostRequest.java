package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * ExchangeV1MarginFetchOrdersPostRequest
 */

@JsonTypeName("_exchange_v1_margin_fetch_orders_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarginFetchOrdersPostRequest {

  private @Nullable String market;

  private Boolean details = false;

  /**
   * The status of the order (default - all orders)
   */
  public enum StatusEnum {
    INIT("init"),
    
    OPEN("open"),
    
    CLOSE("close"),
    
    REJECTED("rejected"),
    
    CANCELLED("cancelled"),
    
    PARTIAL_ENTRY("partial_entry"),
    
    PARTIAL_CLOSE("partial_close"),
    
    TRIGGERED("triggered");

    private final String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable StatusEnum status;

  private BigDecimal size = new BigDecimal("10");

  private Integer timestamp;

  public ExchangeV1MarginFetchOrdersPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1MarginFetchOrdersPostRequest(Integer timestamp) {
    this.timestamp = timestamp;
  }

  public ExchangeV1MarginFetchOrdersPostRequest market(@Nullable String market) {
    this.market = market;
    return this;
  }

  /**
   * The trading pair (default - orders for all markets)
   * @return market
   */
  
  @Schema(name = "market", example = "XRPBTC", description = "The trading pair (default - orders for all markets)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("market")
  public @Nullable String getMarket() {
    return market;
  }

  public void setMarket(@Nullable String market) {
    this.market = market;
  }

  public ExchangeV1MarginFetchOrdersPostRequest details(Boolean details) {
    this.details = details;
    return this;
  }

  /**
   * Whether you want detailed information or not
   * @return details
   */
  
  @Schema(name = "details", example = "false", description = "Whether you want detailed information or not", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("details")
  public Boolean getDetails() {
    return details;
  }

  public void setDetails(Boolean details) {
    this.details = details;
  }

  public ExchangeV1MarginFetchOrdersPostRequest status(@Nullable StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * The status of the order (default - all orders)
   * @return status
   */
  
  @Schema(name = "status", example = "open", description = "The status of the order (default - all orders)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable StatusEnum getStatus() {
    return status;
  }

  public void setStatus(@Nullable StatusEnum status) {
    this.status = status;
  }

  public ExchangeV1MarginFetchOrdersPostRequest size(BigDecimal size) {
    this.size = size;
    return this;
  }

  /**
   * Number of records per page
   * @return size
   */
  @Valid 
  @Schema(name = "size", example = "20", description = "Number of records per page", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("size")
  public BigDecimal getSize() {
    return size;
  }

  public void setSize(BigDecimal size) {
    this.size = size;
  }

  public ExchangeV1MarginFetchOrdersPostRequest timestamp(Integer timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * EPOCH timestamp in milliseconds
   * @return timestamp
   */
  @NotNull 
  @Schema(name = "timestamp", example = "1524211224000", description = "EPOCH timestamp in milliseconds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Integer timestamp) {
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
    ExchangeV1MarginFetchOrdersPostRequest exchangeV1MarginFetchOrdersPostRequest = (ExchangeV1MarginFetchOrdersPostRequest) o;
    return Objects.equals(this.market, exchangeV1MarginFetchOrdersPostRequest.market) &&
        Objects.equals(this.details, exchangeV1MarginFetchOrdersPostRequest.details) &&
        Objects.equals(this.status, exchangeV1MarginFetchOrdersPostRequest.status) &&
        Objects.equals(this.size, exchangeV1MarginFetchOrdersPostRequest.size) &&
        Objects.equals(this.timestamp, exchangeV1MarginFetchOrdersPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(market, details, status, size, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarginFetchOrdersPostRequest {\n");
    sb.append("    market: ").append(toIndentedString(market)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
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

