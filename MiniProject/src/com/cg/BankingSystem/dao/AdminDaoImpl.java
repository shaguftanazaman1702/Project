package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.MaxAccountsDefinedForUserException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;
import com.cg.BankingSystem.exception.UserNotFoundException;

public class AdminDaoImpl implements AdminDao {

//	static Logger adminDaoLogger;
	
	static {
//		adminDaoLogger = Logger.getLogger(AdminDaoImpl.class.getName());
	}
	
	@Override
	public Admin authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException {
		Connection conn = null;
		PreparedStatement checkCredStmt = null, fetchAdminStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			checkCredStmt = conn.prepareStatement(BankingSystemDao.Queries.LOGIN_AUTHENTICATION_BA_QUERY.getValue());
			checkCredStmt.setString(1, bean.getUserId());
			checkCredStmt.setString(2, bean.getPassword());
			
			ResultSet credCheckResult = checkCredStmt.executeQuery();
			
			if (!credCheckResult.next()) {
//				adminDaoLogger.error("InvalidCredentialsException thrown is AdminDao.authenticateUser() method with cause: Invalid Credentials! Please enter valid credentials.");
				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
			}
			fetchAdminStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_ADMIN_DETAILS_QUERY.getValue());
			fetchAdminStmt.setString(1, credCheckResult.getString(1));
			
			ResultSet adminDetails = fetchAdminStmt.executeQuery();
			
			if (!adminDetails.next()) {
//				adminDaoLogger.info("InternalServerException thrown is AdminDao.authenticateUser() method with cause: Server is facing issues, please try again later.");
				throw new InternalServerException("Server is facing issues, please try again later.");
			}
			
