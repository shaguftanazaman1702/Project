package com.cg.BankingSystem.dto;

public enum TransactionType {

	CREDIT ("CR"),
	DEBIT ("DB");
	
	private final String transactionType;
	
	TransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
}
