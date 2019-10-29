package com.cg.BankingSystem.service;

import java.util.List;

import com.cg.BankingSystem.dao.BankingSystemDao;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;

public class BankingSystemServiceImpl implements BankingSystemService {
	private BankingSystemDao dao;
	
	BankingSystemServiceImpl(BankingSystemDao dao) {
		this.dao = dao;
	}
	
	@Override
	public Object authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException {
		return dao.authenticateUser(bean);
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber)
			throws NoTransactionsExistException, InternalServerException {
		return dao.listTransactions(accountNumber);
	}

	@Override
	public boolean updatePassword(String newPassword, String userId) throws InternalServerException {
		return dao.updatePassword(newPassword, userId);
	}

	@Override
	public boolean validateLongEntry(long min, long max, long input) {
		boolean isValidNumber = String.valueOf(input).matches(NUMBER_VALIDATOR);
		boolean isValidRange = input >= min && input <= max;
		return isValidNumber && isValidRange;
	}

	@Override
	public boolean validateDouble(double min, double max, double input) {
		boolean isValidNumber = String.valueOf(input).matches(DOUBLE_VALIDATOR);
		boolean isValidRange = input >= min && input <= max;
		return false;
	}

	@Override
	public boolean validateName(String name) {
		return name.matches(NAME_VALIDATOR);
	}

	@Override
	public boolean validateAlphaNumeric(String input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validateAddress(String address) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validateContact(String mobileNo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validateEmail(String email) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validatePanCard(String panCardNumber) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validateTxnPwd(String transactionPassword) {
		// TODO Auto-generated method stub
		return false;
	}

}
