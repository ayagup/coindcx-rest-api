package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExchangeV1OrdersCancelByIdsPostRequest
 */

@JsonTypeName("_exchange_v1_orders_cancel_by_ids_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersCancelByIdsPostRequest {

  @Valid
  private List<String> ids = new ArrayList<>();

  public ExchangeV1OrdersCancelByIdsPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1OrdersCancelByIdsPostRequest(List<String> ids) {
    this.ids = ids;
  }

  public ExchangeV1OrdersCancelByIdsPostRequest ids(List<String> ids) {
    this.ids = ids;
    return this;
  }

  public ExchangeV1OrdersCancelByIdsPostRequest addIdsItem(String idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * Array of order IDs
   * @return ids
   */
  @NotNull 
  @Schema(name = "ids", example = "[\"8a2f4284-c895-11e8-9e00-5b2c002a6ff4\",\"8a1d1e4c-c895-11e8-9dff-df1480546936\"]", description = "Array of order IDs", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ids")
  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1OrdersCancelByIdsPostRequest exchangeV1OrdersCancelByIdsPostRequest = (ExchangeV1OrdersCancelByIdsPostRequest) o;
    return Objects.equals(this.ids, exchangeV1OrdersCancelByIdsPostRequest.ids);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ids);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersCancelByIdsPostRequest {\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
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

