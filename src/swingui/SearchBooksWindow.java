package swingui;

import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchBooksWindow extends JFrame {
    private User currentUser;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public SearchBooksWindow(User user) {
        this.currentUser = user;
        setupUI();
        loadBooks();
    }

    private void setupUI() {
        setTitle("Search Books - Library Management System");
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        // Table
        String[] columns = {"ISBN", "Title", "Author", "Year", "Type", "Available", "Copies"};
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
        JButton closeBtn = new JButton("Close");

        buttonPanel.add(borrowBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        searchBtn.addActionListener(e -> searchBooks(searchField.getText()));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadBooks();
        });
        closeBtn.addActionListener(e -> dispose());
        borrowBtn.addActionListener(e -> borrowSelectedBook());
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        String sql = "SELECT isbn, title, author, publication_year, book_type, available, copies FROM books WHERE available > 0 ORDER BY title";

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
                        rs.getInt("available"),
                        rs.getInt("copies")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }

    private void searchBooks(String query) {
        tableModel.setRowCount(0);
        String sql = "SELECT isbn, title, author, publication_year, book_type, available, copies FROM books " +
                "WHERE (title LIKE ? OR author LIKE ? OR isbn LIKE ?) AND available > 0 ORDER BY title";

        try (Connection conn = util.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            pstmt.setString(3, "%" + query + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("publication_year"),
                        rs.getString("book_type"),
                        rs.getInt("available"),
                        rs.getInt("copies")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage());
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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Borrow '" + title + "'?",
                "Confirm Borrow",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Call loan service to create loan
            JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
            loadBooks(); // Refresh availability
        }
    }
}