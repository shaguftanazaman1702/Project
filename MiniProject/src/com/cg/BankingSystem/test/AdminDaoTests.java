package com.cg.BankingSystem.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.cg.BankingSystem.dao.AdminDao;
import com.cg.BankingSystem.dao.AdminDaoImpl;
import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;

public class AdminDaoTests {
	
	private AdminDao dao;
	
	@Before
	public void init() {
		dao = new AdminDaoImpl();
	}
	
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
	public void testTransactions() {
		
	}
	
	@Ignore
	@Test
	public void testUpdation() {
		
	}
	
	@Ignore
	@Test
	public void testCreation() {
		
	}
	
	@After
	public void detroy() {
		dao = null;
	}
}
