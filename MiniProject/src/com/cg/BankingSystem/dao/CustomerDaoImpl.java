package com.cg.BankingSystem.dao;

import java.util.List;

import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;

public class CustomerDaoImpl<T> implements CustomerDao<T> {

	@Override
	public T authenticateUser(LoginBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> listTransactions(long accountNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updatePassword(String newPassword) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changeContactNumber(String newNumber) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changeAddress(String newAddress) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int requestForCheckBook() {
		// TODO Auto-generated method stub
		return 0;
	}

}
