package model;

import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Abstract User class for Library Management System
 * Implements SHA-256 password hashing with salt for security
 */
public abstract class User {
    // User attributes
    private String id;
    private String name;
    private String email;
    private String mobile;
    private String username;
    private String passwordHash;  // Stores "salt:hashedPassword" in Base64
    private UserRole role;
    private boolean isActive;

    // ================= CONSTRUCTOR =================
    /**
     * Creates a new User with hashed password
     * @param id Unique user ID
     * @param name Full name
     * @param email Email address
     * @param mobile Phone number
     * @param username Login username
     * @param plainPassword Plain text password (will be hashed)
     * @param role User role (ADMIN, LIBRARIAN, MEMBER)
     */
    public User(String id, String name, String email, String mobile,
                String username, String plainPassword, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.username = username;
        this.passwordHash = hashPassword(plainPassword);  // Hash on creation
        this.role = role;
        this.isActive = true;  // New users are active by default
    }

    // ================= AUTHENTICATION METHODS =================

    /**
     * Attempts to log in the user
     * @param passwordAttempt Plain text password attempt
     * @return true if login successful, false otherwise
     */
    public boolean login(String passwordAttempt) {
        if (!isActive) {
            return false;  // Inactive users cannot log in
        }
        return verifyPassword(passwordAttempt, passwordHash);
    }

    /**
     * Logs out the user (basic implementation)
     */
    public void logout() {
        System.out.println(name + " logged out.");
    }

    /**
     * Changes the user's password
     * @param newPlainPassword New plain text password
     */
    public void changePassword(String newPlainPassword) {
        this.passwordHash = hashPassword(newPlainPassword);
    }

    // ================= SHA-256 PASSWORD HASHING =================

    /**
     * Hashes a password with SHA-256 and random salt
     * @param plainPassword Plain text password to hash
     * @return String in format "salt:hash" (both Base64 encoded)
     */
    private String hashPassword(String plainPassword) {
        try {
            // Generate cryptographically secure random salt (16 bytes = 128 bits)
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Create SHA-256 hash of (salt + password)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);  // Add salt first
            byte[] hashedBytes = digest.digest(plainPassword.getBytes());

            // Convert to Base64 strings and combine as "salt:hash"
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);

            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 should always be available, but handle just in case
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a password attempt against the stored hash
     * @param passwordAttempt Plain text password attempt
     * @param storedHash Stored hash in "salt:hash" format
     * @return true if password matches, false otherwise
     */
    private boolean verifyPassword(String passwordAttempt, String storedHash) {
        try {
            // Split the stored hash into salt and hash parts
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;  // Invalid format
            }

            // Decode Base64 strings back to bytes
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);

            // Hash the attempt with the same salt
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] attemptHashBytes = digest.digest(passwordAttempt.getBytes());

            // Compare hashes using constant-time comparison
            return MessageDigest.isEqual(storedHashBytes, attemptHashBytes);

        } catch (Exception e) {
            // If anything goes wrong (wrong format, decoding error, etc.)
            return false;
        }
    }

    // ================= USER PROFILE METHOD =================

    /**
     * Returns user profile as a Map (useful for JSON serialization or UI display)
     * @return Map containing all user attributes except password
     */
    public Map<String, Object> getProfile() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", id);
        profile.put("name", name);
        profile.put("email", email);
        profile.put("mobile", mobile);
        profile.put("username", username);
        profile.put("role", role);
        profile.put("isActive", isActive);
        return profile;
    }

    // ================= GETTERS AND SETTERS =================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /**
     * Gets the password hash (for debugging or migration purposes only)
     * @return The hashed password in "salt:hash" format
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Sets password hash directly (use only for loading from storage)
     * @param passwordHash Already hashed password in "salt:hash" format
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // ================= STRING REPRESENTATION =================

    /**
     * Returns a readable string representation of the user
     * @return String in format "Name (ROLE)"
     */
    @Override
    public String toString() {
        return name + " (" + role + ")";
    }

    // ================= OPTIONAL: PASSWORD VALIDATION =================

    /**
     * Validates password strength (optional helper method)
     * @param password Password to validate
     * @return true if password meets minimum strength requirements
     */
    public static boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) hasSpecial = true;
        }

        // Require at least 3 of the 4 criteria
        int criteriaMet = 0;
        if (hasUpper) criteriaMet++;
        if (hasLower) criteriaMet++;
        if (hasDigit) criteriaMet++;
        if (hasSpecial) criteriaMet++;

        return criteriaMet >= 3;
    }
}