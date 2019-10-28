/**
 * 
 */
package com.cg.BankingSystem.cli;

import java.io.ObjectInputStream.GetField;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.Admin;
import com.cg.BankingSystem.dto.Customer;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.Request;
import com.cg.BankingSystem.dto.SignUp;
import com.cg.BankingSystem.dto.Transaction;
import com.cg.BankingSystem.exception.AccountNotCreatedException;
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
	 */
	public static void main(String[] args)
			throws InvalidCredentialsException, InternalServerException, AccountNotCreatedException, 
			NoTransactionsExistException, RequestCannotBeProcessedException, NoServicesMadeException, UserNotFoundException {
		System.out.println("WELCOME TO BANKING SYSTEM!");
		
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

	private static void signIn()
			throws InvalidCredentialsException, InternalServerException, AccountNotCreatedException, 
			NoTransactionsExistException, RequestCannotBeProcessedException, NoServicesMadeException, UserNotFoundException {
		System.out.println("***********************************************");
		System.out.println("***** Enter Your Credentials *****");
		
		LoginBean login = new LoginBean();
		
		System.out.print("User ID : ");
		login.setUserId(scanner.next());
		
		System.out.print("Password : ");
		login.setPassword(scanner.next());
		
		// Validate values before logging in
		
		if (login.getUserId().contains("AD")) {
			AdminService adminService = (AdminService) BankingSystemService.getInstance(login);
			
			Admin adminLogin = (Admin) adminService.authenticateUser(login);
			System.out.println("Logged in successfully as Admin. Welcome " + adminLogin.getUserName());
			
			onAdminLogin(adminService);
		} 
		else {
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

	public static void onAdminLogin(AdminService service) throws AccountNotCreatedException, InternalServerException, NoTransactionsExistException, UserNotFoundException {
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
				System.out.println("Invalid choice entered. Please enter a valid choice"); // Default message when none of 0 - 3 is entered
			}
		}

	}

	private static void createNewAccount(AdminService service) throws AccountNotCreatedException, InternalServerException, UserNotFoundException {
		boolean backFlag = false;
		while (true) {
			System.out.println("Select option : \n1: Create new account for existing user. \n2: Create new account for new user. \n3: Back.");
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

	private static void createNewAccountForExistingUser(AdminService service) throws InternalServerException, UserNotFoundException {
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
		
		if (isCreated)
			System.out.println("Account created successfully");
		else
			System.out.println("Account could not be created now.\\nPlease try again later.");
	}

	private static void createNewUserAccount(AdminService service) throws AccountNotCreatedException, InternalServerException {

		SignUp newSignUp = new SignUp();
		AccountType accType = null;

		System.out.print("Enter customer name : ");
		String name = scanner.next();
		
		System.out.print("Enter customer address : ");
		String address = scanner.next();
		
		System.out.print("Enter customer mobile number : ");
		String mobileNo = scanner.next();
		
		System.out.print("Enter customer email : ");
		String email = scanner.next();
		
		System.out.print("Select customer account type : \n 1: for Savings account \n 2: For current account");
		int accountTypeChoice = scanner.nextInt();
		while (true) {
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
		
		System.out.print("Enter customer opening balance : ");
		double openingBal = scanner.nextDouble();
		
		System.out.print("Enter customer PAN no. : ");
		String panCardNumber = scanner.next();
		
		System.out.print("Enter customer user ID : ");
		String userId = scanner.next();
		
		System.out.print("Enter customer password : ");
		String password = scanner.next();
		
		System.out.print("Enter customer transaction password : ");
		String transactionPassword = scanner.next();

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
	}

	private static void viewTransactions(AdminService service) throws NoTransactionsExistException, InternalServerException {
		System.out.print("Enter Customer's Account Number : ");
		long accountNumber = scanner.nextLong();
		
		List<Transaction> transactions = service.listTransactions(accountNumber);
		
		System.out.println("List of transactions --");
		
		System.out.println("Account No.\t" + "Transaction ID\t" + "Transaction Date\t" + "Transaction Amount\t"
				+ "Transaction Type\t" + "Transaction Description");
		
		for (Transaction txn : transactions)
			System.out.println(txn);
	}

	/**
	 * Client methods
	 * @throws InternalServerException 
	 * @throws NoTransactionsExistException 
	 * @throws RequestCannotBeProcessedException 
	 * @throws NoServicesMadeException 
	 */

	public static void onCustomerLogin(Customer customer, CustomerService service) throws NoTransactionsExistException, InternalServerException, RequestCannotBeProcessedException, NoServicesMadeException {
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
				viewProfile(customer,service);
				break;
			case 0:
				System.exit(0); // Exiting the control when 0 is entered.
			default:
				System.out.println("Invalid choice entered"); //
			}
		}

	}

	private static void viewStatement(Customer customer, CustomerService service) throws NoTransactionsExistException, InternalServerException {
		int statementChoice;
		System.out.println("***********************************************");
		System.out.println("Enter 1 to view mini-statement");
		System.out.println("Enter 2 to view detailed-statement");
		System.out.println("***********************************************");
		statementChoice = scanner.nextInt();
		while (true){
			switch (statementChoice) {
			case 1:
				viewMiniStatement(customer, service);
				break;
			case 2:
				viewDetailedStatement(customer,service);
				break;
			case 3:
				System.exit(0); // Write code to go back.
			default:
				System.out.println("Invalid choice entered"); //
			}
		}
		
	}

	private static void viewMiniStatement(Customer customer, CustomerService service) throws NoTransactionsExistException, InternalServerException {
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
	}

	private static void viewDetailedStatement(Customer customer, CustomerService service) throws NoTransactionsExistException, InternalServerException {
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
	}

	private static void changeDetails(Customer customer, CustomerService service) throws InternalServerException {

		System.out.println("MODIFYING EXISTING CUSTOMER DETAILS");
		System.out.println(customer);
		
		System.out.println("Which detail do you want to change?\n1. Contact Number.\n2. Address.\nInput -1 to go back.");
		int inputChoice = scanner.nextInt();
		
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
					System.out.println("Details could not be updated now.\\nPlease try again later.");
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
			if (backFlag) break;
		}
		
	}

	private static void requestChequeBook(Customer customer, CustomerService service) throws RequestCannotBeProcessedException, InternalServerException {
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
				break;
			case 2:
				backFlag = true;
				break;
			default:
				System.out.println("Invalid choice entered.");
				break;
			}
			if (backFlag) break;
		}
	}

	private static void trackService(Customer customer, CustomerService service) throws NoServicesMadeException, InternalServerException {
		// TODO Auto-generated method stub
		if (customer.getRequests() == null) {
			// First request to view progress
			List<Request> requests = service.getRequests(customer.getAccountNumber());
			customer.setRequests(requests);
		}
		
		List<Request> requests = customer.getRequests();
		
		for (Request request: requests) 
			System.out.println(request);
	}

	private static void fundTransfer(Customer customer, CustomerService service) {
		// TODO Auto-generated method stub

	}

	private static void changePassword(Customer customer, CustomerService service) throws InternalServerException {
		while (true) {
			System.out.println("Please enter old password: ");
			String oldPassword = scanner.next();
			
			System.out.println("Please enter new password: ");
			String newPassword = scanner.next();
			
			System.out.println("Confirm new password: ");
			String confirmPassword = scanner.next();
			
			if (!oldPassword.equals(customer.getPassword()) ) {
				System.out.println("Invalid Old password");
				break;
			}
			if (! (confirmPassword.equals(newPassword))) {
				System.out.println("Passwords don't match");
				break;
			}
			boolean result = service.updatePassword(newPassword, customer.getUserId());
			
			if (result)
				System.out.println("Successfully updated password.");
			else
				System.out.println("Password could not be updated now, try again later");
		}
	}
	
	private static void viewProfile(Customer customer, CustomerService service) {
		System.out.println(customer);
	}

}