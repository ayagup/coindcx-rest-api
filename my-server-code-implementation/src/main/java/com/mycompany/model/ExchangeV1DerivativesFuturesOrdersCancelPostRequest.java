package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExchangeV1DerivativesFuturesOrdersCancelPostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_orders_cancel_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesOrdersCancelPostRequest {

  private Long timestamp;

  private String id;

  public ExchangeV1DerivativesFuturesOrdersCancelPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesOrdersCancelPostRequest(Long timestamp, String id) {
    this.timestamp = timestamp;
    this.id = id;
  }

  public ExchangeV1DerivativesFuturesOrdersCancelPostRequest timestamp(Long timestamp) {
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

  public ExchangeV1DerivativesFuturesOrdersCancelPostRequest id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Order ID
   * @return id
   */
  @NotNull 
  @Schema(name = "id", description = "Order ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1DerivativesFuturesOrdersCancelPostRequest exchangeV1DerivativesFuturesOrdersCancelPostRequest = (ExchangeV1DerivativesFuturesOrdersCancelPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesOrdersCancelPostRequest.timestamp) &&
        Objects.equals(this.id, exchangeV1DerivativesFuturesOrdersCancelPostRequest.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesOrdersCancelPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

