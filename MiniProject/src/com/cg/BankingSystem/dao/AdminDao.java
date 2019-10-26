package com.cg.BankingSystem.dao;

import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;

public interface AdminDao extends BankingSystemDao<Admin> {
	
	long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException;
	
}
