package bankingmanagementsystem;

public class Validation {

    // Check if a string is a valid name (letters and spaces only)
    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z\\s]+");
    }

    // Check if a string is a valid phone number (digits only, typical length 10-15)
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10,15}");
    }

    // Check if a string is a valid number (used before parsing to int/double)
    public static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

    // Check if a value is positive
    public static boolean isPositive(double value) {
        return value > 0;
    }
}
