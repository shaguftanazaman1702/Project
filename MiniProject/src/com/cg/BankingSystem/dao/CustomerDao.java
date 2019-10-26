package com.cg.BankingSystem.dao;

import com.cg.BankingSystem.dto.Customer;

public interface CustomerDao extends BankingSystemDao<Customer> {

	boolean changeContactNumber(String newNumber);
	
	boolean changeAddress(String newAddress);
	
	int requestForCheckBook();
	
}
