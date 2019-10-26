package com.cg.BankingSystem.dto;

import java.time.LocalDate;

public class Service {

	private int serviceNumber;
	private int status;
	private long accountNumber;
	private LocalDate serviceDate;
	
	public int getserviceNumber() {
		return serviceNumber;
	}
	public void setserviceNumber(int serviceNo) {
		this.serviceNumber = serviceNo;
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
	public LocalDate getserviceDate() {
		return serviceDate;
	}
	public void setserviceDate(LocalDate serviceDate) {
		this.serviceDate = serviceDate;
	}
	
}
