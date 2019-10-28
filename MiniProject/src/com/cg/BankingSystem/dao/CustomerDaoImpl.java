package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Payee;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InsufficientBalanceException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;

public class CustomerDaoImpl implements CustomerDao {

	@Override
	public Customer authenticateUser(LoginBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updatePassword(String newPassword, String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changeContactNumber(String newNumber) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changeAddress(String newAddress) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int requestForCheckBook() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void fundTransfer(long fromAccountNo, Payee payee, double amount, String transactionPassword)
			throws InsufficientBalanceException, InvalidCredentialsException, InternalServerException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();

			PreparedStatement transactionAuthenticationStmt = conn
					.prepareStatement(BankingSystemDao.Queries.TRANSACTION_AUTHENTICATION_QUERY.getValue());
			transactionAuthenticationStmt.setLong(1, fromAccountNo);
			ResultSet transactionPasswordResultSet = transactionAuthenticationStmt.executeQuery();
			if (!transactionPasswordResultSet.next())
				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
			String transactionPasswordVerifier = transactionPasswordResultSet.getString(1);
			if (transactionPasswordVerifier != transactionPassword)
				throw new InvalidCredentialsException(
						"Invalid transaction password!\nPlease enter valid transaction password.");

			PreparedStatement getDebitAccountBalanceStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_BALANCE_QUERY.getValue());
			getDebitAccountBalanceStmt.setLong(1, fromAccountNo);
			ResultSet accountBalanceResultSet = getDebitAccountBalanceStmt.executeQuery();
			if (!accountBalanceResultSet.next())
				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
			double debitAcountBalance = accountBalanceResultSet.getDouble(1);
			if (amount < debitAcountBalance) {
				throw new InsufficientBalanceException("Balance not sufficient");
			} else {
				debitAcountBalance -= amount;
				PreparedStatement debitStmt = conn
						.prepareStatement(BankingSystemDao.Queries.DEBIT_ACCOUNT_BALANCE_QUERY.getValue());
				debitStmt.setDouble(1, debitAcountBalance);
				debitStmt.setLong(2, fromAccountNo);
				debitStmt.executeUpdate();

				PreparedStatement getCreditAccountBalanceStmt = conn
						.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_BALANCE_QUERY.getValue());
				getCreditAccountBalanceStmt.setLong(1, payee.getAccountNo());
				ResultSet payeeAccountBalanceresultSet = getCreditAccountBalanceStmt.executeQuery();
				if (!payeeAccountBalanceresultSet.next())
					throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
				double payeeAccountBalance = payeeAccountBalanceresultSet.getDouble(1);
				payeeAccountBalance += amount;
				PreparedStatement creditStmt = conn
						.prepareStatement(BankingSystemDao.Queries.CREDIT_ACCOUNT_BALANCE_QUERY.getValue());
				creditStmt.setDouble(1, payeeAccountBalance);
				creditStmt.setLong(2, payee.getAccountNo());
				creditStmt.executeUpdate();
			}

		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
