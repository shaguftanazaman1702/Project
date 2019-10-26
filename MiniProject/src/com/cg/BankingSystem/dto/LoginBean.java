package com.cg.BankingSystem.dto;

public class LoginBean {
	private String userId; // User ID for admin starts with AD, and for customer starts with CC
	private String password;
	
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
	
}
