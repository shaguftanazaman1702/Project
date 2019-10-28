package com.cg.BankingSystem.dao;

import java.util.List;

import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;

public interface CustomerDao extends BankingSystemDao<Customer> {

	boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException;
	
	boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException;
	
	int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException;
	
	List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException;
	
}
