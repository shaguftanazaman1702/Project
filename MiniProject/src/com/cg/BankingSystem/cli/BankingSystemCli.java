/**
 * 
 */
package com.cg.BankingSystem.cli;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

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
import com.cg.BankingSystem.exception.MaxAccountsDefinedForUserException;
import com.cg.BankingSystem.exception.NoServicesMadeException;
import com.cg.BankingSystem.exception.NoTransactionsExistException;
import com.cg.BankingSystem.exception.RequestCannotBeProcessedException;
import com.cg.BankingSystem.exception.UserNotFoundException;
import com.cg.BankingSystem.service.AdminService;
import com.cg.BankingSystem.service.AdminServiceImpl;
import com.cg.BankingSystem.service.BankingSystemService;
import com.cg.BankingSystem.service.CustomerService;
import com.cg.BankingSystem.service.CustomerServiceImpl;

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
	public static void main(String[] args) {
		PropertyConfigurator.configure("src/log4j.properties");
		System.out.println("WELCOME TO BANKING SYSTEM!");

		while (true) {
			int userTypeChoice = -1;
			try {
				System.out.println("***********************************************");
				System.out.println("Enter 1 to Sign In.");
				System.out.println("Enter 0 to Exit.");
				System.out.println("***********************************************");

				userTypeChoice = scanner.nextInt();

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
			} catch (Exception e) {
				if (e instanceof InputMismatchException) { 
					System.out.println("Invalid choice entered.\nProvide a valid input.");
					scanner = new Scanner(System.in);
					continue;
				}
				System.out.println(e.getMessage());
			}
		}
		//Validation done
	}

	private static void signIn() throws InvalidCredentialsException, InternalServerException {
		System.out.println("***********************************************");
		System.out.println("***** Enter Your Credentials *****");

		LoginBean login = new LoginBean();
		
		String userId = null, password = null;
		boolean isValidUserID = false;

		while (!isValidUserID) {
			System.out.print("User ID : ");
			userId = scanner.next();
			
			isValidUserID = BankingSystemService.validateAdminUserID(userId) || BankingSystemService.validateCustomerUserID(userId);
			if (!isValidUserID)
				System.out.println("Invalid User ID.\nPlease enter a valid User ID");
		}

		System.out.print("Password : ");
		password = scanner.next();
		
		login.setUserId(userId);
		login.setPassword(password);

		if (login.getUserId().contains("AD")) {
			AdminService adminService = (AdminService) BankingSystemService.getInstance(login);

			System.out.println("Logging in . . . ");
			Admin adminLogin = (Admin) adminService.authenticateUser(login);
			System.out.println("***********************************************");
			System.out.println("Logged in successfully as Admin. Welcome " + adminLogin.getUserName());

			onAdminLogin(adminService);
		} else if (login.getUserId().contains("CC")) {
			CustomerService customerService = (CustomerService) BankingSystemService.getInstance(login);
			
			while (true) {
				System.out.println("\nPlease enter the type of account you want to login to:\n1. Savings Account.\n2. Current Account.");
				try {
					int inputChoice = scanner.nextInt();
					
					if (inputChoice == 1) {
						login.setAccountType(AccountType.SAVINGS_ACCOUNT);
						break;
					}
					else if (inputChoice == 2) {
						login.setAccountType(AccountType.CURRENT_ACCOUNT);
						break;
					}
					else
						System.out.println("Please enter a valid input");
				} catch (InputMismatchException e) {
					System.out.println("Please enter a valid input");
					scanner = new Scanner(System.in);
				}
			}				
			
			System.out.println("Logging in . . . ");
			Customer customerLogin = (Customer) customerService.authenticateUser(login);
			System.out.println("***********************************************");
			System.out.println("Logged in successfully as User. Welcome " + customerLogin.getName());

			onCustomerLogin(customerLogin, customerService);
		} 
		
		// Testing done completely
	}

	/**
	 * Admin methods
	 * 
	 * @throws InternalServerException
	 * @throws AccountNotCreatedException
	 * @throws NoTransactionsExistException
	 * @throws UserNotFoundException
	 */

	public static void onAdminLogin(AdminService service) {
		while (true) {
			try {
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
					System.out.println("Logging out . . . ");
					System.out.println("Logged Out Successfully.");
					System.exit(0); // Exiting the control when 0 is entered.
				default:
					System.out.println("Invalid choice entered. Please enter a valid choice"); // Default message when none
					// of 0 - 3 is entered
				}
			} catch (Exception e) {
				if (e instanceof InputMismatchException) {
					System.out.println("Invalid choice entered. Please enter a valid choice");
					scanner = new Scanner(System.in);
					continue;
				}
				System.out.println(e.getMessage());
			}
		}

		// Validation done
	}

	private static void createNewAccount(AdminService service)
			throws AccountNotCreatedException, InternalServerException, UserNotFoundException, MaxAccountsDefinedForUserException {
		boolean backFlag = false;
		while (true) {
			try {
				System.out.println("***********************************************");
				System.out.println(
						"Select option : \n1: Create new account for existing user. \n2: Create new account for new user. \n3: Back.");
				System.out.println("***********************************************");
				int existingUserChoice = scanner.nextInt();
				
				switch (existingUserChoice) {
				case 1:
					createNewAccountForExistingUser(service);
					break;
				case 2:
					scanner.nextLine();
					createNewUserAccount(service);
					break;
				case 3:
					backFlag = true;
					break;
				default:
					System.out.println("Invalid choice entered. Please enter a valid choice");
				}
				if (backFlag)
					break;
			} catch (InputMismatchException e) {
				System.out.println("Invalid choice entered. Please enter a valid choice");
				scanner = new Scanner(System.in);
			}
		}
		
		// Testing done completely
	}

	private static void createNewAccountForExistingUser(AdminService service)
			throws InternalServerException, UserNotFoundException, MaxAccountsDefinedForUserException {
		String userId = null;

		boolean isValidCustomer = false;
		
		while (!isValidCustomer) {
			System.out.print("Enter customer userId : ");
			userId = scanner.next();

			isValidCustomer = BankingSystemService.validateCustomerUserID(userId);
			if (!isValidCustomer)
				System.out.println("Enter a valid input");
		}

		Customer existingCustomer = service.findCustomer(userId); // throws if no user

		double min = 1000;
		double max = 1000000;

		System.out.println("***********************************************");
		System.out.println("Customer Details: " + existingCustomer.getName() + "\t" + existingCustomer.getAccountNumber() + "\t" + existingCustomer.getAccountType().getValue());

		System.out.println(" ***** Enter details for the new account ***** ");
		
		double openingBal = -1;

		boolean isValidBalance = false;

		while (!isValidBalance) {
			try {
				System.out.print("Opening Balance (minimum - " + min + ", maximum = " + max + "): ");

				openingBal = scanner.nextDouble();

				isValidBalance = service.validateDouble(min, max, openingBal);
				if (!isValidBalance)
					System.out.println("Please enter a valid input.");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input.");
				scanner = new Scanner(System.in);
			}
		}

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

		System.out.println("Creating account . . .");
		boolean isCreated = service.saveExistingUser(newCustomer);

		System.out.println("***********************************************");
		if (isCreated)
			System.out.println("Account created successfully");
		else
			System.out.println("Account could not be created now.\\nPlease try again later.");

		// Tested completely
	}

	private static void createNewUserAccount(AdminService service) throws AccountNotCreatedException, InternalServerException {
		SignUp newSignUp = new SignUp();
		AccountType accType = null;

		String name = null, address = null, mobileNo = null, email = null, 
				panCardNumber = null, userId = null, password = null, 
				transactionPassword = null;
		double openingBal = -1;

		boolean isValidName = false;
		boolean isValidAddress = false;
		boolean isValidContact = false;
		boolean isValidEmail = false;
		boolean isValidBalance = false;
		boolean isValidPanCard = false;
		boolean isValidUserID = false;
		boolean isValidPassword = false;
		boolean isValidTxnPwd = false;

		while (!isValidName) {
			System.out.print("Enter customer name : ");
			name = scanner.nextLine();

			isValidName = service.validateName(name);
			if (!isValidName)
				System.out.println("Invalid name entered");
		}

		while (!isValidAddress) {
			System.out.print("Enter customer address (max 100 characters): ");
			address = scanner.nextLine();

			isValidAddress = service.validateAddress(address);
			if (!isValidAddress)
				System.out.println("Invalid address entered");
		}

		while (!isValidContact) {
			System.out.print("Enter customer's mobile number : ");
			mobileNo = scanner.next();

			isValidContact = service.validateContact(mobileNo);
			if (!isValidContact)
				System.out.println("Invalid mobile entered");
		}

		while (!isValidEmail) {
			System.out.print("Enter customer's Email ID : ");
			email = scanner.next();

			isValidEmail = service.validateEmail(email);
			if(!isValidEmail)
				System.out.println("Enter a valid Email ID");
		}

		int accountTypeChoice = -1;
		while (true) {
			try {
				System.out.print("Select customer account type : \n 1: for Savings account \n 2: For current account\n");
				accountTypeChoice = scanner.nextInt();
				if (accountTypeChoice == 1) {
					accType = AccountType.SAVINGS_ACCOUNT;
					break;
				}
				else if (accountTypeChoice == 2) {
					accType = AccountType.CURRENT_ACCOUNT;
					break;
				}
				else
					System.out.println("Please Enter a valid choice.");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input.");
				scanner = new Scanner(System.in);
			}
		}

		double min = 1000;
		double max = 1000000;

		while (!isValidBalance) {
			try {
				System.out.print("Enter customer opening balance (minimum - " + min + ", maximum = " + max + "): ");
				openingBal = scanner.nextDouble();

				isValidBalance = service.validateDouble(min, max, openingBal);
				if (!isValidBalance)
					System.out.println("Invalid figure for opening balance.");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input.");
				scanner = new Scanner(System.in);
			}
		}

		while (!isValidPanCard) {
			System.out.print("Enter customer PAN no. : ");
			panCardNumber = scanner.next();

			isValidPanCard = service.validatePanCard(panCardNumber);
			if (!isValidPanCard)
				System.out.println("Invalid Pan Card Number entered");
		}

		while (!isValidUserID) {
			System.out.print("Enter customer user ID : ");
			userId = scanner.next();

			isValidUserID = BankingSystemService.validateCustomerUserID(userId);
			if (!isValidUserID)
				System.out.println("Please enter a Valid user ID for customer");
		}

		while (!isValidPassword) {
			System.out.print("Enter customer password (atleast 1 capital letter, 1 special character (@, #, $, %), and 1 number. Minimum length - 8): ");
			password = scanner.next();

			isValidPassword = BankingSystemService.validatePassword(password);
			if (!isValidPassword)
				System.out.println("Invalid password entered");
		}

		while (!isValidTxnPwd) {
			System.out.print("Enter customer transaction password (atleast 1 capital letter, 1 special character (@, #, $, %), and 1 number. Minimum length - 8): ");
			transactionPassword = scanner.next();

			isValidTxnPwd = service.validateTxnPwd(transactionPassword);
			if (!isValidTxnPwd)
				System.out.println("Invalid password entered");
		}

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

		System.out.println("***********************************************");
		System.out.println("Adding into database . . .");
		long newCustomerAccountNo = service.createNewAccount(newSignUp);

		System.out.println("***********************************************");
		System.out.println("New Account opened successfully with account number: " + newCustomerAccountNo);
		
		// Testing completely done
	}

	private static void viewTransactions(AdminService service)
			throws NoTransactionsExistException, InternalServerException {
		while (true) {
			try {
				System.out.println("***********************************************");
				System.out.print("Enter Customer's Account Number (0 to go back) : ");
				long accountNumber;
				accountNumber = scanner.nextLong();

				long min = 0;
				long max = Long.MAX_VALUE;

				boolean validatedEntry = service.validateLongEntry(min, max, accountNumber);

				if (!validatedEntry) {
					System.out.println("Please enter a valid input");
					continue;
				}
				if (accountNumber == 0L)
					break;

				List<Transaction> transactions = service.listTransactions(accountNumber);

				System.out.println("List of transactions --");

				System.out.println("Account No.\t" + "Transaction ID\t" + "Transaction Date\t" + "Transaction Amount\t"
						+ "Transaction Type\t" + "Transaction Description");

				for (Transaction txn : transactions)
					System.out.println(txn);

				// Testing Done completely
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input");
				scanner = new Scanner(System.in);
			}
		}
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

	public static void onCustomerLogin(Customer customer, CustomerService service) {
		while (true) {
			try {
				System.out.println("***********************************************");
				System.out.println("Enter 1 to view statement");
				System.out.println("Enter 2 to change personal details");
				System.out.println("Enter 3 for cheque book service request");
				System.out.println("Enter 4 to track service request");
				System.out.println("Enter 5 for fund transfer");
				System.out.println("Enter 6 to change password");
				System.out.println("Enter 7 to view your profile");
				System.out.println("Enter 0 to logout.");
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
					System.out.println("Logging out . . .");
					System.out.println("Logged Out successfully.");
					System.exit(0); // Exiting the control when 0 is entered.
				default:
					System.out.println("Invalid choice entered");
				}
			} catch (Exception e) {
				if (e instanceof InputMismatchException) {
					System.out.println("Invalid choice entered");
					scanner = new Scanner(System.in);
					continue;
				}
				System.out.println(e.getMessage());
			}
		}

		// Validation done
	}

	private static void viewStatement(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException {
		
		while (true) {
			try {
				System.out.println("***********************************************");
				System.out.println("Enter 1 to view mini-statement");
				System.out.println("Enter 2 to view detailed-statement");
				System.out.println("***********************************************");
				System.out.print("Enter your choice: ");
				int statementChoice = scanner.nextInt();
				
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
					System.out.println("Invalid choice entered"); 
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid choice entered");
				scanner = new Scanner(System.in);
			}
		}

		// Testing done completely
	}

	private static void viewMiniStatement(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException {
		if (customer.getTransactions() == null) {
			// First request
			List<Transaction> transactions = service.listTransactions(customer.getAccountNumber());
			customer.setTransactions(transactions);
		}

		List<Transaction> transactions = customer.getTransactions();

		System.out.println("***********************************************");
		System.out.println("Transaction ID\t" + "Transaction Amount\t" + "Transaction Date\t" + "Transaction Type\t");
		for (int index = 0; index < 10; index++) {
			Transaction txn = transactions.get(index);
			System.out.println(txn.getTransactionID() + "\t\t" + txn.getTransactionAmount() + "\t\t\t" + txn.getTransactionDate() + "\t\t" + txn.getTransactionType().getValue());
			if (index == transactions.size() -1)
				break;
		}
		
		boolean isValidInput = false;
		int inputChoice = 0;
		
		while (!isValidInput) {
			try {
				System.out.println("Enter -1 to go back: ");
				
				inputChoice = scanner.nextInt();
				
				if (inputChoice == -1)
					return;
				System.out.println("Please enter a valid input");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input");
				scanner = new Scanner(System.in);
			}
		}
		
		// Validation done
	}

	private static void viewDetailedStatement(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException {
		if (customer.getTransactions() == null) {
			// First request
			List<Transaction> transactions = service.listTransactions(customer.getAccountNumber());
			customer.setTransactions(transactions);
		}

		List<Transaction> transactions = customer.getTransactions();
		
		for (Transaction txn : transactions) {
			System.out.println("***********************************************");
			System.out.println(txn);
		}
		
		boolean isValidInput = false;
		int inputChoice = 0;
		
		while (!isValidInput) {
			try {
				System.out.print("Enter -1 to go back: ");
				
				inputChoice = scanner.nextInt();
				
				if (inputChoice == -1)
					return;
				System.out.println("Please enter a valid input");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input");
				scanner = new Scanner(System.in);
			}
		}
		
		// Tested completely
	}

	private static void changeDetails(Customer customer, CustomerService service) throws InternalServerException {
		System.out.println("***********************************************");
		System.out.println("MODIFYING EXISTING CUSTOMER DETAILS");
		System.out.println("***********************************************");
		System.out.println(customer);
		System.out.println("***********************************************");

		int inputChoice = 100;
		boolean isValidInput = false;
		
		while (!isValidInput) {
			try {
				System.out.println("Which detail do you want to change?\n1. Contact Number.\n2. Address.\nInput -1 to go back.");
				System.out.println("***********************************************");
				inputChoice = scanner.nextInt();
				isValidInput = inputChoice == 1 || inputChoice == 2 || inputChoice == -1;
			} catch (InputMismatchException e) {
				System.out.println("Invalid choice entered");
				scanner = new Scanner(System.in);
			}
		}

		boolean backFlag = false;
		while (true) {
			switch (inputChoice) {
			case 1:
				boolean isValidContact = false;
				String newContact = null;
						
				while (!isValidContact) {
					System.out.print("Enter new mobile number: ");
					newContact = scanner.next();
					
					isValidContact = service.validateContact(newContact);
					if (!isValidContact)
						System.out.println("Please enter a valid contact number.");
				}
				
				boolean isContactChanged = service.changeContactNumber(newContact, customer.getAccountNumber());
				if (isContactChanged) {
					customer.setMobileNumber(newContact);
					System.out.println("\nContact details updated successfully");
				} else
					System.out.println("\nDetails could not be updated now.\nPlease try again later.");
				backFlag = true;
				break;
				
			case 2:
				boolean isValidAddress = false;
				String newAddress = null;
						
				while (!isValidAddress) {
					System.out.println("Enter new address: ");
					newAddress = scanner.next();
					
					isValidAddress = service.validateAddress(newAddress);
					if (!isValidAddress)
						System.out.println("Please enter a valid address.");
				}
				
				boolean isAddressChanged = service.changeAddress(newAddress, customer.getAccountNumber());
				if (isAddressChanged) {
					customer.setAddress(newAddress);
					System.out.println("Address details updated successfully");
				} else
					System.out.println("Details could not be updated now.\nPlease try again later.");
				backFlag = true;
				break;
				
			case -1:
				backFlag = true;
				break;
				
			default:
				System.out.println("Invalid choice entered");
				break;
			}
			if (backFlag)
				break;
		}

		// Testing done completely
	}

	private static void requestChequeBook(Customer customer, CustomerService service)
			throws RequestCannotBeProcessedException, InternalServerException {
		boolean backFlag = false;
		int inputChoice = 100;
		
		while (true) {
			try {
				System.out.println("Do you want to request a new cheque book?\n1. Yes.\n2. No.");
				inputChoice = scanner.nextInt();

				switch (inputChoice) {
				case 1:
					Request newRequest = new Request();
					newRequest.setAccountNumber(customer.getAccountNumber());
					newRequest.setRequestDate(LocalDate.now());
					newRequest.setStatus(0);
					service.requestForCheckBook(newRequest);
					System.out.println("Successfully requested for a new cheque book.");
					break;
				case 2:
					backFlag = true;
					break;
				default:
					System.out.println("Invalid choice entered.");
					break;
				}
				if (backFlag)
					break;
			} catch (InputMismatchException e) {
				System.out.println("Invalid input entered.");
				scanner = new Scanner(System.in);
			}
		}
		
		// Tested completely
	}

	private static void trackService(Customer customer, CustomerService service)
			throws NoServicesMadeException, InternalServerException {
		if (customer.getRequests() == null) {
			// First request to view progress
			List<Request> requests = service.getRequests(customer.getAccountNumber());
			customer.setRequests(requests);
		}

		List<Request> requests = customer.getRequests();

		System.out.println("***********************************************");
		for (Request request: requests)
			System.out.println(request);
		
		boolean isValidInput = false;
		int inputChoice = 0;
		
		while (!isValidInput) {
			try {
				System.out.println("***********************************************");
				System.out.println("Enter -1 to go back: ");
				
				inputChoice = scanner.nextInt();
				
				if (inputChoice == -1)
					return;
				System.out.println("Please enter a valid input");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input");
				scanner = new Scanner(System.in);
			}
		}
		
		// Tested completely
	}

	private static void fundTransfer(Customer customer, CustomerService service)
			throws InternalServerException, AccountsNotFoundException {
		boolean backFlag = false;
		while (true) {
			try {
				System.out.println("***********************************************");
				System.out.println(
						"Please select one of the follworing options: \n1. Transfer funds to your other account.\n2. Tranfer funds to acoount of other customers.\n Press 0 to go back.");
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
				if (backFlag)
					break;
			} catch (InputMismatchException e) {
				System.out.println("Invalid input entered.");
				scanner = new Scanner(System.in);
			}
		}
		
		// Tested successfully
	}

	private static void transferAccrossAccounts(Customer customer, CustomerService service)
			throws InternalServerException, AccountsNotFoundException {
		Account otherAccount = service.fetchOtherExistingAccount(customer.getAccountNumber(),
				customer.getAccountType());

		System.out.println("***********************************************");
		System.out.println("***** FROM ACCOUNT *****");
		System.out.println(customer.getAccountNumber() + "\t" + customer.getName() + "\t" + customer.getAccountType().getValue());
		System.out.println("***** TO ACCOUNT *****");
		System.out.println(otherAccount.getAccountNumber() + "\t" + customer.getName() + "\t" + otherAccount.getAccountType().getValue());
		System.out.println("***********************************************");
		
		double transferAmount = 0;

		boolean isValidInput = false;
		while (!isValidInput) {
			try {
				System.out.print("Please enter the amount: ");
				transferAmount = scanner.nextDouble();
				isValidInput = transferAmount > 0;
				
				if (!isValidInput)
					System.out.println("Please enter a valid figure for transfer.");
			} catch (InputMismatchException e) {
				System.out.println("Invalid input, please try again.");
				scanner = new Scanner(System.in);
			}
		}
		
		System.out.print("Please enter your transaction password: ");
		String txnPwd = scanner.next();
		scanner.nextLine();
		System.out.print("Enter message for fund transfer: ");
		String txnDesc = scanner.nextLine();

		boolean isValidTxnAmt = service.validateTransactionAmount(customer, transferAmount);
		boolean isValidPwd = service.checkTransactionPassword(customer, txnPwd);

		if (!isValidTxnAmt) {
			System.out.println("***********************************************");
			System.out.println("Insufficient funds in your account.");
			return;
		}
		if (!isValidPwd) {
			System.out.println("***********************************************");
			System.out.println("Invalid password entered for transaction.");
			return;
		}

		Transaction txnDetails = new Transaction();
		txnDetails.setTransactionAmount(transferAmount);
		txnDetails.setTransactionDescription(txnDesc);
		txnDetails.setAccountNo(customer.getAccountNumber());

		boolean isTransferred = service.transferFund(customer, otherAccount, txnDetails);
		
		System.out.println("***********************************************");
		
		if (isTransferred)
			System.out.println("Successfully transferred funds.");
		else
			System.out.println("Could not transfer funds at this moment, try again later.");
		return;
		
		// Tested successfully
	}

	private static void transferAcrossUsers(Customer customer, CustomerService service) throws InternalServerException {
		List<Account> beneficiaries = service.fetchBeneficiaries(customer.getAccountNumber());

		while (true) {
			try {
				boolean isBeneficiaryAdded = beneficiaries.size() > 0;

				System.out.println("***********************************************");
				
				System.out.println("  Account Number\tNickName");
				for (int index = 1; index <= beneficiaries.size(); index++)
					System.out.println(index + " " + beneficiaries.get(index - 1).getAccountNumber() + "\t\t\t"
							+ beneficiaries.get(index - 1).getNickName());
				
				if (!isBeneficiaryAdded)
					System.out.println("\nNo Beneficiaries added. Please add a beneficiary to tranfer money.");

				System.out.println("***********************************************");
				if (isBeneficiaryAdded)
					System.out.println("Please enter the serial number of the account you want to transfer money to.");

				System.out.println("Press 0 to add a beneficiary.\nPress -1 to go back.");
				int inputChoice = scanner.nextInt();

				System.out.println("***********************************************");
				if (inputChoice == 0) {
					boolean isValidInput = false, isValidNickName = false;
					long accountNumber = 0L;
					String nickName = null;
					
					while (!isValidInput) {
						try {
							System.out.print("Enter beneficiary account number: ");
							accountNumber = scanner.nextLong();
							
							isValidInput = accountNumber > 0L;
							if (!isValidInput)
								System.out.println("Please enter a valid Account Number.");
						} catch (InputMismatchException e) {
							System.out.println("Please enter a valid input.");
							scanner = new Scanner(System.in);
						}
					}
					scanner.nextLine();
					
					while (!isValidNickName) {
						System.out.print("Enter a nick name for beneficiary: ");
						nickName = scanner.nextLine();
						
						isValidNickName = service.validateNickName(nickName);
						if(!isValidNickName)
							System.out.println("Enter a valid Nick-Name");
					}
					
					Account newBeneficiary = new Account();
					newBeneficiary.setAccountNumber(accountNumber);
					newBeneficiary.setNickName(nickName);

					System.out.println("***********************************************");
					boolean isAdded = service.addNewBeneficiary(customer.getAccountNumber(), newBeneficiary);
					if (isAdded) {
						System.out.println("Beneficiary added.");
						beneficiaries.add(newBeneficiary);
					} else
						System.out.println("Beneficiary could not be added now, try again later");
					
				} else if (inputChoice > 0 && inputChoice <= beneficiaries.size()) {
					boolean isValidInput = false;
					double transferAmount = 0;
					double limit = service.getTransactionLimit();
					
					while (!isValidInput) {
						try {
							System.out.print("Please enter the amount: ");
							transferAmount = scanner.nextDouble();
							
							isValidInput = transferAmount > 0 && transferAmount <= limit;
							if (!isValidInput)
								System.out.println("Transfer amount needs to be a valid figure and below 10 Lakhs");
						} catch (InputMismatchException e) {
							System.out.println("Please enter a valid input.");
							scanner = new Scanner(System.in);
						}
					}
					System.out.print("Please enter your transaction password: ");
					String txnPwd = scanner.next();
					scanner.nextLine();
					System.out.print("Enter message for fund transfer: ");
					String txnDesc = scanner.nextLine();

					boolean isValidTxnLimit = transferAmount <= limit;
					boolean isValidTxnAmt = service.validateTransactionAmount(customer, transferAmount);
					boolean isValidPwd = service.checkTransactionPassword(customer, txnPwd);

					if (!isValidTxnAmt) {
						System.out.println("Insufficient funds in your account.");
						return;
					}
					if (!isValidPwd) {
						System.out.println("Invalid password entered for transaction.");
						return;
					}
					if (!isValidTxnLimit) {
						System.out.println("Can't transfer an amount more than 10 Lakhs.");
						return;
					}

					Transaction txnDetails = new Transaction();
					txnDetails.setTransactionAmount(transferAmount);
					txnDetails.setTransactionDescription(txnDesc);
					txnDetails.setAccountNo(beneficiaries.get(inputChoice - 1).getAccountNumber());

					boolean isTransferred = service.transferFund(customer, beneficiaries.get(inputChoice - 1), txnDetails);
					if (isTransferred)
						System.out.println("Successfully transferred funds.");
					else
						System.out.println("Could not transfer funds at this moment, try again later.");
				} else if (inputChoice == -1)
					break; // Go to previous page.
				else
					System.out.println("Please enter a valid choice.");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input.");
				scanner = new Scanner(System.in);
			}
		}
		
		//Tested completely
	}

	private static void changePassword(Customer customer, CustomerService service) throws InternalServerException {
		while (true) {
			System.out.print("Please enter old password: ");
			String oldPassword = scanner.next();

			boolean isValidPassword = false;
			
			String newPassword = null;
			
			while (!isValidPassword) {
				System.out.print("Please enter new password (atleast 1 capital letter, 1 special character (@, #, $, %), and 1 number. Minimum length - 8): ");
				newPassword = scanner.next();
				
				isValidPassword = BankingSystemService.validatePassword(newPassword);
			}

			System.out.print("Confirm new password: ");
			String confirmPassword = scanner.next();

			if (!oldPassword.equals(customer.getPassword())) {
				System.out.println("Invalid Old password");
				continue;
			}
			if (!(confirmPassword.equals(newPassword))) {
				System.out.println("Passwords don't match");
				continue;
			}

			System.out.println("***********************************************");
			
			boolean result = service.updatePassword(newPassword, customer.getUserId());

			if (result)
				System.out.println("Successfully updated password.");
			else
				System.out.println("Password could not be updated now, try again later");
			
			return;
		}
		
		// Tested successfully
	}

	private static void viewProfile(Customer customer, CustomerService service) {
		System.out.println("***********************************************");
		System.out.println(customer);
		
		boolean isValidInput = false;
		int inputChoice = 0;
		
		while (!isValidInput) {
			try {
				System.out.println("***********************************************");
				System.out.println("Enter -1 to go back: ");
				
				inputChoice = scanner.nextInt();
				
				if (inputChoice == -1)
					return;
				System.out.println("Please enter a valid input");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input");
				scanner = new Scanner(System.in);
			}
		}
		
		// Tested Completely
	}

}