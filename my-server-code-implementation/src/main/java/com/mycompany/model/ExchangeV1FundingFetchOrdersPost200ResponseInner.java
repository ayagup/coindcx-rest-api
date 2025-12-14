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
 * ExchangeV1FundingFetchOrdersPost200ResponseInner
 */

@JsonTypeName("_exchange_v1_funding_fetch_orders_post_200_response_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1FundingFetchOrdersPost200ResponseInner {

  private @Nullable String id;

  private @Nullable String currencyShortName;

  public ExchangeV1FundingFetchOrdersPost200ResponseInner id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  public ExchangeV1FundingFetchOrdersPost200ResponseInner currencyShortName(@Nullable String currencyShortName) {
    this.currencyShortName = currencyShortName;
    return this;
  }

  /**
   * Get currencyShortName
   * @return currencyShortName
   */
  
  @Schema(name = "currency_short_name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency_short_name")
  public @Nullable String getCurrencyShortName() {
    return currencyShortName;
  }

  public void setCurrencyShortName(@Nullable String currencyShortName) {
    this.currencyShortName = currencyShortName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1FundingFetchOrdersPost200ResponseInner exchangeV1FundingFetchOrdersPost200ResponseInner = (ExchangeV1FundingFetchOrdersPost200ResponseInner) o;
    return Objects.equals(this.id, exchangeV1FundingFetchOrdersPost200ResponseInner.id) &&
        Objects.equals(this.currencyShortName, exchangeV1FundingFetchOrdersPost200ResponseInner.currencyShortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, currencyShortName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1FundingFetchOrdersPost200ResponseInner {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    currencyShortName: ").append(toIndentedString(currencyShortName)).append("\n");
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

