/**
 * 
 */
package com.cg.BankingSystem.cli;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.log4j.PropertyConfigurator;

import com.cg.BankingSystem.dto.Account;
import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
import com.cg.BankingSystem.exception.AccountsNotFoundException;
import com.cg.BankingSystem.exception.InternalServerException;
import com.cg.BankingSystem.exception.InvalidCredentialsException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;
import com.cg.BankingSystem.exception.UserNotFoundException;
import com.cg.BankingSystem.service.AdminService;
import com.cg.BankingSystem.service.BankingSystemService;
import com.cg.BankingSystem.service.CustomerService;

/**
 * @author admin
 *
 */
public class BankingSystemCli {

	/**
	 * Static objects defined
	 */
	private static Scanner scanner;

	/**
	 * Static block, implements even before the main method
	 */
	static {
		scanner = new Scanner(System.in);
	}
	static Logger myLogger = Logger.getLogger(BankingSystemCli.class.getName());

	/**
	 * 
	 * @param args
	 * @throws AccountNotCreatedException
	 * @throws InternalServerException
	 * @throws InvalidCredentialsException
	 * @throws NoTransactionsExistException
	 * @throws RequestCannotBeProcessedException
	 * @throws NoServicesMadeException
	 * @throws UserNotFoundException
	 * @throws AccountsNotFoundException
	 */
	public static void main(String[] args) throws InvalidCredentialsException, InternalServerException,
			AccountNotCreatedException, NoTransactionsExistException, RequestCannotBeProcessedException,
			NoServicesMadeException, UserNotFoundException, AccountsNotFoundException {
		System.out.println("WELCOME TO BANKING SYSTEM!");
		PropertyConfigurator.configure("src/log4j.properties");
		myLogger.info("Application Starts.");

		while (true) {
			System.out.println("***********************************************");
			System.out.println("Enter 1 to Sign In.");
			System.out.println("Enter 0 to Exit.");
			System.out.println("***********************************************");

			int userTypeChoice = scanner.nextInt();
			switch (userTypeChoice) {
			case 1:
				signIn();
				break;
			case 0:
				System.out.println("SERVICE TERMINATED");
				System.exit(0);
			default:
				System.out.println("Invalid choice entered.\nProvide a valid input.");
			}
		}
	}

	private static void signIn() throws InvalidCredentialsException, InternalServerException,
			AccountNotCreatedException, NoTransactionsExistException, RequestCannotBeProcessedException,
			NoServicesMadeException, UserNotFoundException, AccountsNotFoundException {
		System.out.println("***********************************************");
		System.out.println("***** Enter Your Credentials *****");

		LoginBean login = new LoginBean();

		System.out.print("User ID : ");
		login.setUserId(scanner.next());

		System.out.print("Password : ");
		login.setPassword(scanner.next());

		myLogger.info("Login attempted.");

		// Validate values before logging in

		if (login.getUserId().contains("AD")) {
			AdminService adminService = (AdminService) BankingSystemService.getInstance(login);

			Admin adminLogin = (Admin) adminService.authenticateUser(login);
			System.out.println("Logged in successfully as Admin. Welcome " + adminLogin.getUserName());

			onAdminLogin(adminService);
		} else {
			CustomerService customerService = (CustomerService) BankingSystemService.getInstance(login);
			System.out.println(customerService == null);
			Customer customerLogin = (Customer) customerService.authenticateUser(login);
			System.out.println("Logged in successfully as User. Welcome " + customerLogin.getName());

			onCustomerLogin(customerLogin, customerService);
		}
	}

	/**
	 * Admin methods
	 * 
	 * @throws InternalServerException
	 * @throws AccountNotCreatedException
	 * @throws NoTransactionsExistException
	 * @throws UserNotFoundException
	 */

	public static void onAdminLogin(AdminService service) throws AccountNotCreatedException, InternalServerException,
			NoTransactionsExistException, UserNotFoundException {
		myLogger.info("Logged in as Admin.");
		while (true) {
			int choice;
			System.out.println("***********************************************");
			System.out.println("Enter 1 to Create New Account");
			System.out.println("Enter 2 to View Transactions of Customers");
			System.out.println("Enter 0 to Logout.");
			System.out.println("***********************************************");

			choice = scanner.nextInt();
			switch (choice) {
			case 1:
				createNewAccount(service);
				break;
			case 2:
				viewTransactions(service);
				break;
			case 0:
				System.exit(0); // Exiting the control when 0 is entered.
			default:
				System.out.println("Invalid choice entered. Please enter a valid choice"); // Default message when none
																							// of 0 - 3 is entered
			}
		}

	}

