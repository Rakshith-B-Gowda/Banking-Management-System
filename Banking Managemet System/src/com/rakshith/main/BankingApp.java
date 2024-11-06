package com.rakshith.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {

 	private static final String url = "jdbc:mysql://localhost:3306";
 	private static final String user = "root";
 	private static final String password = "tiger";
	
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Drivers Loaded Successfully.");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver Failed to Load!");
			e.printStackTrace();
		}
		
		try(Connection connection = DriverManager.getConnection(url, user, password); 
			Scanner scanner = new Scanner(System.in)) 
		{                  
			System.out.println("Connection Established!!");
			Accounts accounts = new Accounts(connection, scanner);
			User user = new User(connection, scanner);
			AccountManager accountManager = new AccountManager(connection, scanner);
			
			String email;
			long account_number;
			
			while (true) {
				System.out.println("*** WELCOME TO BANKING SYSTEM ***");
				System.out.println();
				System.out.println("1. Register");
				System.out.println("2. Login");
				System.out.println("3. Exit");
				System.out.println("Enter your choice: ");
				int choice = scanner.nextInt();
				switch (choice) {
				case 1:
					user.register();
					break;
				
				case 2:
					email = user.login();
					if (email != null) {
						System.out.println();
						System.out.println("User Logged In.");
						if (!accounts.accountExists(email)) {
							System.out.println();
							System.out.println("1. Open a new Bank Account.");
							System.out.println("2. Exit");
							if (scanner.nextInt() == 1) {
								account_number = accounts.openAccount(email);
								System.out.println("Account Created Successfully.");
								System.out.println("Your Account Number is: "+account_number);
							} else {
								break;
							}
						}  
						
						account_number = accounts.getAccountNumber(email);
						int choice2 = 0;
						while (choice2 != 5) {
							System.out.println();
							System.out.println("1. Debit Money.");
							System.out.println("2. Credit Money.");
							System.out.println("3. Transfer Money.");
							System.out.println("4. Check Balance");
							System.out.println("5. Log Out.");
							System.out.println("Enter your choice: ");
							choice2 = scanner.nextInt();
							switch (choice2) {
							case 1:
								accountManager.debitMoney(account_number);
								break;
							
							case 2:
								accountManager.creditMoney(account_number);
								break;
								
							case 3:
								accountManager.transferMoney(account_number);
								break;
								
							case 4:
								accountManager.getBalance(account_number);
								break;
								
							case 5:
								break;
							default:
								System.out.println("Enter Valid Choice!");
								break;
							}
						}
					} else {
						System.out.println("Incorrect Email or Password!");
					} 
					break;
					
				case 3:
					System.out.println("THANK YOU FOR USING BANKING SYSTEM!!");
					System.out.println("Exiting System!");
					return;
					
				default:
					System.out.println("Enter Valid Choice!");
					break;
				}	
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

