			Admin fetchedAdmin = new Admin();
			fetchedAdmin.setUserId(adminDetails.getString(1));
			fetchedAdmin.setUserName(adminDetails.getString(2));
			
//			adminDaoLogger.info("Admin logged in successfully at: " + LocalDateTime.now());
			return fetchedAdmin;
		} catch (SQLException ex) {
//			adminDaoLogger.error("InternalServerException thrown is AdminDao.authenticateUser() method with cause: " + ex.getCause());
			throw new InternalServerException(ex.getMessage());
		} finally {
			try {
				if (checkCredStmt != null)
					checkCredStmt.close();
				if (fetchAdminStmt != null)
					fetchAdminStmt.close();
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
//				adminDaoLogger.error("AdminDao.authenticateUser(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber) throws NoTransactionsExistException, InternalServerException {
		Connection conn = null;
		PreparedStatement getTxnsStmt = null; 

		try {
			conn = JDBCUtil.getConnection();
			getTxnsStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_TRANSACTIONS_QUERY.getValue());;
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
//				adminDaoLogger.error("NoTransactionsExistException thrown is AdminDao.listTransactions() method with cause: No transactions found for user: " + accountNumber);
				throw new NoTransactionsExistException("No transactions found for user: " + accountNumber);
			}
			
			return transactions;
		} catch (SQLException ex) {
//			adminDaoLogger.error("InternalServerException thrown is AdminDao.listTransactions() method with cause: " + ex.getCause());
			throw new InternalServerException(ex.getMessage());
		} finally {
			try {
				if (getTxnsStmt != null)
					getTxnsStmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
//				adminDaoLogger.error("AdminDao.listTransactions(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public boolean updatePassword(String newPassword, String userId) throws InternalServerException {
		Connection conn = null;
		PreparedStatement updatePwdStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			updatePwdStmt = conn.prepareStatement(BankingSystemDao.Queries.UPDATE_PASSWORD_QUERY.getValue());
			updatePwdStmt.setString(1, newPassword);
			updatePwdStmt.setString(2, userId);
			
			int rowsAffected = updatePwdStmt.executeUpdate();
			
			if (rowsAffected > 0) {
//				adminDaoLogger.info("Admin updated the password");
				return true;
			}
//			adminDaoLogger.warn("Password changed attempted, but failed");
			return false;
		} catch (SQLException e) {
//			adminDaoLogger.error("InternalServerException thrown is AdminDao.updatePassword() method with cause: " + e.getCause());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (updatePwdStmt != null)
					updatePwdStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
//				adminDaoLogger.error("AdminDao.updatePassword(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException {
		Connection conn = null;
		PreparedStatement accountInsertStmt = null, customerInsertStmt = null, userInsertStmt = null;
		
//		adminDaoLogger.info("Create new account method initiated in DAO Layer.");
		
		try {
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			accountInsertStmt = conn.prepareStatement(BankingSystemDao.Queries.INSERT_ACCOUNT_QUERY.getValue());
			customerInsertStmt = conn.prepareStatement(BankingSystemDao.Queries.INSERT_CUSTOMER_QUERY.getValue());
			userInsertStmt = conn.prepareStatement(BankingSystemDao.Queries.INSERT_USER_QUERY.getValue());
			
			long accountNumber = insertIntoAccount(conn, accountInsertStmt, newCustomer);
//			adminDaoLogger.info("AdminDao.createNewAccount(): User details for " + newCustomer.getUserId() + " entered in account_master table.");
			insertIntoCustomer(conn, customerInsertStmt, newCustomer, accountNumber);
//			adminDaoLogger.info("AdminDao.createNewAccount(): User details for " + newCustomer.getUserId() + " entered in customer table.");
			insertIntoUser(conn, userInsertStmt, newCustomer, accountNumber);
//			adminDaoLogger.info("AdminDao.createNewAccount(): User details for " + newCustomer.getUserId() + " entered in user_table.");
			
			return accountNumber;
		} catch (SQLException e) {
//			adminDaoLogger.error("InternalServerException thrown is AdminDao.createNewAccount() method with cause: " + e.getCause());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (accountInsertStmt != null)
					accountInsertStmt.close();
				if (customerInsertStmt != null)
					customerInsertStmt.close();
				if (userInsertStmt != null)
					userInsertStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
//				adminDaoLogger.error("AdminDao.createNewAccount(): " + e.getStackTrace());
			}
		}
	}

	private void insertIntoUser(Connection conn, PreparedStatement userInsertStmt, SignUp newCustomer, long accountNumber) throws AccountNotCreatedException, SQLException {
		userInsertStmt.setString(1, newCustomer.getUserId());
		userInsertStmt.setString(2, newCustomer.getPassword());
		userInsertStmt.setString(3, newCustomer.getTransactionPassword());
		userInsertStmt.setString(4, "N"); // Change this magic string
		userInsertStmt.setLong(5, accountNumber);
		
		int rowsAffected;
		try {
			rowsAffected = userInsertStmt.executeUpdate();
		} catch (SQLException e) {
//			adminDaoLogger.error("AdminDao.createNewAccount(): User Details for " + newCustomer.getUserId() + " could not be updated in user_table. Rolling back from account_master and customer tables.");
			conn.rollback();
			throw e;
		}
		
		if(rowsAffected == 0) {
//			adminDaoLogger.error("AdminDao.createNewAccount(): User Details for " + newCustomer.getUserId() + " could not be updated in user_table. Rolling back from account_master and customer tables.");
			conn.rollback();
//			adminDaoLogger.error("AccountNotCreatedException thrown is saveExistingUser method with cause: Account couldn't be created, please try again later");
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
		
//		adminDaoLogger.info("AdminDao.createNewAccount(): New account added to user_table for creating new account.");
	}

	private void insertIntoCustomer(Connection conn, PreparedStatement customerInsertStmt, SignUp newCustomer, long accountNumber) throws AccountNotCreatedException, SQLException {
		customerInsertStmt.setLong(1, accountNumber);
		customerInsertStmt.setString(2, newCustomer.getName());
		customerInsertStmt.setString(3, newCustomer.getEmail());
		customerInsertStmt.setString(4, newCustomer.getAddress());
		customerInsertStmt.setString(5, newCustomer.getPanCardNumber());
		customerInsertStmt.setString(6, newCustomer.getMobileNo());
		
		int rowsAffected;
		try {
			rowsAffected = customerInsertStmt.executeUpdate();
		} catch (SQLException e) {
//			adminDaoLogger.error("AdminDao.createNewAccount(): User Details for " + newCustomer.getUserId() + " could not be updated in customer table. Rolling back from account_master table.");
			conn.rollback();
			throw e;
		}
		
		if (rowsAffected == 0) {
//			adminDaoLogger.error("AdminDao.createNewAccount(): User Details for " + newCustomer.getUserId() + " could not be updated in customer table. Rolling back from account_master table.");
			conn.rollback();
//			adminDaoLogger.error("AccountNotCreatedException thrown is saveExistingUser method with cause: Account couldn't be created, please try again later");
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
		
//		adminDaoLogger.info("AdminDao.createNewAccount(): New account added to customer table for creating new account.");
	}

	private long insertIntoAccount(Connection conn, PreparedStatement accountInsertStmt, SignUp newCustomer) throws AccountNotCreatedException, InternalServerException, SQLException {
		accountInsertStmt.setString(1, newCustomer.getAccountType().getValue());
		accountInsertStmt.setDouble(2, newCustomer.getOpeningBal());
		accountInsertStmt.setDate(3, DatabaseUtilities.getSQLDate(LocalDate.now()));
		
		int rowsAffected = accountInsertStmt.executeUpdate();
		
		if (rowsAffected == 0) {
//			adminDaoLogger.error("AccountNotCreatedException thrown is saveExistingUser method with cause: " + e.getCause());
			throw new AccountNotCreatedException("Account couldn't be created now, please try again later");
		}
		
//		adminDaoLogger.info("AdminDao.createNewAccount(): New account added to account_master table for creating new account.");
			
		PreparedStatement getAccNoStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_NUMBER_QUERY.getValue());
			
		ResultSet accountNumber;
		try {
			accountNumber = getAccNoStmt.executeQuery();
		} catch (SQLException e) {
//			adminDaoLogger.error("AdminDao.createNewAccount(): User Details for " + newCustomer.getUserId() + "added to account_master table, account_id could not be fetched. Rolling backing changes from account_master table.");
			conn.rollback();
			throw e;
		}
			
		if (!accountNumber.next()) {
//			adminDaoLogger.error("AdminDao.createNewAccount(): User Details for " + newCustomer.getUserId() + "added to account_master table, account_id could not be fetched. Rolling backing changes from account_master table.");
			conn.rollback();
//			adminDaoLogger.error("AccountNotCreatedException thrown is saveExistingUser method with cause: Account couldn't be created, please try again later");
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
		return accountNumber.getLong(1);
	}

	@Override
	public Customer findCustomer(String userId) throws InternalServerException, UserNotFoundException {
		Connection conn = null;
		PreparedStatement findUserStmt = null, findCustomerStmt = null, findAccountStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			findUserStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_USER_DETAILS_QUERY.getValue());
			findCustomerStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_CUSTOMER_DETAILS_QUERY.getValue());
			findAccountStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_DETAILS_AD_QUERY.getValue());
			
			findUserStmt.setString(1, userId);
			
			ResultSet userDetails = findUserStmt.executeQuery();
			
			if (!userDetails.next()) {
//				adminDaoLogger.error("UserNotFoundException thrown is AdminDao.findCustomer() method with cause: No users found with ID: " + userId);
				throw new UserNotFoundException("No users found with ID: " + userId);
			}
			
