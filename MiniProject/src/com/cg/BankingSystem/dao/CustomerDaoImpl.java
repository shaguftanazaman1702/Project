package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

	@Override
	public Customer authenticateUser(LoginBean bean) throws InternalServerException, InvalidCredentialsException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement checkCredStmt = conn
					.prepareStatement(BankingSystemDao.Queries.LOGIN_AUTHENTICATION_CC_QUERY.getValue());
			checkCredStmt.setString(1, bean.getUserId());
			checkCredStmt.setString(2, bean.getPassword());

			ResultSet credCheckResult = checkCredStmt.executeQuery();

			if (!credCheckResult.next())
				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");

			PreparedStatement fetchCustomerStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_CUSTOMER_DETAILS_QUERY.getValue());
			PreparedStatement fetchAccountStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_DETAILS_QUERY.getValue());
			PreparedStatement fetchTxnPwdStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_TXN_PWD_QUERY.getValue());

			fetchCustomerStmt.setLong(1, credCheckResult.getLong(1));
			fetchAccountStmt.setLong(1, credCheckResult.getLong(1));
			fetchTxnPwdStmt.setLong(1, credCheckResult.getLong(1));

			ResultSet customerDetails = fetchCustomerStmt.executeQuery();
			ResultSet accountDetails = fetchAccountStmt.executeQuery();
			ResultSet txnDetails = fetchTxnPwdStmt.executeQuery();

			if (!customerDetails.next() || !accountDetails.next() || !txnDetails.next())
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
			fetchedCustomer.setUserId(bean.getUserId());
			fetchedCustomer.setPassword(bean.getPassword());
			fetchedCustomer.setTransactionPassword(txnDetails.getString(1));

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

	@Override
	public boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement updateContactStmt = conn.prepareStatement(Queries.CHANGE_CONTACT_NUMBER_QUERY.getValue());
			updateContactStmt.setString(1, newNumber);
			updateContactStmt.setLong(2, accountNumber);
			updateContactStmt.executeUpdate();
			int rowsAffected = updateContactStmt.executeUpdate();
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

	@Override
	public boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement changeAddressStmt = conn.prepareStatement(Queries.CHANGE_ADDRESS_QUERY.getValue());
			changeAddressStmt.setString(1, newAddress);
			changeAddressStmt.setLong(2, accountNumber);
			changeAddressStmt.executeUpdate();
			int rowsAffected = changeAddressStmt.executeUpdate();
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

	@Override
	public int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Queries.CHEQUE_BOOK_SERVICE_QUERY.getValue());
			stmt.setInt(1, request.getStatus());
			stmt.setLong(2, request.getAccountNumber());
			stmt.setDate(3, DatabaseUtilities.getSQLDate(request.getRequestDate()));

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0)
				throw new RequestCannotBeProcessedException(
						"Request could not be made for a cheque book.\nPlease try again later");

			ResultSet rs = conn.createStatement().executeQuery(Queries.REQUEST_ID_QUERY.getValue());
			if (!rs.next()) {
				// Revert adding the value in service tracker table
				throw new InternalServerException("Request cannot be processed not.\nPlease try again later.");
			}

			return rs.getInt(1);
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

	@Override
	public List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException {
		Connection conn = null;

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement requestStmt = conn
					.prepareStatement(BankingSystemDao.Queries.GET_REQUESTS_QUERY.getValue());

			requestStmt.setLong(1, accountNumber);

			ResultSet requestResults = requestStmt.executeQuery();

			List<Request> requests = new ArrayList<Request>();
			while (requestResults.next()) {
				Request request = new Request();
				request.setRequestNumber(requestResults.getInt(1));
				request.setAccountNumber(requestResults.getLong(2));
				request.setRequestDate(DatabaseUtilities.getLocalDate(requestResults.getDate(3)));
				request.setStatus(requestResults.getInt(4));

				requests.add(request);
			}

			if (requests.size() == 0)
				throw new NoServicesMadeException("User has no pending service requests.");

			return requests;
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

	
	@Override
	public Account fetchOtherExistingAccount(long accountNumber, AccountType accountType) throws AccountsNotFoundException, InternalServerException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement fetchOthrAcntsStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_OTHER_ACCOUNTS_QUERY.getValue());
			
			fetchOthrAcntsStmt.setLong(1, accountNumber);
			
			String complimentaryAccountType = accountType == AccountType.SAVINGS_ACCOUNT ? AccountType.CURRENT_ACCOUNT.getValue() : AccountType.SAVINGS_ACCOUNT.getValue();
			
			fetchOthrAcntsStmt.setString(2, complimentaryAccountType);
			
			ResultSet results = fetchOthrAcntsStmt.executeQuery();
			
			int matchedEntry = 0;
			if (results.next())
				matchedEntry = results.getInt(1);
			
			if (matchedEntry == 0)
				throw new AccountsNotFoundException("No Alternate account for this user.");
			
			Account fetchedAccount = new Account();
			fetchedAccount.setAccountNumber(accountNumber);
			fetchedAccount.setAccountType(DatabaseUtilities.getAccountType(complimentaryAccountType));
			
			return fetchedAccount;
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

	@Override
	public List<Account> fetchBeneficiaries(long accountNumber) throws InternalServerException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement fetchBnfcryStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_BENEFICIARIES_QUERY.getValue());
			fetchBnfcryStmt.setLong(1, accountNumber);
			
			ResultSet bnfcryDetails = fetchBnfcryStmt.executeQuery();
			
			List<Account> beneficiaries = new ArrayList<Account>();
			while(bnfcryDetails.next()) {
				Account account = new Account();
				account.setAccountNumber(bnfcryDetails.getLong(1));
				account.setNickName(bnfcryDetails.getString(2));
				beneficiaries.add(account);
			}
			
			return beneficiaries;
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

	@Override
	public boolean transferFund(Customer fromAccount, Account otherAccount, Transaction txnDetails) throws InternalServerException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement creditBlncStmt = conn.prepareStatement(BankingSystemDao.Queries.GET_TRANSFER_ACCOUNT_BALANCE_QUERY.getValue());
			PreparedStatement creditStmt = conn.prepareStatement(BankingSystemDao.Queries.CREDIT_ACCOUNT_BALANCE_QUERY.getValue());
			PreparedStatement debitStmt = conn.prepareStatement(BankingSystemDao.Queries.DEBIT_ACCOUNT_BALANCE_QUERY.getValue());
			
			PreparedStatement creditTxnStmt = conn.prepareStatement(BankingSystemDao.Queries.ADD_TRANSACTION_DETAILS.getValue());
			PreparedStatement debitTxnStmt = conn.prepareStatement(BankingSystemDao.Queries.ADD_TRANSACTION_DETAILS.getValue());
			
			creditBlncStmt.setLong(1, otherAccount.getAccountNumber());
			
			ResultSet balanceDetails = creditBlncStmt.executeQuery();
			
			if (!balanceDetails.next())
				throw new InternalServerException("Server Error, please try again later.");
			
			double toBalance = balanceDetails.getDouble(1);
			double fromBalance = fromAccount.getBalance();
			
			debitStmt.setDouble(1, fromBalance - txnDetails.getTransactionAmount());
			debitStmt.setLong(2, fromAccount.getAccountNumber());
			
			creditStmt.setDouble(1, toBalance + txnDetails.getTransactionAmount());
			creditStmt.setLong(2, otherAccount.getAccountNumber());
			
			int debitRowsAffected = debitStmt.executeUpdate();
			
			if (debitRowsAffected == 0)
				return false;
			
			int creditRowsAffected = creditStmt.executeUpdate();
			
			if(creditRowsAffected == 0) {
				// Revert debiting from account
				return false;
			}
			
			creditTxnStmt.setString(1, txnDetails.getTransactionDescription());
			creditTxnStmt.setDate(2, DatabaseUtilities.getSQLDate(LocalDate.now()));
			creditTxnStmt.setString(3, TransactionType.CREDIT.getValue());
			creditTxnStmt.setDouble(4, txnDetails.getTransactionAmount());
			creditTxnStmt.setLong(5, otherAccount.getAccountNumber());
			
			debitTxnStmt.setString(1, txnDetails.getTransactionDescription());
			debitTxnStmt.setDate(2, DatabaseUtilities.getSQLDate(LocalDate.now()));
			debitTxnStmt.setString(3, TransactionType.DEBIT.getValue());
			debitTxnStmt.setDouble(4, txnDetails.getTransactionAmount());
			debitTxnStmt.setLong(5, fromAccount.getAccountNumber());
			
			creditTxnStmt.executeUpdate();
			debitTxnStmt.executeUpdate();
			
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

	@Override
	public boolean addNewBeneficiary(long accountNumber, Account newBeneficiary) throws InternalServerException {
		Connection conn = null;
		
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement addBnfcryStmt = conn.prepareStatement(BankingSystemDao.Queries.ADD_BENEFICIARY_QUERY.getValue());
			
			addBnfcryStmt.setLong(1, accountNumber);
			addBnfcryStmt.setLong(2, newBeneficiary.getAccountNumber());
			addBnfcryStmt.setString(3, newBeneficiary.getNickName());
			
			int rowsAffected = addBnfcryStmt.executeUpdate();
			
			if (rowsAffected == 0)
				return false;
			
			return true;
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		}
	}

}
	
	
	
	
//	@Override
//	public void fundTransfer(long fromAccountNo, Payee payee, double amount, String transactionPassword)
//			throws InsufficientBalanceException, InvalidCredentialsException, InternalServerException {
//		Connection conn = null;
//
//		try {
//			conn = JDBCUtil.getConnection();
//
//			PreparedStatement transactionAuthenticationStmt = conn
//					.prepareStatement(BankingSystemDao.Queries.TRANSACTION_AUTHENTICATION_QUERY.getValue());
//			transactionAuthenticationStmt.setLong(1, fromAccountNo);
//			ResultSet transactionPasswordResultSet = transactionAuthenticationStmt.executeQuery();
//			if (!transactionPasswordResultSet.next())
//				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
//			String transactionPasswordVerifier = transactionPasswordResultSet.getString(1);
//			if (transactionPasswordVerifier != transactionPassword)
//				throw new InvalidCredentialsException(
//						"Invalid transaction password!\nPlease enter valid transaction password.");
//
//			PreparedStatement getDebitAccountBalanceStmt = conn
//					.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_BALANCE_QUERY.getValue());
//			getDebitAccountBalanceStmt.setLong(1, fromAccountNo);
//			ResultSet accountBalanceResultSet = getDebitAccountBalanceStmt.executeQuery();
//			if (!accountBalanceResultSet.next())
//				throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
//			double debitAcountBalance = accountBalanceResultSet.getDouble(1);
//			if (amount < debitAcountBalance) {
//				throw new InsufficientBalanceException("Balance not sufficient");
//			} else {
//				debitAcountBalance -= amount;
//				PreparedStatement debitStmt = conn
//						.prepareStatement(BankingSystemDao.Queries.DEBIT_ACCOUNT_BALANCE_QUERY.getValue());
//				debitStmt.setDouble(1, debitAcountBalance);
//				debitStmt.setLong(2, fromAccountNo);
//				debitStmt.executeUpdate();
//
//				PreparedStatement getCreditAccountBalanceStmt = conn
//						.prepareStatement(BankingSystemDao.Queries.GET_ACCOUNT_BALANCE_QUERY.getValue());
//				getCreditAccountBalanceStmt.setLong(1, payee.getAccountNo());
//				ResultSet payeeAccountBalanceresultSet = getCreditAccountBalanceStmt.executeQuery();
//				if (!payeeAccountBalanceresultSet.next())
//					throw new InvalidCredentialsException("Invalid Credentials!\nPlease enter valid credentials.");
//				double payeeAccountBalance = payeeAccountBalanceresultSet.getDouble(1);
//				payeeAccountBalance += amount;
//				PreparedStatement creditStmt = conn
//						.prepareStatement(BankingSystemDao.Queries.CREDIT_ACCOUNT_BALANCE_QUERY.getValue());
//				creditStmt.setDouble(1, payeeAccountBalance);
//				creditStmt.setLong(2, payee.getAccountNo());
//				creditStmt.executeUpdate();
//			}
//
//		} catch (SQLException e) {
//			throw new InternalServerException(e.getMessage());
//		} finally {
//			try {
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}

}
