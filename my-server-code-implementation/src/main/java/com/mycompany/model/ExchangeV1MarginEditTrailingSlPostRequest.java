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
 * ExchangeV1MarginEditTrailingSlPostRequest
 */

@JsonTypeName("_exchange_v1_margin_edit_trailing_sl_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1MarginEditTrailingSlPostRequest {

  private String id;

  private Boolean trailingSl;

  private @Nullable BigDecimal trailPercent;

  private Integer timestamp;

  public ExchangeV1MarginEditTrailingSlPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1MarginEditTrailingSlPostRequest(String id, Boolean trailingSl, Integer timestamp) {
    this.id = id;
    this.trailingSl = trailingSl;
    this.timestamp = timestamp;
  }

  public ExchangeV1MarginEditTrailingSlPostRequest id(String id) {
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

  public ExchangeV1MarginEditTrailingSlPostRequest trailingSl(Boolean trailingSl) {
    this.trailingSl = trailingSl;
    return this;
  }

  /**
   * Enable or disable trailing stop loss
   * @return trailingSl
   */
  @NotNull 
  @Schema(name = "trailing_sl", example = "true", description = "Enable or disable trailing stop loss", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("trailing_sl")
  public Boolean getTrailingSl() {
    return trailingSl;
  }

  public void setTrailingSl(Boolean trailingSl) {
    this.trailingSl = trailingSl;
  }

  public ExchangeV1MarginEditTrailingSlPostRequest trailPercent(@Nullable BigDecimal trailPercent) {
    this.trailPercent = trailPercent;
    return this;
  }

  /**
   * Trailing percentage
   * @return trailPercent
   */
  @Valid 
  @Schema(name = "trail_percent", example = "5.0", description = "Trailing percentage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("trail_percent")
  public @Nullable BigDecimal getTrailPercent() {
    return trailPercent;
  }

  public void setTrailPercent(@Nullable BigDecimal trailPercent) {
    this.trailPercent = trailPercent;
  }

  public ExchangeV1MarginEditTrailingSlPostRequest timestamp(Integer timestamp) {
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
    ExchangeV1MarginEditTrailingSlPostRequest exchangeV1MarginEditTrailingSlPostRequest = (ExchangeV1MarginEditTrailingSlPostRequest) o;
    return Objects.equals(this.id, exchangeV1MarginEditTrailingSlPostRequest.id) &&
        Objects.equals(this.trailingSl, exchangeV1MarginEditTrailingSlPostRequest.trailingSl) &&
        Objects.equals(this.trailPercent, exchangeV1MarginEditTrailingSlPostRequest.trailPercent) &&
        Objects.equals(this.timestamp, exchangeV1MarginEditTrailingSlPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, trailingSl, trailPercent, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1MarginEditTrailingSlPostRequest {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    trailingSl: ").append(toIndentedString(trailingSl)).append("\n");
    sb.append("    trailPercent: ").append(toIndentedString(trailPercent)).append("\n");
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

