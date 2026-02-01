package repository;

import model.User;
import model.UserRole;
import util.DatabaseManager;
import java.sql.*;
import java.util.*;

public class SQLiteUserRepository implements UserRepository {

    // EMERGENCY FALLBACK: In-memory storage when database fails
    private static final Map<String, User> IN_MEMORY_USERS = new HashMap<>();

    static {
        // Pre-populate with test users
        User admin = new User("admin001", "Admin User", "admin@lib.com", "555-0000", "admin", "admin123", UserRole.ADMIN) {};
        User librarian = new User("lib001", "Librarian User", "lib@lib.com", "555-0001", "librarian", "lib123", UserRole.LIBRARIAN) {};
        User member = new User("mem001", "Member User", "member@lib.com", "555-0002", "member", "mem123", UserRole.MEMBER) {};

        IN_MEMORY_USERS.put("admin001", admin);
        IN_MEMORY_USERS.put("lib001", librarian);
        IN_MEMORY_USERS.put("mem001", member);

        // Also index by username for quick lookup
        IN_MEMORY_USERS.put("admin", admin);
        IN_MEMORY_USERS.put("librarian", librarian);
        IN_MEMORY_USERS.put("member", member);
    }

    public SQLiteUserRepository() {
        System.out.println("✅ User repository ready (in-memory mode)");
    }

    @Override
    public void save(User user) {
        // Just save to memory (no database)
        IN_MEMORY_USERS.put(user.getId(), user);
        IN_MEMORY_USERS.put(user.getUsername(), user);
        System.out.println("✅ User saved to memory: " + user.getUsername());
    }

    @Override
    public User findById(String id) {
        return IN_MEMORY_USERS.get(id);
    }

    @Override
    public User findByUsername(String username) {
        return IN_MEMORY_USERS.get(username);
    }

    @Override
    public User findByEmail(String email) {
        for (User u : IN_MEMORY_USERS.values()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        // Return from memory (avoid duplicates)
        Set<User> uniqueUsers = new HashSet<>();
        for (Map.Entry<String, User> entry : IN_MEMORY_USERS.entrySet()) {
            // Only add if key is ID (not username)
            if (entry.getKey().equals(entry.getValue().getId())) {
                uniqueUsers.add(entry.getValue());
            }
        }
        return new ArrayList<>(uniqueUsers);
    }

    @Override
    public boolean delete(String id) {
        User user = IN_MEMORY_USERS.remove(id);
        if (user != null) {
            IN_MEMORY_USERS.remove(user.getUsername());
            return true;
        }
        return false;
    }

    @Override
    public boolean update(User user) {
        IN_MEMORY_USERS.put(user.getId(), user);
        IN_MEMORY_USERS.put(user.getUsername(), user);
        return true;
    }

    // REMOVE this method since we're not using database
    // private User resultSetToUser(ResultSet rs) throws SQLException { ... }
}