package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;
import com.cg.BankingSystem.exception.UserNotFoundException;

public class AdminDaoImpl implements AdminDao {

	static Logger myLogger = Logger.getLogger(AdminDaoImpl.class.getName());

	@Override
	public Admin authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement checkCredStmt = conn
					.prepareStatement(BankingSystemDao.Queries.LOGIN_AUTHENTICATION_BA_QUERY.getValue());
			checkCredStmt.setString(1, bean.getUserId());
			checkCredStmt.setString(2, bean.getPassword());

			ResultSet credCheckResult = checkCredStmt.executeQuery();

			if (!credCheckResult.next()) {
				myLogger.info("Invalid credentials entered by Admin. InvalidCredentialsException thrown.");
				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");

			}
			PreparedStatement fetchAdminStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_ADMIN_DETAILS_QUERY.getValue());
			fetchAdminStmt.setString(1, credCheckResult.getString(1));

			ResultSet adminDetails = fetchAdminStmt.executeQuery();

			if (!adminDetails.next()) {
				myLogger.info(
						"Login attempt by admin failed due to unavailability in database. Thrown InternalServerException.");
				throw new InternalServerException("Server is facing issues, please try again later.");
			}
			Admin fetchedAdmin = new Admin();
			fetchedAdmin.setUserId(adminDetails.getString(1));
			fetchedAdmin.setUserName(adminDetails.getString(2));

			myLogger.info("Admin login validated.");
			return fetchedAdmin;
		} catch (SQLException ex) {
			myLogger.info("Login attempt by admin failed. Thrown InternalServerException.");
			throw new InternalServerException(ex.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber)
			throws NoTransactionsExistException, InternalServerException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement getTxnsStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_TRANSACTIONS_QUERY.getValue());
			;
			getTxnsStmt.setLong(1, accountNumber); // Setting account id for which transactions is needed

			ResultSet txnsFetched = getTxnsStmt.executeQuery();

			List<Transaction> transactions = new ArrayList<Transaction>();

			while (txnsFetched.next()) {
				Transaction transaction = new Transaction();
				transaction.setTransactionID(txnsFetched.getInt(1));
				transaction.setTransactionDescription(txnsFetched.getString(2));
				transaction.setTransactionDate(DatabaseUtilities.getLocalDate(txnsFetched.getDate(3)));
				transaction.setTransactionType(DatabaseUtilities.getTransactionType(txnsFetched.getString(4)));
				transaction.setTransactionAmount(txnsFetched.getDouble(5));
				transaction.setAccountNo(txnsFetched.getLong(6));

				transactions.add(transaction);
			}

			if (transactions.size() == 0) {
				myLogger.info(
						"No transactions found in requested account database. Thrown NoTransactionsExistException.");
				throw new NoTransactionsExistException("No transactions found for user: " + accountNumber);
			}
			myLogger.info("Transactions fetched from database and printed.");
			return transactions;
		} catch (SQLException ex) {
			myLogger.info("Error in fetching from database. Thrown InternalServerException.");
			throw new InternalServerException(ex.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean updatePassword(String newPassword, String userId) throws InternalServerException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement updatePwdStmt = conn
					.prepareStatement(BankingSystemDao.Queries.UPDATE_PASSWORD_QUERY.getValue());
			updatePwdStmt.setString(1, newPassword);
			updatePwdStmt.setString(2, userId);

			int rowsAffected = updatePwdStmt.executeUpdate();

			if (rowsAffected > 0) {
				myLogger.info("Password updated by admin.");
				return true;
			} else {
				myLogger.info("Password change failed.");
				return false;
			}
		} catch (SQLException e) {
			myLogger.info("Error changing password. InternalServerException thrown.");
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

	@Override
	public long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement accountInsertStmt = conn
					.prepareStatement(BankingSystemDao.Queries.INSERT_ACCOUNT_QUERY.getValue());
			PreparedStatement customerInsertStmt = conn
					.prepareStatement(BankingSystemDao.Queries.INSERT_CUSTOMER_QUERY.getValue());
			PreparedStatement userInsertStmt = conn
					.prepareStatement(BankingSystemDao.Queries.INSERT_USER_QUERY.getValue());

			long accountNumber = insertIntoAccount(conn, accountInsertStmt, newCustomer);
			insertIntoCustomer(customerInsertStmt, newCustomer, accountNumber);
			insertIntoUser(userInsertStmt, newCustomer, accountNumber);
			myLogger.info("Create new account method initiated in DAO Layer.");

			return accountNumber;
		} catch (SQLException e) {
			myLogger.info("Error creating new account. InternalServerException thrown.");
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

	private void insertIntoUser(PreparedStatement userInsertStmt, SignUp newCustomer, long accountNumber)
			throws AccountNotCreatedException, SQLException {
		userInsertStmt.setString(1, newCustomer.getUserId());
		userInsertStmt.setString(2, newCustomer.getPassword());
		userInsertStmt.setString(3, newCustomer.getTransactionPassword());
		userInsertStmt.setString(4, "N"); // Change this magic string
		userInsertStmt.setLong(5, accountNumber);

		int rowsAffected = userInsertStmt.executeUpdate();

		if (rowsAffected == 0) {
			// revert insert in account master and customer table
			myLogger.info(
					"Inserting new user into user_table failed during creating new account. AccountNotCreatedException thrown.");
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
		myLogger.info("New user added to user_table for creating new account.");
	}

	private void insertIntoCustomer(PreparedStatement customerInsertStmt, SignUp newCustomer, long accountNumber)
			throws AccountNotCreatedException, SQLException {
		customerInsertStmt.setLong(1, accountNumber);
		customerInsertStmt.setString(2, newCustomer.getName());
		customerInsertStmt.setString(3, newCustomer.getEmail());
		customerInsertStmt.setString(4, newCustomer.getAddress());
		customerInsertStmt.setString(5, newCustomer.getPanCardNumber());
		customerInsertStmt.setString(6, newCustomer.getMobileNo());

		int rowsAffected = customerInsertStmt.executeUpdate();

		if (rowsAffected == 0) {
			// Revert insert in account master table
			myLogger.info(
					"Inserting new customer into customer table failed during creating new account. AccountNotCreatedException thrown.");
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
		myLogger.info("New customer added to customer table for creating new account.");
	}

	private long insertIntoAccount(Connection conn, PreparedStatement accountInsertStmt, SignUp newCustomer)
			throws AccountNotCreatedException, InternalServerException, SQLException {
		accountInsertStmt.setString(1, newCustomer.getAccountType().getValue());
		accountInsertStmt.setDouble(2, newCustomer.getOpeningBal());
		accountInsertStmt.setDate(3, DatabaseUtilities.getSQLDate(LocalDate.now()));

		int rowsAffected = accountInsertStmt.executeUpdate();

		if (rowsAffected == 0) {
			myLogger.info(
					"Inserting new account into account_master table failed during creating new account. AccountNotCreatedException thrown.");
			throw new AccountNotCreatedException("Account couldn't be created now, please try again later");

		}
		myLogger.info("New account added to account_master table for creating new account.");
		PreparedStatement getAccNoStmt = conn
				.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_NUMBER_QUERY.getValue());

		ResultSet accountNumber = getAccNoStmt.executeQuery();

		if (!accountNumber.next()) {
			// Write code to revert writing to account table
			myLogger.info("Error creating new account. AccountNotCreatedException thrown.");
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
		myLogger.info("Successfully created new account.");
		return accountNumber.getLong(1);
	}

	@Override
	public Customer findCustomer(String userId) throws InternalServerException, UserNotFoundException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement findUserStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_USER_DETAILS_QUERY.getValue());
			PreparedStatement findCustomerStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_CUSTOMER_DETAILS_QUERY.getValue());
			PreparedStatement findAccountStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_DETAILS_QUERY.getValue());

			findUserStmt.setString(1, userId);

			ResultSet userDetails = findUserStmt.executeQuery();

			if (!userDetails.next()) {
				myLogger.info("Can't find user with specified details. UserNotFoundException thrown.");
				throw new UserNotFoundException("No users found with ID: " + userId);
			}
			myLogger.info("User fetched from database successfully.");
			findCustomerStmt.setLong(1, userDetails.getLong(5));
			findAccountStmt.setLong(1, userDetails.getLong(5));

			ResultSet customerDetails = findCustomerStmt.executeQuery();
			ResultSet accountDetails = findAccountStmt.executeQuery();

			if (!customerDetails.next() || !accountDetails.next()) {
				myLogger.info("Error finding user with specified details. InternalServerException thrown.");
				throw new InternalServerException("Server is facing issues, please try again later.");
			}
			Customer fetchedCustomer = new Customer();

			fetchedCustomer.setAccountNumber(accountDetails.getLong(1));
			fetchedCustomer.setAccountType(DatabaseUtilities.getAccountType(accountDetails.getString(2)));
			fetchedCustomer.setBalance(accountDetails.getDouble(3));
			fetchedCustomer.setName(customerDetails.getString(2));
			fetchedCustomer.setEmailId(customerDetails.getString(3));
			fetchedCustomer.setAddress(customerDetails.getString(4));
			fetchedCustomer.setPanCardNumber(customerDetails.getString(5));
			fetchedCustomer.setMobileNumber(customerDetails.getString(6));
			fetchedCustomer.setUserId(userDetails.getString(1));
			fetchedCustomer.setPassword(userDetails.getString(2));
			fetchedCustomer.setTransactionPassword(userDetails.getString(3));

			myLogger.info("Customer fetched from database successfully.");
			return fetchedCustomer;
		} catch (SQLException e) {
			myLogger.info("Error fetching data about customer from database. InternalServerException thrown");
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

	@Override
	public boolean saveExistingUser(SignUp newCustomer) throws InternalServerException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement saveUserStmt = conn
					.prepareStatement(BankingSystemDao.Queries.INSERT_EXISTING_ACCOUNT_QUERY.getValue());

			saveUserStmt.setLong(1, newCustomer.getAccountNumber());
			saveUserStmt.setString(2, newCustomer.getAccountType().getValue());
			saveUserStmt.setDouble(3, newCustomer.getOpeningBal());
			saveUserStmt.setDate(4, DatabaseUtilities.getSQLDate(LocalDate.now()));

			int rowsAffected = saveUserStmt.executeUpdate();

			if (rowsAffected == 0) {
				myLogger.info("Adding new account into accounts_master table for existing user in database failed.");
				return false;
			}
			myLogger.info("Adding new account into accounts_master table for existing user in database successful.");
			return true;
		} catch (SQLException e) {
			myLogger.info("Error adding new account for existing user into database. InternalServerException thrown");
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