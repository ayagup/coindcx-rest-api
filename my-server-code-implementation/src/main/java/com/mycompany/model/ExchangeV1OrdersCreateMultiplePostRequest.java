package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.mycompany.model.ExchangeV1OrdersCreateMultiplePostRequestOrdersInner;
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
 * ExchangeV1OrdersCreateMultiplePostRequest
 */

@JsonTypeName("_exchange_v1_orders_create_multiple_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1OrdersCreateMultiplePostRequest {

  @Valid
  private List<@Valid ExchangeV1OrdersCreateMultiplePostRequestOrdersInner> orders = new ArrayList<>();

  public ExchangeV1OrdersCreateMultiplePostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1OrdersCreateMultiplePostRequest(List<@Valid ExchangeV1OrdersCreateMultiplePostRequestOrdersInner> orders) {
    this.orders = orders;
  }

  public ExchangeV1OrdersCreateMultiplePostRequest orders(List<@Valid ExchangeV1OrdersCreateMultiplePostRequestOrdersInner> orders) {
    this.orders = orders;
    return this;
  }

  public ExchangeV1OrdersCreateMultiplePostRequest addOrdersItem(ExchangeV1OrdersCreateMultiplePostRequestOrdersInner ordersItem) {
    if (this.orders == null) {
      this.orders = new ArrayList<>();
    }
    this.orders.add(ordersItem);
    return this;
  }

  /**
   * Get orders
   * @return orders
   */
  @NotNull @Valid 
  @Schema(name = "orders", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("orders")
  public List<@Valid ExchangeV1OrdersCreateMultiplePostRequestOrdersInner> getOrders() {
    return orders;
  }

  public void setOrders(List<@Valid ExchangeV1OrdersCreateMultiplePostRequestOrdersInner> orders) {
    this.orders = orders;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1OrdersCreateMultiplePostRequest exchangeV1OrdersCreateMultiplePostRequest = (ExchangeV1OrdersCreateMultiplePostRequest) o;
    return Objects.equals(this.orders, exchangeV1OrdersCreateMultiplePostRequest.orders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1OrdersCreateMultiplePostRequest {\n");
    sb.append("    orders: ").append(toIndentedString(orders)).append("\n");
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

