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
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;
import com.cg.BankingSystem.exception.UserNotFoundException;

public class AdminDaoTests {
	
	private AdminDao dao;
	
	@Before
	public void init() {
		dao = new AdminDaoImpl();
	}
	
	@Ignore
	@Test
	public void testAuthentication() throws InvalidCredentialsException, InternalServerException {
		LoginBean bean = new LoginBean();
		bean.setUserId("AD123");
		bean.setPassword("ABC123");
		
		Admin admin = dao.authenticateUser(bean);
		
		System.out.println(admin.getUserId() + " " + admin.getUserName());
	}
	
	@Ignore
	@Test
	public void testTransactions() throws NoTransactionsExistException, InternalServerException {
		List<Transaction> txns = dao.listTransactions(1010);
		for (Transaction txn: txns)
			System.out.println(txns);
	}
	
	@Ignore
	@Test
	public void testUpdation() throws InternalServerException {
		System.out.println(dao.updatePassword("HelloNew", "AD123"));
	}
	
	@Ignore
	@Test
	public void testCreation() throws InternalServerException, AccountNotCreatedException {
		SignUp newUser = new SignUp();
		newUser.setName("Swaroop");
		newUser.setAccountType(AccountType.SAVINGS_ACCOUNT);
		newUser.setAddress("Mumbai");
		newUser.setEmail("abs@gmail.com");
		newUser.setMobileNo("+919999996666");
		newUser.setOpeningBal(500);
		newUser.setPanCardNumber("AB231");
		newUser.setPassword("124Da");
		newUser.setTransactionPassword("124Da");
		newUser.setUserId("AB234");
		
		System.out.println(dao.createNewAccount(newUser));
	}
	
	@Test
	public void testFindCustomer() throws InternalServerException, UserNotFoundException {
		System.out.println(dao.findCustomer("sh655"));
	}
	
	@After
	public void detroy() {
		dao = null;
	}
}
