package com.cg.BankingSystem.dao;

import org.omg.CORBA.Request;

import com.cg.BankingSystem.dto.Service;

public interface CustomerDao<T> extends BankingSystemDao<T> {

	boolean changeContactNumber(String newNumber, long accountNumber);

	boolean changeAddress(String newAddress, long accountNumber);

	int requestForCheckBook(Service service);

}
