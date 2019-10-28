package com.cg.BankingSystem.service;

import java.util.List;

import com.cg.BankingSystem.dto.Account;
import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountsNotFoundException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;

public interface CustomerService extends BankingSystemService {

	boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException;
	
	boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException;
	
	int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException;

	List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException;

	double getTransactionLimit();

	boolean validateTransactionAmount(Customer customer, double transferAmount);

	boolean validateTransactionPassword(Customer customer, String txnPwd);

	Account fetchOtherExistingAccount(long accountNumber, AccountType accountType) throws AccountsNotFoundException, InternalServerException;

	List<Account> fetchBeneficiaries(long accountNumber) throws InternalServerException;

	boolean transferFund(Customer fromAccount, Account otherAccount, Transaction txnDetails) throws InternalServerException;

	boolean addNewBeneficiary(long accountNumber, Account newBeneficiary) throws InternalServerException;
}
