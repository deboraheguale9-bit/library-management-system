package swingui;

import model.User;
import model.UserRole;
import repository.FileUserRepository;
import repository.SQLiteUserRepository;
import service.UserService;
import util.Validator;
import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<UserRole> roleCombo;

    public LoginWindow() {
        setTitle("Library Management System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        SQLiteUserRepository userRepository = new SQLiteUserRepository();
        userService = new UserService(userRepository);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(UserRole.values());
        panel.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(100, 35));
        panel.add(loginBtn, gbc);

        gbc.gridy = 5;
        JButton registerBtn = new JButton("Register New User");
        registerBtn.setBackground(new Color(60, 179, 113));
        registerBtn.setForeground(Color.WHITE);
        panel.add(registerBtn, gbc);

        gbc.gridy = 6;
        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel, gbc);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            UserRole selectedRole = (UserRole) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields!");
                return;
            }

            User authenticatedUser = userService.authenticate(username, password);

            if (authenticatedUser != null) {
                // Check if user's role matches selected role
                if (authenticatedUser.getRole() == selectedRole) {
                    statusLabel.setText("Login successful!");
                    JOptionPane.showMessageDialog(this,
                            "Welcome, " + authenticatedUser.getName() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    this.dispose();

                    openDashboard(authenticatedUser);

                } else {
                    statusLabel.setText("Wrong role selected! You are a " + authenticatedUser.getRole());
                }
            } else {
                statusLabel.setText("Invalid username or password!");
            }
        });

        registerBtn.addActionListener(e -> {
            openRegistrationWindow();
        });

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ENTER"), "login");
        panel.getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                loginBtn.doClick();
            }
        });

        add(panel);
        setVisible(true);
    }

    private void openDashboard(User user) {
        switch (user.getRole()) {
            case ADMIN:
                new AdminDashboard(user).setVisible(true);
                break;
            case LIBRARIAN:
                new LibrarianDashboard(user).setVisible(true);
                break;
            case MEMBER:
                new MemberDashboard(user).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown role: " + user.getRole(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistrationWindow() {
        JDialog registerDialog = new JDialog(this, "Register New User", true);
        registerDialog.setSize(400, 500);
        registerDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField regUsernameField = new JTextField(20);
        JPasswordField regPasswordField = new JPasswordField(20);
        JComboBox<UserRole> regRoleCombo = new JComboBox<>(UserRole.values());

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        registerDialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(nameField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        registerDialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(emailField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        registerDialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(phoneField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        registerDialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(regUsernameField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        registerDialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(regPasswordField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        registerDialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(regRoleCombo, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton submitBtn = new JButton("Register");
        registerDialog.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            UserRole role = (UserRole) regRoleCombo.getSelectedItem();

            if (!Validator.isValidName(name)) {
                JOptionPane.showMessageDialog(registerDialog, "Invalid name format!");
                return;
            }

            if (!Validator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(registerDialog, "Invalid email format!");
                return;
            }

            if (!Validator.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(registerDialog, "Invalid phone number!");
                return;
            }

            String id = "U" + System.currentTimeMillis();

            User newUser = new User(id, name, email, phone, username, password, role) {
            };

            if (userService.registerUser(newUser)) {
                JOptionPane.showMessageDialog(registerDialog, "Registration successful!");
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registerDialog, "Registration failed. Username may already exist.");
            }
        });

        registerDialog.setLocationRelativeTo(this);
        registerDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}









