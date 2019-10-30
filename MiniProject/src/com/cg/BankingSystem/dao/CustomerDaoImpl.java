package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.log4j.Logger;

import com.cg.BankingSystem.dto.Account;
import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.dto.TransactionType;
import com.cg.BankingSystem.exception.AccountsNotFoundException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;

public class CustomerDaoImpl implements CustomerDao {
	
	static Logger customerDaoLogger ;
	
	static {
		customerDaoLogger = Logger.getLogger(CustomerDaoImpl.class.getName());
	}

	@Override
	public Customer authenticateUser(LoginBean bean) throws InternalServerException, InvalidCredentialsException {
		Connection conn = null;
		PreparedStatement checkCredStmt = null, fetchCustomerStmt = null, fetchAccountStmt = null, fetchTxnPwdStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			checkCredStmt = conn.prepareStatement(BankingSystemDao.Queries.LOGIN_AUTHENTICATION_CC_QUERY.getValue());
			checkCredStmt.setString(1, bean.getUserId());
			checkCredStmt.setString(2, bean.getPassword());
			
			ResultSet credCheckResult = checkCredStmt.executeQuery();
			
			if (!credCheckResult.next()) {
				customerDaoLogger.error("InvalidCredentialsException thrown in CustomerDao.authenticateUser() method with cause: Invalid Credentials! Please enter valid credentials.");
				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
			}
			
			fetchCustomerStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_CUSTOMER_DETAILS_QUERY.getValue());
			fetchAccountStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_DETAILS_CC_QUERY.getValue());
			fetchTxnPwdStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_TXN_PWD_QUERY.getValue());
			
			fetchCustomerStmt.setLong(1, credCheckResult.getLong(1));
			fetchAccountStmt.setLong(1, credCheckResult.getLong(1));
			fetchAccountStmt.setString(2, bean.getAccountType().getValue());
			fetchTxnPwdStmt.setLong(1, credCheckResult.getLong(1));
			
			ResultSet customerDetails = fetchCustomerStmt.executeQuery();
			ResultSet accountDetails = fetchAccountStmt.executeQuery();
			ResultSet txnDetails = fetchTxnPwdStmt.executeQuery();
			
			if (!customerDetails.next() || !accountDetails.next() || !txnDetails.next()) {
				customerDaoLogger.error("InvalidCredentialsException thrown in CustomerDao.authenticateUser() method with cause: Server is facing issues, please try again later.");
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
			fetchedCustomer.setUserId(bean.getUserId());
			fetchedCustomer.setPassword(new String(Base64.getDecoder().decode(bean.getPassword().getBytes())));
			fetchedCustomer.setTransactionPassword(new String(Base64.getDecoder().decode(txnDetails.getString(1))));
			
			customerDaoLogger.info("Customer with user ID: " + fetchedCustomer.getUserId() + " logged in at: " + LocalDateTime.now());
			return fetchedCustomer;
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.authenticateUser() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (checkCredStmt != null)
					checkCredStmt.close();
				if (fetchCustomerStmt != null)
					fetchCustomerStmt.close();
				if (fetchAccountStmt != null)
					fetchAccountStmt.close();
				if (fetchTxnPwdStmt != null)
					fetchTxnPwdStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.authenticateUser(): " + e.getStackTrace());
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
				customerDaoLogger.error("NoTransactionsExistException thrown in CustomerDao.listTransactions() method with cause: No transactions found for user: " + accountNumber);
				throw new NoTransactionsExistException("No transactions found for user: " + accountNumber);
			}
			
			return transactions;
		} catch (SQLException ex) {
			customerDaoLogger.error("InternalServerException in CustomerDao.listTransactions() method thrown with cause: " + ex.getMessage());
			throw new InternalServerException(ex.getMessage());
		} finally {
			try {
				if (getTxnsStmt != null)
					getTxnsStmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				customerDaoLogger.error("CustomerDao.listTransactions(): " + e.getStackTrace());
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
				customerDaoLogger.info("Customer " + userId + " updated password successfully.");
				return true;
			}
			customerDaoLogger.warn("Customer " + userId + " couldn't update password.");
			return false;
		} catch (SQLException e) {
			customerDaoLogger.info("InternalServerException thrown in CustomerDao.updatePassword() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (updatePwdStmt != null)
					updatePwdStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.updatePassword(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException {
		Connection conn = null;
		PreparedStatement updateContactStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			updateContactStmt = conn.prepareStatement(Queries.CHANGE_CONTACT_NUMBER_QUERY.getValue());
			updateContactStmt.setString(1, newNumber);
			updateContactStmt.setLong(2, accountNumber);
			updateContactStmt.executeUpdate();
			int rowsAffected = updateContactStmt.executeUpdate();
			if (rowsAffected == 0) {
				customerDaoLogger.warn("Customer with account_id: " + accountNumber + " couldn't update contact number.");
				return false;
			}
			customerDaoLogger.info("Customer with account_id: " + accountNumber + " updated contact number successfully.");
			return true;
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.changeContactNumber() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (updateContactStmt != null)
					updateContactStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.changeContactNumber(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException {
		Connection conn = null;
		PreparedStatement changeAddressStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			changeAddressStmt = conn.prepareStatement(Queries.CHANGE_ADDRESS_QUERY.getValue());
			changeAddressStmt.setString(1, newAddress);
			changeAddressStmt.setLong(2, accountNumber);
			changeAddressStmt.executeUpdate();
			int rowsAffected = changeAddressStmt.executeUpdate();
			if (rowsAffected == 0) {
				customerDaoLogger.warn("Customer with account_id: " + accountNumber + " couldn't update address.");
				return false;
			}
			customerDaoLogger.info("Customer with account_id: " + accountNumber + " updated address successfully.");
			return true;
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.changeAddress() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (changeAddressStmt != null)
					changeAddressStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.changeAddress(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException {
		Connection conn = null;
		PreparedStatement chckBookStmt = null;
		Statement rqstIDStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			conn.setAutoCommit(false);
			chckBookStmt = conn.prepareStatement(Queries.CHEQUE_BOOK_SERVICE_QUERY.getValue());
			chckBookStmt.setLong(1, request.getAccountNumber());
			chckBookStmt.setDate(2, DatabaseUtilities.getSQLDate(request.getRequestDate()));
			chckBookStmt.setInt(3, request.getStatus());

			int rowsAffected = chckBookStmt.executeUpdate();
			if (rowsAffected == 0) {
				customerDaoLogger.error("RequestCannotBeProcessedException thrown in CustomerDao.requestForCheckBook() method with cause: Request could not be made for a cheque book. Please try again later");
				throw new RequestCannotBeProcessedException("Request could not be made for a cheque book.\nPlease try again later");
			}
			
			rqstIDStmt = conn.createStatement();
			ResultSet rs = rqstIDStmt.executeQuery(Queries.REQUEST_ID_QUERY.getValue());
			
			if (!rs.next()) {
				customerDaoLogger.error("CustomerDao.requestForCheckBook(): Request placed into database, but request id could not be fetched. Rolling back the transaction.");
				conn.rollback();
				customerDaoLogger.error("InternalServerException thrown in CustomerDao.requestForCheckBook() method with cause: Request cannot be processed not. Please try again later.");
				throw new InternalServerException("Request cannot be processed not.\nPlease try again later.");
			}
			
			customerDaoLogger.info("Successfully added new cheque book service request to service_tracker table in database, with service_id: " + rs.getInt(1));
			return rs.getInt(1);
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.requestForCheckBook() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (chckBookStmt != null)
					chckBookStmt.close();
				if (rqstIDStmt != null)
					rqstIDStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.requestForCheckBook(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException {
		Connection conn = null;
		PreparedStatement requestStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			requestStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_REQUESTS_QUERY.getValue());
			
			requestStmt.setLong(1, accountNumber);
			
			ResultSet requestResults = requestStmt.executeQuery();
			
			List<Request> requests = new ArrayList<Request>();
			int counter = 0;
			
			while (requestResults.next() && counter < 20) {
				// Getting only 20 requests that have been made in the past 180 days
				if (! (DatabaseUtilities.getDuration(DatabaseUtilities.getLocalDate(requestResults.getDate(3)), LocalDate.now()) < 180L)) 
					continue;
				Request request = new Request();
				request.setRequestNumber(requestResults.getInt(1));
				request.setAccountNumber(requestResults.getLong(2));
				request.setRequestDate(DatabaseUtilities.getLocalDate(requestResults.getDate(3)));
				request.setStatus(requestResults.getInt(4));
				
				requests.add(request);
				counter ++;
			}
			
			if (requests.size() == 0) {
				customerDaoLogger.error("NoServicesMadeException thrown in CustomerDao.getRequests() with cause: User has no pending service requests.");
				throw new NoServicesMadeException("User has no pending service requests.");
			}
			
			return requests;
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.getRequests() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (requestStmt != null)
					requestStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.getRequests(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public Account fetchOtherExistingAccount(long accountNumber, AccountType accountType) throws AccountsNotFoundException, InternalServerException {
		Connection conn = null;
		PreparedStatement fetchOthrAcntsStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			fetchOthrAcntsStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_OTHER_ACCOUNTS_QUERY.getValue());
			
			fetchOthrAcntsStmt.setLong(1, accountNumber);
			
			String complimentaryAccountType = accountType == AccountType.SAVINGS_ACCOUNT ? AccountType.CURRENT_ACCOUNT.getValue() : AccountType.SAVINGS_ACCOUNT.getValue();
			
			fetchOthrAcntsStmt.setString(2, complimentaryAccountType);
			
			ResultSet results = fetchOthrAcntsStmt.executeQuery();
			
			int matchedEntry = 0;
			if (results.next())
				matchedEntry = results.getInt(1);
			
			if (matchedEntry == 0) {
				customerDaoLogger.error("AccountsNotFoundException thrown in CustomerDao.fetchOtherExistingAccount() method with cause: No Alternate account for this user.");
				throw new AccountsNotFoundException("No Alternate account for this user.");
			}
			
			Account fetchedAccount = new Account();
			fetchedAccount.setAccountNumber(accountNumber);
			fetchedAccount.setAccountType(DatabaseUtilities.getAccountType(complimentaryAccountType));
			
			return fetchedAccount;
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.fetchOtherExistingAccount() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (fetchOthrAcntsStmt != null)
					fetchOthrAcntsStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.fetchOtherExistingAccount(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public List<Account> fetchBeneficiaries(long accountNumber) throws InternalServerException {
		Connection conn = null;
		PreparedStatement fetchBnfcryStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			fetchBnfcryStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_BENEFICIARIES_QUERY.getValue());
			fetchBnfcryStmt.setLong(1, accountNumber);
			
			ResultSet bnfcryDetails = fetchBnfcryStmt.executeQuery();
			
			List<Account> beneficiaries = new ArrayList<Account>();
			while(bnfcryDetails.next()) {
				Account account = new Account();
				account.setAccountNumber(bnfcryDetails.getLong(1));
				account.setNickName(bnfcryDetails.getString(2));
				beneficiaries.add(account);
			}
			
			// Returning the beneficiaries fetched from database without checking if no beneficiaries are present in the list
			return beneficiaries;
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.fetchBeneficiaries() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (fetchBnfcryStmt != null)
					fetchBnfcryStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.fetchBeneficiaries(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public boolean transferFund(Customer fromAccount, Account otherAccount, Transaction txnDetails) throws InternalServerException {
		Connection conn = null;
		PreparedStatement creditBlncStmt = null, creditStmt = null, debitStmt = null, creditTxnStmt = null, debitTxnStmt = null, creditBackStmt = null, creditBackTxnStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			creditBlncStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_TRANSFER_ACCOUNT_BALANCE_QUERY.getValue());
			creditStmt = conn.prepareStatement(BankingSystemDao.Queries.TXN_ACCOUNT_BALANCE_QUERY.getValue());
			debitStmt = conn.prepareStatement(BankingSystemDao.Queries.TXN_ACCOUNT_BALANCE_QUERY.getValue());
			
			creditTxnStmt = conn.prepareStatement(BankingSystemDao.Queries.ADD_TRANSACTION_DETAILS.getValue());
			debitTxnStmt = conn.prepareStatement(BankingSystemDao.Queries.ADD_TRANSACTION_DETAILS.getValue());
			
			creditBlncStmt.setLong(1, otherAccount.getAccountNumber());
			
			ResultSet balanceDetails = creditBlncStmt.executeQuery();
			
			if (!balanceDetails.next()) {
				customerDaoLogger.info("InternalServerException thrown in CustomerDao.transferFund() with cause: Server Error, please try again later.");
				throw new InternalServerException("Server Error, please try again later.");
			}
			
			double toBalance = balanceDetails.getDouble(1);
			double fromBalance = fromAccount.getBalance();
			
			debitStmt.setDouble(1, fromBalance - txnDetails.getTransactionAmount());
			debitStmt.setLong(2, fromAccount.getAccountNumber());
			debitStmt.setString(3, fromAccount.getAccountType().getValue());
			
			creditStmt.setDouble(1, toBalance + txnDetails.getTransactionAmount());
			creditStmt.setLong(2, otherAccount.getAccountNumber());
			creditStmt.setString(3, AccountType.SAVINGS_ACCOUNT.getValue());
			
			int debitRowsAffected = debitStmt.executeUpdate();
			
			if (debitRowsAffected == 0) {
				customerDaoLogger.info("CustomerDao.transferFund(): Could not debit funds from: " + fromAccount.getAccountNumber() + ". Cancelling Transaction.");
				return false;
			}
			
			customerDaoLogger.info("CustomerDao.transferFund(): " + txnDetails.getTransactionAmount() + " debited from: " + fromAccount.getAccountNumber() + ". Attempting crediting into: " + otherAccount.getAccountNumber());

//			 Updating balance in local profile details
			fromAccount.setBalance(fromBalance - txnDetails.getTransactionAmount());
			
			debitTxnStmt.setString(1, txnDetails.getTransactionDescription());
			debitTxnStmt.setDate(2, DatabaseUtilities.getSQLDate(LocalDate.now()));
			debitTxnStmt.setString(3, TransactionType.DEBIT.getValue());
			debitTxnStmt.setDouble(4, txnDetails.getTransactionAmount());
			debitTxnStmt.setLong(5, otherAccount.getAccountNumber());
			
			int creditRowsAffected = creditStmt.executeUpdate();
			
			if(creditRowsAffected == 0) {
				customerDaoLogger.warn("CustomerDao.transferFund(): Amount debited from: " + fromAccount + " could not be credited in: " + otherAccount.getAccountNumber() + ". Initiating Rollback.");
				creditBackStmt = conn.prepareStatement(BankingSystemDao.Queries.TXN_ACCOUNT_BALANCE_QUERY.getValue());
				creditBackStmt.setDouble(1, fromAccount.getBalance() + txnDetails.getTransactionAmount());
				creditBackStmt.setLong(2, fromAccount.getAccountNumber());
				
				int creditBackRowsAffected = creditBackStmt.executeUpdate();
				
				if (creditBackRowsAffected == 0) {
					customerDaoLogger.error("CustomerDao.transferFund(): Rollback could not be done into: " + fromAccount.getAccountNumber());
					throw new InternalServerException("Transaction Failed. Refund will be initiated soon");
				}
				
				fromAccount.setBalance(fromAccount.getBalance() + txnDetails.getTransactionAmount());
				
				creditBackTxnStmt = conn.prepareStatement(BankingSystemDao.Queries.ADD_TRANSACTION_DETAILS.getValue());
				creditBackTxnStmt.setString(1, txnDetails.getTransactionDescription());
				creditBackTxnStmt.setDate(2, DatabaseUtilities.getSQLDate(LocalDate.now()));
				creditBackTxnStmt.setString(3, TransactionType.CREDIT.getValue());
				creditBackTxnStmt.setDouble(4, txnDetails.getTransactionAmount());
				creditBackTxnStmt.setLong(5, fromAccount.getAccountNumber());
				
				creditBackTxnStmt.executeUpdate();
				
				customerDaoLogger.info("CustomerDao.transferFund(): Rollback done for account: " + fromAccount.getAccountNumber() + ". Amount refunded: " + txnDetails.getTransactionAmount());
				
				return false;
			}
			
			creditTxnStmt.setString(1, txnDetails.getTransactionDescription());
			creditTxnStmt.setDate(2, DatabaseUtilities.getSQLDate(LocalDate.now()));
			creditTxnStmt.setString(3, TransactionType.CREDIT.getValue());
			creditTxnStmt.setDouble(4, txnDetails.getTransactionAmount());
			creditTxnStmt.setLong(5, fromAccount.getAccountNumber());
			
			creditTxnStmt.executeUpdate();
			debitTxnStmt.executeUpdate();
			
			customerDaoLogger.info("CustomerDao.transferFund(): Transaction for amount: " + txnDetails.getTransactionAmount() + " from: " + 
					fromAccount.getAccountNumber() + " to: " + otherAccount.getAccountNumber() + " completed successfully at: " + LocalDateTime.now());
			
			return true;
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.transferFund() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (creditBlncStmt != null)
					creditBlncStmt.close();
				if (creditStmt != null)
					creditStmt.close();
				if (debitStmt != null)
					debitStmt.close();
				if (creditTxnStmt != null)
					creditTxnStmt.close();
				if (debitTxnStmt != null)
					debitTxnStmt.close();
				if (creditBackStmt != null)
					creditBackStmt.close();
				if (creditBackTxnStmt != null)
					creditBackTxnStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.transferFund(): " + e.getStackTrace());
			}
		}
	}

	@Override
	public boolean addNewBeneficiary(long accountNumber, Account newBeneficiary) throws InternalServerException {
		Connection conn = null;
		PreparedStatement addBnfcryStmt = null;
		
		try {
			conn = JDBCUtil.getConnection();
			addBnfcryStmt = conn.prepareStatement(BankingSystemDao.Queries.ADD_BENEFICIARY_QUERY.getValue());
			
			addBnfcryStmt.setLong(1, accountNumber);
			addBnfcryStmt.setLong(2, newBeneficiary.getAccountNumber());
			addBnfcryStmt.setString(3, newBeneficiary.getNickName());
			
			int rowsAffected = addBnfcryStmt.executeUpdate();
			
			if (rowsAffected == 0) {
				customerDaoLogger.warn("CustomerDao.addNewBeneficiary(): Could not add beneficiary to: " + accountNumber);
				return false;
			}
			
			customerDaoLogger.info("CustomerDao.addNewBeneficiary(): Successfully added beneficiary to beneficiary_details table in database, for: " + accountNumber);
			
			return true;
		} catch (SQLException e) {
			customerDaoLogger.error("InternalServerException thrown in CustomerDao.addNewBeneficiary() method with cause: " + e.getMessage());
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				if (addBnfcryStmt != null)
					addBnfcryStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				customerDaoLogger.error("CustomerDao.addNewBeneficiary(): " + e.getStackTrace());
			}
		}
	}

}
