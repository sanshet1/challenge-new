package com.db.awmd.challenge.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;

@SpringBootTest
@AutoConfigureMockMvc
public class FundTransferControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@BeforeEach
	public void prepareMockMvc() {
		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();

	}

	@Test
	public void transferFundTestSuccess() throws Exception {
		accountsService.createAccount(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		accountsService.createAccount(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{ \"accountFrom\": \"001\",\"accountTo\": \"002\",\"amount\":200}"))
				.andExpect(status().isOk());

		Account accountFrom = accountsService.getAccount("001");
		assertThat(accountFrom.getBalance()).isEqualByComparingTo("800");
		Account accountTo = accountsService.getAccount("002");
		assertThat(accountTo.getBalance()).isEqualByComparingTo("1200");
	}

	@Test
	public void transferFundTestForTransferNegativeAmount() throws Exception {
		accountsService.createAccount(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		accountsService.createAccount(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{ \"accountFrom\": \"001\",\"accountTo\": \"002\",\"amount\":-200}"))
				.andExpect(status().isBadRequest());

		Account accountFrom = accountsService.getAccount("001");
		assertThat(accountFrom.getBalance()).isEqualByComparingTo("1000");
		Account accountTo = accountsService.getAccount("002");
		assertThat(accountTo.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void transferFundTestForTransferMoreAmountThenPresentBalance() throws Exception {
		accountsService.createAccount(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		accountsService.createAccount(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{ \"accountFrom\": \"001\",\"accountTo\": \"002\",\"amount\":1200}"))
				.andExpect(status().isBadRequest()).andExpect(content().string(
						containsString("Money Transfer can't be performed due to lack of funds in the account.")));

		Account accountFrom = accountsService.getAccount("001");
		assertThat(accountFrom.getBalance()).isEqualByComparingTo("1000");
		Account accountTo = accountsService.getAccount("002");
		assertThat(accountTo.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void transferFundTestForTransferToInvalidToAccount() throws Exception {
		accountsService.createAccount(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		accountsService.createAccount(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{ \"accountFrom\": \"001\",\"accountTo\": \"003\",\"amount\":200}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("003 account doesn't exist")));

		Account accountFrom = accountsService.getAccount("001");
		assertThat(accountFrom.getBalance()).isEqualByComparingTo("1000");
		Account accountTo = accountsService.getAccount("002");
		assertThat(accountTo.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void transferFundTestForTransferToInvalidFromAccount() throws Exception {
		accountsService.createAccount(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		accountsService.createAccount(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{ \"accountFrom\": \"003\",\"accountTo\": \"002\",\"amount\":200}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("003 account doesn't exist")));

		Account accountFrom = accountsService.getAccount("001");
		assertThat(accountFrom.getBalance()).isEqualByComparingTo("1000");
		Account accountTo = accountsService.getAccount("002");
		assertThat(accountTo.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void transferFundTestForTransferZeroAmount() throws Exception {
		accountsService.createAccount(Account.builder().accountId("001").balance(new BigDecimal(1000)).build());
		accountsService.createAccount(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{ \"accountFrom\": \"003\",\"accountTo\": \"002\",\"amount\":0}"))
				.andExpect(status().isBadRequest());

		Account accountFrom = accountsService.getAccount("001");
		assertThat(accountFrom.getBalance()).isEqualByComparingTo("1000");
		Account accountTo = accountsService.getAccount("002");
		assertThat(accountTo.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void transferFundTestForTransferAmountBTNSameAccount() throws Exception {

		accountsService.createAccount(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{ \"accountFrom\": \"002\",\"accountTo\": \"002\",\"amount\":100}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Money transfer between same account is not allowed")));

		Account accountTo = accountsService.getAccount("002");
		assertThat(accountTo.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void transferFundTestForTransferWhenAccountIDISSpace() throws Exception {

		accountsService.createAccount(Account.builder().accountId("002").balance(new BigDecimal(1000)).build());

		this.mockMvc
				.perform(post("/v1/transfer").contentType(MediaType.APPLICATION_JSON)
						.content("{ \"accountFrom\": \" \",\"accountTo\": \"002\",\"amount\":100}"))
				.andExpect(status().isBadRequest());

		Account accountTo = accountsService.getAccount("002");
		assertThat(accountTo.getBalance()).isEqualByComparingTo("1000");
	}

}
