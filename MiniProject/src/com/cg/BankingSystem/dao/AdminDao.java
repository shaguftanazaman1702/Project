package com.cg.BankingSystem.dao;

import com.cg.BankingSystem.dto.Customer;

public interface AdminDao<T> extends BankingSystemDao<T> {
	
	long createNewAccount(Customer newCustomer);
	
}
