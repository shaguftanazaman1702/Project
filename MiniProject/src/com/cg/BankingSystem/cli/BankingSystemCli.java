/**
 * 
 */
package com.cg.BankingSystem.cli;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

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
		System.out.println("WELCOME TO BANKING SYSTEM!");

		while (true) {
			try {
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
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		//Validation done
	}

	private static void signIn() throws InvalidCredentialsException, InternalServerException,
	AccountNotCreatedException, NoTransactionsExistException, RequestCannotBeProcessedException,
	NoServicesMadeException, UserNotFoundException, AccountsNotFoundException {
		System.out.println("***********************************************");
		System.out.println("***** Enter Your Credentials *****");

		LoginBean login = new LoginBean();
		
		String userId = null, password = null;
		boolean isValidUserID = false, isValidPassword = false;

		while (!isValidUserID) {
			System.out.print("User ID : ");
			userId = scanner.next();
			
			isValidUserID = BankingSystemService.validateAdminUserID(userId) || BankingSystemService.validateCustomerUserID(userId);
		}

		while (!isValidPassword) {
			System.out.print("Password : ");
			password = scanner.next();
			
			isValidPassword = BankingSystemService.validatePassword(password);
		}

		if (login.getUserId().contains("AD")) {
			AdminService adminService = (AdminService) BankingSystemService.getInstance(login);

			Admin adminLogin = (Admin) adminService.authenticateUser(login);
			System.out.println("***********************************************");
			System.out.println("Logged in successfully as Admin. Welcome " + adminLogin.getUserName());

			onAdminLogin(adminService);
		} else {
			CustomerService customerService = (CustomerService) BankingSystemService.getInstance(login);
			System.out.println(customerService == null);
			Customer customerLogin = (Customer) customerService.authenticateUser(login);
			System.out.println("***********************************************");
			System.out.println("Logged in successfully as User. Welcome " + customerLogin.getName());

			onCustomerLogin(customerLogin, customerService);
		}
		
		// Validation done
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
					System.exit(0); // Exiting the control when 0 is entered.
				default:
					System.out.println("Invalid choice entered. Please enter a valid choice"); // Default message when none
					// of 0 - 3 is entered
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid choice entered. Please enter a valid choice");
			}
		}

		// Validation done
	}

	private static void createNewAccount(AdminService service)
			throws AccountNotCreatedException, InternalServerException, UserNotFoundException {
		boolean backFlag = false;
		while (true) {
			try {
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
			} catch (InputMismatchException e) {
				System.out.println("Invalid choice entered. Please enter a valid choice");
			}
		}
		
		// Validation done
	}

	private static void createNewAccountForExistingUser(AdminService service)
			throws InternalServerException, UserNotFoundException {
		while (true) {
			try {
				System.out.print("Enter customer userId : ");
				String userId = scanner.next();

				boolean isValidCustomer = BankingSystemService.validateCustomerUserID(userId);

				if (!isValidCustomer) {
					System.out.println("Enter a valid input");
					continue;
				}

				Customer existingCustomer = service.findCustomer(userId); // throws if no user

				double min = 1000;
				double max = 1000000;

				System.out.println(" ***** Enter details for the new account ***** ");
				System.out.println("Opening Balance (minimum - " + min + ", maximum = " + max + "): ");

				double openingBal = scanner.nextDouble();

				boolean isValidBalance = service.validateDouble(min, max, openingBal);

				if (!isValidBalance) {
					System.out.println("Please enter a valid input.");
					continue;
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

				boolean isCreated = service.saveExistingUser(newCustomer);

				if (isCreated)
					System.out.println("Account created successfully");
				else
					System.out.println("Account could not be created now.\\nPlease try again later.");
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input");
			}
		}

		// Validation done
	}

	private static void createNewUserAccount(AdminService service) throws AccountNotCreatedException, InternalServerException {
		try {
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
				name = scanner.next();
	
				isValidName = service.validateName(name);
			}

			while (!isValidAddress) {
				System.out.print("Enter customer address : ");
				address = scanner.next();
				
				isValidAddress = service.validateAddress(address);
			}

			while (!isValidContact) {
				System.out.print("Enter customer mobile number : ");
				mobileNo = scanner.next();
				
				isValidContact = service.validateContact(mobileNo);
			}

			while (!isValidEmail) {
				System.out.print("Enter customer email : ");
				email = scanner.next();
				
				isValidEmail = service.validateEmail(email);
			}

			int accountTypeChoice = -1;
			while (true) {
				System.out.print("Select customer account type : \n 1: for Savings account \n 2: For current account");
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
			}

			double min = 1000;
			double max = 1000000;
			
			while (!isValidBalance) {
				System.out.print("Enter customer opening balance (minimum - " + min + ", maximum = " + max + "): ");
				openingBal = scanner.nextDouble();
				
				isValidBalance = service.validateDouble(min, max, openingBal);
			}

			while (!isValidPanCard) {
				System.out.print("Enter customer PAN no. : ");
				panCardNumber = scanner.next();
				
				isValidPanCard = service.validatePanCard(panCardNumber);
			}

			while (!isValidUserID) {
				System.out.print("Enter customer user ID : ");
				userId = scanner.next();
				
				isValidUserID = BankingSystemService.validateCustomerUserID(userId);
			}

			while (!isValidPassword) {
				System.out.print("Enter customer password : ");
				password = scanner.next();
				
				isValidPassword = BankingSystemService.validatePassword(password);
			}

			while (!isValidTxnPwd) {
				System.out.print("Enter customer transaction password : ");
				transactionPassword = scanner.next();
				
				isValidTxnPwd = service.validateTxnPwd(transactionPassword);
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

			long newCustomerAccountNo = service.createNewAccount(newSignUp);

			System.out.println("New Account opened successfully with account number: " + newCustomerAccountNo);
		} catch (InputMismatchException e) {
			System.out.println("Please enter a valid input.");
		}
		
		//Validation done
	}

	private static void viewTransactions(AdminService service)
			throws NoTransactionsExistException, InternalServerException {
		while (true) {
			try {
				System.out.println("***********************************************");
				System.out.print("Enter Customer's Account Number (-1 to go back) : ");
				long accountNumber;
				accountNumber = scanner.nextLong();

				long min = -1;
				long max = Long.MAX_VALUE;

				boolean validatedEntry = service.validateLongEntry(min, max, accountNumber);

				if (!validatedEntry) {
					System.out.println("Please enter a valid input");
					continue;
				}
				if (accountNumber == -1L)
					break;

				List<Transaction> transactions = service.listTransactions(accountNumber);

				System.out.println("List of transactions --");

				System.out.println("Account No.\t" + "Transaction ID\t" + "Transaction Date\t" + "Transaction Amount\t"
						+ "Transaction Type\t" + "Transaction Description");

				for (Transaction txn : transactions)
					System.out.println(txn);

				// Validation done.
			} catch (InputMismatchException e) {
				System.out.println("Please enter a valid input");
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

	public static void onCustomerLogin(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException, RequestCannotBeProcessedException,
			NoServicesMadeException, AccountsNotFoundException {
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
					System.out.println("Invalid choice entered");
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid choice entered");
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
			}
		}

		// Validation done
	}

	private static void viewMiniStatement(Customer customer, CustomerService service)
			throws NoTransactionsExistException, InternalServerException {
		if (customer.getTransactions() == null) {
			// First request
			List<Transaction> transactions = service.listTransactions(customer.getAccountNumber());
			customer.setTransactions(transactions);
		}

		List<Transaction> transactions = customer.getTransactions();
		
		boolean doesTxnsExist = transactions.size() > 0;
		
		if (!doesTxnsExist)
			System.out.println("You have not done any transactions yet.");

		System.out.println("Account No.\t" + "Transaction ID\t" + "Transaction Date\t" + "Transaction Amount\t"
				+ "Transaction Type\t" + "Transaction Description");
		for (int i = 0; i < 10; i++)
			System.out.println(transactions.get(i));
		
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

		boolean doesTxnsExist = transactions.size() > 0;
		
		if (!doesTxnsExist)
			System.out.println("You have not done any transactions yet.");
		
		System.out.println("Account No.\t" + "Transaction ID\t" + "Transaction Date\t" + "Transaction Amount\t"
				+ "Transaction Type\t" + "Transaction Description");
		for (Transaction txn : transactions)
			System.out.println(txn);
		
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
			}
		}
		
		// Validation done
	}

	private static void changeDetails(Customer customer, CustomerService service) throws InternalServerException {
		System.out.println("MODIFYING EXISTING CUSTOMER DETAILS");
		System.out.println(customer);

		int inputChoice = 100;
		boolean isValidInput = false;
		
		while (!isValidInput) {
			try {
				System.out.println("Which detail do you want to change?\n1. Contact Number.\n2. Address.\nInput -1 to go back.");
				inputChoice = scanner.nextInt();
				isValidInput = true;
			} catch (InputMismatchException e) {
				System.out.println("Invalid choice entered");
			}
		}

		boolean backFlag = false;
		while (true) {
			switch (inputChoice) {
			case 1:
				System.out.println("Enter new mobile number: ");
				String newContact = scanner.next();
				
				boolean isContactChanged = service.changeContactNumber(newContact, customer.getAccountNumber());
				if (isContactChanged) {
					customer.setMobileNumber(newContact);
					System.out.println("Contact details updated successfully");
				} else
					System.out.println("Details could not be updated now.\nPlease try again later.");
				break;
				
			case 2:
				System.out.println("Enter new address: ");
				String newAddress = scanner.next();
				
				boolean isAddressChanged = service.changeAddress(newAddress, customer.getAccountNumber());
				if (isAddressChanged) {
					customer.setAddress(newAddress);
					System.out.println("Address details updated successfully");
				} else
					System.out.println("Details could not be updated now.\nPlease try again later.");
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

		// Validations done
	}

	private static void requestChequeBook(Customer customer, CustomerService service)
			throws RequestCannotBeProcessedException, InternalServerException {
		boolean backFlag = false;
		int inputChoice = 100;
		
		while (true) {
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
		}
		
		// Validations done
	}

	private static void trackService(Customer customer, CustomerService service)
			throws NoServicesMadeException, InternalServerException {
		if (customer.getRequests() == null) {
			// First request to view progress
			List<Request> requests = service.getRequests(customer.getAccountNumber());
			customer.setRequests(requests);
		}

		List<Request> requests = customer.getRequests();
		boolean doesRequestExist = requests.size() > 0;
		
		if (!doesRequestExist)
			System.out.println("You have no pending requests");

		for (Request request: requests)
			System.out.println(request);
		
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
			}
		}
		
		// Validations done
	}

	private static void fundTransfer(Customer customer, CustomerService service)
			throws InternalServerException, AccountsNotFoundException {
		boolean backFlag = false;
		while (true) {
			try {
				System.out.println(
						"Please select one of the follworing options: \n1. Transfer funds to your other account.\n2.Tranfer funds to acoount of other customers.\n Press 0 to go back.");
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
			}
		}
		
		// Validations done
	}

	private static void transferAccrossAccounts(Customer customer, CustomerService service)
			throws InternalServerException, AccountsNotFoundException {
		Account otherAccount = service.fetchOtherExistingAccount(customer.getAccountNumber(),
				customer.getAccountType());

		System.out.println("***** FROM ACCOUNT *****");
		System.out.println(customer.getAccountNumber() + "\t" + customer.getName());
		System.out.println("***** TO ACCOUNT *****");
		System.out.println(otherAccount.getAccountNumber() + "\t" + customer.getName());
		
		double transferAmount = 0;

		boolean isValidInput = false;
		while (!isValidInput) {
			try {
				System.out.print("Please enter the amount: ");
				transferAmount = scanner.nextDouble();
				isValidInput = true;
			} catch (InputMismatchException e) {
				System.out.println("Invalid input, please try again.");
			}
		}
		
		System.out.print("Please enter your transaction password: ");
		String txnPwd = scanner.next();
		System.out.print("Enter message for fund transfer: ");
		String txnDesc = scanner.next();

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

		Transaction txnDetails = new Transaction();
		txnDetails.setTransactionAmount(transferAmount);
		txnDetails.setTransactionDescription(txnDesc);

		boolean isTransferred = service.transferFund(customer, otherAccount, txnDetails);
		if (isTransferred)
			System.out.println("Successfully transferred funds.");
		else
			System.out.println("Could not transfer funds at this moment, try again later.");
		return;
		
		// Validations done
	}

	private static void transferAcrossUsers(Customer customer, CustomerService service) throws InternalServerException {
		List<Account> beneficiaries = service.fetchBeneficiaries(customer.getAccountNumber());

		while (true) {
			boolean isBeneficiaryAdded = beneficiaries.size() > 0;

			System.out.println("  Account Number\tNickName");
			for (int index = 1; index <= beneficiaries.size(); index++)
				System.out.println(index + " " + beneficiaries.get(index).getAccountNumber() + "\t"
						+ beneficiaries.get(index).getNickName());
			
			if (!isBeneficiaryAdded)
				System.out.println("No Beneficiaries added. Please add a beneficiary to tranfer money.");

			if (isBeneficiaryAdded)
				System.out.println("Please enter a number in the range 1 - " + beneficiaries.size());
			
			System.out.println("Press 0 to add a beneficiary.\nPress -1 to go back.");
			int inputChoice = scanner.nextInt();

			if (inputChoice == 0) {
				boolean isValidInput = false;
				long accountNumber = 0L;
				
				while (!isValidInput) {
					try {
						System.out.print("Enter beneficiary account number: ");
						accountNumber = scanner.nextLong();
						isValidInput = true;
					} catch (InputMismatchException e) {
						System.out.println("Please enter a valid input.");
					}
				}
				System.out.print("Enter a nick name for beneficiary: ");
				String nickName = scanner.next();
				Account newBeneficiary = new Account();
				newBeneficiary.setAccountNumber(accountNumber);
				newBeneficiary.setNickName(nickName);

				boolean isAdded = service.addNewBeneficiary(customer.getAccountNumber(), newBeneficiary);
				if (isAdded) {
					System.out.println("Beneficiary added.");
					beneficiaries.add(newBeneficiary);
				} else
					System.out.println("Beneficiary could not be added now, try again later");
				
			} else if (inputChoice <= beneficiaries.size()) {
				boolean isValidInput = false;
				double transferAmount = 0;
				
				while (!isValidInput) {
					try {
						System.out.print("Please enter the amount: ");
						transferAmount = scanner.nextDouble();
					} catch (InputMismatchException e) {
						System.out.println("Please enter a valid input.");
					}
				}
				System.out.print("Please enter your transaction password: ");
				String txnPwd = scanner.next();
				System.out.print("Enter message for fund transfer: ");
				String txnDesc = scanner.next();

				double limit = service.getTransactionLimit();

				boolean isValidTxnLimit = transferAmount <= limit;
				boolean isValidTxnAmt = service.validateTransactionAmount(customer, transferAmount);
				boolean isValidPwd = service.checkTransactionPassword(customer, txnPwd);

				if (!isValidTxnAmt) {
					System.out.println("Insufficient funds in your account, or transaction");
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

				boolean isTransferred = service.transferFund(customer, beneficiaries.get(inputChoice), txnDetails);
				if (isTransferred)
					System.out.println("Successfully transferred funds.");
				else
					System.out.println("Could not transfer funds at this moment, try again later.");
			} else if (inputChoice == -1)
				break; // Go to previous page.
			else
				System.out.println("Please enter a valid choice.");
		}
	}

	private static void changePassword(Customer customer, CustomerService service) throws InternalServerException {
		while (true) {
			System.out.println("Please enter old password: ");
			String oldPassword = scanner.next();

			boolean isValidPassword = false;
			
			String newPassword = null;
			
			while (!isValidPassword) {
				System.out.println("Please enter new password: ");
				newPassword = scanner.next();
				
				isValidPassword = BankingSystemService.validatePassword(newPassword);
			}

			System.out.println("Confirm new password: ");
			String confirmPassword = scanner.next();

			if (!oldPassword.equals(customer.getPassword())) {
				System.out.println("Invalid Old password");
				break;
			}
			if (!(confirmPassword.equals(newPassword))) {
				System.out.println("Passwords don't match");
				break;
			}
			boolean result = service.updatePassword(newPassword, customer.getUserId());

			if (result)
				System.out.println("Successfully updated password.");
			else
				System.out.println("Password could not be updated now, try again later");
			
			return;
		}
	}

	private static void viewProfile(Customer customer, CustomerService service) {
		System.out.println(customer);
	}

}