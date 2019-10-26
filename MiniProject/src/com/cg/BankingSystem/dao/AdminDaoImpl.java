package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;

public class AdminDaoImpl<T> implements AdminDao<T> {

	@Override
	public T authenticateUser(LoginBean bean) {
		return null;
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber) throws Exception {
		Connection conn = null;
		List<Transaction> transactions = new ArrayList<Transaction>(); 

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(BankingSystemDao.Queries.GET_TRANSACTIONS_QUERY);
			stmt.setLong(1, accountNumber);
			//date from to
			

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				do {
					Transaction trans = new Transaction();
					trans.setTransactionDate(rs.getDate(1));
					trans.setTransactionAmount(rs.getDouble(2));
					trans.setTransactionType(rs.getString(3));
					trans.setTransactionID(rs.getInt(4));
					
					transactions.add(trans);
				} while(rs.next());
			} else
				throw new Exception("Product doesnt exist");

			return transactions;
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
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
	public boolean updatePassword(String newPassword) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long createNewAccount(Customer newCustomer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
