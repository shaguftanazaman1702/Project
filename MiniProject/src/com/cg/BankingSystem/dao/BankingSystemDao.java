package com.cg.BankingSystem.dao;

import java.util.List;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;

public interface BankingSystemDao<T> {

	T authenticateUser(LoginBean bean);
	
	List<Transaction> listTransactions(long accountNumber);
	
	boolean updatePassword(String newPassword);
	
}
