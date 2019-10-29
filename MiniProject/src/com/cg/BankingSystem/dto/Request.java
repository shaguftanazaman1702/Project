package com.cg.BankingSystem.dto;

import java.time.LocalDate;

//to generate a request for cheque book or to change any personal detail
public class Request {

	private int requestNumber;
	private int status;
	private long accountNumber;
	private LocalDate requestDate;
	
	public int getRequestNumber() {
		return requestNumber;
	}
	public void setRequestNumber(int requestNo) {
		this.requestNumber = requestNo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public LocalDate getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(LocalDate requestDate) {
		this.requestDate = requestDate;
	}
	
}
