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
 * ExchangeV1FundingLendPostRequest
 */

@JsonTypeName("_exchange_v1_funding_lend_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1FundingLendPostRequest {

  private String currencyShortName;

  private Long timestamp;

  public ExchangeV1FundingLendPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1FundingLendPostRequest(String currencyShortName, Long timestamp) {
    this.currencyShortName = currencyShortName;
    this.timestamp = timestamp;
  }

  public ExchangeV1FundingLendPostRequest currencyShortName(String currencyShortName) {
    this.currencyShortName = currencyShortName;
    return this;
  }

  /**
   * Currency to lend
   * @return currencyShortName
   */
  @NotNull 
  @Schema(name = "currency_short_name", example = "BTC", description = "Currency to lend", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency_short_name")
  public String getCurrencyShortName() {
    return currencyShortName;
  }

  public void setCurrencyShortName(String currencyShortName) {
    this.currencyShortName = currencyShortName;
  }

  public ExchangeV1FundingLendPostRequest timestamp(Long timestamp) {
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
    ExchangeV1FundingLendPostRequest exchangeV1FundingLendPostRequest = (ExchangeV1FundingLendPostRequest) o;
    return Objects.equals(this.currencyShortName, exchangeV1FundingLendPostRequest.currencyShortName) &&
        Objects.equals(this.timestamp, exchangeV1FundingLendPostRequest.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyShortName, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1FundingLendPostRequest {\n");
    sb.append("    currencyShortName: ").append(toIndentedString(currencyShortName)).append("\n");
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

