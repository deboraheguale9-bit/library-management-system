package util;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:mem:librarydb;DB_CLOSE_DELAY=-1";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("✅ Connected to in-memory database");

            // Create USERS table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(100) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    mobile VARCHAR(20),
                    is_active BOOLEAN DEFAULT true,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create BOOKS table (THIS IS MISSING!)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS books (
                    isbn VARCHAR(20) PRIMARY KEY,
                    title VARCHAR(200) NOT NULL,
                    author VARCHAR(100) NOT NULL,
                    publication_year INTEGER,
                    copies INTEGER DEFAULT 1,
                    available INTEGER DEFAULT 1,
                    book_type VARCHAR(20) NOT NULL,
                    
                    -- EBook specific fields
                    file_size_mb DECIMAL(5,2),
                    format VARCHAR(20),
                    download_link VARCHAR(500),
                    drm_protected BOOLEAN DEFAULT false,
                    
                    -- PrintedBook specific fields
                    shelf_location VARCHAR(50),
                    condition VARCHAR(50),
                    edition INTEGER
                )
            """);

            // Create LOANS table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS loans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id VARCHAR(50) NOT NULL,
                    book_isbn VARCHAR(20) NOT NULL,
                    borrow_date DATE NOT NULL,
                    due_date DATE NOT NULL,
                    return_date DATE,
                    fine_amount DECIMAL(10,2) DEFAULT 0.0,
                    status VARCHAR(20) DEFAULT 'ACTIVE',
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (book_isbn) REFERENCES books(isbn)
                )
            """);

            System.out.println("✅ Tables created: users, books, loans");

            // Insert sample books if empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("""
                    INSERT INTO books (isbn, title, author, publication_year, copies, available, book_type, 
                                      file_size_mb, format, download_link, drm_protected, 
                                      shelf_location, condition, edition)
                    VALUES 
                    ('9780134685991', 'Effective Java', 'Joshua Bloch', 2018, 3, 3, 'E-BOOK', 
                     2.5, 'PDF', 'https://example.com/effective-java.pdf', false, NULL, NULL, NULL),
                    
                    ('9780596009205', 'Head First Java', 'Kathy Sierra', 2005, 2, 2, 'PRINTED', 
                     NULL, NULL, NULL, NULL, 'CS-101-A', 'Good', 2),
                    
                    ('9780201633610', 'Design Patterns', 'Erich Gamma', 1994, 1, 1, 'PRINTED', 
                     NULL, NULL, NULL, NULL, 'CS-102-B', 'Excellent', 1)
                """);
                System.out.println("✅ Added sample books");
            }

        } catch (Exception e) {
            System.err.println("⚠️  Database error: " + e.getMessage());
            // Don't exit - use fallback
        }
    }
}