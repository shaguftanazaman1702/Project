package com.cg.BankingSystem.service;

import com.cg.BankingSystem.exception.MaxAccountsDefinedForUserException;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.UserNotFoundException;

public interface AdminService extends BankingSystemService {
	
	/**
	 * This service is used to create an account for a new customer of the bank.
	 * @param newCustomer The Java bean encapsulating all necessary details required to open a bank account.
	 * @return It returns the account number of the newly created account, if account is created successfully.
	 * @throws AccountNotCreatedException Thrown if account could not be created.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException;

	/**
	 * This service is used to find an existing customer of the bank by their userId. This method is specially
	 * helpful when the Bank Admin wants to create a new account for an existing customer, with a different account
	 * type
	 * @param userId The unique ID of the customer in bank's database.
	 * @return The customer details if found.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 * @throws UserNotFoundException Thrown if no customer exists for the given ID.
	 */
	Customer findCustomer(String userId) throws InternalServerException, UserNotFoundException;

	/**
	 * This service is used to create an account for an existing customer of the bank,
	 * with a different account type.
	 * @param newCustomer The details of the customer.
	 * @return A boolean value indicating the account has been successfully opened or not.
	 * @throws MaxAccountsDefinedForUserException Thrown if the customer already has multiple accounts in the bank.
	 */
	boolean saveExistingUser(SignUp newCustomer) throws MaxAccountsDefinedForUserException;

}
