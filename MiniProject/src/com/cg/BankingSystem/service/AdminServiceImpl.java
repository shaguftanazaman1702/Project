package com.cg.BankingSystem.service;

import java.util.Base64;

import com.cg.BankingSystem.dao.AdminDao;
import com.cg.BankingSystem.dao.AdminDaoImpl;
import com.cg.BankingSystem.exception.MaxAccountsDefinedForUserException;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.UserNotFoundException;

public class AdminServiceImpl extends BankingSystemServiceImpl implements AdminService {
	
	private AdminDao dao;
	
	public AdminServiceImpl(AdminDaoImpl dao) {
		super(dao);
		this.dao = dao;
	}

	@Override
	public long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException {
		encodePassword(newCustomer);
		return dao.createNewAccount(newCustomer);
	}

	private void encodePassword(SignUp newCustomer) {
		Base64.Encoder encoder = Base64.getEncoder();
		newCustomer.setPassword(encoder.encodeToString(newCustomer.getPassword().getBytes()));
		newCustomer.setTransactionPassword(encoder.encodeToString(newCustomer.getTransactionPassword().getBytes()));
	}

	@Override
	public Customer findCustomer(String userId) throws InternalServerException, UserNotFoundException {
		return dao.findCustomer(userId);
	}

	@Override
	public boolean saveExistingUser(SignUp newCustomer) throws MaxAccountsDefinedForUserException {
		return dao.saveExistingUser(newCustomer);
	}

}
