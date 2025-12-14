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
 * ExchangeV1WalletsSubAccountTransferPost200Response
 */

@JsonTypeName("_exchange_v1_wallets_sub_account_transfer_post_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1WalletsSubAccountTransferPost200Response {

  private @Nullable String status;

  private @Nullable Integer message;

  private @Nullable Integer code;

  public ExchangeV1WalletsSubAccountTransferPost200Response status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", example = "success", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  public ExchangeV1WalletsSubAccountTransferPost200Response message(@Nullable Integer message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", example = "200", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public @Nullable Integer getMessage() {
    return message;
  }

  public void setMessage(@Nullable Integer message) {
    this.message = message;
  }

  public ExchangeV1WalletsSubAccountTransferPost200Response code(@Nullable Integer code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", example = "200", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public @Nullable Integer getCode() {
    return code;
  }

  public void setCode(@Nullable Integer code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1WalletsSubAccountTransferPost200Response exchangeV1WalletsSubAccountTransferPost200Response = (ExchangeV1WalletsSubAccountTransferPost200Response) o;
    return Objects.equals(this.status, exchangeV1WalletsSubAccountTransferPost200Response.status) &&
        Objects.equals(this.message, exchangeV1WalletsSubAccountTransferPost200Response.message) &&
        Objects.equals(this.code, exchangeV1WalletsSubAccountTransferPost200Response.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message, code);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1WalletsSubAccountTransferPost200Response {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