	private static void createNewAccount(AdminService service)
			throws AccountNotCreatedException, InternalServerException, UserNotFoundException {
		boolean backFlag = false;
		myLogger.info("Create new account method initiated.");

		while (true) {
			System.out.println(
					"Select option : \n1: Create new account for existing user. \n2: Create new account for new user. \n3: Back.");
			int existingUserChoice = scanner.nextInt();
			switch (existingUserChoice) {
			case 1:
				createNewAccountForExistingUser(service);
				break;
			case 2:
				createNewUserAccount(service);
				System.out.println("HELLO");
				break;
			case 3:
				backFlag = true;
				break;
			default:
				System.out.println("Invalid choice entered. Please enter a valid choice");
			}
			if (backFlag)
				break;
		}
	}

	private static void createNewAccountForExistingUser(AdminService service)
			throws InternalServerException, UserNotFoundException {

		myLogger.info("Create new account for existing user method initiated.");
		System.out.print("Enter customer userId : ");
		String userId = scanner.next();

		Customer existingCustomer = service.findCustomer(userId); // throws if no user

		System.out.println(" ***** Enter details for the new account ***** ");
		System.out.println("Opening Balance: ");

		double openingBal = scanner.nextDouble();

		SignUp newCustomer = new SignUp();

		newCustomer.setUserId(existingCustomer.getUserId());
		newCustomer.setPassword(existingCustomer.getPassword());
		newCustomer.setName(existingCustomer.getName());
		newCustomer.setEmail(existingCustomer.getEmailId());
		newCustomer.setAddress(existingCustomer.getAddress());
		newCustomer.setMobileNo(existingCustomer.getMobileNumber());
		newCustomer.setAccountNumber(existingCustomer.getAccountNumber());
		newCustomer.setTransactionPassword(existingCustomer.getTransactionPassword());
		newCustomer.setOpeningBal(openingBal);
		newCustomer.setPanCardNumber(existingCustomer.getPanCardNumber());

		if (existingCustomer.getAccountType() == AccountType.SAVINGS_ACCOUNT)
			newCustomer.setAccountType(AccountType.CURRENT_ACCOUNT);
		else
			newCustomer.setAccountType(AccountType.SAVINGS_ACCOUNT);

		boolean isCreated = service.saveExistingUser(newCustomer);

		if (isCreated) {
			System.out.println("Account created successfully");
			myLogger.info("New account created for existing user.");
		} else {
			System.out.println("Account could not be created now.\\nPlease try again later.");
			myLogger.info("New account creation failed.");
		}
	}

	private static void createNewUserAccount(AdminService service)
			throws AccountNotCreatedException, InternalServerException {

		myLogger.info("Create new account method initiated for new user.");
		SignUp newSignUp = new SignUp();
		AccountType accType = null;

		String name;
		do {
			System.out.print("Enter customer name : ");
			name = scanner.next();
		} while (!service.validateName(name));

		System.out.print("Enter customer address : ");
		String address = scanner.next();

		String mobileNo;
		do {
			System.out.print("Enter customer mobile number : ");
			mobileNo = scanner.next();
		} while (!service.validateMobile(mobileNo));

		String email;
		do {
			System.out.print("Enter customer email : ");
			email = scanner.next();
		} while (!service.validateEMail(email));

		System.out.print("Select customer account type : \n 1: for Savings account \n 2: For current account");
		int accountTypeChoice = scanner.nextInt();
		while (true) {
			if (accountTypeChoice == 1) {
				accType = AccountType.SAVINGS_ACCOUNT;
				break;
			} else if (accountTypeChoice == 2) {
				accType = AccountType.CURRENT_ACCOUNT;
				break;
			} else
				System.out.println("Please Enter a valid choice.");
		}

		System.out.print("Enter customer opening balance : ");
		double openingBal = scanner.nextDouble();

		String panCardNumber;
		do {
			System.out.print("Enter customer PAN no. : ");
			panCardNumber = scanner.next();
		} while (!service.validatePanCard(panCardNumber));

		String userId;
		do {
			System.out.print("Enter customer user ID : ");
			userId = scanner.next();
		} while (!service.validateUserId(userId));

		String password;
		do {
			System.out.print("Enter customer password : ");
			password = scanner.next();
		} while (!service.validatePassword(password));

		String transactionPassword;
		do {
			System.out.print("Enter customer transaction password : ");
			transactionPassword = scanner.next();
		} while (!service.validatePassword(transactionPassword));

		newSignUp.setAccountType(accType);
		newSignUp.setAddress(address);
		newSignUp.setEmail(email);
		newSignUp.setMobileNo(mobileNo);
		newSignUp.setName(name);
		newSignUp.setOpeningBal(openingBal);
		newSignUp.setPanCardNumber(panCardNumber);
		newSignUp.setUserId(userId);
		newSignUp.setPassword(password);
		newSignUp.setTransactionPassword(transactionPassword);

		long newCustomerAccountNo = service.createNewAccount(newSignUp);

		System.out.println("New Account opened successfully with account number: " + newCustomerAccountNo);
		myLogger.info("New user registered and new account created.");
	}

