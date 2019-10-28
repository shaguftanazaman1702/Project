package com.cg.BankingSystem.service;

import com.cg.BankingSystem.dao.CustomerDao;
import com.cg.BankingSystem.dao.CustomerDaoImpl;

public class CustomerServiceImpl extends BankingSystemServiceImpl implements CustomerService {
	
	private CustomerDao dao;

	public CustomerServiceImpl(CustomerDaoImpl dao) {
		super(dao);
		this.dao = dao;
	}
	
	@Override
	public boolean changeContactNumber(String newNumber) {
		return dao.changeContactNumber(newNumber);
	}

	@Override
	public boolean changeAddress(String newAddress) {
		return dao.changeAddress(newAddress);
	}

	@Override
	public int requestForCheckBook() {
		return dao.requestForCheckBook();
	}

}
