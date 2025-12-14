package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * ExchangeV1OrdersStatusMultiplePostRequest
 */

@JsonTypeName("_exchange_v1_orders_status_multiple_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersStatusMultiplePostRequest {

  @Valid
  private List<String> ids = new ArrayList<>();

  @Valid
  private List<String> clientOrderIds = new ArrayList<>();

  private Long timestamp;

  public ExchangeV1OrdersStatusMultiplePostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1OrdersStatusMultiplePostRequest(List<String> ids, Long timestamp) {
    this.ids = ids;
    this.timestamp = timestamp;
  }

  public ExchangeV1OrdersStatusMultiplePostRequest ids(List<String> ids) {
    this.ids = ids;
    return this;
  }

  public ExchangeV1OrdersStatusMultiplePostRequest addIdsItem(String idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * Array of order IDs
   * @return ids
   */
  @NotNull 
  @Schema(name = "ids", example = "[\"ead19992-43fd-11e8-b027-bb815bcb14ed\",\"8a1d1e4c-c895-11e8-9dff-df1480546936\"]", description = "Array of order IDs", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ids")
  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }

  public ExchangeV1OrdersStatusMultiplePostRequest clientOrderIds(List<String> clientOrderIds) {
    this.clientOrderIds = clientOrderIds;
    return this;
  }

  public ExchangeV1OrdersStatusMultiplePostRequest addClientOrderIdsItem(String clientOrderIdsItem) {
    if (this.clientOrderIds == null) {
      this.clientOrderIds = new ArrayList<>();
    }
    this.clientOrderIds.add(clientOrderIdsItem);
    return this;
  }

  /**
   * Array of client order IDs
   * @return clientOrderIds
   */
  
  @Schema(name = "client_order_ids", example = "[\"2022.02.14-btcinr_1\",\"2022.02.14-btcinr_2\"]", description = "Array of client order IDs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("client_order_ids")
  public List<String> getClientOrderIds() {
    return clientOrderIds;
  }

  public void setClientOrderIds(List<String> clientOrderIds) {
    this.clientOrderIds = clientOrderIds;
  }

  public ExchangeV1OrdersStatusMultiplePostRequest timestamp(Long timestamp) {
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
    ExchangeV1OrdersStatusMultiplePostRequest exchangeV1OrdersStatusMultiplePostRequest = (ExchangeV1OrdersStatusMultiplePostRequest) o;
    return Objects.equals(this.ids, exchangeV1OrdersStatusMultiplePostRequest.ids) &&
        Objects.equals(this.clientOrderIds, exchangeV1OrdersStatusMultiplePostRequest.clientOrderIds) &&
        Objects.equals(this.timestamp, exchangeV1OrdersStatusMultiplePostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ids, clientOrderIds, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersStatusMultiplePostRequest {\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
    sb.append("    clientOrderIds: ").append(toIndentedString(clientOrderIds)).append("\n");
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

