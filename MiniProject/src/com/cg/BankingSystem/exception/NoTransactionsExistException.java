package com.cg.BankingSystem.exception;

public class NoTransactionsExistException extends Exception {

	public NoTransactionsExistException() {
		super();
	}

	public NoTransactionsExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoTransactionsExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoTransactionsExistException(String message) {
		super(message);
	}

	public NoTransactionsExistException(Throwable cause) {
		super(cause);
	}

}
