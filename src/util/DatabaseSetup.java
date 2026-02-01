package util;

import java.sql.*;

public class DatabaseSetup {
    private static final String DB_URL = "jdbc:derby:librarydb;create=true";

    public static void main(String[] args) {
        System.out.println("=== DATABASE SETUP ===");
        createDatabase();
        System.out.println("✅ Database setup complete!");
        System.out.println("Database file: library.db");
    }

    public static void createDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            System.out.println("Creating tables...");

            // Enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON");

            // Create users table
            String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    mobile TEXT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL,
                    is_active BOOLEAN DEFAULT true,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;

            // Create books table
            String booksTable = """
                CREATE TABLE IF NOT EXISTS books (
                    isbn TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    publication_year INTEGER,
                    copies INTEGER DEFAULT 1,
                    available INTEGER DEFAULT 1,
                    book_type TEXT NOT NULL,
                    
                    -- EBook specific fields
                    file_size_mb REAL,
                    format TEXT,
                    download_link TEXT,
                    drm_protected BOOLEAN DEFAULT false,
                    
                    -- PrintedBook specific fields
                    shelf_location TEXT,
                    condition TEXT,
                    edition INTEGER
                )
            """;

            // Create loans table
            String loansTable = """
                CREATE TABLE IF NOT EXISTS loans (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id TEXT NOT NULL,
                    book_isbn TEXT NOT NULL,
                    borrow_date TEXT NOT NULL,
                    due_date TEXT NOT NULL,
                    return_date TEXT,
                    fine_amount REAL DEFAULT 0.0,
                    status TEXT DEFAULT 'ACTIVE',
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (book_isbn) REFERENCES books(isbn) ON DELETE CASCADE
                )
            """;

            stmt.execute(usersTable);
            stmt.execute(booksTable);
            stmt.execute(loansTable);

            System.out.println("✅ Tables created: users, books, loans");

            // Insert default admin
            insertDefaultAdmin(conn);

            // Insert sample books
            insertSampleBooks(conn);

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertDefaultAdmin(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        try (Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery(checkSql)) {

            if (rs.getInt(1) == 0) {
                // Create default admin
                String insertSql = """
                    INSERT INTO users (id, name, email, mobile, username, password_hash, role)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "admin001");
                    pstmt.setString(2, "System Administrator");
                    pstmt.setString(3, "admin@library.com");
                    pstmt.setString(4, "555-0000");
                    pstmt.setString(5, "admin");
                    pstmt.setString(6, "admin123:salt:hash"); // Temporary hash
                    pstmt.setString(7, "ADMIN");

                    pstmt.executeUpdate();
                    System.out.println("✅ Default admin created: admin/admin123");
                }
            } else {
                System.out.println("✅ Admin already exists");
            }
        }
    }

    private static void insertSampleBooks(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM books";
        try (Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery(checkSql)) {

            if (rs.getInt(1) == 0) {
                System.out.println("Inserting sample books...");

                // Sample EBook
                String ebookSql = """
                    INSERT INTO books (isbn, title, author, publication_year, copies, available, book_type,
                                       file_size_mb, format, download_link, drm_protected)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement pstmt = conn.prepareStatement(ebookSql)) {
                    pstmt.setString(1, "9780134685991");
                    pstmt.setString(2, "Effective Java");
                    pstmt.setString(3, "Joshua Bloch");
                    pstmt.setInt(4, 2018);
                    pstmt.setInt(5, 3);
                    pstmt.setInt(6, 1);
                    pstmt.setString(7, "E-Book");
                    pstmt.setDouble(8, 2.5);
                    pstmt.setString(9, "PDF");
                    pstmt.setString(10, "https://example.com/effective-java.pdf");
                    pstmt.setBoolean(11, false);

                    pstmt.executeUpdate();
                }

                // Sample Printed Book
                String printedSql = """
                    INSERT INTO books (isbn, title, author, publication_year, copies, available, book_type,
                                       shelf_location, condition, edition)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement pstmt = conn.prepareStatement(printedSql)) {
                    pstmt.setString(1, "9780596009205");
                    pstmt.setString(2, "Head First Java");
                    pstmt.setString(3, "Kathy Sierra");
                    pstmt.setInt(4, 2005);
                    pstmt.setInt(5, 2);
                    pstmt.setInt(6, 1);
                    pstmt.setString(7, "Printed Book");
                    pstmt.setString(8, "CS-101-A");
                    pstmt.setString(9, "Good");
                    pstmt.setInt(10, 2);

                    pstmt.executeUpdate();
                }

                System.out.println("✅ Sample books inserted");
            } else {
                System.out.println("✅ Books already exist in database");
            }
        }
    }
}