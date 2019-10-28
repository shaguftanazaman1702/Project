package com.cg.BankingSystem.dao;

import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.Payee;
import com.cg.BankingSystem.exception.InsufficientBalanceException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;

public interface CustomerDao extends BankingSystemDao<Customer> {

	boolean changeContactNumber(String newNumber);
	
	boolean changeAddress(String newAddress);
	
	int requestForCheckBook();
	
	void fundTransfer(long fromAccountNo, Payee payee, double amount, String transactionPassword)
			throws InsufficientBalanceException, InvalidCredentialsException, InternalServerException;
	
}
