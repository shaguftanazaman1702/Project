package com.cg.BankingSystem.dto;

import java.util.List;

//variables defined for a customer
public class Customer {

	private String userId;
	private String password;
	private long accountNumber;
	private String name;
	private String address;
	private String mobileNumber;
	private String emailId;
	private String panCardNumber;
	private AccountType accountType;
	private String transactionPassword;
	private double balance;
	private List<Request> requests;
	private List<Transaction> transactions;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(long accountNo) {
		this.accountNumber = accountNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public AccountType getAccountType() {
		return accountType;
	}
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public List<Request> getRequests() {
		return requests;
	}
	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}
	public List<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	public String getPanCardNumber() {
		return panCardNumber;
	}
	public void setPanCardNumber(String panCardNumber) {
		this.panCardNumber = panCardNumber;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getTransactionPassword() {
		return transactionPassword;
	}
	public void setTransactionPassword(String transactionPassword) {
		this.transactionPassword = transactionPassword;
	}
	@Override
	public String toString() {
		return "Customer [userId=" + userId + ", accountNumber=" + accountNumber + ", name=" + name + ", address="
				+ address + ", mobileNumber=" + mobileNumber + ", emailId=" + emailId + ", panCardNumber="
				+ panCardNumber + ", accountType=" + accountType + ", balance=" + balance + "]";
	}
}
