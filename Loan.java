package bankingmanagementsystem;

import javax.swing.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Loan {
    private int loanID;
    private int accountNumber;
    private double loanAmount;
    private int termMonths;
    private double interestRate;
    private String date;

    public Loan(int accountNumber, double loanAmount, double interestRate) {
        this.accountNumber = accountNumber;
        this.loanAmount = loanAmount;
        this.termMonths = 12; // fixed
        this.interestRate = interestRate;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public void saveToDatabase() {
        try {
            Connection conn = DatabaseHandler.connect();

            String sql = "INSERT INTO Loans (AccountNumber, LoanAmount, TermMonths, InterestRate, DateIssued) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, accountNumber);
            stmt.setDouble(2, loanAmount);
            stmt.setInt(3, termMonths); // always 12
            stmt.setDouble(4, interestRate);
            stmt.setString(5, date);
            stmt.executeUpdate();

            String updateBalanceSQL = "UPDATE Accounts SET Balance = Balance + ? WHERE AccountNumber = ?";
            PreparedStatement Stmt = conn.prepareStatement(updateBalanceSQL);
            Stmt.setDouble(1, loanAmount);
            Stmt.setInt(2, accountNumber);
            Stmt.executeUpdate();

            conn.close();

            JOptionPane.showMessageDialog(null, "Loan applied and balance updated successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to save loan");
            System.out.println("Error");
        }
    }



    public static void displayLoanMenu() {
        String[] options = {"Apply for Loan", "Back"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null, "Loan Menu", "Choose Option",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (choice == 0) {
                applyForLoan();
            } else {
                break;
            }
        }
    }


    private static void applyForLoan() {
        try {
            int accNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Account Number"));
            double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter Loan Amount"));
            double interest = Double.parseDouble(JOptionPane.showInputDialog("Enter Interest Rate (%)"));

            if (amount <= 0 || interest <= 0) {
                JOptionPane.showMessageDialog(null, "All values must be positive");
                return;
            }

            Loan loan = new Loan(accNo, amount, interest);
            loan.saveToDatabase();

            double monthlyInstallment = calculateInstallment(amount, interest, 12);
            JOptionPane.showMessageDialog(null, "Estimated Monthly Payment " + String.format("%.2f", monthlyInstallment));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input");
        }
    }


    private static double calculateInstallment(double principal, double rate, double months) {
        double total = principal + (principal * rate * months / (100 * 12));
        return total / months;
    }
}
