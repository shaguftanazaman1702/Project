package com.cg.BankingSystem.dao;

import java.util.List;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;

/**
 * This is the base interface for conducting Banking operations.
 * @param <T> - Possible Values: Admin and Customer.
 */
public interface BankingSystemDao<T> {

	/**
	 * This is a generic method responsible for authenticating both Admin and the Customer.
	 * @param LoginBean The bean encapsulating the login details - userID, password and accountType (for customer). 
	 * @return Admin/Customer bean.
	 * @throws InvalidCredentialsException If invalid login credentials are passed to the method.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	T authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException;
	
	/**
	 * This service is used to fetch all the transaction done in a particular account.
	 * @param accountNumber The account number for which transactions are to be fetched.
	 * @return The list of transaction conducted by the account.
	 * @throws NoTransactionsExistException Thrown when the passed account does not have any transactions.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	List<Transaction> listTransactions(long accountNumber) throws NoTransactionsExistException, InternalServerException;
	
	/**
	 * This service encapsulates the logic of updating password for an user.
	 * @param newPassword The updated password input by the user.
	 * @param userId The unique ID for the user who wishes to update the password.
	 * @return Either true or false depending on whether the operation is successfull or not, respectively
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	boolean updatePassword(String newPassword, String userId) throws InternalServerException;
	
	/**
	 * An enum class which enumerates various SQL Queries which are used during DML, DQL operations in the DAO layer.
	 * @author Sayantan Sarkar
	 */
	public static enum Queries {
		GET_TRANSACTIONS_QUERY ("SELECT * FROM transactions WHERE account_id = ?"), // Query to fetch transactions for a given accountNumber.
		
		LOGIN_AUTHENTICATION_BA_QUERY ("SELECT user_id FROM user_table WHERE user_id = ? AND password = ?"), // Query used during authentication of admin, fetches userId for admin.
		
		LOGIN_AUTHENTICATION_CC_QUERY ("SELECT account_id FROM user_table WHERE user_id = ? AND password = ?"), // Query used during authentication of customer, fetches accountNumber for customer.
		
		GET_ADMIN_DETAILS_QUERY ("SELECT * FROM admin_master WHERE user_id = ?"), // Query to fetch admin details from admin_master table.
		
		GET_USER_DETAILS_QUERY ("SELECT * FROM user_table WHERE user_id = ?"), // Query to fetch customer details from user_table.
		
		GET_CUSTOMER_DETAILS_QUERY ("SELECT * FROM customer WHERE account_id = ?"), // Query to fetch customer details from customer table.
		
		GET_ACCOUNT_DETAILS_CC_QUERY ("SELECT * FROM account_master WHERE account_id = ? and account_type = ?"), // Query to fetch account details of an user, to be used in CustomerDao.
		
		GET_ACCOUNT_DETAILS_AD_QUERY ("SELECT * FROM account_master WHERE account_id = ?"), // Query to fetch account details of an user, to be used in AdminDao.
		
		GET_TXN_PWD_QUERY ("SELECT transaction_password FROM user_table WHERE account_id = ?"), // Query to fetch the transaction password for a given accountNumber.
		
		UPDATE_PASSWORD_QUERY ("UPDATE user_table SET password = ? WHERE user_id = ?"), // Query to update password.
		
		INSERT_CUSTOMER_QUERY ("INSERT INTO customer VALUES (?, ?, ?, ?, ?, ?)"), // Query to insert customer details in customer table
		
		INSERT_ACCOUNT_QUERY ("INSERT INTO account_master VALUES (acc_id_sequence.nextval, ?, ?, ?)"), // Query to insert customer's account details into account_master table.
		
		INSERT_EXISTING_ACCOUNT_QUERY ("INSERT INTO account_master VALUES (?, ?, ?, ?)"), // Query to create new accout for existing user
		
		INSERT_USER_QUERY ("INSERT INTO user_table VALUES (?, ?, ?, ?, ?)"), // Query to insert customer details into user_table.
		
		GET_ACCOUNT_NUMBER_QUERY ("SELECT acc_id_sequence.currval FROM DUAL"), // Query to get account number of the newly created account.
		
		CHANGE_ADDRESS_QUERY("UPDATE customer SET address = ? WHERE account_id = ?"), // Query to change address details of a customer.
		
		CHANGE_CONTACT_NUMBER_QUERY("UPDATE customer SET mobile_number = ? WHERE account_id = ?"), // Query to change mobile number of the customer.
		
		REQUEST_ID_QUERY("SELECT service_sequence.currval FROM DUAL"), // Query to fetch the service id for the raised request.
		
		CHEQUE_BOOK_SERVICE_QUERY("INSERT INTO service_tracker VALUES(service_sequence.nextval,?,?,?)"), // Query to raise a new request.
		
		GET_REQUESTS_QUERY ("SELECT * FROM service_tracker WHERE account_id = ?"), // Query to fetch raised services by a customer.
		
		TXN_ACCOUNT_BALANCE_QUERY("UPDATE account_master SET account_balance = ? WHERE account_id = ? AND account_type = ?"), // Query to update balance after transaction.
		
		GET_TRANSFER_ACCOUNT_BALANCE_QUERY ("SELECT account_balance from account_master where account_id = ?"), // Query to get account balance while fund transfer.
		
		GET_OTHER_ACCOUNTS_QUERY ("SELECT COUNT(*) FROM account_master WHERE account_id = ? and account_type = ?"), // Query to get the number of accounts of a user.
		
		GET_BENEFICIARIES_QUERY ("SELECT beneficiary_id, nick_name FROM beneficiary_details WHERE account_id = ?"), // Query to get beneficiary details for a user.
		
		ADD_BENEFICIARY_QUERY ("INSERT INTO beneficiary_details VALUES (?, ?, ?)"), // Query insert new beneficiaries.
		
		ADD_TRANSACTION_DETAILS ("INSERT INTO transactions values (txn_sequence.nextval, ?, ?, ?, ?, ?)"); // Query to insert transaction details into transactions table;
		
		private String query;
		
		private Queries(String query) {
			this.query = query;
		}
		
		// Returns the SQL Query held by the enumeration
		public String getValue() {
			return this.query;
		}
	}
	
}
