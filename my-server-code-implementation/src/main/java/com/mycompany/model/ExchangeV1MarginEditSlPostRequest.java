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
 * ExchangeV1MarginEditSlPostRequest
 */

@JsonTypeName("_exchange_v1_margin_edit_sl_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarginEditSlPostRequest {

  private String id;

  private BigDecimal slPrice;

  private Integer timestamp;

  public ExchangeV1MarginEditSlPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1MarginEditSlPostRequest(String id, BigDecimal slPrice, Integer timestamp) {
    this.id = id;
    this.slPrice = slPrice;
    this.timestamp = timestamp;
  }

  public ExchangeV1MarginEditSlPostRequest id(String id) {
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

  public ExchangeV1MarginEditSlPostRequest slPrice(BigDecimal slPrice) {
    this.slPrice = slPrice;
    return this;
  }

  /**
   * New stop loss price
   * @return slPrice
   */
  @NotNull @Valid 
  @Schema(name = "sl_price", example = "0.00005005", description = "New stop loss price", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sl_price")
  public BigDecimal getSlPrice() {
    return slPrice;
  }

  public void setSlPrice(BigDecimal slPrice) {
    this.slPrice = slPrice;
  }

  public ExchangeV1MarginEditSlPostRequest timestamp(Integer timestamp) {
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
    ExchangeV1MarginEditSlPostRequest exchangeV1MarginEditSlPostRequest = (ExchangeV1MarginEditSlPostRequest) o;
    return Objects.equals(this.id, exchangeV1MarginEditSlPostRequest.id) &&
        Objects.equals(this.slPrice, exchangeV1MarginEditSlPostRequest.slPrice) &&
        Objects.equals(this.timestamp, exchangeV1MarginEditSlPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, slPrice, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarginEditSlPostRequest {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    slPrice: ").append(toIndentedString(slPrice)).append("\n");
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

