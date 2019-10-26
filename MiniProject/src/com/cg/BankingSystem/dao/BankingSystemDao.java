package com.cg.BankingSystem.dao;

import java.util.List;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;

public interface BankingSystemDao<T> {

	T authenticateUser(LoginBean bean);
	
	List<Transaction> listTransactions(long accountNumber) throws Exception;
	
	boolean updatePassword(String newPassword);
	
	public static enum Queries {
		GET_TRANSACTIONS_QUERY ("SELECT * FROM TRANSACTIONS WHERE acoount_id = ?");
		
		private String query;
		
		private Queries(String query) {

			this.query = query;
		}
		
		public String getValue() {
			return this.query;
		}
	}
	
}
