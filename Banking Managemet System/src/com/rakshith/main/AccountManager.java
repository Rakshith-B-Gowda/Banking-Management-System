package com.rakshith.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {

	private Connection connection;
	private Scanner scanner;

	public AccountManager(Connection connection, Scanner scanner) {
		this.connection = connection;
		this.scanner = scanner;
	}


	public void creditMoney(long accountNumber) throws SQLException {
		scanner.nextLine();
		System.out.println("Enter Amount: ");
		double amount = scanner.nextDouble();
		scanner.nextLine();
		System.out.println("Enter Security Pin: ");
		String security_pin = scanner.nextLine();

		try {
			connection.setAutoCommit(false);
			if (accountNumber != 0) {
				PreparedStatement validationStatement = connection.prepareStatement("SELECT * FROM banking_system.accounts WHERE account_number = ? AND security_pin = ? ") ;
				validationStatement.setLong(1, accountNumber);
				validationStatement.setString(2, security_pin);
				ResultSet resultSet = validationStatement.executeQuery();

				if (resultSet.next()) {
					String credit_query = "UPDATE banking_system.accounts SET balance = balance + ? WHERE account_number = ?";
					PreparedStatement updateStatement = connection.prepareStatement(credit_query);
					updateStatement.setDouble(1, amount);
					updateStatement.setLong(2, accountNumber);
					int rowsAffected = updateStatement.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("₹"+amount+" credited successfully.");
						connection.commit();
						connection.setAutoCommit(true);
						return;
					} else {
						System.out.println("Transaction Failed!");
						connection.rollback();
						connection.setAutoCommit(true);
					}
				} else {
					System.out.println("Invalid Security Pin!");
				}
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connection.setAutoCommit(true);
	}


	public void debitMoney(long accountNumber) throws SQLException {
		scanner.nextLine();
		System.out.println("Enter Amount: ");
		double amount = scanner.nextDouble();
		scanner.nextLine();
		System.out.println("Enter Security Pin: ");
		String security_pin = scanner.nextLine();
		try {
			connection.setAutoCommit(false);
			if (accountNumber != 0) {
				PreparedStatement validationStatement = connection.prepareStatement("SELECT * FROM banking_system.accounts WHERE account_number = ? AND security_pin = ? ") ;
				validationStatement.setLong(1, accountNumber);
				validationStatement.setString(2, security_pin);
				ResultSet resultSet = validationStatement.executeQuery();

				if (resultSet.next()) {
					double current_balance = resultSet.getDouble("balance");
					if (amount <= current_balance) {
						String debit_query = "UPDATE banking_system.accounts SET balance = balance - ? WHERE account_number = ? ";
						PreparedStatement updateStatement = connection.prepareStatement(debit_query);
						updateStatement.setDouble(1, amount);
						updateStatement.setLong(2, accountNumber);
						int rowsAffected = updateStatement.executeUpdate();
						if (rowsAffected > 0) {
							System.out.println("₹"+amount+" debited successfully.");
							connection.commit();
							connection.setAutoCommit(true);
						} else {
							System.out.println("Transaction Failed!");
							connection.rollback();
							connection.setAutoCommit(true);
						}
					} else {
						System.out.println("Insufficient Balance!");
					}
				} else {
					System.out.println("Invalid Pin!");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connection.setAutoCommit(true);
	}


	public void transferMoney(long senderAccountNumber) throws SQLException {
		scanner.nextLine();
		System.out.println("Enter Receiver Account Number: ");
		long receiver_account_number = scanner.nextLong();
		System.out.println("Enter Amount: ");
		double amount = scanner.nextDouble();
		scanner.nextLine();
		System.out.println("Enter Security Pin: ");
		String security_pin = scanner.nextLine();

		try {
			connection.setAutoCommit(false);
			if (senderAccountNumber != 0 && receiver_account_number != 0) {
				PreparedStatement validationStatement = connection.prepareStatement("SELECT * FROM banking_system.accounts WHERE account_number = ? AND security_pin = ? ");
				validationStatement.setLong(1, senderAccountNumber);
				validationStatement.setString(2, security_pin);
				ResultSet resultSet = validationStatement.executeQuery();

				if (resultSet.next()) {
					double current_balance = resultSet.getDouble("balance");

					if (amount <= current_balance) {

						String debit_query = "UPDATE banking_system.accounts SET balance = balance - ? WHERE account_number = ?";
						String credit_query = "UPDATE banking_system.accounts SET balance = balance + ? WHERE account_number = ?";

						PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
						PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);

						debitPreparedStatement.setDouble(1, amount);
						debitPreparedStatement.setLong(2, senderAccountNumber);
						creditPreparedStatement.setDouble(1, amount);
						creditPreparedStatement.setLong(2, receiver_account_number);
						int debitRowsAffected = debitPreparedStatement.executeUpdate();
						int creditRowsAffected = creditPreparedStatement.executeUpdate();
						if (debitRowsAffected > 0 && creditRowsAffected > 0) {
							System.out.println("Transaction Successful.");
							System.out.println("₹"+amount+" transferred successfully");
							connection.commit();
							connection.setAutoCommit(true);
							return;
						} else {
							System.out.println("Transaction Failed!");
							connection.rollback();
							connection.setAutoCommit(true);
						}
					} else {
						System.out.println("Insufficient Balance!");
					}
				} else {
					System.out.println("Invalid Pin!");
				}
			} else {
				System.out.println("Invalid account number!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connection.setAutoCommit(true);
	}


	public void getBalance(long accountNumber) {
		scanner.nextLine();
		System.out.println("Enter Security Pin: ");
		String security_pin = scanner.nextLine();
		try {
			String balance_query = "SELECT balance FROM banking_system.accounts WHERE account_number = ? AND security_pin = ?";
			PreparedStatement statement = connection.prepareStatement(balance_query);
			statement.setLong(1, accountNumber);
			statement.setString(2, security_pin);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				double balance = resultSet.getDouble("balance");
				System.out.println("Balance: "+balance);
			} else {
				System.out.println("Invalid Pin!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
