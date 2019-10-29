package com.cg.BankingSystem.dto;

//class to define userId and password for admin and generate equivalent getter and setter methods
public class Admin {
	
	private String userId;
	private String userName;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "Admin [userId=" + userId + ", userName=" + userName + "]";
	}

}
