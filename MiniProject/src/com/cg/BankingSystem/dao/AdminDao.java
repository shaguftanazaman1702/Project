package com.cg.BankingSystem.dao;

import com.cg.BankingSystem.dto.Customer;

public interface AdminDao<T> extends BankingSystemDao<T> {

	String getTransactionsQuery = "SELECT transaction FROM TRANSACTION WHERE accountNo = ? AND transactionDate BETWEEN ? AND ?";
	
	long createNewAccount(Customer newCustomer);
	
}
