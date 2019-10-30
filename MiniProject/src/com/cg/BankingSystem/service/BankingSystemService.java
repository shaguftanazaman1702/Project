package com.cg.BankingSystem.service;

import java.util.List;

import com.cg.BankingSystem.dao.AdminDaoImpl;
import com.cg.BankingSystem.dao.CustomerDaoImpl;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;

/**
 * This is the base interface which declares functionalities which are common
 * to both Customer and Admin. Along with that it declares validation methods
 * to be used by the client.
 */
public interface BankingSystemService {

	/**
	 * This is a generic method responsible for authenticating both Admin and the Customer.
	 * @param LoginBean The bean encapsulating the login details - userID, password and accountType (for customer). 
	 * @return Admin/Customer bean.
	 * @throws InvalidCredentialsException If invalid login credentials are passed to the method.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	Object authenticateUser(LoginBean bean) throws InvalidCredentialsException, InternalServerException;
	
	/**
	 * This service is used to fetch all the transaction done in a particular account.
	 * @param accountNumber The account number for which transactions are to be fetched.
	 * @return The list of transaction conducted by the account.
	 * @throws NoTransactionsExistException Thrown when the passed account does not have any transactions.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	List<Transaction> listTransactions(long accountNumber) throws NoTransactionsExistException, InternalServerException;
	
	/**
	 * This service encapsulates the logic of updating password for an user.
	 * @param newPassword The updated password input by the user.
	 * @param userId The unique ID for the user who wishes to update the password.
	 * @return Either true or false depending on whether the operation is successfull or not, respectively
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	boolean updatePassword(String newPassword, String userId) throws InternalServerException;
	
	/**
	 * This method is used to validate inputs for {@link Long} values.
	 * @param min The minimum value.
	 * @param max The maximum value.
	 * @param input The input given by the user.
	 * @return True if input is a valid number conforms to the range 
	 */
	boolean validateLongEntry(long min, long max, long input);
	
	/**
	 * This method is used to validate inputs for {@link Double} values.
	 * @param min The minimum value.
	 * @param max The maximum value.
	 * @param input The input given by the user.
	 * @return True if input is a valid number conforms to the range 
	 */
	boolean validateDouble(double min, double max, double input);
	
	/**
	 * This method validates name inputs given by the user
	 * @param name The name input by the user.
	 * @return True if name is valid, else false.
	 */
	boolean validateName(String name);

	/**
	 * This method validates address inputs given by the user
	 * @param address The address input by the user.
	 * @return True if address is valid, else false.
	 */
	boolean validateAddress(String address);

	/**
	 * This method validates contact inputs given by the user
	 * @param mobileNo The contact input by the user.
	 * @return True if contact is valid, else false.
	 */
	boolean validateContact(String mobileNo);

	/**
	 * This method validates email inputs given by the user
	 * @param email The email input by the user.
	 * @return True if email is valid, else false.
	 */
	boolean validateEmail(String email);

	/**
	 * This method validates pan card details given by the user
	 * @param panCardNumber The pan card number input by the user.
	 * @return True if pan card details is valid, else false.
	 */
	boolean validatePanCard(String panCardNumber);

	/**
	 * This method validates password inputs given by the user.
	 * Password must contain atleast one caps, one number and 
	 * one special character (@, #, $, %).
	 * @param transactionPassword The password input by the user.
	 * @return True if password is valid, else false.
	 */
	boolean validateTxnPwd(String transactionPassword);

	/**
	 * This method validates Nick Name inputs given by the user
	 * @param nickName The Nick Name input by the user.
	 * @return True if Nick Name is valid, else false.
	 */
	boolean validateNickName(String nickName);
	
	/**
	 * This is the main dependency injector, which injects the
	 * appropriate service implementation in the UI layer. It
	 * also injects the appropriate DAO dependency in the
	 * service layer
	 * @param bean The Login details.
	 * @return The appropriate service layer dependency.
	 */
	static BankingSystemService getInstance(LoginBean bean) {
		if (bean.getUserId().contains("AD"))
			return new AdminServiceImpl(new AdminDaoImpl());
		else if (bean.getUserId().contains("CC"))
			return new CustomerServiceImpl(new CustomerDaoImpl());
		return null;
	}

	/**
	 * This method validates User ID (for Admin) inputs given by the user
	 * @param userId The User ID input by the user.
	 * @return True if User ID is valid, else false.
	 */
	static boolean validateAdminUserID(String userId) {
		return userId.matches(ADMIN_USER_ID_VALIDATOR);
	}

	/**
	 * This method validates User ID (for Customer) inputs given by the user
	 * @param userId The User ID input by the user.
	 * @return True if User ID is valid, else false.
	 */
	static boolean validateCustomerUserID(String userId) {
		return userId.matches(CUSTOMER_USER_ID_VALIDATOR);
	}
	
	/**
	 * This method validates password inputs given by the user
	 * Password must contain atleast one caps, one number and 
	 * one special character (@, #, $, %).
	 * @param password The password input by the user.
	 * @return True if password is valid, else false.
	 */
	static boolean validatePassword(String password) {
		return password.matches(PASSWORD_VALIDATOR);
	}
	
	String NUMBER_VALIDATOR = "[0-9]+"; // Regex to validate integral number
	String DOUBLE_VALIDATOR = "[0-9.]+"; // Regex to validate decimal number
	String ADMIN_USER_ID_VALIDATOR = "AD[A-Za-z0-9]+"; // Regex to validate admin user ID.
	String CUSTOMER_USER_ID_VALIDATOR = "CC[A-Za-z0-9]+"; // Regex to validate customer user ID.
	String NAME_VALIDATOR = "[A-Z][A-Za-z ]+"; // Regex to validate name.
	String PASSWORD_VALIDATOR = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})"; // Regex to validate password - Must contain atleast one caps, one number and one special character (@, #, $, %).
	String ADDRESS_VALIDATOR = "[A-Z a-z0-9,-.]+"; // Regex to validate address.
	String MOBILE_NUMBER_VALIDATOR = "\\+91[6-9][0-9]{9}"; // Regex to validate contact. 
	String EMAIL_VALIDATOR = "[\\w_]+@[a-z]{3,20}.[a-z]{2,4}"; // Regex to validate email.
	String PAN_CARD_VALIDATOR = "[A-Z]{5}[0-9]{4}[A-Z]"; // Regex to validate pan card details.

}