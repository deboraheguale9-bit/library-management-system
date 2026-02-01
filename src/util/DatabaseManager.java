package util;

import java.sql.Connection;

public class DatabaseManager {
    public static void initializeDatabase() {
        System.out.println("✅ Using in-memory mode (no database)");
        System.out.println("✅ Demo users: admin/admin123, librarian/lib123, member/mem123");
    }

    public static Connection getConnection() {
        System.out.println("⚠️ Database not available, using in-memory mode");
        return null;
    }
}