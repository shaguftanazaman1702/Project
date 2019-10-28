package com.cg.BankingSystem.service;

public interface CustomerService extends BankingSystemService {

	boolean changeContactNumber(String newNumber);
	
	boolean changeAddress(String newAddress);
	
	int requestForCheckBook();
	
}
