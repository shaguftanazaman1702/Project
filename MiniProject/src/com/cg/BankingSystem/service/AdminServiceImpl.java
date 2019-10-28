package com.cg.BankingSystem.service;

import com.cg.BankingSystem.dao.AdminDao;
import com.cg.BankingSystem.dao.AdminDaoImpl;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;

public class AdminServiceImpl extends BankingSystemServiceImpl implements AdminService {
	
	private AdminDao dao;
	
	public AdminServiceImpl(AdminDaoImpl dao) {
		super(dao);
		this.dao = dao;
	}

	@Override
	public long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException {
		return dao.createNewAccount(newCustomer);
	}

}
