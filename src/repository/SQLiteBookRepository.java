package repository;

import model.Book;
import util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteBookRepository implements BookRepository {

    public SQLiteBookRepository() {
        // Database already initialized
    }

    @Override
    public void save(Book book) {
        Map<String, Object> profile = book.getProfile();

        String sql = """
            INSERT OR REPLACE INTO books 
            (isbn, title, author, publication_year, copies, available, book_type,
             file_size_mb, format, download_link, drm_protected,
             shelf_location, condition, edition)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Common fields
            pstmt.setString(1, (String) profile.get("isbn"));
            pstmt.setString(2, (String) profile.get("title"));
            pstmt.setString(3, (String) profile.get("author"));
            pstmt.setInt(4, (int) profile.get("publication_year"));
            pstmt.setInt(5, (int) profile.get("copies"));
            pstmt.setInt(6, (boolean) profile.get("available") ? 1 : 0);
            pstmt.setString(7, (String) profile.get("book_type"));

            // EBook fields (nullable)
            setNullable(pstmt, 8, profile.get("file_size_mb"));
            setNullable(pstmt, 9, profile.get("format"));
            setNullable(pstmt, 10, profile.get("download_link"));
            setNullable(pstmt, 11, profile.get("drm_protected"));

            // PrintedBook fields (nullable)
            setNullable(pstmt, 12, profile.get("shelf_location"));
            setNullable(pstmt, 13, profile.get("condition"));
            setNullable(pstmt, 14, profile.get("edition"));

            pstmt.executeUpdate();
            System.out.println("✅ Book saved: " + book.getTitle());

        } catch (SQLException e) {
            System.err.println("❌ Error saving book: " + e.getMessage());
        }
    }

    private void setNullable(PreparedStatement pstmt, int index, Object value) throws SQLException {
        if (value != null) {
            if (value instanceof String) pstmt.setString(index, (String) value);
            else if (value instanceof Integer) pstmt.setInt(index, (Integer) value);
            else if (value instanceof Double) pstmt.setDouble(index, (Double) value);
            else if (value instanceof Boolean) pstmt.setBoolean(index, (Boolean) value);
            else pstmt.setObject(index, value);
        } else {
            pstmt.setNull(index, Types.NULL);
        }
    }

    @Override
    public Book findById(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToBook(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error finding book: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(resultSetToBook(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error finding all books: " + e.getMessage());
        }
        return books;
    }

    @Override
    public boolean delete(String isbn) {
        String sql = "DELETE FROM books WHERE isbn = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting book: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Book book) {
        // Save uses INSERT OR REPLACE which updates
        save(book);
        return true;
    }

    @Override
    public List<Book> findByTitle(String title) {
        return searchByColumn("title", title);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return searchByColumn("author", author);
    }

    private List<Book> searchByColumn(String column, String value) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE " + column + " LIKE ? ORDER BY title";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + value + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(resultSetToBook(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error searching books: " + e.getMessage());
        }
        return books;
    }

    private Book resultSetToBook(ResultSet rs) throws SQLException {
        Map<String, Object> data = new HashMap<>();

        // Common fields
        data.put("isbn", rs.getString("isbn"));
        data.put("title", rs.getString("title"));
        data.put("author", rs.getString("author"));
        data.put("publication_year", rs.getInt("publication_year"));
        data.put("copies", rs.getInt("copies"));
        data.put("available", rs.getInt("available") == 1);
        data.put("book_type", rs.getString("book_type"));

        // EBook fields
        data.put("file_size_mb", rs.getObject("file_size_mb"));
        data.put("format", rs.getObject("format"));
        data.put("download_link", rs.getObject("download_link"));
        data.put("drm_protected", rs.getObject("drm_protected"));

        // PrintedBook fields
        data.put("shelf_location", rs.getObject("shelf_location"));
        data.put("condition", rs.getObject("condition"));
        data.put("edition", rs.getObject("edition"));

        // Use Book's factory method
        return Book.fromMap(data);
    }
}