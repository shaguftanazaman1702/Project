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
	
	static BankingSystemService getInstance(LoginBean bean) {
		if (bean.getUserId().contains("AD"))
			return new AdminServiceImpl(new AdminDaoImpl());
		else if (bean.getUserId().contains("CC"))
			return new CustomerServiceImpl(new CustomerDaoImpl());
		return null;
	}

}