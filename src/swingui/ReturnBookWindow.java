package swingui;

import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReturnBookWindow extends JFrame {
    private User currentUser;
    private JTable loansTable;
    private DefaultTableModel tableModel;

    public ReturnBookWindow(User user) {
        this.currentUser = user;
        setupUI();
        loadActiveLoans();
    }

    private void setupUI() {
        setTitle("Return Book - Library Management System");
        setSize(800, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Books to Return", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Book Title", "ISBN", "Borrow Date", "Due Date", "Days Overdue", "Fine ($)"};
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
        JButton returnBtn = new JButton("Return Selected");
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(returnBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        refreshBtn.addActionListener(e -> loadActiveLoans());
        closeBtn.addActionListener(e -> dispose());
        returnBtn.addActionListener(e -> returnSelectedBook());
    }

    private void loadActiveLoans() {
        tableModel.setRowCount(0);

        // HARDCODED DEMO LOANS
        Object[][] demoLoans = {
                {"Effective Java", "9780134685991", "2024-01-15", "2024-01-29", 0, "$0.00"},
                {"Head First Java", "9780596009205", "2024-01-20", "2024-02-03", 2, "$1.00"},
                {"Design Patterns", "9780201633610", "2024-01-10", "2024-01-24", 8, "$4.00"}
        };

        for (Object[] loan : demoLoans) {
            tableModel.addRow(loan);
        }

        System.out.println("📚 Showing books to return for: " + currentUser.getUsername());

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "You have no books to return.");
        }
    }

    private void returnSelectedBook() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to return.");
            return;
        }

        String title = (String) tableModel.getValueAt(selectedRow, 0);
        String fine = (String) tableModel.getValueAt(selectedRow, 5);
        int overdueDays = (Integer) tableModel.getValueAt(selectedRow, 4);

        String message = "DEMO: '" + title + "' returned successfully!\n";
        if (overdueDays > 0) {
            message += "Overdue: " + overdueDays + " days\n";
            message += "Fine paid: " + fine + "\n";
        }
        message += "(Database would update book availability)";

        JOptionPane.showMessageDialog(this, message);

        // Remove from table (demo only)
        tableModel.removeRow(selectedRow);

        // Show success
        JOptionPane.showMessageDialog(this, "Book returned successfully!");
    }
}