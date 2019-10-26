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
		GET_TRANSACTIONS_QUERY ("SELECT * FROM transactions WHERE acoount_id = ?"),
		LOGIN_AUTHENTICATION_BA_QUERY ("SELECT user_id FROM user_table WHERE user_id = ? AND password = ?"),
		LOGIN_AUTHENTICATION_CC_QUERY ("SELECT account_id FROM user_table WHERE user_id = ? AND password = ?"),
		GET_ADMIN_DETAILS_QUERY ("SELECT * FROM admin_master WHERE user_id = ?"),
		UPDATE_PASSWORD_QUERY ("UPDATE user_table SET password = ? WHERE user_id = ?"),
		INSERT_CUSTOMER_QUERY ("INSERT INTO customer VALUES (?, ?, ?, ?, ?, ?)"),
		INSERT_ACCOUNT_QUERY ("INSERT INTO account_master VALUES (acc_id_sequence.nextval, ?, ?, ?)"),
		INSERT_USER_QUERY ("INSERT INTO user_table VALUES (?, ?, ?, ?, ?)"),
		GET_ACCOUNT_NUMBER_QUERY ("SELECT acc_id_sequence FROM DUAL");
		
		private String query;
		
		private Queries(String query) {

			this.query = query;
		}
		
		public String getValue() {
			return this.query;
		}
	}
	
}
