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
 * ExchangeV1WalletsSubAccountTransferPostRequest
 */

@JsonTypeName("_exchange_v1_wallets_sub_account_transfer_post_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1WalletsSubAccountTransferPostRequest {

  private Integer timestamp;

  private String fromAccountId;

  private String toAccountId;

  private String currencyShortName;

  private Float amount;

  public ExchangeV1WalletsSubAccountTransferPostRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ExchangeV1WalletsSubAccountTransferPostRequest(Integer timestamp, String fromAccountId, String toAccountId, String currencyShortName, Float amount) {
    this.timestamp = timestamp;
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.currencyShortName = currencyShortName;
    this.amount = amount;
  }

  public ExchangeV1WalletsSubAccountTransferPostRequest timestamp(Integer timestamp) {
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

  public ExchangeV1WalletsSubAccountTransferPostRequest fromAccountId(String fromAccountId) {
    this.fromAccountId = fromAccountId;
    return this;
  }

  /**
   * The account from which assets are being transferred (main or sub-account ID)
   * @return fromAccountId
   */
  @NotNull 
  @Schema(name = "from_account_id", example = "xxxxx", description = "The account from which assets are being transferred (main or sub-account ID)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("from_account_id")
  public String getFromAccountId() {
    return fromAccountId;
  }

  public void setFromAccountId(String fromAccountId) {
    this.fromAccountId = fromAccountId;
  }

  public ExchangeV1WalletsSubAccountTransferPostRequest toAccountId(String toAccountId) {
    this.toAccountId = toAccountId;
    return this;
  }

  /**
   * The account to which assets are being transferred (main or sub-account ID)
   * @return toAccountId
   */
  @NotNull 
  @Schema(name = "to_account_id", example = "yyyyy", description = "The account to which assets are being transferred (main or sub-account ID)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("to_account_id")
  public String getToAccountId() {
    return toAccountId;
  }

  public void setToAccountId(String toAccountId) {
    this.toAccountId = toAccountId;
  }

  public ExchangeV1WalletsSubAccountTransferPostRequest currencyShortName(String currencyShortName) {
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

  public ExchangeV1WalletsSubAccountTransferPostRequest amount(Float amount) {
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
    ExchangeV1WalletsSubAccountTransferPostRequest exchangeV1WalletsSubAccountTransferPostRequest = (ExchangeV1WalletsSubAccountTransferPostRequest) o;
    return Objects.equals(this.timestamp, exchangeV1WalletsSubAccountTransferPostRequest.timestamp) &&
        Objects.equals(this.fromAccountId, exchangeV1WalletsSubAccountTransferPostRequest.fromAccountId) &&
        Objects.equals(this.toAccountId, exchangeV1WalletsSubAccountTransferPostRequest.toAccountId) &&
        Objects.equals(this.currencyShortName, exchangeV1WalletsSubAccountTransferPostRequest.currencyShortName) &&
        Objects.equals(this.amount, exchangeV1WalletsSubAccountTransferPostRequest.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, fromAccountId, toAccountId, currencyShortName, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1WalletsSubAccountTransferPostRequest {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    fromAccountId: ").append(toIndentedString(fromAccountId)).append("\n");
    sb.append("    toAccountId: ").append(toIndentedString(toAccountId)).append("\n");
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

