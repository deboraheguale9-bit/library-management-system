package swingui;

import model.User;
import javax.swing.*;
import java.awt.*;

public class LibrarianDashboard extends JFrame {
    private User currentUser;

    public LibrarianDashboard(User loggedInUser) {
        this.currentUser = loggedInUser;
        setupUI();
    }

    private void setupUI() {
        setTitle("Librarian Dashboard - Library Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Librarian Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName());
        welcomeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        headerPanel.add(logoutBtn, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton manageBooksBtn = new JButton("Manage Books");
        manageBooksBtn.setFont(new Font("Arial", Font.BOLD, 16));
        manageBooksBtn.setPreferredSize(new Dimension(200, 80));

        JButton manageLoansBtn = new JButton("Manage Loans");
        manageLoansBtn.setFont(new Font("Arial", Font.BOLD, 16));
        manageLoansBtn.setPreferredSize(new Dimension(200, 80));

        JButton searchBooksBtn = new JButton("Search Books");
        searchBooksBtn.setFont(new Font("Arial", Font.BOLD, 16));
        searchBooksBtn.setPreferredSize(new Dimension(200, 80));

        JButton processReturnsBtn = new JButton("Process Returns");
        processReturnsBtn.setFont(new Font("Arial", Font.BOLD, 16));
        processReturnsBtn.setPreferredSize(new Dimension(200, 80));

        JButton viewReportsBtn = new JButton("View Reports");
        viewReportsBtn.setFont(new Font("Arial", Font.BOLD, 16));
        viewReportsBtn.setPreferredSize(new Dimension(200, 80));

        centerPanel.add(manageBooksBtn);
        centerPanel.add(manageLoansBtn);
        centerPanel.add(searchBooksBtn);
        centerPanel.add(processReturnsBtn);
        centerPanel.add(viewReportsBtn);

        // Add empty panel for layout
        centerPanel.add(new JPanel());

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Role: Librarian | User: " + currentUser.getUsername()));
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

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