//			adminDaoLogger.info("AdminDao.findCustomer(): Details fetched from user_table successfully.");
			
			findCustomerStmt.setLong(1, userDetails.getLong(5));
			findAccountStmt.setLong(1, userDetails.getLong(5));
			
			ResultSet customerDetails = findCustomerStmt.executeQuery();
			ResultSet accountDetails = findAccountStmt.executeQuery();
			
			if (!customerDetails.next() || !accountDetails.next()) {
//				adminDaoLogger.error("InternalServerException thrown is AdminDao.findCustomer() method with cause: Server is facing issues, please try again later.");
				throw new InternalServerException("Server is facing issues, please try again later.");
			}
			
//			adminDaoLogger.info("AdminDao.findCustomer(): Details fetched from customer and account_master tables successfully.");
			
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
			
			return fetchedCustomer;
		} catch (SQLException e) {
//			adminDaoLogger.error("InternalServerException thrown is AdminDao.findCustomer() method with cause: " + e.getCause());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (findUserStmt != null)
					findUserStmt.close();
				if (findCustomerStmt != null)
					findCustomerStmt.close();
				if (findAccountStmt != null)
					findAccountStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
//				adminDaoLogger.error("AdminDao.findCustomer(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public boolean saveExistingUser(SignUp newCustomer) throws MaxAccountsDefinedForUserException {
		Connection conn = null;
		PreparedStatement saveUserStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			saveUserStmt = conn.prepareStatement(BankingSystemDao.Queries.INSERT_EXISTING_ACCOUNT_QUERY.getValue());
			
			saveUserStmt.setLong(1, newCustomer.getAccountNumber());
			saveUserStmt.setString(2, newCustomer.getAccountType().getValue());
			saveUserStmt.setDouble(3, newCustomer.getOpeningBal());
			saveUserStmt.setDate(4, DatabaseUtilities.getSQLDate(LocalDate.now()));
			
			int rowsAffected = saveUserStmt.executeUpdate();
			
			if (rowsAffected == 0) {
//				adminDaoLogger.error("AdminDao.saveExistingUser(): Adding new account into accounts_master table for existing user failed.");
				return false;
			}
//			adminDaoLogger.info("AdminDao.saveExistingUser(): Adding new account into accounts_master table for existing user successful.");
			return true;
		} catch (SQLException e) {
//			adminDaoLogger.error("MaxAccountsDefinedForUserException thrown is saveExistingUser method with cause: " + e.getCause());
			throw new MaxAccountsDefinedForUserException("User already has Savings and Current account in the Bank.");
		} finally {
			try {
				if (saveUserStmt != null)
					saveUserStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
