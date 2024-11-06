package com.rakshith.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Accounts {

	private Connection connection;
	private Scanner scanner;
	
	public Accounts(Connection connection, Scanner scanner) {
		this.connection = connection;
		this.scanner = scanner;
	}

	
	public long openAccount(String email) {
		if (!accountExists(email)) {
			String open_account_query = "INSERT INTO banking_system.accounts(account_number, full_name, email, balance, security_pin) VALUES(? , ? , ? , ?, ?)" ;
			scanner.nextLine();
			System.out.println("Enter Full Name: ");
			String full_name = scanner.nextLine();
			System.out.println("Enter Initial Amount: ");
			double balance = scanner.nextDouble();
			scanner.nextLine();
			System.out.println("Enter Security PIN: ");
			String security_pin = scanner.nextLine();
			
			try(PreparedStatement statement = connection.prepareStatement(open_account_query)) {
				long account_number = generateAccountNumber();
				statement.setLong(1, account_number);
				statement.setString(2, full_name);
				statement.setString(3, email);
				statement.setDouble(4, balance);
				statement.setString(5, security_pin);
				int rowsAffected = statement.executeUpdate();
				if (rowsAffected > 0) {
					return account_number;
				} else {
					throw new RuntimeException("Account Creation Failed.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("Account Already Exist.");
	}
	
	
	public long getAccountNumber(String email) {
		String query = "SELECT account_number FROM banking_system.accounts WHERE email = ?";		
		try(PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, email);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getLong("account_number");
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Account Number does not exist.");
	}
	
	
	public long generateAccountNumber() {
		try(Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT account_number FROM banking_system.accounts ORDER BY account_number DESC LIMIT 1")	) 
		{
			if (resultSet.next()) {
				long last_account_number = resultSet.getLong("account_number");
				return last_account_number + 1;	
			} else {
				return 10000100;
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 10000100;
	}
	
	
	public boolean accountExists(String email) {
		String query = "SELECT account_number FROM banking_system.accounts WHERE email = ? ";
		try(PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, email);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
