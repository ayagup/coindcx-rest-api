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
 * ExchangeV1MarginOrderPostRequest
 */

@JsonTypeName("_exchange_v1_margin_order_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarginOrderPostRequest {

  private String id;

  private Boolean details = false;

  private Integer timestamp;

  public ExchangeV1MarginOrderPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1MarginOrderPostRequest(String id, Integer timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }

  public ExchangeV1MarginOrderPostRequest id(String id) {
    this.id = id;
    return this;
  }

  /**
   * ID of the order
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "30b5002f-d9c1-413d-8a8d-0fd32b054c9c", description = "ID of the order", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ExchangeV1MarginOrderPostRequest details(Boolean details) {
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

  public ExchangeV1MarginOrderPostRequest timestamp(Integer timestamp) {
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
    ExchangeV1MarginOrderPostRequest exchangeV1MarginOrderPostRequest = (ExchangeV1MarginOrderPostRequest) o;
    return Objects.equals(this.id, exchangeV1MarginOrderPostRequest.id) &&
        Objects.equals(this.details, exchangeV1MarginOrderPostRequest.details) &&
        Objects.equals(this.timestamp, exchangeV1MarginOrderPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, details, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarginOrderPostRequest {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

