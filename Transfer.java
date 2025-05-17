package bankingmanagementsystem;

import javax.swing.*;
import java.sql.*;

public class Transfer {

    public static void transferFunds() {
        try {
            int fromAcc = Integer.parseInt(JOptionPane.showInputDialog("Enter Sender Account Number:"));
            int toAcc = Integer.parseInt(JOptionPane.showInputDialog("Enter Receiver Account Number:"));
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter Amount to Transfer:"));

            if (fromAcc == toAcc) {
                JOptionPane.showMessageDialog(null, "Sender and receiver cannot be the same.");
                return;
            }

            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Amount must be greater than zero.");
                return;
            }

            if (!accountExists(fromAcc) || !accountExists(toAcc)) {
                JOptionPane.showMessageDialog(null, "One or both accounts do not exist.");
                return;
            }

            if (!hasSufficientBalance(fromAcc, amount)) {
                JOptionPane.showMessageDialog(null, "Sender does not have sufficient balance.");
                return;
            }

            Connection conn = DatabaseHandler.connect();
            conn.setAutoCommit(false); // Begin transaction

            // Withdraw from sender
            String withdrawSQL = "UPDATE Accounts SET Balance = Balance - ? WHERE AccountNumber = ?";
            PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSQL);
            withdrawStmt.setDouble(1, amount);
            withdrawStmt.setInt(2, fromAcc);
            withdrawStmt.executeUpdate();

            // Deposit to receiver
            String depositSQL = "UPDATE Accounts SET Balance = Balance + ? WHERE AccountNumber = ?";
            PreparedStatement depositStmt = conn.prepareStatement(depositSQL);
            depositStmt.setDouble(1, amount);
            depositStmt.setInt(2, toAcc);
            depositStmt.executeUpdate();

            // Record the transfer
            String transferSQL = "INSERT INTO Transfers (FromAccount, ToAccount, Amount, Date) VALUES (?, ?, ?, NOW())";
            PreparedStatement transferStmt = conn.prepareStatement(transferSQL);
            transferStmt.setInt(1, fromAcc);
            transferStmt.setInt(2, toAcc);
            transferStmt.setDouble(3, amount);
            transferStmt.executeUpdate();

            // Commit transaction
            conn.commit();
            conn.close();

            JOptionPane.showMessageDialog(null, "Transfer successful.");
        } catch (Exception e) {
            try {
                Connection conn = DatabaseHandler.connect();
                conn.rollback(); // Undo changes if something fails
                conn.close();
            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }

            JOptionPane.showMessageDialog(null, "Transfer failed. Please check account numbers.");
            e.printStackTrace();
        }
    }

    // Check if account exists
    private static boolean accountExists(int accNo) {
        try {
            Connection conn = DatabaseHandler.connect();
            String sql = "SELECT 1 FROM Accounts WHERE AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accNo);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();
            conn.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if sender has enough balance
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
            e.printStackTrace();
        }
        return false;
    }
}
