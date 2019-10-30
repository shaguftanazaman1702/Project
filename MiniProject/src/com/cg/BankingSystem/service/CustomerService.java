package com.cg.BankingSystem.service;

import java.util.List;

import com.cg.BankingSystem.dto.Account;
import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountsNotFoundException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;

/**
 * This interface defines the functionalities specific to a
 * Customer. It alson extends the functionalities declared by
 * {@link BankingSystemService}
 */
public interface CustomerService extends BankingSystemService {

	/**
	 * This service enables a user to change their contact number in their bank account.
	 * @param newNumber The new contact number of the customer.
	 * @param accountNumber The account number for which the contact details are to updated.
	 * @return A boolean value indicating whether updation has been successful or not.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	boolean changeContactNumber(String newNumber, long accountNumber) throws InternalServerException;
	
	/**
	 * This service enables a user to change their address details in their bank account.
	 * @param newNumber The new address details of the customer.
	 * @param accountNumber The account number for which the address details are to updated.
	 * @return A boolean value indicating whether updation has been successful or not.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	boolean changeAddress(String newAddress, long accountNumber) throws InternalServerException;
	
	/**
	 * This service lets user request for a new Cheque Book whenever they need one.
	 * @param request The details about the service required by the customer.
	 * @return The reference number to the raised request.
	 * @throws RequestCannotBeProcessedException Thrown if the request could not raised successfully.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	int requestForCheckBook(Request request) throws RequestCannotBeProcessedException, InternalServerException;

	/**
	 * This method lets user fetch the requests made by them in the past 6 months (180 days). A maximum of 20 requests can be fetched.
	 * @param accountNumber The account number for which raised requests are to be fetched.
	 * @return The list of request raised by the customer in the past 180 days.
	 * @throws NoServicesMadeException Thrown if the customer has not raised any requests in the past 180 days.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	List<Request> getRequests(long accountNumber) throws NoServicesMadeException, InternalServerException;

	/**
	 * This method returns the maximum limit for a transaction. Max - 10 Lakh
	 * @return The limit of the transaction.
	 */
	double getTransactionLimit();

	/**
	 * This method validates the transaction amount that the customer
	 * wants to transfer. It checks if the transaction amount conforms
	 * to the account balance of the Customer.
	 * @param customer The customer who wants to transfer the funds.
	 * @param transferAmount
	 * @return
	 */
	boolean validateTransactionAmount(Customer customer, double transferAmount);

	/**
	 * This method validates the transaction password that the customer
	 * has entered for transferring funds. It checks if the transaction password entered conforms
	 * to the transaction password of the Customer.
	 * @param customer The customer who wants to transfer the funds.
	 * @param transferAmount
	 * @return
	 */
	boolean checkTransactionPassword(Customer customer, String txnPwd);

	/**
	 * This method enables a customer to fetch details about their other account in the bank, if any
	 * @param accountNumber The account number of the customer.
	 * @param accountType The account type of the customer.
	 * @return The account details of the customer.
	 * @throws AccountsNotFoundException Thrown if the user doesn't have any multiple account in the bank.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	Account fetchOtherExistingAccount(long accountNumber, AccountType accountType) throws AccountsNotFoundException, InternalServerException;

	/**
	 * This method is used to fetch beneficiaries that have been added by the user in their bank account.
	 * @param accountNumber The account number for which beneficiaries are to be fetched
	 * @return The list of beneficiaries fetched in the operation, an empty list is returned if not beneficiaries have been added.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	List<Account> fetchBeneficiaries(long accountNumber) throws InternalServerException;

	/**
	 * This method encapsulates the prime functionality of a bank account -transferring funds to other accounts. It can
	 * be used to transfer funds to alternate accounts of a customer, or to different users too.
	 * @param fromAccount The details of the account from which amount has to be debited.
	 * @param otherAccount The details of the account from which amount has to be credited.
	 * @param txnDetails The details of the transaction - like amount and description of the transaction.
	 * @return A boolean value indicating whether the transaction is successful or not.
	 * @throws InternalServerException In case any backend problem arises. Common ones include connectivity problem with database, connection timeout.
	 */
	boolean transferFund(Customer fromAccount, Account otherAccount, Transaction txnDetails) throws InternalServerException;

	/**
	 * This method is used to add a new beneficiary to a customer's account.
	 * @param accountNumber The account number to which beneficiary has to be added.
	 * @param newBeneficiary The details of the beneficiary - account number, nick-name.
	 * @return A boolean value indicating whether beneficiary has been added successfully.
	 * @throws InternalServerException
	 */
	boolean addNewBeneficiary(long accountNumber, Account newBeneficiary) throws InternalServerException;

}
