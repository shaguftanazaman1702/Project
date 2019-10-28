package com.cg.BankingSystem.service;

import java.util.List;

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

	public CustomerServiceImpl(CustomerDaoImpl dao) {
		super(dao);
		this.dao = dao;
	}
	
	@Override
	public boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException {
		return dao.changeContactNumber(newNumber, accountNumber);
	}

	@Override
	public boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException {
		return dao.changeAddress(newAddress, accountNumber);
	}

	@Override
	public int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException {
		return dao.requestForCheckBook(request);
	}

	@Override
	public List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException {
		return dao.getRequests(accountNumber);
	}
	
	@Override
	public double getTransactionLimit() {
		return 1000000;
	}

	@Override
	public boolean validateTransactionAmount(Customer customer, double transferAmount) {
		return transferAmount < customer.getBalance();
	}

	@Override
	public boolean validateTransactionPassword(Customer customer, String txnPwd) {
		return txnPwd.equals(customer.getTransactionPassword());
	}

	@Override
	public Account fetchOtherExistingAccount(long accountNumber, AccountType accountType)
			throws AccountsNotFoundException, InternalServerException {
		return dao.fetchOtherExistingAccount(accountNumber, accountType);
	}

	@Override
	public List<Account> fetchBeneficiaries(long accountNumber) throws InternalServerException {
		return dao.fetchBeneficiaries(accountNumber);
	}

	@Override
	public boolean transferFund(Customer fromAccount, Account otherAccount, Transaction txnDetails)
			throws InternalServerException {
		return dao.transferFund(fromAccount, otherAccount, txnDetails);
	}

	@Override
	public boolean addNewBeneficiary(long accountNumber, Account newBeneficiary) throws InternalServerException {
		return dao.addNewBeneficiary(accountNumber, newBeneficiary);
	}

}