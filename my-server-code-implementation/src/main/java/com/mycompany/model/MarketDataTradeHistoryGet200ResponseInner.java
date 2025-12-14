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
 * MarketDataTradeHistoryGet200ResponseInner
 */

@JsonTypeName("_market_data_trade_history_get_200_response_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class MarketDataTradeHistoryGet200ResponseInner {

  private @Nullable BigDecimal p;

  private @Nullable BigDecimal q;

  private @Nullable Integer T;

  private @Nullable Boolean m;

  public MarketDataTradeHistoryGet200ResponseInner p(@Nullable BigDecimal p) {
    this.p = p;
    return this;
  }

  /**
   * Trade price
   * @return p
   */
  @Valid 
  @Schema(name = "p", description = "Trade price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("p")
  public @Nullable BigDecimal getP() {
    return p;
  }

  public void setP(@Nullable BigDecimal p) {
    this.p = p;
  }

  public MarketDataTradeHistoryGet200ResponseInner q(@Nullable BigDecimal q) {
    this.q = q;
    return this;
  }

  /**
   * Quantity
   * @return q
   */
  @Valid 
  @Schema(name = "q", description = "Quantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("q")
  public @Nullable BigDecimal getQ() {
    return q;
  }

  public void setQ(@Nullable BigDecimal q) {
    this.q = q;
  }

  public MarketDataTradeHistoryGet200ResponseInner T(@Nullable Integer T) {
    this.T = T;
    return this;
  }

  /**
   * Timestamp
   * @return T
   */
  
  @Schema(name = "T", description = "Timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("T")
  public @Nullable Integer getT() {
    return T;
  }

  public void setT(@Nullable Integer T) {
    this.T = T;
  }

  public MarketDataTradeHistoryGet200ResponseInner m(@Nullable Boolean m) {
    this.m = m;
    return this;
  }

  /**
   * Whether the buyer is market maker
   * @return m
   */
  
  @Schema(name = "m", description = "Whether the buyer is market maker", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("m")
  public @Nullable Boolean getM() {
    return m;
  }

  public void setM(@Nullable Boolean m) {
    this.m = m;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarketDataTradeHistoryGet200ResponseInner marketDataTradeHistoryGet200ResponseInner = (MarketDataTradeHistoryGet200ResponseInner) o;
    return Objects.equals(this.p, marketDataTradeHistoryGet200ResponseInner.p) &&
        Objects.equals(this.q, marketDataTradeHistoryGet200ResponseInner.q) &&
        Objects.equals(this.T, marketDataTradeHistoryGet200ResponseInner.T) &&
        Objects.equals(this.m, marketDataTradeHistoryGet200ResponseInner.m);
  }

  @Override
  public int hashCode() {
    return Objects.hash(p, q, T, m);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarketDataTradeHistoryGet200ResponseInner {\n");
    sb.append("    p: ").append(toIndentedString(p)).append("\n");
    sb.append("    q: ").append(toIndentedString(q)).append("\n");
    sb.append("    T: ").append(toIndentedString(T)).append("\n");
    sb.append("    m: ").append(toIndentedString(m)).append("\n");
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

