package bankingmanagementsystem;

import javax.swing.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private int transactionID;
    private int accountNumber;
    private double amount;
    private String type;
    private String date;


    public Transaction(int accountNumber, double amount, String type) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = type;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void saveToDatabase() {
        try {
            Connection conn = DatabaseHandler.connect();

            // 1. Insert the transaction into the Transactions table
            String insertSQL = "INSERT INTO Transactions (AccountNumber, Amount, Type, Date) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
            insertStmt.setInt(1, accountNumber);
            insertStmt.setDouble(2, amount);
            insertStmt.setString(3, type);
            insertStmt.setString(4, date);
            insertStmt.executeUpdate();

            // 2. Update the account balance
            String updateSQL;
            if (type.equals("Deposit")) {
                updateSQL = "UPDATE Accounts SET Balance = Balance + ? WHERE AccountNumber = ?";
            } else {
                updateSQL = "UPDATE Accounts SET Balance = Balance - ? WHERE AccountNumber = ?";
            }

            PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, accountNumber);
            updateStmt.executeUpdate();

            conn.close();
            JOptionPane.showMessageDialog(null, type + " successful");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Transaction failed. Please check account number.");
            e.printStackTrace(); // optional for debugging
        }
    }


    public static void displayTransactionMenu() {
        String[] options = {"Deposit", "Withdraw", "Search Transactions", "Back"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Transaction Menu",
                    "Choose Option",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                handleTransaction("Deposit");
            } else if (choice == 1) {
                handleTransaction("Withdraw");
            } else if (choice == 2) {
                searchTransactions();
            } else {
                break;
            }
        }
    }

    private static void handleTransaction(String type) {
        try {
            int accNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Account Number"));
            double amt = Double.parseDouble(JOptionPane.showInputDialog("Enter Amount"));

            if (amt <= 0) {
                JOptionPane.showMessageDialog(null, "Amount must be positive");
                return;
            }

            if (type.equals("Withdraw") && !hasSufficientBalance(accNo, amt)) {
                JOptionPane.showMessageDialog(null, "Insufficient balance");
                return;
            }

            Transaction txn = new Transaction(accNo, amt, type);
            txn.saveToDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input, please try again");
        }
    }

    private static boolean hasSufficientBalance(int accNo, double amt) {
        try {
            Connection conn = DatabaseHandler.connect();
            String sql = "SELECT Balance FROM Accounts WHERE AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("Balance");
                conn.close();
                return balance >= amt;
            }
        } catch (SQLException e) {
            System.out.println("Error");
        }
        return false;
    }


    private static void searchTransactions() {
        try {
            int accNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Account Number to Search Transactions:"));

            Connection conn = DatabaseHandler.connect();
            String sql = "SELECT * FROM Transactions WHERE AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accNo);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Transaction ID: ").append(rs.getInt("ID")).append("\n");
                sb.append("Type: ").append(rs.getString("Type")).append("\n");
                sb.append("Amount: ").append(rs.getDouble("Amount")).append("\n");
                sb.append("Date: ").append(rs.getString("Date")).append("\n\n");
            }

            if (sb.length() == 0) {
                JOptionPane.showMessageDialog(null, "No transactions found for this account.");
            } else {
                JTextArea textArea = new JTextArea(sb.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
                JOptionPane.showMessageDialog(null, scrollPane, "Transactions", JOptionPane.INFORMATION_MESSAGE);
            }

            conn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input or error during search.");
        }
    }
}
