package com.cg.BankingSystem.service;

import java.util.List;
import java.util.logging.Logger;

import com.cg.BankingSystem.dao.CustomerDao;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dto.Account;
import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountsNotFoundException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;

public class CustomerServiceImpl extends BankingSystemServiceImpl implements CustomerService {

	private CustomerDao dao;
	static Logger myLogger = Logger.getLogger(AdminServiceImpl.class.getName());

	public CustomerServiceImpl(CustomerDaoImpl dao) {
		super(dao);
		this.dao = dao;
	}

	@Override
	public boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException {
		myLogger.info("Successfully changed contact number of customer with with account number: " + accountNumber);
		return dao.changeContactNumber(newNumber, accountNumber);
	}

	@Override
	public boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException {
		myLogger.info("Successfully changed address of customer with account number: " + accountNumber);
		return dao.changeAddress(newAddress, accountNumber);
	}

	@Override
	public int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException {
		myLogger.info("Successfully placed request for cheque book for customer with account number: "
				+ request.getAccountNumber());
		return dao.requestForCheckBook(request);
	}

	@Override
	public List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException {
		myLogger.info("Successfully fetched list of requests of customer with account number: " + accountNumber);
		return dao.getRequests(accountNumber);
	}

	@Override
	public double getTransactionLimit() {
		return 1000000;
	}

	@Override
	public boolean validateTransactionAmount(Customer customer, double transferAmount) {
		myLogger.info("Successfully validated transaction amount for customer with account number: "
				+ customer.getAccountNumber());
		return transferAmount < customer.getBalance();
	}

	@Override
	public boolean validateTransactionPassword(Customer customer, String txnPwd) {
		myLogger.info("Successfully validated transaction password for customer with account number: "
				+ customer.getAccountNumber());
		return txnPwd.equals(customer.getTransactionPassword());
	}

	@Override
	public Account fetchOtherExistingAccount(long accountNumber, AccountType accountType)
			throws AccountsNotFoundException, InternalServerException {
		myLogger.info("Successfully fetched other existing account of customer with account number: " + accountNumber);
		return dao.fetchOtherExistingAccount(accountNumber, accountType);
	}

	@Override
	public List<Account> fetchBeneficiaries(long accountNumber) throws InternalServerException {
		myLogger.info(
				"Successfully fetched list of beneficiaries saved by customer with account number: " + accountNumber);
		return dao.fetchBeneficiaries(accountNumber);
	}

	@Override
	public boolean transferFund(Customer fromAccount, Account otherAccount, Transaction txnDetails)
			throws InternalServerException {
		myLogger.info("Successfully transferred funds by customer with account number: "
				+ fromAccount.getAccountNumber() + " to account number: " + otherAccount);
		return dao.transferFund(fromAccount, otherAccount, txnDetails);
	}

	@Override
	public boolean addNewBeneficiary(long accountNumber, Account newBeneficiary) throws InternalServerException {
		myLogger.info("Successfully added new beneficiary for customer with account number: " + accountNumber);
		return dao.addNewBeneficiary(accountNumber, newBeneficiary);
	}

}