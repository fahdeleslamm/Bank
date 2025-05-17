package bankingmanagementsystem;
public class Validation {
    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z\\s]+");
    }
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10,15}");
    }
    public static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }
    public static boolean isPositive(double value) {
        return value > 0;
    }
}