	private static void viewTransactions(AdminService service)
			throws NoTransactionsExistException, InternalServerException {
		myLogger.info("View transactions method initiated by admin.");
		System.out.print("Enter Customer's Account Number : ");
		long accountNumber = scanner.nextLong();

		List<Transaction> transactions = service.listTransactions(accountNumber);

		System.out.println("List of transactions --");

		System.out.println("Account No.\t" + "Transaction ID\t" + "Transaction Date\t" + "Transaction Amount\t"
				+ "Transaction Type\t" + "Transaction Description");

		for (Transaction txn : transactions)
			System.out.println(txn);
		myLogger.info("List of transactions printed by admin.");
	}

	/**
	 * Client methods
	 * 
	 * @throws InternalServerException
	 * @throws NoTransactionsExistException
	 * @throws RequestCannotBeProcessedException
	 * @throws NoServicesMadeException
	 * @throws AccountsNotFoundException
	 */

	public static void onCustomerLogin(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException, RequestCannotBeProcessedException,
			NoServicesMadeException, AccountsNotFoundException {
		myLogger.info("Logged in as Customer.");
		while (true) {
			System.out.println("***********************************************");
			System.out.println("Enter 1 to view statement");
			System.out.println("Enter 2 to change personal details");
			System.out.println("Enter 3 for cheque book service request");
			System.out.println("Enter 4 to track service request");
			System.out.println("Enter 5 for fund transfer");
			System.out.println("Enter 6 to change password");
			System.out.println("Enter 7 to view your profile");
			System.out.println("Enter 0 to exit.");
			System.out.println("***********************************************");

			int customerChoice = scanner.nextInt();
			switch (customerChoice) {
			case 1:
				viewStatement(customer, service);
				break;
			case 2:
				changeDetails(customer, service);
				break;
			case 3:
				requestChequeBook(customer, service);
				break;
			case 4:
				trackService(customer, service);
				break;
			case 5:
				fundTransfer(customer, service);
				break;
			case 6:
				changePassword(customer, service);
				break;
			case 7:
				viewProfile(customer, service);
				break;
			case 0:
				System.exit(0); // Exiting the control when 0 is entered.
			default:
				System.out.println("Invalid choice entered"); //
			}
		}

	}

	private static void viewStatement(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException {
		myLogger.info("View statement method initiated by Customer.");
		int statementChoice;
		System.out.println("***********************************************");
		System.out.println("Enter 1 to view mini-statement");
		System.out.println("Enter 2 to view detailed-statement");
		System.out.println("***********************************************");
		statementChoice = scanner.nextInt();
		while (true) {
			switch (statementChoice) {
			case 1:
				viewMiniStatement(customer, service);
				break;
			case 2:
				viewDetailedStatement(customer, service);
				break;
			case 3:
				System.exit(0); // Write code to go back.
			default:
				System.out.println("Invalid choice entered"); //
			}
		}

	}

	private static void viewMiniStatement(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException {
		myLogger.info("View mini statement method initiated by Customer.");
		if (customer.getTransactions() == null) {
			// First request
			List<Transaction> transactions = service.listTransactions(customer.getAccountNumber());
			customer.setTransactions(transactions);
		}

		List<Transaction> transactions = customer.getTransactions();

		System.out.println("Account No.\t" + "Transaction ID\t" + "Transaction Date\t" + "Transaction Amount\t"
				+ "Transaction Type\t" + "Transaction Description");
		for (int i = 0; i < 10; i++)
			System.out.println(transactions.get(i));
		myLogger.info("Mini statement printed by Customer.");
	}

	private static void viewDetailedStatement(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException {
		myLogger.info("View detailed statement method initiated by Customer.");
		if (customer.getTransactions() == null) {
			// First request
			List<Transaction> transactions = service.listTransactions(customer.getAccountNumber());
			customer.setTransactions(transactions);
		}

		List<Transaction> transactions = customer.getTransactions();

		System.out.println("Account No.\t" + "Transaction ID\t" + "Transaction Date\t" + "Transaction Amount\t"
				+ "Transaction Type\t" + "Transaction Description");
		for (Transaction txn : transactions)
			System.out.println(txn);
		myLogger.info("Detailed statement printed by Customer.");
	}

	private static void changeDetails(Customer customer, CustomerService service) throws InternalServerException {

		myLogger.info("Change details method initiated by Customer.");
		System.out.println("MODIFYING EXISTING CUSTOMER DETAILS");
		System.out.println(customer);

		System.out
				.println("Which detail do you want to change?\n1. Contact Number.\n2. Address.\nInput -1 to go back.");
		int inputChoice = scanner.nextInt();

		boolean backFlag = false;
		while (true) {
			switch (inputChoice) {
			case 1:
				System.out.println("Enter new mobile number: ");
				String newContact;
				myLogger.info("Change mobile number method initiated by Customer.");
				do {
					newContact = scanner.next();
				} while (!service.validateMobile(newContact));
				boolean isContactChanged = service.changeContactNumber(newContact, customer.getAccountNumber());
				if (isContactChanged) {
					customer.setMobileNumber(newContact);
					System.out.println("Contact details updated successfully");
					myLogger.info("Mobile number changed by customer successfully.");
				} else {
					System.out.println("Details could not be updated now.\\nPlease try again later.");
					myLogger.info("Change mobile number by customer unsuccessful.");
				}
				break;
			case 2:
				System.out.println("Enter new address: ");
				String newAddress = scanner.next();
				myLogger.info("Change address method initiated by Customer.");
				boolean isAddressChanged = service.changeAddress(newAddress, customer.getAccountNumber());
				if (isAddressChanged) {
					customer.setAddress(newAddress);
					System.out.println("Address details updated successfully");
					myLogger.info("Address changed by Customer successfully.");
				} else {
					System.out.println("Details could not be updated now.\nPlease try again later.");
					myLogger.info("Address change by customer unsuccessful.");
				}
				break;
			case -1:
				backFlag = true;
				break;
			default:
				System.out.println("Invalid choice entered");

				break;
			}
			if (backFlag) {
				myLogger.info("Invalid choice entered by customer.");
				break;
			}
		}

	}

	private static void requestChequeBook(Customer customer, CustomerService service)
			throws RequestCannotBeProcessedException, InternalServerException {
		myLogger.info("Request Cheque Book method initiated by Customer.");
		System.out.println("Do you want to request a new cheque book?\n1. Yes.\n2. No.");
		int inputChoice = scanner.nextInt();

		boolean backFlag = false;
		while (true) {
			switch (inputChoice) {
			case 1:
				Request newRequest = new Request();
				newRequest.setAccountNumber(customer.getAccountNumber());
				newRequest.setRequestDate(LocalDate.now());
				newRequest.setStatus(0);
				service.requestForCheckBook(newRequest);
				System.out.println("Successfully requested for a new cheque book.");
				myLogger.info("New cheque book requested successfully by Customer.");
				break;
			case 2:
				backFlag = true;
				myLogger.info("No new cheque book requested by Customer.");
				break;
			default:
				System.out.println("Invalid choice entered.");
				break;
			}
			if (backFlag) {
				myLogger.info("Invalid choice entered by customer.");
				break;
			}
		}
	}

	private static void trackService(Customer customer, CustomerService service)
			throws NoServicesMadeException, InternalServerException {

		myLogger.info("Track service method initiated by Customer.");
		if (customer.getRequests() == null) {
			// First request to view progress
			List<Request> requests = service.getRequests(customer.getAccountNumber());
			customer.setRequests(requests);
		}

		List<Request> requests = customer.getRequests();

		for (Request request : requests) {
			System.out.println(request);
			myLogger.info("List of requests and status printed by Customer.");
		}
	}

	private static void fundTransfer(Customer customer, CustomerService service)
			throws InternalServerException, AccountsNotFoundException {
		boolean backFlag = false;
		myLogger.info("Fund transfer method initiated by Customer.");
		while (true) {
			System.out.println(
					"Please select one of the follworing options: \n1. Transfer funds to your other account.\n2.Tranfer funds to acoount of other customers.");
			int inputChoice = scanner.nextInt();
			switch (inputChoice) {
			case 1:
				transferAccrossAccounts(customer, service);
				break;
			case 2:
				transferAcrossUsers(customer, service);
				break;
			case 0:
				backFlag = true;
				break;
			default:
				System.out.println("Invalid input entered.");
				break;
			}
			if (backFlag) {
				myLogger.info("Invalid choice entered by Customer.");
				break;
			}
		}
	}

	private static void transferAccrossAccounts(Customer customer, CustomerService service)
			throws InternalServerException, AccountsNotFoundException {
		myLogger.info("Fund transfer across same user's different accounts initiated by Customer.");
		Account otherAccount = service.fetchOtherExistingAccount(customer.getAccountNumber(),
				customer.getAccountType());

		System.out.println("***** FROM ACCOUNT *****");
		System.out.println(customer.getAccountNumber() + "\t" + customer.getName());
		System.out.println("***** TO ACCOUNT *****");
		System.out.println(otherAccount.getAccountNumber() + "\t" + customer.getName());

		System.out.print("Please enter the amount: ");
		double transferAmount = scanner.nextDouble();
		System.out.print("Please enter your transaction password: ");
		String txnPwd = scanner.next();
		System.out.print("Enter message for fund transfer: ");
		String txnDesc = scanner.next();

		boolean isValidTxnAmt = service.validateTransactionAmount(customer, transferAmount);
		boolean isValidPwd = service.validateTransactionPassword(customer, txnPwd);

		if (!isValidTxnAmt) {
			System.out.println("Insufficient funds in your account.");
			myLogger.info("Invalid transaction amount entered by Customer.");
			return;
		}
		if (!isValidPwd) {
			System.out.println("Invalid password entered for transaction.");
			myLogger.info("Invalid transaction password entered by Customer.");
			return;
		}

		Transaction txnDetails = new Transaction();
		txnDetails.setTransactionAmount(transferAmount);
		txnDetails.setTransactionDescription(txnDesc);

		boolean isTransferred = service.transferFund(customer, otherAccount, txnDetails);
		if (isTransferred) {
			System.out.println("Successfully transferred funds.");
			myLogger.info("Fund transfer successful.");
		} else {
			System.out.println("Could not transfer funds at this moment, try again later.");
			myLogger.info("Fund transfer unsuccessful.");
		}
	}

	private static void transferAcrossUsers(Customer customer, CustomerService service) throws InternalServerException {

		myLogger.info("Fund transfer to other user's account method initiated by Customer.");
		List<Account> beneficiaries = service.fetchBeneficiaries(customer.getAccountNumber());

		while (true) {
			boolean isBeneficiaryAdded = beneficiaries.size() == 0;
			if (isBeneficiaryAdded)
				System.out.println("No Beneficiaries added. Please add a beneficiary to tranfer money.");

			System.out.println("  Account Number\tNickName");
			for (int index = 1; index <= beneficiaries.size(); index++)
				System.out.println(index + " " + beneficiaries.get(index).getAccountNumber() + "\t"
						+ beneficiaries.get(index).getNickName());

			if (isBeneficiaryAdded)
				System.out.println("Please enter a number in the range 1 - " + beneficiaries.size());
			System.out.println("Press 0 to add a beneficiary.\nPress -1 to go back.");

			int inputChoice = scanner.nextInt();

			if (inputChoice == 0) {
				System.out.print("Enter beneficiary account number: ");
				long accountNumber = scanner.nextLong();
				System.out.print("Enter a nick name for beneficiary: ");
				String nickName = scanner.next();
				Account newBeneficiary = new Account();
				newBeneficiary.setAccountNumber(accountNumber);
				newBeneficiary.setNickName(nickName);

				boolean isAdded = service.addNewBeneficiary(customer.getAccountNumber(), newBeneficiary);
				if (isAdded) {
					System.out.println("Beneficiary added.");
					beneficiaries.add(newBeneficiary);
					myLogger.info("New beneficiary added by Customer.");
				} else {
					System.out.println("Beneficiary could not be added now, try again later");
					myLogger.info("Adding new beneficiary unsuccessful.");
				}
			} else if (inputChoice <= beneficiaries.size()) {
				System.out.print("Please enter the amount: ");
				double transferAmount = scanner.nextDouble();
				System.out.print("Please enter your transaction password: ");
				String txnPwd = scanner.next();
				System.out.print("Enter message for fund transfer: ");
				String txnDesc = scanner.next();

				double limit = service.getTransactionLimit();

				boolean isValidTxnLimit = transferAmount <= limit;
				boolean isValidTxnAmt = service.validateTransactionAmount(customer, transferAmount);
				boolean isValidPwd = service.validateTransactionPassword(customer, txnPwd);

				if (!isValidTxnAmt) {
					System.out.println("Insufficient funds in your account, or transaction");
					myLogger.info("Insufficient funds for transfer requested by customer.");
					return;
				}
				if (!isValidPwd) {
					System.out.println("Invalid password entered for transaction.");
					myLogger.info("Invalid transaction password entered by Customer.");
					return;
				}
				if (!isValidTxnLimit) {
					System.out.println("Can't transfer an amount more than 10 Lakhs.");
					myLogger.info("Invalid transaction amount entered by Customer.");
					return;
				}

				Transaction txnDetails = new Transaction();
				txnDetails.setTransactionAmount(transferAmount);
				txnDetails.setTransactionDescription(txnDesc);

				boolean isTransferred = service.transferFund(customer, beneficiaries.get(inputChoice), txnDetails);
				if (isTransferred) {
					System.out.println("Successfully transferred funds.");
					myLogger.info("Fund transfered successfully by Customer.");
				} else {
					System.out.println("Could not transfer funds at this moment, try again later.");
					myLogger.info("Fund transfer unsuccessful.");
				}
			} else if (inputChoice == -1) {
				myLogger.info("Previous page requested by Customer.");
				break; // Go to previous page.
			} else {
				System.out.println("Please enter a valid choice.");
				myLogger.info("Invalid choice entered by Customer.");
			}
		}
	}

	private static void changePassword(Customer customer, CustomerService service) throws InternalServerException {
		myLogger.info("Password change requested by Customer.");
		while (true) {
			System.out.println("Please enter old password: ");
			String oldPassword = scanner.next();

			String newPassword;
			do {
				System.out.println("Please enter new password: ");
				newPassword = scanner.next();
			} while (!service.validatePassword(newPassword));

			String confirmPassword;
			do {
				System.out.println("Confirm new password: ");
				confirmPassword = scanner.next();
			} while (!service.validatePassword(confirmPassword));

			if (!oldPassword.equals(customer.getPassword())) {
				System.out.println("Invalid Old password");
				myLogger.info("Invalid old password entered by Customer.");
				break;
			}
			if (!(confirmPassword.equals(newPassword))) {
				System.out.println("Passwords don't match");
				myLogger.info("Unmatching old passwords entered by Customer.");
				break;
			}
			boolean result = service.updatePassword(newPassword, customer.getUserId());

			if (result) {
				System.out.println("Successfully updated password.");
				myLogger.info("Password updated by Customer.");
			} else {
				System.out.println("Password could not be updated now, try again later");
				myLogger.info("Password change unsuccessful.");
			}
		}
	}

	private static void viewProfile(Customer customer, CustomerService service) {
		myLogger.info("View profile method initiated by Customer.");
		System.out.println(customer);
	}

}
