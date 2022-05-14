package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.exception.IllegalOperationException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }
  
  
  public BigDecimal credit(BigDecimal amount) {
      validate(amount);

      balance = balance.add(amount);
      return balance;
  }

  public BigDecimal debit(BigDecimal amount) {
      validate(amount);

      if (balance.compareTo(amount) < 0) {
          throw new InsufficientBalanceException("Debit can't be performed due to lack of funds on the account.");
      }

      balance = balance.subtract(amount);
      return balance;
  }
  
  private void validate(BigDecimal amount) {
      if (Objects.isNull(amount) || BigDecimal.ZERO.compareTo(amount) > 0) {
			throw new IllegalOperationException("Negative amount can not be transferred");
      }
  }
}
