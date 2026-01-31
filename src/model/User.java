package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Abstract User class for Library Management System.
 * Implements SHA-256 password hashing with salt for security.
 * Designed for file-based persistence (JSON) and Swing UI integration.
 */
public abstract class User {

    // User attributes (all private - Encapsulation)
    private String id;
    private String name;
    private String email;
    private String mobile;
    private String username;
    private String passwordHash;  // Format: "salt:hashedPassword" in Base64
    private UserRole role;
    private boolean isActive;

    // ================= CONSTRUCTORS =================

    /**
     * Creates a new User with hashed password.
     * Used when creating brand new users.
     *
     * @param id Unique user ID (e.g., "ADM001", "LIB002", "MEM003")
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
        this.passwordHash = hashPassword(plainPassword);  // Auto-hash on creation
        this.role = role;
        this.isActive = true;  // New users are active by default
    }

    /**
     * Alternative constructor for loading users from file.
     * Use this when you already have the hashed password.
     *
     * @param id User ID
     * @param name Full name
     * @param email Email
     * @param mobile Phone
     * @param username Username
     * @param passwordHash Already hashed password (from file)
     * @param role User role
     * @param isActive Active status
     */
    public User(String id, String name, String email, String mobile,
                String username, String passwordHash, UserRole role, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.username = username;
        this.passwordHash = passwordHash;  // Already hashed from file
        this.role = role;
        this.isActive = isActive;
    }

    // ================= AUTHENTICATION METHODS =================

    /**
     * Attempts to authenticate the user.
     * Called from Swing login screen.
     *
     * @param passwordAttempt Plain text password from login form
     * @return true if login successful, false otherwise
     */
    public boolean authenticate(String passwordAttempt) {
        if (!isActive) {
            return false;  // Inactive users cannot log in
        }
        return verifyPassword(passwordAttempt, passwordHash);
    }

    /**
     * Changes the user's password.
     * Called from Swing "Change Password" dialog.
     *
     * @param newPlainPassword New password from text field
     * @throws IllegalArgumentException if password is weak
     */
    public void changePassword(String newPlainPassword) {
        if (!validatePasswordStrength(newPlainPassword)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters with mix of uppercase, lowercase, numbers, and symbols"
            );
        }
        this.passwordHash = hashPassword(newPlainPassword);
    }

    /**
     * Deactivates the user account.
     * Called from Admin panel in Swing.
     */
    public void deactivateAccount() {
        this.isActive = false;
    }

    /**
     * Reactivates the user account.
     * Called from Admin panel in Swing.
     */
    public void reactivateAccount() {
        this.isActive = true;
    }

    // ================= SECURE PASSWORD HASHING =================

    /**
     * Hashes a password with SHA-256 and random salt.
     * Uses cryptographically secure random bytes.
     *
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

            // Convert to Base64 strings for safe storage
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);

            return saltBase64 + ":" + hashBase64;  // Format for easy parsing

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is standard in Java, but handle gracefully
            throw new RuntimeException("Security algorithm unavailable", e);
        }
    }

    /**
     * Verifies a password attempt against the stored hash.
     * Uses constant-time comparison to prevent timing attacks.
     *
     * @param passwordAttempt Plain text password attempt
     * @param storedHash Stored hash in "salt:hash" format
     * @return true if password matches, false otherwise
     */
    private boolean verifyPassword(String passwordAttempt, String storedHash) {
        try {
            // Split the stored hash into salt and hash parts
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;  // Invalid format - corrupted data
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

        } catch (IllegalArgumentException e) {
            // Invalid Base64 encoding
            return false;
        } catch (Exception e) {
            // Any other error
            return false;
        }
    }

    // ================= UI HELPER METHODS =================

    /**
     * Gets user data as array for Swing JTable.
     * Used in Admin panel to display user list.
     *
     * @return String array for table row
     */
    public String[] toTableRow() {
        return new String[] {
                id,
                name,
                email,
                mobile,
                username,
                role.toString(),
                isActive ? "Active" : "Inactive",
                "••••••••"  // Masked password for security
        };
    }

    /**
     * Gets user profile for display in UI.
     *
     * @return Formatted profile string
     */
    public String getProfileDisplay() {
        return String.format("""
            User Profile
            ============
            ID: %s
            Name: %s
            Email: %s
            Phone: %s
            Username: %s
            Role: %s
            Status: %s
            """,
                id, name, email, mobile, username, role,
                isActive ? "Active" : "Inactive"
        );
    }

    /**
     * Checks if this user can perform an action based on role.
     * Useful for UI button enabling/disabling.
     *
     * @param requiredRole Minimum role required
     * @return true if user has sufficient privileges
     */
    public boolean hasPermission(UserRole requiredRole) {
        // Role hierarchy: ADMIN > LIBRARIAN > MEMBER
        return this.role.ordinal() <= requiredRole.ordinal();
    }

    // ================= PASSWORD VALIDATION =================

    /**
     * Validates password strength for UI feedback.
     * Called from password change dialog.
     *
     * @param password Password to validate
     * @return Validation result with message
     */
    public static PasswordValidationResult validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return new PasswordValidationResult(false,
                    "Password must be at least 8 characters long");
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

        // Build feedback message
        StringBuilder feedback = new StringBuilder();
        int criteriaMet = 0;

        if (hasUpper) criteriaMet++; else feedback.append("✗ Needs uppercase letter\n");
        if (hasLower) criteriaMet++; else feedback.append("✗ Needs lowercase letter\n");
        if (hasDigit) criteriaMet++; else feedback.append("✗ Needs number\n");
        if (hasSpecial) criteriaMet++; else feedback.append("✗ Needs special character\n");

        boolean isValid = criteriaMet >= 3;
        if (isValid) {
            feedback.insert(0, "✓ Password strength: Good\n");
        } else {
            feedback.insert(0, "Password needs at least 3 of 4 criteria:\n");
        }

        return new PasswordValidationResult(isValid, feedback.toString());
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
     * Gets the password hash (for file persistence only).
     * DO NOT use for authentication - use authenticate() instead.
     *
     * @return The hashed password in "salt:hash" format
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Sets password hash (for loading from file only).
     * DO NOT use for new passwords - they should be hashed via constructor.
     *
     * @param passwordHash Already hashed password in "salt:hash" format
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // ================= STRING REPRESENTATION =================

    @Override
    public String toString() {
        return String.format("%s [%s] - %s", name, role, isActive ? "Active" : "Inactive");
    }

    // ================= INNER CLASSES =================

    /**
     * Helper class for password validation results.
     * Used for Swing UI feedback.
     */
    public static class PasswordValidationResult {
        private final boolean valid;
        private final String message;

        public PasswordValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}