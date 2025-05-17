package bankingmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {

    // Database connection info
    static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb"; // make sure database name matches
    static final String USER = "root";
    static final String PASS = ""; // empty for XAMPP's default setup

    // Method to connect to the database
    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }
}
