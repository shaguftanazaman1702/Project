package com.cg.BankingSystem.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.service.BankingSystemService;

public class ValidationTests {
	
	private BankingSystemService service;
	private LoginBean bean;
	
	@Before
	public void init() {
		bean = Mockito.mock(LoginBean.class);
		Mockito.when(bean.getUserId()).thenReturn("AD");
		service = BankingSystemService.getInstance(bean);
	}
	
	@Ignore
	@Test
	public void assertDependencyInjection() {
		assertNotNull(bean);
		assertNotNull(service);
	}
	
	@Ignore
	@Test
	public void testValidateName() {
		assertTrue(service.validateName("Swaroop Nath"));
	}
	
	@Ignore
	@Test
	public void testValidateAddress() {
		assertTrue(service.validateAddress("Flat No.-2, Marol"));
		
	}
	
	@Ignore
	@Test
	public void testContact() {
		assertTrue(service.validateContact("+919876543210"));
		assertFalse(service.validateContact("+9167q24"));
	}
	
	@Ignore
	@Test
	public void testEmail() {
		assertTrue(service.validateEmail("abc_12@gmail.com"));
		assertFalse(service.validateEmail("12414@asdg . in"));
	}
	
	@Ignore
	@Test
	public void testPassword() {
		assertTrue(BankingSystemService.validatePassword("ABCa2@12"));
		assertFalse(BankingSystemService.validatePassword("ABcd"));
		assertFalse(BankingSystemService.validatePassword("124"));
		assertFalse(BankingSystemService.validatePassword("Abc12"));
		assertFalse(BankingSystemService.validatePassword("Abc@"));
		assertFalse(BankingSystemService.validatePassword("124#"));
	}
	
	@Ignore
	@Test
	public void testPanCard() {
		assertTrue(service.validatePanCard("AAAAA1234C"));
		assertFalse(service.validatePanCard("AA1q24"));
	}
	
	@Ignore
	@Test
	public void testDoubleValidator() {
		assertTrue(service.validateDouble(1000, 1000000, 1500.0));
	}
	
	@After
	public void tearDown() {
		service = null;
		bean = null;
	}

}
