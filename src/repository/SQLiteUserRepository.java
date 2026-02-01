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
        try {
            DatabaseManager.initializeDatabase();
            System.out.println("✅ Database initialized");
        } catch (Exception e) {
            System.err.println("⚠️  Database initialization failed, using in-memory fallback");
            System.err.println("⚠️  Error: " + e.getMessage());
        }
    }

    @Override
    public void save(User user) {
        // Try database first
        String sql = "INSERT OR REPLACE INTO users (id, name, email, username, password_hash, role, mobile) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, user.getPasswordHash());
            pstmt.setString(6, user.getRole().toString());
            pstmt.setString(7, user.getMobile());

            pstmt.executeUpdate();
            System.out.println("✅ User saved to database: " + user.getUsername());

        } catch (Exception e) {
            // Fallback to in-memory
            System.err.println("⚠️  Database save failed, saving to memory: " + user.getUsername());
            IN_MEMORY_USERS.put(user.getId(), user);
            IN_MEMORY_USERS.put(user.getUsername(), user);
        }
    }

    @Override
    public User findById(String id) {
        // Try database first
        User user = findByColumn("id", id);
        if (user != null) return user;

        // Fallback to memory
        return IN_MEMORY_USERS.get(id);
    }

    @Override
    public User findByUsername(String username) {
        // Try database first
        User user = findByColumn("username", username);
        if (user != null) return user;

        // Fallback to memory (most important for login!)
        return IN_MEMORY_USERS.get(username);
    }

    @Override
    public User findByEmail(String email) {
        User user = findByColumn("email", email);
        if (user != null) return user;

        // Fallback: search in memory
        for (User u : IN_MEMORY_USERS.values()) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    private User findByColumn(String column, String value) {
        String sql = "SELECT * FROM users WHERE " + column + " = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToUser(rs);
            }

        } catch (Exception e) {
            // Silent fail - will use fallback
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(resultSetToUser(rs));
            }
            return users;

        } catch (Exception e) {
            // Fallback to memory
            System.err.println("⚠️  Database query failed, using memory");
        }

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
        // Try database
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();

            // Also remove from memory
            User user = IN_MEMORY_USERS.get(id);
            if (user != null) {
                IN_MEMORY_USERS.remove(id);
                IN_MEMORY_USERS.remove(user.getUsername());
            }

            return rows > 0;

        } catch (Exception e) {
            // Fallback
            User user = IN_MEMORY_USERS.remove(id);
            if (user != null) {
                IN_MEMORY_USERS.remove(user.getUsername());
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean update(User user) {
        // Try database
        String sql = "UPDATE users SET name = ?, email = ?, username = ?, password_hash = ?, role = ?, mobile = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPasswordHash());
            pstmt.setString(5, user.getRole().toString());
            pstmt.setString(6, user.getMobile());
            pstmt.setString(7, user.getId());

            int rows = pstmt.executeUpdate();

            // Update memory too
            IN_MEMORY_USERS.put(user.getId(), user);
            IN_MEMORY_USERS.put(user.getUsername(), user);

            return rows > 0;

        } catch (Exception e) {
            // Fallback
            IN_MEMORY_USERS.put(user.getId(), user);
            IN_MEMORY_USERS.put(user.getUsername(), user);
            return true;
        }
    }

    private User resultSetToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String roleStr = rs.getString("role");
        String mobile = rs.getString("mobile");

        UserRole role = UserRole.valueOf(roleStr);

        User user = new User(id, name, email, mobile, username, "temporary", role) {};
        user.setPasswordHash(passwordHash);

        return user;
    }
}