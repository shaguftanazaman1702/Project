package com.cg.BankingSystem.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.cg.BankingSystem.dao.AdminDao;
import com.cg.BankingSystem.dao.AdminDaoImpl;
import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;
import com.cg.BankingSystem.exception.UserNotFoundException;

//class that tests the strength with which the admin has been authorized
public class AdminDaoTests {
	
	private AdminDao dao;
	
	@Before
	public void init() {
		dao = new AdminDaoImpl();
	}
	
	@Ignore
	@Test
	//test method to check for authentication of a customer
	public void testAuthentication() throws InvalidCredentialsException, InternalServerException {
		LoginBean bean = new LoginBean();
		bean.setUserId("AD123");
		bean.setPassword("ABC123");
		
		Admin admin = dao.authenticateUser(bean);
		
		System.out.println(admin.getUserId() + " " + admin.getUserName());
	}
	
	@Ignore
	@Test
	//method to test the transaction operation
	public void testTransactions() throws NoTransactionsExistException, InternalServerException {
		List<Transaction> txns = dao.listTransactions(1010);
		for (Transaction txn: txns)
			System.out.println(txns);
	}
	
	@Ignore
	@Test
	//method to test whether password gets updated or not
	public void testUpdation() throws InternalServerException {
		System.out.println(dao.updatePassword("HelloNew", "AD123"));
	}
	
	@Ignore
	@Test
	//method to test the creation of a new account for a customer
	public void testCreation() throws InternalServerException, AccountNotCreatedException {
		SignUp newUser = new SignUp();
		newUser.setName("Shubham");
		newUser.setAccountType(AccountType.CURRENT_ACCOUNT);
		newUser.setAddress("Mumbai");
		newUser.setEmail("abc@gmail.com");
		newUser.setMobileNo("+919999996666");
		newUser.setOpeningBal(2000);
		newUser.setPanCardNumber("AB331");
		newUser.setPassword("121D4");
		newUser.setTransactionPassword("121D4");
		newUser.setUserId("CC185");
		
		System.out.println(dao.createNewAccount(newUser));
	}
	
	@Ignore
	@Test
	//method to test whether a certain customer exists or not
	public void testFindCustomer() throws InternalServerException, UserNotFoundException {
		System.out.println(dao.findCustomer("sh655"));
	}
	
	@Ignore
	@Test
	//test method to save data of customer after making changes in his account
	public void testSaveExistingUser() throws InternalServerException, UserNotFoundException {
		Customer existingCustomer = dao.findCustomer("CC185");
		
		double openingBal = 10000;
		
		SignUp newCustomer = new SignUp();
		
		newCustomer.setUserId(existingCustomer.getUserId());
		newCustomer.setPassword(existingCustomer.getPassword());
		newCustomer.setName(existingCustomer.getName());
		newCustomer.setEmail(existingCustomer.getEmailId());
		newCustomer.setAddress(existingCustomer.getAddress());
		newCustomer.setMobileNo(existingCustomer.getMobileNumber());
		newCustomer.setAccountNumber(existingCustomer.getAccountNumber());
		newCustomer.setTransactionPassword(existingCustomer.getTransactionPassword());
		newCustomer.setOpeningBal(openingBal);
		newCustomer.setPanCardNumber(existingCustomer.getPanCardNumber());
		
		if (existingCustomer.getAccountType() == AccountType.SAVINGS_ACCOUNT)
			newCustomer.setAccountType(AccountType.CURRENT_ACCOUNT);
		else
			newCustomer.setAccountType(AccountType.SAVINGS_ACCOUNT);
		
		dao.saveExistingUser(newCustomer);
	}
	
	@After
	public void detroy() {
		dao = null;
	}
}
