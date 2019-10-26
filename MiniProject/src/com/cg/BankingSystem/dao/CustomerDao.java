package com.cg.BankingSystem.dao;

public interface CustomerDao<T> extends BankingSystemDao<T> {


		boolean changeContactNumber(String newNumber,long accountNumber);
		
		boolean changeAddress(String newAddress,long accountNumber);
		
		int requestForCheckBook();
	
}
