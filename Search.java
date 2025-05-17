package bankingmanagementsystem;

import javax.swing.*;
import java.sql.*;

public class Search {

    // GUI menu for search
    public static void displaySearchMenu() {
        String[] options = {"Search by Account Number", "Search by Customer Name", "Back"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null, "Search Menu", "Choose Option",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (choice == 0) {
                searchByAccountNumber();
            } else if (choice == 1) {
                searchByCustomerName();
            } else {
                break;
            }
        }
    }

    // Search using account number
    private static void searchByAccountNumber() {
        try {
            int accNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Account Number:"));

            Connection conn = DatabaseHandler.connect();
            String sql = "SELECT * FROM Accounts WHERE AccountNumber = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                showResult(rs);
            } else {
                JOptionPane.showMessageDialog(null, "No account found with that number.");
            }

            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input.");
        }
    }

    // Search using customer name
    private static void searchByCustomerName() {
        try {
            String name = JOptionPane.showInputDialog("Enter Customer Name:");

            Connection conn = DatabaseHandler.connect();
            String sql = "SELECT * FROM Accounts WHERE CustomerName LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append("Account No: ").append(rs.getInt("AccountNumber")).append("\n");
                result.append("Name: ").append(rs.getString("CustomerName")).append("\n");
                result.append("Mobile: ").append(rs.getString("MobileNumber")).append("\n");
                result.append("Balance: ").append(rs.getDouble("Balance")).append("\n\n");
            }

            if (result.length() == 0) {
                JOptionPane.showMessageDialog(null, "No accounts found.");
            } else {
                JTextArea textArea = new JTextArea(result.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
                JOptionPane.showMessageDialog(null, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }

            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error occurred.");
        }
    }

    // Helper to show one result
    private static void showResult(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("Account No: ").append(rs.getInt("AccountNumber")).append("\n");
        sb.append("Name: ").append(rs.getString("CustomerName")).append("\n");
        sb.append("Mobile: ").append(rs.getString("MobileNumber")).append("\n");
        sb.append("Balance: ").append(rs.getDouble("Balance")).append("\n");

        JOptionPane.showMessageDialog(null, sb.toString(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
    }
}
