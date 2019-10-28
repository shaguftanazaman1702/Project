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

	String emailRule = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	String userIdRule = "^[a-z0-9_-]{5,15}$";

	String passwordRule = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$";

	String nameRule = "^[a-zA-Z\s]+$";     // "[a-zA-Z]*";

	String mobileRule = "[7-9][0-9]{9}";

	String pancardRule = "[A-Z]{3}[ABCFGHLJPTF]{1}[A-Z]{1}[0-9]{4}[A-Z]{1}";

	default boolean validateEMail(String email) {
		return email.matches(emailRule);
	}

	default boolean validateUserId(String userId) {
		return userId.matches(userIdRule);
	}

	default boolean validatePassword(String password) {
		return password.matches(passwordRule);
	}

	default boolean validateName(String name) {
		return name.matches(nameRule);
	}

	default boolean validateMobile(String mobile) {
		return mobile.matches(mobileRule);
	}

	default boolean validatePanCard(String pancard) {
		return pancard.matches(pancardRule);
	}

	Object authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException;

	List<Transaction> listTransactions(long accountNumber) throws NoTransactionsExistException, InternalServerException;

	boolean updatePassword(String newPassword, String userId) throws InternalServerException;

	static BankingSystemService getInstance(LoginBean bean) {
		if (bean.getUserId().contains("AD"))
			return new AdminServiceImpl(new AdminDaoImpl());
		else if (bean.getUserId().contains("CC"))
			return new CustomerServiceImpl(new CustomerDaoImpl());
		return null;
	}
}