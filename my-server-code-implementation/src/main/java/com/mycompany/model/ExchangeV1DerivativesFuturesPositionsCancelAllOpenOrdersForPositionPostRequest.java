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
 * ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_positions_cancel_all_open_orders_for_position_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest {

  private Long timestamp;

  private String id;

  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest(Long timestamp, String id) {
    this.timestamp = timestamp;
    this.id = id;
  }

  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest timestamp(Long timestamp) {
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

  public ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Position ID
   * @return id
   */
  @NotNull 
  @Schema(name = "id", description = "Position ID", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest = (ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest.timestamp) &&
        Objects.equals(this.id, exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest {\n");
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

