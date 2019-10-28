/**
 * 
 */
package com.cg.BankingSystem.exception;

/**
 * @author lenovo
 *
 */
public class RequestCannotBeProcessedException extends Exception {

	/**
	 * 
	 */
	public RequestCannotBeProcessedException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public RequestCannotBeProcessedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public RequestCannotBeProcessedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RequestCannotBeProcessedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RequestCannotBeProcessedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
