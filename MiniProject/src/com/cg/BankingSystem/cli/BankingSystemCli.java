/**
 * 
 */
package com.cg.BankingSystem.cli;

import java.util.Scanner;

import com.cg.BankingSystem.dto.AccountType;
import com.cg.BankingSystem.dto.LoginBean;
import com.cg.BankingSystem.dto.SignUp;

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
	 */
	public static void main(String[] args) {
		int userTypeChoice;
		System.out.println("WELCOME TO BANKING SYSTEM!");
		do {
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
				System.out.println("TERMINATED !");
				System.exit(0);
			default:
				System.out.println("Invalid choice entered.\nProvide a valid input.");
			}
		} while (true);
	}

	private static void signIn() {
		System.out.println("***********************************************");
		System.out.println("Sign In: ");
		LoginBean login = new LoginBean();
		String userId;
		String password;
		System.out.print("Enter User ID : ");
		userId = scanner.next();
		System.out.print("Enter your Password : ");
		password = scanner.next();

		login.setUserId(userId);
		login.setPassword(password);

		// authenticate this bean----check the method
		//then call either adminLogin or call customerLogin

	}

	/**
	 * Admin methods
	 */
	
	public static void adminLogin() {
		do {
			int choice;
			System.out.println("***********************************************");
			System.out.println("Enter 1 to create new account");
			System.out.println("Enter 2 to view transactions");
			System.out.println("Enter 0 to exit.");
			System.out.println("***********************************************");
			choice = scanner.nextInt();
			switch (choice) {
			case 1:
				CreateNewAccount();
				break;
			case 2:
				ViewTransactions();
				break;
			case 0:
				System.exit(0); // Exiting the control when 0 is entered.
			default:
				System.out.println("Invalid choice entered"); // Default message when none of 0 - 3 is entered
			}
		} while (true);

	}

	private static void CreateNewAccount() {

		int existinguserchoice;
		System.out.print("Select option : \n 1: New account for existing user \n 2: Create new user");
		existinguserchoice = scanner.nextInt();
		do {
			switch (existinguserchoice) {
			case 1:
				CreateNewAccountForExistingUser();
				break;
			case 2:
				CreateNewUserAccount();
				break;
			default:
				System.out.println("Invalid choice entered");
			}
		} while (existinguserchoice != 1 || existinguserchoice != 2);
	}

	private static void CreateNewAccountForExistingUser() {
		String userId;
		System.out.print("Enter customer userId : ");
		userId = scanner.next();
		
		//find customer with userid and create new account
		
	}

	private static void CreateNewUserAccount() {

		SignUp newUser = new SignUp();
		int accountTypeChoice;

		// long accNo; ---should be auto-generated

		String name;
		String address;
		long mobileNo;
		String email;
		AccountType accType = null;
		int openingBal;

		System.out.print("Enter customer name : ");
		name = scanner.next();
		System.out.print("Enter customer address : ");
		address = scanner.next();
		System.out.print("Enter customer mobile number : ");
		mobileNo = scanner.nextLong();
		System.out.print("Enter customer email : ");
		email = scanner.next();
		System.out.print("Select customer account type : \n 1: for Savings account \n 2: For current account");
		accountTypeChoice = scanner.nextInt();
		do {
			switch (accountTypeChoice) {
			case 1:
				accType = AccountType.SAVINGS_ACCOUNT;
				break;
			case 2:
				accType = AccountType.SAVINGS_ACCOUNT;
				break;
			default:
				System.out.println("Invalid choice entered");
			}
		} while (accountTypeChoice != 1 || accountTypeChoice != 2);
		System.out.print("Enter customer opening balance : ");
		openingBal = scanner.nextInt();

		newUser.setAccType(accType);
		newUser.setAddress(address);
		newUser.setEmail(email);
		newUser.setMobileNo(mobileNo);
		newUser.setName(name);
		newUser.setOpeningBal(openingBal);
	}

	private static void ViewTransactions() {

		//view transactions from past dates
	}
	
	
	/**
	 * Client methods
	 */
	
	public static void customerLogin() {
		do {
			int customerChoice;
			System.out.println("***********************************************");
			System.out.println("Enter 1 to view statement");
			System.out.println("Enter 2 to change personal details");
			System.out.println("Enter 3 for cheque book service request");
			System.out.println("Enter 4 to track service request");
			System.out.println("Enter 5 for fund transfer");
			System.out.println("Enter 6 to change password");
			System.out.println("Enter 0 to exit.");
			System.out.println("***********************************************");
			customerChoice = scanner.nextInt();
			switch (customerChoice) {
			case 1:
				ViewStatement();
				break;
			case 2:
				ChangeDetails();
				break;
			case 3:
				RequestChequeBook();
				break;
			case 4:
				TrackService();
				break;
			case 5:
				FundTransfer();
				break;
			case 6:
				ChangePassword();
				break;
			case 0:
				System.exit(0); // Exiting the control when 0 is entered.
			default:
				System.out.println("Invalid choice entered"); // 
			}
		} while (true);

	}

	private static void ViewStatement() {
		int statementChoice;
		System.out.println("***********************************************");
		System.out.println("Enter 1 to view mini-statement");
		System.out.println("Enter 2 to view detailed-statement");
		System.out.println("***********************************************");
		statementChoice = scanner.nextInt();
		do {
			switch (statementChoice) {
			case 1:
				ViewMiniStatement();
				break;
			case 2:
				ViewDetailedStatement();
				break;
			case 0:
				System.exit(0); // Exiting the control when 0 is entered.
			default:
				System.out.println("Invalid choice entered"); // 
			}
		} while (true);
		
	}

	private static void ViewMiniStatement() {
		// same as view transactions of admin --last 10 trans
		
	}
	
	private static void ViewDetailedStatement() {
		// same as view transactions of admin --should be able to view all accounts transactions
	}

	private static void ChangeDetails() {

		// displays current details
		
		System.out.println("MODIFYING EXISTING CUSTOMER DETAILS");
		String custId = "";
		do {
			System.out.print("Enter Employee ID : ");
			custId = scanner.next();
		} while (/**validate custid*/);
		
		
	}

	private static void RequestChequeBook() {
		// TODO Auto-generated method stub
		
	}

	private static void TrackService() {
		// TODO Auto-generated method stub
		
	}

	private static void FundTransfer() {
		// TODO Auto-generated method stub
		
	}

	private static void ChangePassword() {
		// TODO Auto-generated method stub
		
	}
	

}