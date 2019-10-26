package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.Request;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Service;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.dto.TransactionType;

public class CustomerDaoImpl<T> implements CustomerDao<T> {

	@Override
	public T authenticateUser(LoginBean bean) {
		return null;
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber) {
		Connection conn = null;
		List<Transaction> transactions = new ArrayList<Transaction>();

		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Queries.GET_TRANSACTIONS_QUERY.getValue());
			stmt.setLong(1, accountNumber);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Transaction transaction = new Transaction();
				transaction.setTransactionDate(rs.getDate(1));
				transaction.setTransactionAmount(rs.getDouble(2));
				transaction.setTransactionType((TransactionType) rs.getObject(3));
				transaction.setTransactionID(rs.getInt(4));
				transaction.setAccountNo(rs.getLong(5));
				transaction.setTransactionDescription(rs.getString(6));

				transactions.add(transaction);
			}
			return transactions;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public boolean updatePassword(String newPassword, long accountNumber) {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Queries.UPDATE_PASSWORD_QUERY.getValue());
			stmt.setString(1, newPassword);
			stmt.setLong(2, accountNumber);
			stmt.executeUpdate();
			int flag = stmt.executeUpdate();
			if (flag == 0)
				return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return false;
	}

	@Override
	public boolean changeContactNumber(String newNumber, long accountNumber) {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Queries.CHANGE_CONTACT_NUMBER_QUERY.getValue());
			stmt.setString(1, newNumber);
			stmt.setLong(2, accountNumber);
			stmt.executeUpdate();
			int flag = stmt.executeUpdate();
			if (flag == 0)
				return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return false;
	}

	@Override
	public boolean changeAddress(String newAddress, long accountNumber) {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Queries.CHANGE_ADDRESS_QUERY.getValue());
			stmt.setString(1, newAddress);
			stmt.setLong(2, accountNumber);
			stmt.executeUpdate();
			int flag = stmt.executeUpdate();
			if (flag == 0)
				return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return false;
	}

	@Override
	public int requestForCheckBook(Service service) {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Queries.CHEQUE_BOOK_SERVICE_QUERY.getValue());
			stmt.setInt(1, service.getStatus());
			stmt.setLong(2, service.getAccountNumber());
			stmt.setDate(3, service.getserviceDate());

			stmt.executeUpdate();
			ResultSet rs = conn.createStatement().executeQuery(Queries.SERVICE_SEQUENCE_QUERY.getValue());
			if (rs.next())
				return rs.getInt(1);
			else
				return 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}
}
