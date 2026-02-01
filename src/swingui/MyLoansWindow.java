package swingui;

import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyLoansWindow extends JFrame {
    private User currentUser;
    private JTable loansTable;
    private DefaultTableModel tableModel;

    public MyLoansWindow(User user) {
        this.currentUser = user;
        setupUI();
        loadLoans();
    }

    private void setupUI() {
        setTitle("My Loans - Library Management System");
        setSize(900, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("My Current Loans", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Book Title", "ISBN", "Borrow Date", "Due Date", "Status", "Fine ($)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        loansTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loansTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        JButton returnBtn = new JButton("Return Selected");
        JButton closeBtn = new JButton("Close");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(returnBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        refreshBtn.addActionListener(e -> loadLoans());
        closeBtn.addActionListener(e -> dispose());
        returnBtn.addActionListener(e -> returnSelectedLoan());
    }

    private void loadLoans() {
        tableModel.setRowCount(0);
        String sql = "SELECT b.title, b.isbn, l.borrow_date, l.due_date, l.status, l.fine_amount " +
                "FROM loans l JOIN books b ON l.book_isbn = b.isbn " +
                "WHERE l.user_id = ? AND l.status = 'ACTIVE'";

        try (Connection conn = util.DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getString("title"),
                        rs.getString("isbn"),
                        rs.getDate("borrow_date"),
                        rs.getDate("due_date"),
                        rs.getString("status"),
                        rs.getDouble("fine_amount")
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "You have no active loans.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading loans: " + e.getMessage());
        }
    }

    private void returnSelectedLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a loan to return.");
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 1);
        String title = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Return '" + title + "'?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update loan status and book availability
            updateLoanReturn(isbn);
            loadLoans();
        }
    }

    private void updateLoanReturn(String isbn) {
        try (Connection conn = util.DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            // Update loan status
            String updateLoan = "UPDATE loans SET status = 'RETURNED', return_date = CURRENT_DATE WHERE user_id = ? AND book_isbn = ? AND status = 'ACTIVE'";
            try (PreparedStatement pstmt = conn.prepareStatement(updateLoan)) {
                pstmt.setString(1, currentUser.getId());
                pstmt.setString(2, isbn);
                pstmt.executeUpdate();
            }

            // Update book availability
            String updateBook = "UPDATE books SET available = available + 1 WHERE isbn = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBook)) {
                pstmt.setString(1, isbn);
                pstmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Book returned successfully!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage());
        }
    }
}