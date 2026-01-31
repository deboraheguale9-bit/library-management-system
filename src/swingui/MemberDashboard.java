package swingui;

import model.User;
import javax.swing.*;
import java.awt.*;

public class MemberDashboard extends JFrame {
    private User currentUser;

    public MemberDashboard(User loggedInUser) {
        this.currentUser = loggedInUser;
        setupUI();
    }

    private void setupUI() {
        setTitle("Member Dashboard - Library Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Member Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName());
        welcomeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        headerPanel.add(logoutBtn, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton searchBooksBtn = new JButton("Search Books");
        searchBooksBtn.setFont(new Font("Arial", Font.BOLD, 16));

        JButton viewLoansBtn = new JButton("My Loans");
        viewLoansBtn.setFont(new Font("Arial", Font.BOLD, 16));

        JButton borrowBookBtn = new JButton("Borrow Book");
        borrowBookBtn.setFont(new Font("Arial", Font.BOLD, 16));

        JButton returnBookBtn = new JButton("Return Book");
        returnBookBtn.setFont(new Font("Arial", Font.BOLD, 16));

        centerPanel.add(searchBooksBtn);
        centerPanel.add(viewLoansBtn);
        centerPanel.add(borrowBookBtn);
        centerPanel.add(returnBookBtn);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            currentUser.logout();
            this.dispose();
            new LoginWindow().setVisible(true);
        }
    }
}