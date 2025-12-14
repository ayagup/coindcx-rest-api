package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.mycompany.model.ExchangeV1DerivativesFuturesPositionsExitPost200ResponseData;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExchangeV1DerivativesFuturesPositionsExitPost200Response
 */

@JsonTypeName("_exchange_v1_derivatives_futures_positions_exit_post_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesPositionsExitPost200Response {

  private @Nullable String message;

  private @Nullable Integer status;

  private @Nullable Integer code;

  private @Nullable ExchangeV1DerivativesFuturesPositionsExitPost200ResponseData data;

  public ExchangeV1DerivativesFuturesPositionsExitPost200Response message(@Nullable String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public @Nullable String getMessage() {
    return message;
  }

  public void setMessage(@Nullable String message) {
    this.message = message;
  }

  public ExchangeV1DerivativesFuturesPositionsExitPost200Response status(@Nullable Integer status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable Integer getStatus() {
    return status;
  }

  public void setStatus(@Nullable Integer status) {
    this.status = status;
  }

  public ExchangeV1DerivativesFuturesPositionsExitPost200Response code(@Nullable Integer code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public @Nullable Integer getCode() {
    return code;
  }

  public void setCode(@Nullable Integer code) {
    this.code = code;
  }

  public ExchangeV1DerivativesFuturesPositionsExitPost200Response data(@Nullable ExchangeV1DerivativesFuturesPositionsExitPost200ResponseData data) {
    this.data = data;
    return this;
  }

  /**
   * Get data
   * @return data
   */
  @Valid 
  @Schema(name = "data", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("data")
  public @Nullable ExchangeV1DerivativesFuturesPositionsExitPost200ResponseData getData() {
    return data;
  }

  public void setData(@Nullable ExchangeV1DerivativesFuturesPositionsExitPost200ResponseData data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1DerivativesFuturesPositionsExitPost200Response exchangeV1DerivativesFuturesPositionsExitPost200Response = (ExchangeV1DerivativesFuturesPositionsExitPost200Response) o;
    return Objects.equals(this.message, exchangeV1DerivativesFuturesPositionsExitPost200Response.message) &&
        Objects.equals(this.status, exchangeV1DerivativesFuturesPositionsExitPost200Response.status) &&
        Objects.equals(this.code, exchangeV1DerivativesFuturesPositionsExitPost200Response.code) &&
        Objects.equals(this.data, exchangeV1DerivativesFuturesPositionsExitPost200Response.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, status, code, data);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesPositionsExitPost200Response {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

