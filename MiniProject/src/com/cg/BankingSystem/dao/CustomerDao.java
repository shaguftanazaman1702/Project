package com.cg.BankingSystem.dao;

public interface CustomerDao<T> extends BankingSystemDao<T> {

	boolean changeContactNumber(String newNumber);
	
	boolean changeAddress(String newAddress);
	
	int requestForCheckBook();
	
}
