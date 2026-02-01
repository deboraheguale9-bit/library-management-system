import swingui.LoginWindow;
import util.DatabaseManager;
import repository.FileUserRepository;
import model.User;
import model.UserRole;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM ===\n");

        // DEMONSTRATE FILE I/O (Required for project)
        demonstrateFileStorage();

        DatabaseManager.initializeDatabase();

        // Add these VM options to handle SQLite native access warnings
        System.setProperty("org.sqlite.lib.path", ".");
        System.setProperty("org.sqlite.lib.name", "sqlite-jdbc");

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("✅ System look and feel set");
        } catch (Exception e) {
            System.out.println("⚠️ Using default look and feel");
        }

        // Start on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🚀 Launching application...");
                new LoginWindow();
                System.out.println("✅ Application started successfully");

                // Print test credentials
                System.out.println("\n=== TEST CREDENTIALS ===");
                System.out.println("Admin:      admin / admin123");
                System.out.println("Librarian:  librarian / lib123");
                System.out.println("Member:     member / mem123");
                System.out.println("========================\n");

            } catch (Exception e) {
                System.err.println("❌ CRITICAL ERROR:");
                e.printStackTrace();

                JOptionPane.showMessageDialog(null,
                        "Fatal Error: " + e.getMessage() +
                                "\n\nCheck if:\n1. SQLite JAR is added to project\n2. Java version is compatible\n3. Database file exists",
                        "Startup Failed",
                        JOptionPane.ERROR_MESSAGE);

                System.exit(1);
            }
        });
    }

    private static void demonstrateFileStorage() {
        System.out.println("=== FILE I/O DEMONSTRATION ===");
        System.out.println("(Required for project: Demonstrating file-based storage)");

        try {
            // Create file-based repository
            FileUserRepository fileRepo = new FileUserRepository("users.csv");

            // Show users from file storage
            List<User> fileUsers = fileRepo.findAll();
            System.out.println("✅ Users in file storage: " + fileUsers.size());

            // List each user
            for (User user : fileUsers) {
                System.out.println("  - " + user.getUsername() + " (" + user.getRole() + ")");
            }

            // Test: Try to find a user by username
            User foundUser = fileRepo.findByUsername("admin");
            if (foundUser != null) {
                System.out.println("✅ Verified: Found user 'admin' in file storage");
            }

            // Test: Add a new user to file (if not exists)
            User testUser = fileRepo.findByUsername("testuser");
            if (testUser == null) {
                testUser = new User("test001", "Test User", "test@library.com",
                        "555-9999", "testuser", "test123", UserRole.MEMBER) {};
                fileRepo.save(testUser);
                System.out.println("✅ Test user saved to file storage: testuser/test123");
            }

            System.out.println("=== END FILE I/O DEMONSTRATION ===\n");

        } catch (Exception e) {
            System.err.println("⚠️ File I/O demonstration failed: " + e.getMessage());
            System.out.println("⚠️ Continuing with database only...\n");
        }
    }
}