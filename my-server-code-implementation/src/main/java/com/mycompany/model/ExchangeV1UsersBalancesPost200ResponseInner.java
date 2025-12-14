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
 * ExchangeV1UsersBalancesPost200ResponseInner
 */

@JsonTypeName("_exchange_v1_users_balances_post_200_response_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-04T20:05:18.570459+05:30[Asia/Calcutta]", comments = "Generator version: 7.17.0")
public class ExchangeV1UsersBalancesPost200ResponseInner {

  private @Nullable String currency;

  private @Nullable BigDecimal balance;

  private @Nullable BigDecimal lockedBalance;

  public ExchangeV1UsersBalancesPost200ResponseInner currency(@Nullable String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public @Nullable String getCurrency() {
    return currency;
  }

  public void setCurrency(@Nullable String currency) {
    this.currency = currency;
  }

  public ExchangeV1UsersBalancesPost200ResponseInner balance(@Nullable BigDecimal balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Get balance
   * @return balance
   */
  @Valid 
  @Schema(name = "balance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("balance")
  public @Nullable BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(@Nullable BigDecimal balance) {
    this.balance = balance;
  }

  public ExchangeV1UsersBalancesPost200ResponseInner lockedBalance(@Nullable BigDecimal lockedBalance) {
    this.lockedBalance = lockedBalance;
    return this;
  }

  /**
   * Balance currently being used by an open order
   * @return lockedBalance
   */
  @Valid 
  @Schema(name = "locked_balance", description = "Balance currently being used by an open order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locked_balance")
  public @Nullable BigDecimal getLockedBalance() {
    return lockedBalance;
  }

  public void setLockedBalance(@Nullable BigDecimal lockedBalance) {
    this.lockedBalance = lockedBalance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExchangeV1UsersBalancesPost200ResponseInner exchangeV1UsersBalancesPost200ResponseInner = (ExchangeV1UsersBalancesPost200ResponseInner) o;
    return Objects.equals(this.currency, exchangeV1UsersBalancesPost200ResponseInner.currency) &&
        Objects.equals(this.balance, exchangeV1UsersBalancesPost200ResponseInner.balance) &&
        Objects.equals(this.lockedBalance, exchangeV1UsersBalancesPost200ResponseInner.lockedBalance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currency, balance, lockedBalance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExchangeV1UsersBalancesPost200ResponseInner {\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("    lockedBalance: ").append(toIndentedString(lockedBalance)).append("\n");
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

