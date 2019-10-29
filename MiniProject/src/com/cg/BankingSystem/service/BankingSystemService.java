package com.cg.BankingSystem.service;

import java.util.List;

import com.cg.BankingSystem.dao.AdminDaoImpl;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;

public interface BankingSystemService {

	Object authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException;
	
	List<Transaction> listTransactions(long accountNumber) throws NoTransactionsExistException, InternalServerException;
	
	boolean updatePassword(String newPassword, String userId) throws InternalServerException;
	
	boolean validateLongEntry(long min, long max, long input);
	
	boolean validateAlphaNumeric(String input);
	
	boolean validateDouble(double min, double max, double input);
	
	boolean validateName(String name);

	boolean validateAddress(String address);

	boolean validateContact(String mobileNo);

	boolean validateEmail(String email);

	boolean validatePanCard(String panCardNumber);

	boolean validateTxnPwd(String transactionPassword);
	
	static BankingSystemService getInstance(LoginBean bean) {
		if (bean.getUserId().contains("AD"))
			return new AdminServiceImpl(new AdminDaoImpl());
		else if (bean.getUserId().contains("CC"))
			return new CustomerServiceImpl(new CustomerDaoImpl());
		return null;
	}

	static boolean validateAdminUserID(String input) {
		return input.matches(ADMIN_USER_ID_VALIDATOR);
	}

	static boolean validateCustomerUserID(String input) {
		return input.matches(CUSTOMER_USER_ID_VALIDATOR);
	}
	
	static boolean validatePassword(String password) {
		return password.matches(PASSWORD_VALIDATOR);
	}
	
	String NUMBER_VALIDATOR = "[0-9]+";
	String DOUBLE_VALIDATOR = "[0-9]+.[0-9]+";
	String ADMIN_USER_ID_VALIDATOR = "AD[A-Za-z0-9]+";
	String CUSTOMER_USER_ID_VALIDATOR = "CC[A-Za-z0-9]+";
	String NAME_VALIDATOR = "[A-Z][A_Za-z ]+";
	String PASSWORD_VALIDATOR = "[A-Za-z0-9@_!$]{8,15}";

}