package model;

import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public abstract class User {
    private String id;
    private String name;
    private String email;
    private String mobile;
    private String username;
    private String passwordHash;
    private UserRole role;
    private boolean isActive;

    public User(String id, String name, String email, String mobile,
                String username, String plainPassword, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.username = username;
        this.passwordHash = plainPassword;
        this.role = role;
        this.isActive = true;
    }

    public boolean login(String passwordAttempt) {
        System.out.println("[LOGIN DEBUG] User: " + username);
        System.out.println("[LOGIN DEBUG] Attempt: '" + passwordAttempt + "'");
        System.out.println("[LOGIN DEBUG] Stored hash: '" + passwordHash + "'");

        if (!isActive) {
            System.out.println("[LOGIN DEBUG] User inactive");
            return false;
        }

        if (!passwordHash.contains(":") || passwordHash.split(":").length < 2) {
            System.out.println("[LOGIN DEBUG] Simple password comparison");
            boolean simpleMatch = passwordHash.equals(passwordAttempt);
            System.out.println("[LOGIN DEBUG] Result: " + simpleMatch);
            return simpleMatch;
        }

        if (passwordHash.contains(":")) {
            String[] parts = passwordHash.split(":");
            if (parts.length >= 1) {
                String storedPassword = parts[0]; // First part is the password
                System.out.println("[LOGIN DEBUG] First part comparison: '" + storedPassword + "'");
                boolean match = storedPassword.equals(passwordAttempt);
                System.out.println("[LOGIN DEBUG] Result: " + match);
                return match;
            }
        }

        System.out.println("[LOGIN DEBUG] Trying hash verification...");
        boolean hashMatch = verifyPassword(passwordAttempt, passwordHash);
        System.out.println("[LOGIN DEBUG] Hash verification result: " + hashMatch);

        return hashMatch;
    }

    public void logout() {
        System.out.println(name + " logged out.");
    }
    public void changePassword(String newPlainPassword) {
        this.passwordHash = hashPassword(newPlainPassword);
    }
    private String hashPassword(String plainPassword) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);  // Add salt first
            byte[] hashedBytes = digest.digest(plainPassword.getBytes());

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);

            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private boolean verifyPassword(String passwordAttempt, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] attemptHashBytes = digest.digest(passwordAttempt.getBytes());

            return MessageDigest.isEqual(storedHashBytes, attemptHashBytes);

        } catch (Exception e) {
            return false;
        }
    }

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

    public String getPasswordHash() { return passwordHash; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }

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

        int criteriaMet = 0;
        if (hasUpper) criteriaMet++;
        if (hasLower) criteriaMet++;
        if (hasDigit) criteriaMet++;
        if (hasSpecial) criteriaMet++;

        return criteriaMet >= 3;
    }
}