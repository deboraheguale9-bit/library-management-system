package swingui;

import model.User;
import model.UserRole;
import repository.FileUserRepository;
import service.UserService;
import util.Validator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private UserService userService;
    private JTable userTable;
    private UserTableModel tableModel;

    public AdminDashboard(User loggedInUser) {
        this.currentUser = loggedInUser;

        String userFilePath = "users.txt";
        FileUserRepository userRepository = new FileUserRepository(userFilePath);
        userService = new UserService(userRepository);

        setupUI();
        loadUsers();
    }

    private void setupUI() {
        setTitle("Admin Dashboard - Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("User Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (Admin)");
        welcomeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        headerPanel.add(logoutBtn, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());

        tableModel = new UserTableModel(new ArrayList<>());

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton addBtn = new JButton("Add User");
        addBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setPreferredSize(new Dimension(120, 40));
        addBtn.addActionListener(e -> addUser());

        JButton editBtn = new JButton("Edit User");
        editBtn.setFont(new Font("Arial", Font.BOLD, 14));
        editBtn.setBackground(new Color(30, 144, 255));
        editBtn.setForeground(Color.WHITE);
        editBtn.setPreferredSize(new Dimension(120, 40));
        editBtn.addActionListener(e -> editUser());

        JButton deleteBtn = new JButton("Delete User");
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 14));
        deleteBtn.setBackground(new Color(220, 20, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setPreferredSize(new Dimension(120, 40));
        deleteBtn.addActionListener(e -> deleteUser());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setPreferredSize(new Dimension(120, 40));
        refreshBtn.addActionListener(e -> loadUsers());

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchBtn.addActionListener(e -> searchUsers());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(searchBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers();

        tableModel.setUsers(users);

        JOptionPane.showMessageDialog(this,
                "Loaded " + users.size() + " users",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addUser() {
        JDialog addDialog = new JDialog(this, "Add New User", true);
        addDialog.setSize(400, 500);
        addDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
        JCheckBox activeCheckBox = new JCheckBox("Active", true);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        addDialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        addDialog.add(nameField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        addDialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        addDialog.add(emailField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        addDialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        addDialog.add(phoneField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        addDialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        addDialog.add(usernameField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        addDialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        addDialog.add(passwordField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        addDialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        addDialog.add(roleCombo, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        addDialog.add(activeCheckBox, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        addDialog.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            UserRole role = (UserRole) roleCombo.getSelectedItem();
            boolean active = activeCheckBox.isSelected();

            if (!Validator.isValidName(name)) {
                JOptionPane.showMessageDialog(addDialog, "Invalid name format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Validator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(addDialog, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Validator.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(addDialog, "Invalid phone number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(addDialog, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Validator.isStrongPassword(password)) {
                JOptionPane.showMessageDialog(addDialog,
                        "Password must be at least 8 characters with uppercase, lowercase, number, and special character!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!userService.isUsernameAvailable(username)) {
                JOptionPane.showMessageDialog(addDialog, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!userService.isEmailAvailable(email)) {
                JOptionPane.showMessageDialog(addDialog, "Email already registered!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = "U" + System.currentTimeMillis();
            User newUser = new User(id, name, email, phone, username, password, role) {
            };
            newUser.setActive(active);

            if (userService.registerUser(newUser)) {
                JOptionPane.showMessageDialog(addDialog, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                addDialog.dispose();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(addDialog, "Failed to add user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> addDialog.dispose());

        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = tableModel.getUserAt(selectedRow);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog editDialog = new JDialog(this, "Edit User", true);
        editDialog.setSize(400, 500);
        editDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(user.getName(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JTextField phoneField = new JTextField(user.getMobile(), 20);
        JTextField usernameField = new JTextField(user.getUsername(), 20);
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setSelectedItem(user.getRole());
        JCheckBox activeCheckBox = new JCheckBox("Active", user.isActive());

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        editDialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        editDialog.add(nameField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        editDialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        editDialog.add(emailField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        editDialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        editDialog.add(phoneField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        editDialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        editDialog.add(usernameField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        editDialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        editDialog.add(roleCombo, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        editDialog.add(activeCheckBox, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        editDialog.add(new JLabel("Change Password (leave empty to keep current):"), gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        editDialog.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        editDialog.add(passwordField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        editDialog.add(buttonPanel, gbc);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = usernameField.getText().trim();
            UserRole role = (UserRole) roleCombo.getSelectedItem();
            boolean active = activeCheckBox.isSelected();
            String newPassword = new String(passwordField.getPassword());

            if (!Validator.isValidName(name)) {
                JOptionPane.showMessageDialog(editDialog, "Invalid name format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Validator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(editDialog, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Validator.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(editDialog, "Invalid phone number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!username.equals(user.getUsername()) && !userService.isUsernameAvailable(username)) {
                JOptionPane.showMessageDialog(editDialog, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.equals(user.getEmail()) && !userService.isEmailAvailable(email)) {
                JOptionPane.showMessageDialog(editDialog, "Email already registered!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            user.setName(name);
            user.setEmail(email);
            user.setMobile(phone);
            user.setUsername(username);
            user.setRole(role);
            user.setActive(active);

            if (!newPassword.isEmpty()) {
                if (!Validator.isStrongPassword(newPassword)) {
                    JOptionPane.showMessageDialog(editDialog,
                            "Password must be at least 8 characters with uppercase, lowercase, number, and special character!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                user.changePassword(newPassword);
            }

            if (userService.updateUser(user)) {
                JOptionPane.showMessageDialog(editDialog, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(editDialog, "Failed to update user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> editDialog.dispose());

        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = tableModel.getUserAt(selectedRow);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user: " + user.getName() + "?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userService.deleteUser(user.getId())) {
                tableModel.removeUser(selectedRow);
                JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchUsers() {
        String searchTerm = JOptionPane.showInputDialog(this, "Enter name to search:", "Search Users", JOptionPane.QUESTION_MESSAGE);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            List<User> searchResults = userService.searchUsersByName(searchTerm.trim());

            tableModel.setUsers(searchResults);

            JOptionPane.showMessageDialog(this,
                    "Found " + searchResults.size() + " user(s)",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
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

    public static void main(String[] args) {
        // For testing only - create a mock admin user
        User mockAdmin = new User("admin001", "Test Admin", "admin@test.com",
                "123-4567", "admin", "admin123", UserRole.ADMIN) {
        };

        SwingUtilities.invokeLater(() -> new AdminDashboard(mockAdmin));
    }
}






