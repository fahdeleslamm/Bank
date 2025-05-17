package bankingmanagementsystem;

import java.sql.*;
import javax.swing.*;
import java.awt.*;

public class BankGUI {

    private JFrame frame;

    public BankGUI() {
        frame = new JFrame();
        displayMainMenu();
    }

    // Show main menu
    public void displayMainMenu() {
        frame.setTitle("Bank System Main Menu");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        JButton accountBtn = new JButton("Account Management");
        JButton transactionBtn = new JButton("Transaction");
        JButton loanBtn = new JButton("Loan Management");
        JButton transferBtn = new JButton("Transfer");
        JButton searchBtn = new JButton("Search");
        JButton exitBtn = new JButton("Exit");

        panel.add(accountBtn);
        panel.add(transactionBtn);
        panel.add(loanBtn);
        panel.add(transferBtn);
        panel.add(searchBtn);
        panel.add(exitBtn);

        frame.add(panel);

        accountBtn.addActionListener(e -> Account.displayAccountMenu());
        transactionBtn.addActionListener(e -> Transaction.displayTransactionMenu());
        loanBtn.addActionListener(e -> Loan.displayLoanMenu());
        transferBtn.addActionListener(e -> Transfer.transferFunds());
        searchBtn.addActionListener(e -> Search.displaySearchMenu());
        exitBtn.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    public static void displayRecordList() {
        try {
            Connection conn = DatabaseHandler.connect();
            String sql = "SELECT * FROM Accounts";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Account No: ").append(rs.getInt("AccountNumber")).append("\n");
                sb.append("Name: ").append(rs.getString("CustomerName")).append("\n");
                sb.append("Phone: ").append(rs.getString("MobileNumber")).append("\n");
                sb.append("Balance: ").append(rs.getDouble("Balance")).append("\n\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(null, scrollPane, "Account List", JOptionPane.INFORMATION_MESSAGE);

            conn.close();
        } catch (Exception e) {
            System.out.println("error");
            JOptionPane.showMessageDialog(null, "Error displaying account list.");
        }
    }
}
