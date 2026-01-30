package model;

import java.util.HashMap;
import java.util.Map;


public abstract class User {
    private String id;
    private String name;
    private String email;
    private String mobile;
    private String username;
    private String passwordHash;
    private UserRole role;
    private boolean isActive;

    // Constructor
    public User(String id, String name, String email, String mobile,
                String username, String passwordHash, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = true;
    }

    // Methods
    public boolean login(String passwordAttempt) {
        return isActive && passwordHash.equals(passwordAttempt);
    }

    public void logout() {
        System.out.println(name + " logged out.");
    }

    public void changePassword(String newPassword) {
        this.passwordHash = newPassword;
    }

    public UserRole getRole() {
        return role;
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

    // Getters and Setters
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

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
