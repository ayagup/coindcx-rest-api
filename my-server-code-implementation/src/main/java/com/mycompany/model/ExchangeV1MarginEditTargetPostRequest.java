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
 * ExchangeV1MarginEditTargetPostRequest
 */

@JsonTypeName("_exchange_v1_margin_edit_target_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarginEditTargetPostRequest {

  private String id;

  private BigDecimal targetPrice;

  private Integer timestamp;

  public ExchangeV1MarginEditTargetPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1MarginEditTargetPostRequest(String id, BigDecimal targetPrice, Integer timestamp) {
    this.id = id;
    this.targetPrice = targetPrice;
    this.timestamp = timestamp;
  }

  public ExchangeV1MarginEditTargetPostRequest id(String id) {
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

  public ExchangeV1MarginEditTargetPostRequest targetPrice(BigDecimal targetPrice) {
    this.targetPrice = targetPrice;
    return this;
  }

  /**
   * New target price
   * @return targetPrice
   */
  @NotNull @Valid 
  @Schema(name = "target_price", example = "0.082", description = "New target price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("target_price")
  public BigDecimal getTargetPrice() {
    return targetPrice;
  }

  public void setTargetPrice(BigDecimal targetPrice) {
    this.targetPrice = targetPrice;
  }

  public ExchangeV1MarginEditTargetPostRequest timestamp(Integer timestamp) {
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
    ExchangeV1MarginEditTargetPostRequest exchangeV1MarginEditTargetPostRequest = (ExchangeV1MarginEditTargetPostRequest) o;
    return Objects.equals(this.id, exchangeV1MarginEditTargetPostRequest.id) &&
        Objects.equals(this.targetPrice, exchangeV1MarginEditTargetPostRequest.targetPrice) &&
        Objects.equals(this.timestamp, exchangeV1MarginEditTargetPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, targetPrice, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarginEditTargetPostRequest {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    targetPrice: ").append(toIndentedString(targetPrice)).append("\n");
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

