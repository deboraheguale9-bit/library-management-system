package swingui;

import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MyLoansWindow extends JFrame {
    private User currentUser;
    private JTable loansTable;
    private DefaultTableModel tableModel; // <-- THIS WAS MISSING

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

        JLabel titleLabel = new JLabel("My Current Loans", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        String[] columns = {"Book Title", "ISBN", "Borrow Date", "Due Date", "Status", "Fine ($)"};
        tableModel = new DefaultTableModel(columns, 0) { // <-- INITIALIZE IT
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loansTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loansTable);

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
        refreshBtn.addActionListener(e -> loadLoans());
        closeBtn.addActionListener(e -> dispose());
        returnBtn.addActionListener(e -> returnSelectedLoan());
    }

    private void loadLoans() {
        tableModel.setRowCount(0);
        Object[][] demoLoans = {
                {"Effective Java", "9780134685991", "2024-01-15", "2024-01-29", "ACTIVE", "$0.00"},
                {"Head First Java", "9780596009205", "2024-01-20", "2024-02-03", "ACTIVE", "$0.00"},
                {"Design Patterns", "9780201633610", "2024-01-10", "2024-01-24", "ACTIVE", "$2.50"}
        };

        for (Object[] loan : demoLoans) {
            tableModel.addRow(loan);
        }

        System.out.println("📋 Showing demo loan data for: " + currentUser.getUsername());

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "You have no active loans.");
        }
    }

    private void returnSelectedLoan() {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a loan to return.");
            return;
        }

        String title = (String) tableModel.getValueAt(selectedRow, 0);
        String fine = (String) tableModel.getValueAt(selectedRow, 5);

        String message = "DEMO: '" + title + "' returned successfully!\n";
        if (!fine.equals("$0.00")) {
            message += "Fine paid: " + fine + "\n";
        }
        message += "(Database integration would update availability)";

        JOptionPane.showMessageDialog(this, message);

        tableModel.removeRow(selectedRow);
    }
}