package com.cg.BankingSystem.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.cg.BankingSystem.dao.CustomerDao;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;

public class CustomerDaoTests {

	private CustomerDao dao;
	
	@Before
	public void init() {
		dao = new CustomerDaoImpl();
	}
	
	@Ignore
	@Test
	public void testAuthentication() throws InvalidCredentialsException, InternalServerException {
		LoginBean bean = new LoginBean();
		bean.setUserId("AB234");
		bean.setPassword("124Da");
		
		Customer customer = dao.authenticateUser(bean);
		System.out.println(customer.getEmailId());
	}
	
	@Ignore
	@Test
	public void testContactChange() throws InternalServerException {
		System.out.println(dao.changeContactNumber("+914445558881", 1016L));
	}
	
	@Test
	public void testAddressChange() throws InternalServerException {
		System.out.println(dao.changeAddress("Guwahati", 1016L));
	}
	
	@After
	public void tearDown() {
		dao = null;
	}
}
