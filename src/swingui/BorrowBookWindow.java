package swingui;

import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BorrowBookWindow extends JFrame {
    private User currentUser;
    private JTable booksTable;
    private DefaultTableModel tableModel; // <-- ADD THIS LINE

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

        // Table - Initialize tableModel here
        String[] columns = {"ISBN", "Title", "Author", "Year", "Type", "Available"};
        tableModel = new DefaultTableModel(columns, 0) { // <-- INITIALIZE HERE
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
        tableModel.setRowCount(0); // Clear the table

        // HARDCODED DEMO BOOKS
        Object[][] demoBooks = {
                {"9780134685991", "Effective Java", "Joshua Bloch", 2018, "E-BOOK", 3},
                {"9780596009205", "Head First Java", "Kathy Sierra", 2005, "PRINTED", 2},
                {"9780201633610", "Design Patterns", "Erich Gamma", 1994, "PRINTED", 1},
                {"9780321356680", "Effective Java 2nd Ed", "Joshua Bloch", 2008, "PRINTED", 1}
        };

        for (Object[] book : demoBooks) {
            tableModel.addRow(book);
        }

        System.out.println("📚 Showing demo book data for: " + currentUser.getUsername());
    }

    private void borrowSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to borrow.");
            return;
        }

        String title = (String) tableModel.getValueAt(selectedRow, 1);
        int available = (Integer) tableModel.getValueAt(selectedRow, 5);

        if (available <= 0) {
            JOptionPane.showMessageDialog(this, "Sorry, this book is not available.");
            return;
        }

        // Update availability in the table (demo)
        tableModel.setValueAt(available - 1, selectedRow, 5);

        JOptionPane.showMessageDialog(this,
                "DEMO: '" + title + "' borrowed successfully!\n" +
                        "New availability: " + (available - 1) + " copies\n" +
                        "(Database integration would save this loan)");
    }
}