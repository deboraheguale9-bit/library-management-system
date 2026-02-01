package repository;

import model.User;
import model.UserRole;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteUserRepository implements UserRepository {

    public SQLiteUserRepository() {
        DatabaseManager.initializeDatabase();
    }

    @Override
    public void save(User user) {
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
            System.out.println("✅ User saved: " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("❌ Error saving user: " + e.getMessage());
        }
    }

    @Override
    public User findById(String id) {
        return findByColumn("id", id);
    }

    @Override
    public User findByUsername(String username) {
        return findByColumn("username", username);
    }

    @Override
    public User findByEmail(String email) {
        return findByColumn("email", email);
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

        } catch (SQLException e) {
            System.err.println("❌ Error finding user: " + e.getMessage());
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

        } catch (SQLException e) {
            System.err.println("❌ Error finding all users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(User user) {
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
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating user: " + e.getMessage());
            return false;
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

        // Create user
        User user = new User(id, name, email, mobile, username, "temporary", role) {
            // Anonymous class since User is abstract
        };
        user.setPasswordHash(passwordHash);

        return user;
    }
}