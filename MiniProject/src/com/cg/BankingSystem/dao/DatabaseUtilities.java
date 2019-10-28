package com.cg.BankingSystem.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cg.BankingSystem.dto.AccountType;
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
	
	public static AccountType getAccountType(String rawType) {
		if (rawType.equals("SAV"))
			return AccountType.SAVINGS_ACCOUNT;
		return AccountType.CURRENT_ACCOUNT;
	}

}
