package com.cg.BankingSystem.service;

import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;

public interface AdminService extends BankingSystemService {
	
	long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException;

	Customer findCustomer(String userId);

	void saveExistingUser(SignUp newCustomer);
	
}
