package com.cg.BankingSystem.dto;

import java.time.LocalDate;
import java.util.HashMap;

public class Request {

	private int requestNumber;
	private int status;
	private long accountNumber;
	private LocalDate requestDate;
	
	private HashMap<Integer, String> requestStatusMapper = new HashMap<Integer, String>();
	
	{
		requestStatusMapper.put(0, "REQUEST_PLACED");
		requestStatusMapper.put(1, "IN_PROGRESS");
		requestStatusMapper.put(2, "REQUEST_PROCESSED");
	}
	
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
	
	@Override
	public String toString() {
		String printValue = "Service ID: " + requestNumber + "\n" +
							"Service Status: " + requestStatusMapper.get(status) + "\n" +
							"Request Placed On: " + requestDate; 
		return printValue;
	}
	
}
