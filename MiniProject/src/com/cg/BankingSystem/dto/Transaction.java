package com.cg.BankingSystem.dto;

import java.time.LocalDate;

public class Transaction {

	private LocalDate transactionDate;
	private double transactionAmount;
	private TransactionType transactionType;
	private int transactionID;
	private long accountNo;
	private String transactionDescription;
	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public int getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}
	public long getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}
	public LocalDate getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}
	public double getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getTransactionDescription() {
		return transactionDescription;
	}
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	@Override
	public String toString() {
		String printValue = "Transaction ID: " + transactionID + "\n" +
							"Transaction Amount: " + transactionAmount + "\n" + 
							"Transaction Date: " + transactionDate + "\n" + 
							"Transaction Type: " + transactionType + "\n" +
							"Account Number: " + accountNo + "\n" +
							"Transaction Description: " + transactionDescription;
		return printValue;
	}
	
}
