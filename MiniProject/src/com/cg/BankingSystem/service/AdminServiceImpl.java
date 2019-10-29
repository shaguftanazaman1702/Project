package com.cg.BankingSystem.service;

import java.util.logging.Logger;

import com.cg.BankingSystem.dao.AdminDao;
import com.cg.BankingSystem.dao.AdminDaoImpl;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.UserNotFoundException;

public class AdminServiceImpl extends BankingSystemServiceImpl implements AdminService {

	static Logger myLogger = Logger.getLogger(AdminServiceImpl.class.getName());

	private AdminDao dao;

	public AdminServiceImpl(AdminDaoImpl dao) {
		super(dao);
		this.dao = dao;
	}

	@Override
	public long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException {
		myLogger.info("Successfully created new account with account number: " + newCustomer.getAccountNumber());
		return dao.createNewAccount(newCustomer);
	}

	@Override
	public Customer findCustomer(String userId) throws InternalServerException, UserNotFoundException {
		myLogger.info("Successfully fetched customer details with userID: " + userId);
		return dao.findCustomer(userId);
	}

	@Override
	public boolean saveExistingUser(SignUp newCustomer) throws InternalServerException {
		myLogger.info("Successfully created new account for existing customer with userID: " + newCustomer.getUserId());
		return dao.saveExistingUser(newCustomer);
	}

}
