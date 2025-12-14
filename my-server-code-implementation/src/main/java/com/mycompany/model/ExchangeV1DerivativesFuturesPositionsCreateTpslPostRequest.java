package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.mycompany.model.ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss;
import com.mycompany.model.ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest
 */

@JsonTypeName("_exchange_v1_derivatives_futures_positions_create_tpsl_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest {

  private Long timestamp;

  private String id;

  private @Nullable ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit takeProfit;

  private @Nullable ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss stopLoss;

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest(Long timestamp, String id) {
    this.timestamp = timestamp;
    this.id = id;
  }

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest timestamp(Long timestamp) {
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

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Position ID
   * @return id
   */
  @NotNull 
  @Schema(name = "id", description = "Position ID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest takeProfit(@Nullable ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit takeProfit) {
    this.takeProfit = takeProfit;
    return this;
  }

  /**
   * Get takeProfit
   * @return takeProfit
   */
  @Valid 
  @Schema(name = "take_profit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("take_profit")
  public @Nullable ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit getTakeProfit() {
    return takeProfit;
  }

  public void setTakeProfit(@Nullable ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit takeProfit) {
    this.takeProfit = takeProfit;
  }

  public ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest stopLoss(@Nullable ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss stopLoss) {
    this.stopLoss = stopLoss;
    return this;
  }

  /**
   * Get stopLoss
   * @return stopLoss
   */
  @Valid 
  @Schema(name = "stop_loss", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stop_loss")
  public @Nullable ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss getStopLoss() {
    return stopLoss;
  }

  public void setStopLoss(@Nullable ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss stopLoss) {
    this.stopLoss = stopLoss;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest = (ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest.timestamp) &&
        Objects.equals(this.id, exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest.id) &&
        Objects.equals(this.takeProfit, exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest.takeProfit) &&
        Objects.equals(this.stopLoss, exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest.stopLoss);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, id, takeProfit, stopLoss);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    takeProfit: ").append(toIndentedString(takeProfit)).append("\n");
    sb.append("    stopLoss: ").append(toIndentedString(stopLoss)).append("\n");
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

