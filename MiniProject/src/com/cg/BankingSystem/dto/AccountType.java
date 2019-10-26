package com.cg.BankingSystem.dto;

public enum AccountType {
	SAVINGS_ACCOUNT("SAV"),
	CURRENT_ACCOUNT("CURR");
	
	private final String typeCode;
	
	AccountType(String typeCode) {
		this.typeCode = typeCode;
	}
	
	public String getValue() {
		return typeCode;
	}
}
