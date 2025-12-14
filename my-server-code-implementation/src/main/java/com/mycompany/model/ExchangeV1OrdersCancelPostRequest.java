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
 * ExchangeV1OrdersCancelPostRequest
 */

@JsonTypeName("_exchange_v1_orders_cancel_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersCancelPostRequest {

  private String id;

  private Long timestamp;

  public ExchangeV1OrdersCancelPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1OrdersCancelPostRequest(String id, Long timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }

  public ExchangeV1OrdersCancelPostRequest id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The ID of the order
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "ead19992-43fd-11e8-b027-bb815bcb14ed", description = "The ID of the order", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ExchangeV1OrdersCancelPostRequest timestamp(Long timestamp) {
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
    ExchangeV1OrdersCancelPostRequest exchangeV1OrdersCancelPostRequest = (ExchangeV1OrdersCancelPostRequest) o;
    return Objects.equals(this.id, exchangeV1OrdersCancelPostRequest.id) &&
        Objects.equals(this.timestamp, exchangeV1OrdersCancelPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersCancelPostRequest {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

