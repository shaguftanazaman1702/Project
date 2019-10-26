package com.cg.BankingSystem.exception;

public class AccountNotCreatedException extends Exception {

	public AccountNotCreatedException() {
		super();
	}

	public AccountNotCreatedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AccountNotCreatedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccountNotCreatedException(String message) {
		super(message);
	}

	public AccountNotCreatedException(Throwable cause) {
		super(cause);
	}
}
