/**
 * 
 */
package com.cg.BankingSystem.dto;

/**
 * @author lenovo
 *
 */
public class Account {

	private long accountNumber;
	private AccountType accountType;
	private String nickName;

	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
