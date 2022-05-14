package com.db.awmd.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.FundTransfer;
import com.db.awmd.challenge.exception.IllegalOperationException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.InvalidAccountNumberException;
import com.db.awmd.challenge.repository.AccountsRepository;

public class FundTransferServiceTest {

	private AccountsRepository repository = Mockito.mock(AccountsRepository.class);

	private NotificationService notificationService = Mockito.mock(NotificationService.class);

	private FundTransferService fundTransferService = new FundTransferService(repository, notificationService);

	@Test
	public void transferFundTestSuccess() {

		FundTransfer fundTransfer = FundTransfer.builder().accountFrom("001").accountTo("002")
				.amount(new BigDecimal(100)).build();
		when(repository.getAccount("001"))
				.thenReturn(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		when(repository.getAccount("002"))
				.thenReturn(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());
		String response = fundTransferService.transferFund(fundTransfer);
		assertThat(response.equalsIgnoreCase("Success"));
	}

	@Test
	public void transferFundTestForNegativeAmount() {

		FundTransfer fundTransfer = FundTransfer.builder().accountFrom("001").accountTo("002")
				.amount(new BigDecimal(-100)).build();
		when(repository.getAccount("001"))
				.thenReturn(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		when(repository.getAccount("002"))
				.thenReturn(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());
		Assertions.assertThrows(IllegalOperationException.class, () -> {
			fundTransferService.transferFund(fundTransfer);
		});
	}

	@Test
	public void transferFundTestForTransferMoreAmountThenPresentBalance() {

		FundTransfer fundTransfer = FundTransfer.builder().accountFrom("001").accountTo("002")
				.amount(new BigDecimal(1500)).build();
		when(repository.getAccount("001"))
				.thenReturn(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		when(repository.getAccount("002"))
				.thenReturn(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());
		Assertions.assertThrows(InsufficientBalanceException.class, () -> {
			fundTransferService.transferFund(fundTransfer);
		});
	}

	@Test
	public void transferFundTestForTransferToInvalidToAccount() {

		FundTransfer fundTransfer = FundTransfer.builder().accountFrom("001").accountTo("003")
				.amount(new BigDecimal(100)).build();
		when(repository.getAccount("001"))
				.thenReturn(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		when(repository.getAccount("002"))
				.thenReturn(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());
		Assertions.assertThrows(InvalidAccountNumberException.class, () -> {
			fundTransferService.transferFund(fundTransfer);
		});
	}

	@Test
	public void transferFundTestForTransferToInvalidFromAccount() {

		FundTransfer fundTransfer = FundTransfer.builder().accountFrom("003").accountTo("002")
				.amount(new BigDecimal(100)).build();
		when(repository.getAccount("001"))
				.thenReturn(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		when(repository.getAccount("002"))
				.thenReturn(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());
		Assertions.assertThrows(InvalidAccountNumberException.class, () -> {
			fundTransferService.transferFund(fundTransfer);
		});
	}

	@Test
	public void transferFundTestForTransferZeroAmount() {

		FundTransfer fundTransfer = FundTransfer.builder().accountFrom("001").accountTo("002").amount(new BigDecimal(0))
				.build();
		when(repository.getAccount("001"))
				.thenReturn(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		when(repository.getAccount("002"))
				.thenReturn(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());
		Assertions.assertThrows(IllegalOperationException.class, () -> {
			fundTransferService.transferFund(fundTransfer);
		});
	}

	@Test
	public void transferFundTestForTransferAmountBTNSameAccount() {

		FundTransfer fundTransfer = FundTransfer.builder().accountFrom("001").accountTo("001")
				.amount(new BigDecimal(100)).build();
		when(repository.getAccount("001"))
				.thenReturn(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		Assertions.assertThrows(InvalidAccountNumberException.class, () -> {
			fundTransferService.transferFund(fundTransfer);
		});
	}

	@Test
	public void transferFundTestForTransferWhenAccountIDISSpace() {

		FundTransfer fundTransfer = FundTransfer.builder().accountFrom("001").accountTo(" ").amount(new BigDecimal(100))
				.build();
		when(repository.getAccount("001"))
				.thenReturn(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		Assertions.assertThrows(InvalidAccountNumberException.class, () -> {
			fundTransferService.transferFund(fundTransfer);
		});
	}
}
