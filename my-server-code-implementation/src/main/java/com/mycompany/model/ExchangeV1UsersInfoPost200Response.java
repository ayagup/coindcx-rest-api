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
 * ExchangeV1UsersInfoPost200Response
 */

@JsonTypeName("_exchange_v1_users_info_post_200_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1UsersInfoPost200Response {

  private @Nullable String coindcxId;

  public ExchangeV1UsersInfoPost200Response coindcxId(@Nullable String coindcxId) {
    this.coindcxId = coindcxId;
    return this;
  }

  /**
   * User ID
   * @return coindcxId
   */
  
  @Schema(name = "coindcx_id", description = "User ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("coindcx_id")
  public @Nullable String getCoindcxId() {
    return coindcxId;
  }

  public void setCoindcxId(@Nullable String coindcxId) {
    this.coindcxId = coindcxId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1UsersInfoPost200Response exchangeV1UsersInfoPost200Response = (ExchangeV1UsersInfoPost200Response) o;
    return Objects.equals(this.coindcxId, exchangeV1UsersInfoPost200Response.coindcxId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coindcxId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1UsersInfoPost200Response {\n");
    sb.append("    coindcxId: ").append(toIndentedString(coindcxId)).append("\n");
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

