package com.cg.BankingSystem.service;

import java.util.List;

import com.cg.BankingSystem.dao.CustomerDao;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;

public class CustomerServiceImpl extends BankingSystemServiceImpl implements CustomerService {
	
	private CustomerDao dao;

	public CustomerServiceImpl(CustomerDaoImpl dao) {
		super(dao);
		this.dao = dao;
	}
	
	@Override
	public boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException {
		return dao.changeContactNumber(newNumber, accountNumber);
	}

	@Override
	public boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException {
		return dao.changeAddress(newAddress, accountNumber);
	}

	@Override
	public int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException {
		return dao.requestForCheckBook(request);
	}

	@Override
	public List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException {
		return dao.getRequests(accountNumber);
	}

}