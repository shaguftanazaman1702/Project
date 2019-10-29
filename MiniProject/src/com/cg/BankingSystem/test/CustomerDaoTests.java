package com.cg.BankingSystem.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.cg.BankingSystem.dao.CustomerDao;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dto.Account;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;

//test class in which features of customer are being checked 
public class CustomerDaoTests {

	private CustomerDao dao;
	
	@Before
	public void init() {
		dao = new CustomerDaoImpl();
	}
	
	@Ignore
	@Test
	//test whether he is a valid customer
	public void testAuthentication() throws InvalidCredentialsException, InternalServerException {
		LoginBean bean = new LoginBean();
		bean.setUserId("CC234");
		bean.setPassword("124Da");
		
		Customer customer = dao.authenticateUser(bean);
		System.out.println(customer.getEmailId());
	}
	
	@Ignore
	@Test
	//test method to change the contact number
	public void testContactChange() throws InternalServerException {
		System.out.println(dao.changeContactNumber("+914445558881", 1016L));
	}
	
	@Ignore
	@Test
	//test method to change the address for communication
	public void testAddressChange() throws InternalServerException {
		System.out.println(dao.changeAddress("Guwahati", 1016L));
	}
	
	@Ignore
	@Test
	//test method to add a new payee
	public void addNewBeneficiary() throws InternalServerException {
		Account newBeneficiary = new Account();
		newBeneficiary.setAccountNumber(1021L);
		newBeneficiary.setNickName("Villu");
		
		System.out.println(dao.addNewBeneficiary(1019, newBeneficiary));
	}
	
	@Ignore
	@Test
	//test method to list down all the payees added to the account of the customer
	public void testFetchBeneficiaries() throws InternalServerException {
		List<Account> beneficiaries = dao.fetchBeneficiaries(1019L);
		for (Account beneficiary: beneficiaries)
			System.out.println(beneficiary.getNickName());
	}
	
	@Ignore
	@Test
	//test method to check if fund transfer is taking place with proper validations
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
	@After
	public void tearDown() {
		dao = null;
	}
	
}
