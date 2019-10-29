package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import com.cg.BankingSystem.exception.NoTransactionsExistException;
import com.cg.BankingSystem.exception.UserNotFoundException;

public class AdminDaoImpl implements AdminDao {

	// method to verify that the user has signed up previously and has an existing account
	@Override
	public Admin authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement checkCredStmt = conn.prepareStatement(BankingSystemDao.Queries.LOGIN_AUTHENTICATION_BA_QUERY.getValue());
			checkCredStmt.setString(1, bean.getUserId());
			checkCredStmt.setString(2, bean.getPassword());
			
			ResultSet credCheckResult = checkCredStmt.executeQuery();
			
			if (!credCheckResult.next())
				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
			PreparedStatement fetchAdminStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_ADMIN_DETAILS_QUERY.getValue());
			fetchAdminStmt.setString(1, credCheckResult.getString(1));
			
			ResultSet adminDetails = fetchAdminStmt.executeQuery();
			

			// throw error if server faces a glitch
			if (!adminDetails.next())
				throw new InternalServerException("Server is facing issues, please try again later.");
			
			Admin fetchedAdmin = new Admin();
			fetchedAdmin.setUserId(adminDetails.getString(1));
			fetchedAdmin.setUserName(adminDetails.getString(2));
			
			return fetchedAdmin;
		} catch (SQLException ex) {
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


	// to display the entire list of transactions for any customer
	@Override
	public List<Transaction> listTransactions(long accountNumber) throws NoTransactionsExistException, InternalServerException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement getTxnsStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_TRANSACTIONS_QUERY.getValue());;
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
			

			// throw exception if no transactions have been performed yet
			if (transactions.size() == 0)
				throw new NoTransactionsExistException("No transactions found for user: " + accountNumber);
			
			return transactions;
		} catch (SQLException ex) {
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

	// process request of customer to update the new password
	@Override
	public boolean updatePassword(String newPassword, String userId) throws InternalServerException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement updatePwdStmt = conn.prepareStatement(BankingSystemDao.Queries.UPDATE_PASSWORD_QUERY.getValue());
			updatePwdStmt.setString(1, newPassword);
			updatePwdStmt.setString(2, userId);
			
			int rowsAffected = updatePwdStmt.executeUpdate();
			
			if (rowsAffected > 0)
				return true;
			return false;
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



	// method to create new account for a customer. Can be a new customer or an existing one having a second account
	@Override
	public long createNewAccount(SignUp newCustomer) throws AccountNotCreatedException, InternalServerException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement accountInsertStmt = conn.prepareStatement(BankingSystemDao.Queries.INSERT_ACCOUNT_QUERY.getValue());
			PreparedStatement customerInsertStmt = conn.prepareStatement(BankingSystemDao.Queries.INSERT_CUSTOMER_QUERY.getValue());
			PreparedStatement userInsertStmt = conn.prepareStatement(BankingSystemDao.Queries.INSERT_USER_QUERY.getValue());
			
			long accountNumber = insertIntoAccount(conn, accountInsertStmt, newCustomer);
			insertIntoCustomer(conn, customerInsertStmt, newCustomer, accountNumber);
			insertIntoUser(conn, userInsertStmt, newCustomer, accountNumber);
			
			return accountNumber;
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

	// enter the data required for creation of a new account
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
			System.out.println("In User SQL Exception");
			conn.rollback();
			throw e;
		}
		
		if(rowsAffected == 0) {
			conn.rollback();
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
	}

	//  enter the data required for creation of a new account
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
			System.out.println("In Customer SQL Exception");
			conn.rollback();
			throw e;
		}
		
		if (rowsAffected == 0) {
			conn.rollback();
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
	}

	private long insertIntoAccount(Connection conn, PreparedStatement accountInsertStmt, SignUp newCustomer) throws AccountNotCreatedException, InternalServerException, SQLException {
		accountInsertStmt.setString(1, newCustomer.getAccountType().getValue());
		accountInsertStmt.setDouble(2, newCustomer.getOpeningBal());
		accountInsertStmt.setDate(3, DatabaseUtilities.getSQLDate(LocalDate.now()));
		
		int rowsAffected = accountInsertStmt.executeUpdate();
		
		if (rowsAffected == 0)
			throw new AccountNotCreatedException("Account couldn't be created now, please try again later");
			
		PreparedStatement getAccNoStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_NUMBER_QUERY.getValue());
			
		ResultSet accountNumber;
		try {
			accountNumber = getAccNoStmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("In Account SQL Exception");
			conn.rollback();
			throw e;
		}
			
		if (!accountNumber.next()) {
			conn.rollback();
			throw new AccountNotCreatedException("Account couldn't be created, please try again later");
		}
		return accountNumber.getLong(1);
	}

	// method to find details of a particular customer
	@Override
	public Customer findCustomer(String userId) throws InternalServerException, UserNotFoundException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement findUserStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_USER_DETAILS_QUERY.getValue());
			PreparedStatement findCustomerStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_CUSTOMER_DETAILS_QUERY.getValue());
			PreparedStatement findAccountStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_DETAILS_QUERY.getValue());
			
			findUserStmt.setString(1, userId);
			
			ResultSet userDetails = findUserStmt.executeQuery();
			
			if (!userDetails.next())
				throw new UserNotFoundException("No users found with ID: " + userId);
			
			findCustomerStmt.setLong(1, userDetails.getLong(5));
			findAccountStmt.setLong(1, userDetails.getLong(5));
			
			ResultSet customerDetails = findCustomerStmt.executeQuery();
			ResultSet accountDetails = findAccountStmt.executeQuery();
			
			if (!customerDetails.next() || !accountDetails.next())
				throw new InternalServerException("Server is facing issues, please try again later.");
			
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

	// method to save all the details of an existing user
	@Override
	public boolean saveExistingUser(SignUp newCustomer) throws InternalServerException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement saveUserStmt = conn.prepareStatement(BankingSystemDao.Queries.INSERT_EXISTING_ACCOUNT_QUERY.getValue());
			
			saveUserStmt.setLong(1, newCustomer.getAccountNumber());
			saveUserStmt.setString(2, newCustomer.getAccountType().getValue());
			saveUserStmt.setDouble(3, newCustomer.getOpeningBal());
			saveUserStmt.setDate(4, DatabaseUtilities.getSQLDate(LocalDate.now()));
			
			int rowsAffected = saveUserStmt.executeUpdate();
			
			if (rowsAffected == 0)
				return false;
			return true;
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
