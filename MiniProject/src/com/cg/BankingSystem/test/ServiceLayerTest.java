package com.cg.BankingSystem.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.service.AdminService;
import com.cg.BankingSystem.service.AdminServiceImpl;
import com.cg.BankingSystem.service.BankingSystemService;
import com.cg.BankingSystem.service.CustomerService;
import com.cg.BankingSystem.service.CustomerServiceImpl;

public class ServiceLayerTest {

	@Ignore
	@Test
	public void testInjection() {
		LoginBean adminBean = new LoginBean();
		adminBean.setUserId("AD123");
		adminBean.setPassword("124akjsf");
		
		AdminService adminService = (AdminServiceImpl) BankingSystemService.getInstance(adminBean);
		
		assertNotNull(adminService);
		
		LoginBean customerBean = new LoginBean();
		customerBean.setUserId("CC123");
		customerBean.setPassword("asif13jsb");
		
		CustomerService customerService = (CustomerService) BankingSystemService.getInstance(customerBean);
		
		assertNotNull(customerService);
		
	}
	
	@Ignore
	@Test
	public void testAuthentication() throws InvalidCredentialsException, InternalServerException {
		LoginBean bean = new LoginBean();
		bean.setUserId("CC123");
		bean.setPassword("HelloNew");
		
		BankingSystemService service = (CustomerServiceImpl) BankingSystemService.getInstance(bean);
		
		Customer admin = (Customer) service.authenticateUser(bean);
		System.out.println(admin);
	}
}
