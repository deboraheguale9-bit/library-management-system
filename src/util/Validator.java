package util;

import java.util.regex.Pattern;

public class Validator {

    // Email validation
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    // ISBN validation (10 or 13 digits)
    public static boolean isValidISBN(String isbn) {
        if (isbn == null) return false;
        String cleanISBN = isbn.replaceAll("[\\s-]", "");
        return cleanISBN.matches("\\d{10}|\\d{13}");
    }

    // Phone number validation (basic)
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        // Allow formats: 555-1234, (555) 123-4567, 5551234567
        return phone.matches("^\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}$");
    }

    // Password strength check
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^A-Za-z0-9]").matcher(password).find();

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    // Name validation (letters, spaces, hyphens, apostrophes)
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return name.matches("^[A-Za-zÀ-ÿ-'\\s]{2,50}$");
    }

    // Year validation (reasonable publication year)
    public static boolean isValidYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        return year >= 1000 && year <= currentYear + 1; // Allow next year for pre-orders
    }

    // Positive number validation
    public static boolean isPositive(double number) {
        return number > 0;
    }

    // Non-negative number validation
    public static boolean isNonNegative(double number) {
        return number >= 0;
    }

    // General input validation
    public static boolean validateUserInput(String input, String type) {
        if (input == null || input.trim().isEmpty()) return false;

        return switch (type.toLowerCase()) {
            case "email" -> isValidEmail(input);
            case "isbn" -> isValidISBN(input);
            case "phone" -> isValidPhone(input);
            case "name" -> isValidName(input);
            case "password" -> isStrongPassword(input);
            default -> input.length() <= 255; // Default max length check
        };
    }
}