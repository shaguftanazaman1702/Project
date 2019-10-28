package com.cg.BankingSystem.service;

import java.util.List;

import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;

public interface CustomerService extends BankingSystemService {

	boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException;
	
	boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException;
	
	int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException;

	List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException;	
}