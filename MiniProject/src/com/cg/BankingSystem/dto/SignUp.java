package com.cg.BankingSystem.dto;

public class SignUp {

	private long accNo;
	private String name;
	private String address;
	private long mobileNo;
	private String email;
	private AccountType accType;
	private int openingBal;

	public long getAccNo() {
		return accNo;
	}
	public void setAccNo(long accNo) {
		this.accNo = accNo;
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
	public long getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public AccountType getAccType() {
		return accType;
	}
	public void setAccType(AccountType accType) {
		this.accType = accType;
	}
	public int getOpeningBal() {
		return openingBal;
	}
	public void setOpeningBal(int openingBal) {
		this.openingBal = openingBal;
	}
	
	
	
	
}
