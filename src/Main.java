import swingui.LoginWindow;
import util.DatabaseManager;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        System.out.println("=== LIBRARY MANAGEMENT SYSTEM ===\n");

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
}