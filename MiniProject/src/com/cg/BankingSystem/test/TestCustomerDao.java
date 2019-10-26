package com.cg.BankingSystem.test;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cg.BankingSystem.dao.CustomerDao;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Service;

public class TestCustomerDao {
	CustomerDao<Customer> dao;
	
	
	@BeforeClass
	public void init() {
		
	}
	
	@Before
	public void setUp() {
		CustomerDao<Customer> dao = new  CustomerDaoImpl<Customer>();
	}
	
	@After
	public void closeUp(){
		dao = null;
	}	
	
	@AfterClass
	public void tearDown() {
		
	}
	
	@Test
	public void checkContactNumber() {
		System.out.println(dao.changeContactNumber("8503147852", 4565123789L));
	}
	
	@Test
	public void checkPassword() {
		System.out.println(dao.updatePassword("S#f$j#",4502485529L ));
	}
	
	@Test
	public void checkAddress() {
		dao.changeAddress("Kolkata",4502485529L);
	}
	
	@Test
	public void checkAuthentication() {
		LoginBean bean = new LoginBean();
		bean.setUserId("A123");
		bean.setPassword("ABCVNM");
		
		Customer customer = dao.authenticateUser(bean);
		System.out.println(customer);
	}
	
	@Test
	public void checkChequeBookRequest() {
		Service service = new Service();
		service.setAccountNumber(4502485529L);
		service.setserviceDate(LocalDate.now());
		service.setStatus(3);
		System.out.println(dao.requestForCheckBook(service));
	}
	
	@Test
	public void checkListTransaction() throws Exception {
		System.out.println(dao.listTransactions(4502485529L));
	}
}
