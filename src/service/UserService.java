package service;

import model.User;
import model.UserRole;
import repository.UserRepository;
import java.util.List;

public class UserService {
    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // Authentication
    public User authenticate(String username, String password) {
        User user = repository.findByUsername(username);
        if (user != null && user.login(password)) {
            return user;
        }
        return null;
    }

    // Registration
    public boolean registerUser(User user) {
        if (user == null || repository.findByUsername(user.getUsername()) != null) {
            return false;
        }
        repository.save(user);
        return true;
    }

    // User management
    public boolean updateUser(User user) {
        return repository.update(user);
    }

    public boolean deleteUser(String userId) {
        return repository.delete(userId);
    }

    public User getUserById(String id) {
        return repository.findById(id);
    }

    public User getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public List<User> getUsersByRole(UserRole role) {
        return repository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .toList();
    }

    // Validation
    public boolean isUsernameAvailable(String username) {
        return repository.findByUsername(username) == null;
    }

    public boolean isEmailAvailable(String email) {
        return repository.findByEmail(email) == null;
    }
}