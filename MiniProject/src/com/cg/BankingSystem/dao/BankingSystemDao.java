package com.cg.BankingSystem.dao;

import java.util.List;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;

public interface BankingSystemDao<T> {

	T authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException;
	
	List<Transaction> listTransactions(long accountNumber) throws NoTransactionsExistException, InternalServerException;
	
	boolean updatePassword(String newPassword, String userId) throws InternalServerException;
	
	public static enum Queries {
		GET_TRANSACTIONS_QUERY ("SELECT * FROM transactions WHERE account_id = ?"),
		LOGIN_AUTHENTICATION_BA_QUERY ("SELECT user_id FROM user_table WHERE user_id = ? AND password = ?"),
		LOGIN_AUTHENTICATION_CC_QUERY ("SELECT account_id FROM user_table WHERE user_id = ? AND password = ?"),
		GET_ADMIN_DETAILS_QUERY ("SELECT * FROM admin_master WHERE user_id = ?"),
		GET_USER_DETAILS_QUERY ("SELECT * FROM user_table WHERE user_id = ?"),
		GET_CUSTOMER_DETAILS_QUERY ("SELECT * FROM customer WHERE account_id = ?"),
		GET_ACCOUNT_DETAILS_CC_QUERY ("SELECT * FROM account_master WHERE account_id = ? and account_type = ?"),
		GET_ACCOUNT_DETAILS_AD_QUERY ("SELECT * FROM account_master WHERE account_id = ?"),
		GET_TXN_PWD_QUERY ("SELECT transaction_password FROM user_table WHERE account_id = ?"),
		UPDATE_PASSWORD_QUERY ("UPDATE user_table SET password = ? WHERE user_id = ?"),
		INSERT_CUSTOMER_QUERY ("INSERT INTO customer VALUES (?, ?, ?, ?, ?, ?)"),
		INSERT_ACCOUNT_QUERY ("INSERT INTO account_master VALUES (acc_id_sequence.nextval, ?, ?, ?)"),
		INSERT_EXISTING_ACCOUNT_QUERY ("INSERT INTO account_master VALUES (?, ?, ?, ?)"),
		INSERT_USER_QUERY ("INSERT INTO user_table VALUES (?, ?, ?, ?, ?)"),
		GET_ACCOUNT_NUMBER_QUERY ("SELECT acc_id_sequence.currval FROM DUAL"),
		CHANGE_ADDRESS_QUERY("UPDATE customer SET address = ? WHERE account_id = ?"),
		CHANGE_CONTACT_NUMBER_QUERY("UPDATE customer SET mobile_number = ? WHERE account_id = ?"),
		REQUEST_ID_QUERY("SELECT service_sequence.currval FROM DUAL"),
		CHEQUE_BOOK_SERVICE_QUERY("INSERT INTO service_tracker VALUES(service_sequence.nextval,?,?,?)"),
		GET_REQUESTS_QUERY ("SELECT * FROM service_tracker WHERE account_id = ?"),
		TXN_ACCOUNT_BALANCE_QUERY("UPDATE account_master SET account_balance = ? WHERE account_id = ? AND account_type = ?"),
		GET_TRANSFER_ACCOUNT_BALANCE_QUERY ("SELECT account_balance from account_master where account_id = ?"),
		GET_OTHER_ACCOUNTS_QUERY ("SELECT COUNT(*) FROM account_master WHERE account_id = ? and account_type = ?"),
		GET_BENEFICIARIES_QUERY ("SELECT beneficiary_id, nick_name FROM beneficiary_details WHERE account_id = ?"),
		ADD_BENEFICIARY_QUERY ("INSERT INTO beneficiary_details VALUES (?, ?, ?)"),
		ADD_TRANSACTION_DETAILS ("INSERT INTO transactions values (txn_sequence.nextval, ?, ?, ?, ?, ?)");
		
		private String query;
		
		private Queries(String query) {

			this.query = query;
		}
		
		public String getValue() {
			return this.query;
		}
	}
	
}
