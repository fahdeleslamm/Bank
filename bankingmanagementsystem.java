package bankingmanagementsystem;

import javax.swing.*;
import java.sql.*;


public class bankingmanagementsystem {
    public static void main(String[] args) {
        Connection conn = DatabaseHandler.connect();
        if (conn != null) {
            System.out.println("Database connection successful.");
        } else {
            System.out.println("Failed to connect to database.");
            System.exit(1);
        }


        SwingUtilities.invokeLater(() -> new BankGUI()); //el window mesh byban
    }
}
