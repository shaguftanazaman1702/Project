package com.cg.BankingSystem.service;

import java.util.List;
import java.util.logging.Logger;

import com.cg.BankingSystem.dao.BankingSystemDao;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;

public class BankingSystemServiceImpl implements BankingSystemService {

	private BankingSystemDao dao;
	static Logger myLogger = Logger.getLogger(BankingSystemServiceImpl.class.getName());

	BankingSystemServiceImpl(BankingSystemDao dao) {
		this.dao = dao;
	}

	@Override
	public Object authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException {
		myLogger.info("Successfully authenticated user login with userID: " + bean.getUserId());
		return dao.authenticateUser(bean);
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber)
			throws NoTransactionsExistException, InternalServerException {
		myLogger.info("Successfully listed transactions for user with account number: " + accountNumber);
		return dao.listTransactions(accountNumber);
	}

	@Override
	public boolean updatePassword(String newPassword, String userId) throws InternalServerException {
		myLogger.info("Successfully changed password for user with userID: " + userId);
		return dao.updatePassword(newPassword, userId);
	}

}
