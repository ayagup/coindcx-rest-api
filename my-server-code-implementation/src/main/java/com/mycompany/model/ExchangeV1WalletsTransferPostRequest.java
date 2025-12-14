package com.mycompany.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExchangeV1WalletsTransferPostRequest
 */

@JsonTypeName("_exchange_v1_wallets_transfer_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1WalletsTransferPostRequest {

  private Integer timestamp;

  /**
   * The wallet type from which balance has to be transferred
   */
  public enum SourceWalletTypeEnum {
    SPOT("spot"),
    
    FUTURES("futures");

    private final String value;

    SourceWalletTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SourceWalletTypeEnum fromValue(String value) {
      for (SourceWalletTypeEnum b : SourceWalletTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private SourceWalletTypeEnum sourceWalletType;

  /**
   * The wallet type to which balance has to be transferred
   */
  public enum DestinationWalletTypeEnum {
    SPOT("spot"),
    
    FUTURES("futures");

    private final String value;

    DestinationWalletTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static DestinationWalletTypeEnum fromValue(String value) {
      for (DestinationWalletTypeEnum b : DestinationWalletTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private DestinationWalletTypeEnum destinationWalletType;

  private String currencyShortName;

  private Float amount;

  public ExchangeV1WalletsTransferPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1WalletsTransferPostRequest(Integer timestamp, SourceWalletTypeEnum sourceWalletType, DestinationWalletTypeEnum destinationWalletType, String currencyShortName, Float amount) {
    this.timestamp = timestamp;
    this.sourceWalletType = sourceWalletType;
    this.destinationWalletType = destinationWalletType;
    this.currencyShortName = currencyShortName;
    this.amount = amount;
  }

  public ExchangeV1WalletsTransferPostRequest timestamp(Integer timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * EPOCH timestamp in milliseconds
   * @return timestamp
   */
  @NotNull 
  @Schema(name = "timestamp", example = "1629878400000", description = "EPOCH timestamp in milliseconds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public Integer getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Integer timestamp) {
    this.timestamp = timestamp;
  }

  public ExchangeV1WalletsTransferPostRequest sourceWalletType(SourceWalletTypeEnum sourceWalletType) {
    this.sourceWalletType = sourceWalletType;
    return this;
  }

  /**
   * The wallet type from which balance has to be transferred
   * @return sourceWalletType
   */
  @NotNull 
  @Schema(name = "source_wallet_type", example = "spot", description = "The wallet type from which balance has to be transferred", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("source_wallet_type")
  public SourceWalletTypeEnum getSourceWalletType() {
    return sourceWalletType;
  }

  public void setSourceWalletType(SourceWalletTypeEnum sourceWalletType) {
    this.sourceWalletType = sourceWalletType;
  }

  public ExchangeV1WalletsTransferPostRequest destinationWalletType(DestinationWalletTypeEnum destinationWalletType) {
    this.destinationWalletType = destinationWalletType;
    return this;
  }

  /**
   * The wallet type to which balance has to be transferred
   * @return destinationWalletType
   */
  @NotNull 
  @Schema(name = "destination_wallet_type", example = "futures", description = "The wallet type to which balance has to be transferred", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("destination_wallet_type")
  public DestinationWalletTypeEnum getDestinationWalletType() {
    return destinationWalletType;
  }

  public void setDestinationWalletType(DestinationWalletTypeEnum destinationWalletType) {
    this.destinationWalletType = destinationWalletType;
  }

  public ExchangeV1WalletsTransferPostRequest currencyShortName(String currencyShortName) {
    this.currencyShortName = currencyShortName;
    return this;
  }

  /**
   * The type of asset being transferred (e.g., BTC, ETH, USDT)
   * @return currencyShortName
   */
  @NotNull 
  @Schema(name = "currency_short_name", example = "USDT", description = "The type of asset being transferred (e.g., BTC, ETH, USDT)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency_short_name")
  public String getCurrencyShortName() {
    return currencyShortName;
  }

  public void setCurrencyShortName(String currencyShortName) {
    this.currencyShortName = currencyShortName;
  }

  public ExchangeV1WalletsTransferPostRequest amount(Float amount) {
    this.amount = amount;
    return this;
  }

  /**
   * The amount of the asset to transfer
   * @return amount
   */
  @NotNull 
  @Schema(name = "amount", example = "1.5", description = "The amount of the asset to transfer", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1WalletsTransferPostRequest exchangeV1WalletsTransferPostRequest = (ExchangeV1WalletsTransferPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1WalletsTransferPostRequest.timestamp) &&
        Objects.equals(this.sourceWalletType, exchangeV1WalletsTransferPostRequest.sourceWalletType) &&
        Objects.equals(this.destinationWalletType, exchangeV1WalletsTransferPostRequest.destinationWalletType) &&
        Objects.equals(this.currencyShortName, exchangeV1WalletsTransferPostRequest.currencyShortName) &&
        Objects.equals(this.amount, exchangeV1WalletsTransferPostRequest.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, sourceWalletType, destinationWalletType, currencyShortName, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1WalletsTransferPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    sourceWalletType: ").append(toIndentedString(sourceWalletType)).append("\n");
    sb.append("    destinationWalletType: ").append(toIndentedString(destinationWalletType)).append("\n");
    sb.append("    currencyShortName: ").append(toIndentedString(currencyShortName)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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

