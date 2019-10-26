package com.cg.BankingSystem.dao;

import java.sql.Date;
import java.time.LocalDate;

import com.cg.BankingSystem.dto.TransactionType;

public class DatabaseUtilities {

	public static LocalDate getLocalDate(Date date) {
		return date.toLocalDate();
	}
	
	public static TransactionType getTransactionType(String rawType) {
		if (rawType.equals("CR"))
			return TransactionType.CREDIT;
		return TransactionType.DEBIT;
	}

	public static Date getSQLDate(LocalDate date) {
		return Date.valueOf(date);
	}
}
