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
 * MarketDataCandlesGet200ResponseInner
 */

@JsonTypeName("_market_data_candles_get_200_response_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class MarketDataCandlesGet200ResponseInner {

  private @Nullable Integer time;

  private @Nullable BigDecimal open;

  private @Nullable BigDecimal high;

  private @Nullable BigDecimal low;

  private @Nullable BigDecimal close;

  private @Nullable BigDecimal volume;

  public MarketDataCandlesGet200ResponseInner time(@Nullable Integer time) {
    this.time = time;
    return this;
  }

  /**
   * Get time
   * @return time
   */
  
  @Schema(name = "time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("time")
  public @Nullable Integer getTime() {
    return time;
  }

  public void setTime(@Nullable Integer time) {
    this.time = time;
  }

  public MarketDataCandlesGet200ResponseInner open(@Nullable BigDecimal open) {
    this.open = open;
    return this;
  }

  /**
   * Get open
   * @return open
   */
  @Valid 
  @Schema(name = "open", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("open")
  public @Nullable BigDecimal getOpen() {
    return open;
  }

  public void setOpen(@Nullable BigDecimal open) {
    this.open = open;
  }

  public MarketDataCandlesGet200ResponseInner high(@Nullable BigDecimal high) {
    this.high = high;
    return this;
  }

  /**
   * Get high
   * @return high
   */
  @Valid 
  @Schema(name = "high", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("high")
  public @Nullable BigDecimal getHigh() {
    return high;
  }

  public void setHigh(@Nullable BigDecimal high) {
    this.high = high;
  }

  public MarketDataCandlesGet200ResponseInner low(@Nullable BigDecimal low) {
    this.low = low;
    return this;
  }

  /**
   * Get low
   * @return low
   */
  @Valid 
  @Schema(name = "low", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("low")
  public @Nullable BigDecimal getLow() {
    return low;
  }

  public void setLow(@Nullable BigDecimal low) {
    this.low = low;
  }

  public MarketDataCandlesGet200ResponseInner close(@Nullable BigDecimal close) {
    this.close = close;
    return this;
  }

  /**
   * Get close
   * @return close
   */
  @Valid 
  @Schema(name = "close", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("close")
  public @Nullable BigDecimal getClose() {
    return close;
  }

  public void setClose(@Nullable BigDecimal close) {
    this.close = close;
  }

  public MarketDataCandlesGet200ResponseInner volume(@Nullable BigDecimal volume) {
    this.volume = volume;
    return this;
  }

  /**
   * Get volume
   * @return volume
   */
  @Valid 
  @Schema(name = "volume", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("volume")
  public @Nullable BigDecimal getVolume() {
    return volume;
  }

  public void setVolume(@Nullable BigDecimal volume) {
    this.volume = volume;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarketDataCandlesGet200ResponseInner marketDataCandlesGet200ResponseInner = (MarketDataCandlesGet200ResponseInner) o;
    return Objects.equals(this.time, marketDataCandlesGet200ResponseInner.time) &&
        Objects.equals(this.open, marketDataCandlesGet200ResponseInner.open) &&
        Objects.equals(this.high, marketDataCandlesGet200ResponseInner.high) &&
        Objects.equals(this.low, marketDataCandlesGet200ResponseInner.low) &&
        Objects.equals(this.close, marketDataCandlesGet200ResponseInner.close) &&
        Objects.equals(this.volume, marketDataCandlesGet200ResponseInner.volume);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, open, high, low, close, volume);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarketDataCandlesGet200ResponseInner {\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    open: ").append(toIndentedString(open)).append("\n");
    sb.append("    high: ").append(toIndentedString(high)).append("\n");
    sb.append("    low: ").append(toIndentedString(low)).append("\n");
    sb.append("    close: ").append(toIndentedString(close)).append("\n");
    sb.append("    volume: ").append(toIndentedString(volume)).append("\n");
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

