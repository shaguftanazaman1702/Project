package com.cg.BankingSystem.dto;

import java.util.Date;

public class Transaction {

	private Date transactionDate;
	private double transactionAmount;
	private String bankName;
	private int numOfRequest;
	
	
	public int getNumOfRequest() {
		return numOfRequest;
	}
	public void setNumOfRequest(int numOfRequest) {
		this.numOfRequest = numOfRequest;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public double getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	
}
