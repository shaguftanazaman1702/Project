package com.cg.BankingSystem.dao;

import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.UserNotFoundException;

public interface AdminDao extends BankingSystemDao<Admin> {
	
		long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException;
	
		Customer findCustomer(String userId) throws InternalServerException, UserNotFoundException;

		boolean saveExistingUser(SignUp newCustomer) throws InternalServerException;
}
