package com.rakshith.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {

	private Connection connection;
	private Scanner scanner;
	
	public User(Connection connection, Scanner scanner) {
		this.connection = connection;
		this.scanner = scanner;
	}

	public void register() {
		scanner.nextLine();
        System.out.print("Full Name: ");
        String full_name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (userExists(email)) {
			System.out.println("User Exists!");
        	return;
		}
        
        String register_query = "INSERT INTO banking_system.user(full_name, email, password) VALUES(? , ? , ?)";
        
        try(PreparedStatement statement = connection.prepareStatement(register_query)) {
			statement.setString(1, full_name);
			statement.setString(2, email);
			statement.setString(3, password);
			int affectedRows = statement.executeUpdate();
			if (affectedRows > 0) {
				System.out.println("Registration Successful.");
			} else {
				System.out.println("Registration Failed.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	
	public String login() {
		scanner.nextLine();
		System.out.println("Email: ");
		String email = scanner.nextLine();
		System.out.println("Password: ");
		String password = scanner.nextLine();
		String login_query = "SELECT * FROM banking_system.user WHERE email = ? AND password = ?";
		try(PreparedStatement statement = connection.prepareStatement(login_query)) {
			statement.setString(1, email);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return email;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	public boolean userExists(String email) {
		String query = "SELECT * FROM banking_system.user WHERE email = ?";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
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
