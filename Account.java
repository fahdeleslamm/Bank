package bankingmanagementsystem;

import javax.swing.*;
import java.sql.*;

public class Account {

    private int accountNumber;
    private String customerName;
    private String mobileNumber;
    private double balance;


    public Account(int accNo, String name, String mobile, double bal) {
        this.accountNumber = accNo;
        this.customerName = name;
        this.mobileNumber = mobile;
        this.balance = bal;
    }


    public void saveToDatabase() {
        try {
            Connection conn = DatabaseHandler.connect();
            String sql = "INSERT INTO Accounts (AccountNumber, CustomerName, MobileNumber, Balance) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountNumber);
            stmt.setString(2, customerName);
            stmt.setString(3, mobileNumber);
            stmt.setDouble(4, balance);
            stmt.executeUpdate();
            conn.close();
            JOptionPane.showMessageDialog(null, "Account created successfully");
        } catch (SQLException e) {
            System.out.println("Error");
        }
    }

    public static void displayAccounts() {
        try {
            Connection conn = DatabaseHandler.connect();
            String sql = "SELECT * FROM Accounts";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                sb.append(String.format(
                        "Account No: %d\nName: %s\nMobile: %s\nBalance: %.2f\n\n",
                        rs.getInt("AccountNumber"),
                        rs.getString("CustomerName"),
                        rs.getString("MobileNumber"),
                        rs.getDouble("Balance")
                ));
            }

            JTextArea textArea = new JTextArea(sb.toString());   // takes the info of the account and puts it in a panel with a scroll wheel
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
            JOptionPane.showMessageDialog(null, scrollPane, "All Accounts", JOptionPane.INFORMATION_MESSAGE);
            conn.close();
        } catch (SQLException e) {
            System.out.println("Display error");
        }
    }

    public static void displayAccountMenu() {
        String[] options = {"Create Account", "Display Accounts", "Deposit", "Withdraw", "Close Account", "Back"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null, "Account Management", "Choose Option",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (choice == 0) {
                createAccountFromGUI();
            } else if (choice == 1) {
                displayAccounts();
            } else if (choice == 2) {
                depositToAccount();
            } else if (choice == 3) {
                withdrawFromAccount();
            } else if (choice == 4) {
                closeAccount();
            } else {
                break;
            }
        }
    }

    private static void createAccountFromGUI() {
        try {
            int accNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Account Number"));
            String name = JOptionPane.showInputDialog("Enter Customer Name");
            String mobile = JOptionPane.showInputDialog("Enter Mobile Number");
            double balance = Double.parseDouble(JOptionPane.showInputDialog("Enter Initial Balance"));

            Account acc = new Account(accNo, name, mobile, balance);
            acc.saveToDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input");
        }
    }

    private static void depositToAccount() {
        try {
            int accNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Account Number:"));
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter Deposit Amount:"));

            Connection conn = DatabaseHandler.connect();
            String sql = "UPDATE accounts SET Balance = Balance + ? WHERE AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, amount);
            stmt.setInt(2, accNo);
            int rows = stmt.executeUpdate();

            if (rows > 0)
                JOptionPane.showMessageDialog(null, "Deposit successful");
            else
                JOptionPane.showMessageDialog(null, "Account not found");

            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input");
        }
    }


    private static void withdrawFromAccount() {
        try {
            int accNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Account Number:"));
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter Withdrawal Amount:"));

            Connection conn = DatabaseHandler.connect();
            String checkSQL = "SELECT Balance FROM accounts WHERE AccountNumber = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, accNo);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("Balance");
                if (currentBalance >= amount) {
                    String withdrawSQL = "UPDATE accounts SET Balance = Balance - ? WHERE AccountNumber = ?";
                    PreparedStatement stmt = conn.prepareStatement(withdrawSQL);
                    stmt.setDouble(1, amount);
                    stmt.setInt(2, accNo);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Withdrawal successful");
                } else {
                    JOptionPane.showMessageDialog(null, "Insufficient balance");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Account not found");
            }
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input");
        }
    }


    private static void closeAccount() {
        try {
            String input = JOptionPane.showInputDialog("Enter account Number to close:");

            if (input == null || input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cancelled or no input provided.");
                return;
            }

            int accNo = Integer.parseInt(input.trim());

            Connection conn = DatabaseHandler.connect();
            String sql = "DELETE FROM accounts WHERE AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accNo);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Account closed successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Account not found.");
            }

            conn.close();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid numeric account number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while closing the account.");
            e.printStackTrace(); // Optional for debugging
        }
    }}


