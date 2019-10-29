package com.cg.BankingSystem.dao;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

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
	
	public static long getDuration(LocalDate before, LocalDate after) {
		Duration duration = Duration.between(before.atStartOfDay(), after.atStartOfDay());
		return duration.toDays();
	}

}
