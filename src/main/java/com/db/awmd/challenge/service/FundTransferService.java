package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.FundTransfer;
import com.db.awmd.challenge.exception.IllegalOperationException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.InvalidAccountNumberException;
import com.db.awmd.challenge.repository.AccountsRepository;

@Service
public class FundTransferService {

	private final AccountsRepository repository;

	private final NotificationService notificationService;

	@Autowired
	public FundTransferService(AccountsRepository repository, NotificationService notificationService) {
		this.repository = repository;
		this.notificationService = notificationService;
	}

	public String transferFund(final FundTransfer fundTransfer) {

		if (BigDecimal.ZERO.compareTo(fundTransfer.getAmount()) == 0) {
			throw new IllegalOperationException("Transfer amount should be greater than zero.");
		}

		Account accountFrom = repository.getAccount(fundTransfer.getAccountFrom());
		Account accountTo = repository.getAccount(fundTransfer.getAccountTo());

		validate(fundTransfer, accountFrom, accountTo);

		synchronized (accountFrom) {
			synchronized (accountTo) {
				accountFrom.debit(fundTransfer.getAmount());
				accountTo.credit(fundTransfer.getAmount());

			}
		}

		notificationService.notifyAboutTransfer(accountFrom,
				accountFrom.getAccountId() + " account debited for " + fundTransfer.getAmount() + ".");

		notificationService.notifyAboutTransfer(accountTo,
				accountTo.getAccountId() + " account credited for " + fundTransfer.getAmount() + ".");

		return "Success";

	}

	private void validate(final FundTransfer fundTransfer, Account accountFrom, Account accountTo) {
		if (accountFrom == null) {
			throw new InvalidAccountNumberException(fundTransfer.getAccountFrom() + " account doesn't exist");
		}

		if (accountTo == null) {
			throw new InvalidAccountNumberException(fundTransfer.getAccountTo() + " account doesn't exist");
		}

		if (accountFrom.getAccountId().equalsIgnoreCase(accountTo.getAccountId())) {
			throw new InvalidAccountNumberException("Money transfer between same account is not allowed");
		}

		if (accountFrom.getBalance().compareTo(fundTransfer.getAmount()) < 0) {
			throw new InsufficientBalanceException(
					"Money Transfer can't be performed due to lack of funds in the account.");
		}
	}

}
