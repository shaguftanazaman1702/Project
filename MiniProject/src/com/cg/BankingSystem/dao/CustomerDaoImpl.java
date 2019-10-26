package com.cg.BankingSystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;

public class CustomerDaoImpl<T> implements CustomerDao<T> {


	@Override
	public T authenticateUser(LoginBean bean) {
		return null;
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber) {
		return null;
	}

	@Override
	public boolean updatePassword(String newPassword,long accountNumber) {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Queries.UPDATE_PASSWORD_QUERY.getValue());
			stmt.setString(1, newPassword);
			stmt.setLong(2, accountNumber);
			stmt.executeUpdate();
			int flag = stmt.executeUpdate();
			if(flag == 0)
				return true;
		
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		finally {
			if(conn != null)
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
			if(flag == 0)
				return true;
		
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		finally {
			if(conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return false;
	}

	@Override
	public boolean changeAddress(String newAddress,long accountNumber) {
		Connection conn = null;
		try {
			conn = JDBCUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Queries.CHANGE_ADDRESS_QUERY.getValue());
			stmt.setString(1, newAddress);
			stmt.setLong(2, accountNumber);
			stmt.executeUpdate();
			int flag = stmt.executeUpdate();
			if(flag == 0)
				return true;
		
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		finally {
			if(conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return false;
	}

	@Override
	public int requestForCheckBook() {
		return 0;
	}


}
