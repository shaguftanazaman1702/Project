package com.cg.BankingSystem.test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.cg.BankingSystem.dao.CustomerDao;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dao.DatabaseUtilities;
import com.cg.BankingSystem.dto.Account;
import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;

public class CustomerDaoTests {

	private CustomerDao dao;
	
	@Before
	public void init() {
		dao = new CustomerDaoImpl();
	}
	
//	@Ignore
	@Test
	public void testAuthentication() throws InvalidCredentialsException, InternalServerException {
		LoginBean bean = new LoginBean();
		bean.setUserId("CC125");
		bean.setPassword(Base64.getEncoder().encodeToString("Abcdefg%4".getBytes()));
		bean.setAccountType(AccountType.SAVINGS_ACCOUNT);
		
		Customer customer = dao.authenticateUser(bean);
		System.out.println(customer.getPassword() + "\t" + customer.getTransactionPassword());
	}
	
	@Ignore
	@Test
	public void testContactChange() throws InternalServerException {
		System.out.println(dao.changeContactNumber("+914445558881", 1016L));
	}
	
	@Ignore
	@Test
	public void testAddressChange() throws InternalServerException {
		System.out.println(dao.changeAddress("Guwahati", 1016L));
	}
	
	@Ignore
	@Test
	public void addNewBeneficiary() throws InternalServerException {
		Account newBeneficiary = new Account();
		newBeneficiary.setAccountNumber(1021L);
		newBeneficiary.setNickName("Villu");
		
		System.out.println(dao.addNewBeneficiary(1019, newBeneficiary));
	}
	
	@Ignore
	@Test
	public void testFetchBeneficiaries() throws InternalServerException {
		List<Account> beneficiaries = dao.fetchBeneficiaries(1022L);
		for (Account beneficiary: beneficiaries)
			System.out.println(beneficiary.getNickName());
	}
	
	@Ignore
	@Test
	public void testFundTransfer() throws InternalServerException, InvalidCredentialsException {
		LoginBean bean = new LoginBean();
		bean.setUserId("CC234");
		bean.setPassword("124Da");
		
		Customer fromAccount = dao.authenticateUser(bean);
		
		Account otherAccount = dao.fetchBeneficiaries(fromAccount.getAccountNumber()).get(0);
		
		Transaction txnDetails = new Transaction();
		txnDetails.setTransactionAmount(100);
		txnDetails.setTransactionDescription("DIWALI");
		
		dao.transferFund(fromAccount, otherAccount, txnDetails);
	}
	
	@Ignore
	@Test
	public void testServiceRequest() throws RequestCannotBeProcessedException, InternalServerException {
		Request newRequest = new Request();
		newRequest.setAccountNumber(1022L);
		newRequest.setRequestDate(LocalDate.now());
		newRequest.setStatus(0);
		System.out.println(dao.requestForCheckBook(newRequest));
	}
	
	@Ignore
	@Test
	public void testServiceTracking() throws NoServicesMadeException, InternalServerException {
		List<Request> requests = dao.getRequests(1022L);
		
		for (Request request: requests)
			System.out.println(request);
	}
	
	@After
	public void tearDown() {
		dao = null;
	}
	
}
