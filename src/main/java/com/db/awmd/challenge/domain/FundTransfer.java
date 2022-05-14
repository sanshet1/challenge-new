package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FundTransfer {

	@NotNull
	@NotBlank
	private final String accountFrom;

	@NotNull
	@NotBlank
	private final String accountTo;

	@Positive
	private BigDecimal amount;

	@JsonCreator
	public FundTransfer(@JsonProperty("accountFrom") String accountFrom, @JsonProperty("accountTo") String accountTo,
			@JsonProperty("amount") BigDecimal amount) {
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
		this.amount = amount;
	}

}
