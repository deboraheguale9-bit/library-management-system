package service;

import model.User;
import model.UserRole;
import repository.UserRepository;
import util.Validator;
import java.util.List;

public class UserService {
    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // Authentication
    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            System.out.println("Username and password cannot be empty");
            return null;
        }

        User user = repository.findByUsername(username);
        if (user != null && user.login(password) && user.isActive()) {
            System.out.println("✅ Authentication successful for: " + username);
            return user;
        }

        System.out.println("❌ Authentication failed for: " + username);
        return null;
    }

    // Registration with validation
    public boolean registerUser(User user) {
        if (user == null) {
            System.out.println("❌ User cannot be null");
            return false;
        }

        // Check if username already exists
        if (repository.findByUsername(user.getUsername()) != null) {
            System.out.println("❌ Username already exists: " + user.getUsername());
            return false;
        }

        // Check if email already exists
        if (repository.findByEmail(user.getEmail()) != null) {
            System.out.println("❌ Email already registered: " + user.getEmail());
            return false;
        }

        // Validate user data
        if (!Validator.isValidName(user.getName())) {
            System.out.println("❌ Invalid name: " + user.getName());
            return false;
        }

        if (!Validator.isValidEmail(user.getEmail())) {
            System.out.println("❌ Invalid email format: " + user.getEmail());
            return false;
        }

        if (!Validator.isValidPhone(user.getMobile())) {
            System.out.println("❌ Invalid phone number: " + user.getMobile());
            return false;
        }

        if (!Validator.isStrongPassword(user.getPasswordHash())) {
            System.out.println("❌ Password is too weak. Must be at least 8 characters with uppercase, lowercase, number, and special character");
            return false;
        }

        // Save the user
        repository.save(user);
        System.out.println("✅ User registered successfully: " + user.getUsername());
        return true;
    }

    // Update user with validation
    public boolean updateUser(User user) {
        if (user == null) {
            return false;
        }

        // Validate updated data
        if (!Validator.isValidName(user.getName())) {
            System.out.println("❌ Invalid name: " + user.getName());
            return false;
        }

        if (!Validator.isValidEmail(user.getEmail())) {
            System.out.println("❌ Invalid email: " + user.getEmail());
            return false;
        }

        if (!Validator.isValidPhone(user.getMobile())) {
            System.out.println("❌ Invalid phone: " + user.getMobile());
            return false;
        }

        return repository.update(user);
    }

    // Delete user
    public boolean deleteUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        return repository.delete(userId);
    }

    // Get user by ID
    public User getUserById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return repository.findById(id);
    }

    // Get user by username
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return repository.findByUsername(username);
    }

    // Get user by email
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return repository.findByEmail(email);
    }

    // Get all users
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // Get users by role
    public List<User> getUsersByRole(UserRole role) {
        if (role == null) {
            return List.of();
        }
        return repository.findAll().stream()
                .filter(user -> user.getRole() == role && user.isActive())
                .toList();
    }

    // Get active users only
    public List<User> getActiveUsers() {
        return repository.findAll().stream()
                .filter(User::isActive)
                .toList();
    }

    // Validation methods
    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return repository.findByUsername(username) == null;
    }

    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return repository.findByEmail(email) == null;
    }

    // Change password with validation
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            return false;
        }

        User user = repository.findByUsername(username);
        if (user == null) {
            System.out.println("❌ User not found: " + username);
            return false;
        }

        if (!user.login(oldPassword)) {
            System.out.println("❌ Old password is incorrect");
            return false;
        }

        if (!Validator.isStrongPassword(newPassword)) {
            System.out.println("❌ New password is too weak");
            return false;
        }

        user.changePassword(newPassword);
        return repository.update(user);
    }

    // Activate/Deactivate user
    public boolean activateUser(String userId) {
        User user = repository.findById(userId);
        if (user != null) {
            user.setActive(true);
            return repository.update(user);
        }
        return false;
    }

    public boolean deactivateUser(String userId) {
        User user = repository.findById(userId);
        if (user != null) {
            user.setActive(false);
            return repository.update(user);
        }
        return false;
    }

    // Get user count by role
    public long getUserCountByRole(UserRole role) {
        return repository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .count();
    }

    // Search users by name
    public List<User> searchUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }

        String searchTerm = name.toLowerCase();
        return repository.findAll().stream()
                .filter(user -> user.getName().toLowerCase().contains(searchTerm))
                .toList();
    }
}