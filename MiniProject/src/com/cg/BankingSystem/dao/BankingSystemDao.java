package com.cg.BankingSystem.dao;

import java.util.List;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;

public interface BankingSystemDao<T> {

	T authenticateUser(LoginBean bean);

	List<Transaction> listTransactions(long accountNumber) throws Exception;

	boolean updatePassword(String newPassword, long accountNumber);

	public static enum Queries {
		GET_TRANSACTIONS_QUERY("SELECT * FROM TRANSACTION WHERE accountNo = ?"),
		UPDATE_PASSWORD_QUERY("update customer set password = ? where accountNumber = ?"),
		CHANGE_CONTACT_NUMBER_QUERY("update customer set mobileNumber = ? where accountNumber = ?"),
		CHANGE_ADDRESS_QUERY("update customer set address = ? where accountNumber = ?"),
		GET_SERVICE_STATUS_QUERY("SELECT * FROM service WHERE accountNumber = ?"),
		SERVICE_SEQUENCE_QUERY("SELECT service_sequence.currval FROM DUAL"),
		CHEQUE_BOOK_SERVICE_QUERY("INSERT INTO service VALUES(service_sequence.nextval,?,?,?)");

		private String query;

		private Queries(String query) {

			this.query = query;
		}

		public String getValue() {
			return this.query;
		}
	}

}
