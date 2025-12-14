package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * ExchangeV1MarginAddMarginPostRequest
 */

@JsonTypeName("_exchange_v1_margin_add_margin_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarginAddMarginPostRequest {

  private String id;

  private BigDecimal amount;

  private Integer timestamp;

  public ExchangeV1MarginAddMarginPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1MarginAddMarginPostRequest(String id, BigDecimal amount, Integer timestamp) {
    this.id = id;
    this.amount = amount;
    this.timestamp = timestamp;
  }

  public ExchangeV1MarginAddMarginPostRequest id(String id) {
    this.id = id;
    return this;
  }

  /**
   * ID of the margin order
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "ead19992-43fd-11e8-b027-bb815bcb14ed", description = "ID of the margin order", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ExchangeV1MarginAddMarginPostRequest amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Amount to add in the margin to decrease effective leverage
   * @return amount
   */
  @NotNull @Valid 
  @Schema(name = "amount", example = "0.06", description = "Amount to add in the margin to decrease effective leverage", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public ExchangeV1MarginAddMarginPostRequest timestamp(Integer timestamp) {
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
    ExchangeV1MarginAddMarginPostRequest exchangeV1MarginAddMarginPostRequest = (ExchangeV1MarginAddMarginPostRequest) o;
    return Objects.equals(this.id, exchangeV1MarginAddMarginPostRequest.id) &&
        Objects.equals(this.amount, exchangeV1MarginAddMarginPostRequest.amount) &&
        Objects.equals(this.timestamp, exchangeV1MarginAddMarginPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, amount, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarginAddMarginPostRequest {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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

