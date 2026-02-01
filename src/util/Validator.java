package util;

import java.util.regex.Pattern;

public class Validator {

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public static boolean isValidISBN(String isbn) {
        if (isbn == null) return false;
        String cleanISBN = isbn.replaceAll("[\\s-]", "");
        return cleanISBN.matches("\\d{10}|\\d{13}");
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        // Allow formats: 555-1234, (555) 123-4567, 5551234567
        return phone.matches("^\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}$");
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^A-Za-z0-9]").matcher(password).find();

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return name.matches("^[A-Za-zÀ-ÿ-'\\s]{2,50}$");
    }

    public static boolean isValidYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        return year >= 1000 && year <= currentYear + 1;
    }

    public static boolean isPositive(double number) {
        return number > 0;
    }

    public static boolean isNonNegative(double number) {
        return number >= 0;
    }

    public static boolean validateUserInput(String input, String type) {
        if (input == null || input.trim().isEmpty()) return false;

        return switch (type.toLowerCase()) {
            case "email" -> isValidEmail(input);
            case "isbn" -> isValidISBN(input);
            case "phone" -> isValidPhone(input);
            case "name" -> isValidName(input);
            case "password" -> isStrongPassword(input);
            default -> input.length() <= 255;
        };
    }
}