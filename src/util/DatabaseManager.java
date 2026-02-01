package util;

import java.sql.Connection;  // ADD THIS IMPORT

public class DatabaseManager {
    // NO DATABASE - Just print message
    public static void initializeDatabase() {
        System.out.println("✅ Using in-memory mode (no database)");
        System.out.println("✅ Demo users: admin/admin123, librarian/lib123, member/mem123");
    }

    // Return null for compatibility
    public static Connection getConnection() {
        System.out.println("⚠️ Database not available, using in-memory mode");
        return null;
    }
}