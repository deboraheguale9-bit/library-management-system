package swingui;

import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BorrowBookWindow extends JFrame {
    private User currentUser;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public BorrowBookWindow(User user) {
        this.currentUser = user;
        setupUI();
        loadAvailableBooks();
    }

    private void setupUI() {
        setTitle("Borrow Book - Library Management System");
        setSize(800, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Available Books for Borrowing", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ISBN", "Title", "Author", "Year", "Type", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton borrowBtn = new JButton("Borrow Selected");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(borrowBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        refreshBtn.addActionListener(e -> loadAvailableBooks());
        closeBtn.addActionListener(e -> dispose());
        borrowBtn.addActionListener(e -> borrowSelectedBook());
    }

    private void loadAvailableBooks() {
        tableModel.setRowCount(0);
        String sql = "SELECT isbn, title, author, publication_year, book_type, available FROM books WHERE available > 0 ORDER BY title";

        try (Connection conn = util.DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publication_year"),
                        rs.getString("book_type"),
                        rs.getInt("available")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }

    private void borrowSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to borrow.");
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);

        // Check if user already has this book borrowed
        if (hasActiveLoan(isbn)) {
            JOptionPane.showMessageDialog(this, "You already have this book borrowed!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Borrow '" + title + "' for 14 days?",
                "Confirm Borrow",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = createLoan(isbn);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book borrowed successfully! Due in 14 days.");
                loadAvailableBooks();
            }
        }
    }

    private boolean hasActiveLoan(String isbn) {
        String sql = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND book_isbn = ? AND status = 'ACTIVE'";

        try (Connection conn = util.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUser.getId());
            pstmt.setString(2, isbn);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking loan: " + e.getMessage());
        }
        return false;
    }

    private boolean createLoan(String isbn) {
        Connection conn = null;
        try {
            conn = util.DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            // 1. Create loan record
            String insertLoan = "INSERT INTO loans (user_id, book_isbn, borrow_date, due_date, status) VALUES (?, ?, CURRENT_DATE, DATEADD('DAY', 14, CURRENT_DATE), 'ACTIVE')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertLoan)) {
                pstmt.setString(1, currentUser.getId());
                pstmt.setString(2, isbn);
                pstmt.executeUpdate();
            }

            // 2. Update book availability
            String updateBook = "UPDATE books SET available = available - 1 WHERE isbn = ? AND available > 0";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBook)) {
                pstmt.setString(1, isbn);
                int updated = pstmt.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("Book no longer available or not found");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Error borrowing book: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
