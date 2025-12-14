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
 * ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_positions_remove_margin_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest {

  private Long timestamp;

  private String id;

  private BigDecimal amount;

  public ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest(Long timestamp, String id, BigDecimal amount) {
    this.timestamp = timestamp;
    this.id = id;
    this.amount = amount;
  }

  public ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest timestamp(Long timestamp) {
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

  public ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest id(String id) {
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

  public ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Amount to remove (in INR for INR margin, USDT for USDT margin)
   * @return amount
   */
  @NotNull @Valid 
  @Schema(name = "amount", description = "Amount to remove (in INR for INR margin, USDT for USDT margin)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest exchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest = (ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest.timestamp) &&
        Objects.equals(this.id, exchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest.id) &&
        Objects.equals(this.amount, exchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, id, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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

