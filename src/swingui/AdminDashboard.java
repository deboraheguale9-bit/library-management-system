package swingui;

import model.User;
import model.Role;
import service.UserService;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {
    private JTable userTable;
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> statusFilter;
    private UserTableModel tableModel; // Changed from DefaultTableModel to UserTableModel
    private UserService userService;
    private TableRowSorter<UserTableModel> sorter;

    public AdminDashboard() {
        this.userService = new UserService();
        setTitle("Library System - Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadUsers();
    }

    private void initComponents() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TOP: Title
        JLabel titleLabel = new JLabel("User Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // CENTER: Table with scroll pane
        tableModel = new UserTableModel(new ArrayList<>()); // Now using UserTableModel
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(25);

        // Make table sortable
        sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Users List"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // WEST: Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");
        JButton logoutButton = new JButton("Logout");

        // Style buttons
        Dimension buttonSize = new Dimension(150, 35);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        refreshButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        addButton.setMaximumSize(buttonSize);
        editButton.setMaximumSize(buttonSize);
        deleteButton.setMaximumSize(buttonSize);
        refreshButton.setMaximumSize(buttonSize);
        logoutButton.setMaximumSize(buttonSize);

        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(logoutButton);
        buttonPanel.add(Box.createVerticalGlue());

        mainPanel.add(buttonPanel, BorderLayout.WEST);

        // SOUTH: Search and filter panel
        JPanel southPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        southPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        // Search field
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        southPanel.add(new JLabel("Search:"), gbc);

        searchField = new JTextField(20);
        gbc.gridx = 1;
        southPanel.add(searchField, gbc);

        // Role filter
        gbc.gridx = 2;
        southPanel.add(new JLabel("Role:"), gbc);

        roleFilter = new JComboBox<>(new String[]{"All", "ADMIN", "LIBRARIAN", "MEMBER"});
        gbc.gridx = 3;
        southPanel.add(roleFilter, gbc);

        // Status filter
        gbc.gridx = 4;
        southPanel.add(new JLabel("Status:"), gbc);

        statusFilter = new JComboBox<>(new String[]{"All", "Active", "Inactive"});
        gbc.gridx = 5;
        southPanel.add(statusFilter, gbc);

        // Search button
        JButton searchButton = new JButton("Search");
        gbc.gridx = 6;
        gbc.fill = GridBagConstraints.NONE;
        southPanel.add(searchButton, gbc);

        // Clear button
        JButton clearButton = new JButton("Clear");
        gbc.gridx = 7;
        southPanel.add(clearButton, gbc);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUsers();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterUsers();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFilters();
            }
        });

        setContentPane(mainPanel);
    }

    private void loadUsers() {
        // Get all users from service
        List<User> users = userService.getAllUsers();

        // Update table model with users
        tableModel.setUsers(users); // Using UserTableModel's setUsers method

        JOptionPane.showMessageDialog(this,
                "Loaded " + users.size() + " users",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addUser() {
        UserDialog dialog = new UserDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            User newUser = dialog.getUser();
            boolean success = userService.addUser(newUser);
            if (success) {
                tableModel.addUser(newUser);
                JOptionPane.showMessageDialog(this,
                        "User added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add user. Username may already exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to edit",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view index to model index (in case of sorting)
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        User selectedUser = tableModel.getUserAt(modelRow);

        if (selectedUser != null) {
            UserDialog dialog = new UserDialog(this, selectedUser);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                User updatedUser = dialog.getUser();
                boolean success = userService.updateUser(updatedUser);
                if (success) {
                    tableModel.updateUser(modelRow, updatedUser);
                    JOptionPane.showMessageDialog(this,
                            "User updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update user",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = userTable.convertRowIndexToModel(selectedRow);
            User selectedUser = tableModel.getUserAt(modelRow);

            if (selectedUser != null) {
                boolean success = userService.deleteUser(selectedUser.getId());
                if (success) {
                    tableModel.removeUser(modelRow);
                    JOptionPane.showMessageDialog(this,
                            "User deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete user",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = (String) roleFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        RowFilter<UserTableModel, Object> rf = RowFilter.regexFilter(".*", 0); // Show all initially

        if (!searchText.isEmpty() || !"All".equals(selectedRole) || !"All".equals(selectedStatus)) {
            rf = new RowFilter<UserTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends UserTableModel, ? extends Object> entry) {
                    // Get row data
                    UserTableModel model = entry.getModel();
                    int row = entry.getIdentifier().intValue();

                    // Check search text
                    boolean matchesSearch = true;
                    if (!searchText.isEmpty()) {
                        matchesSearch = false;
                        for (int i = 0; i < model.getColumnCount(); i++) {
                            Object value = model.getValueAt(row, i);
                            if (value != null && value.toString().toLowerCase().contains(searchText)) {
                                matchesSearch = true;
                                break;
                            }
                        }
                    }

                    // Check role filter
                    boolean matchesRole = true;
                    if (!"All".equals(selectedRole)) {
                        String userRole = model.getValueAt(row, 5).toString(); // Role is column 5
                        matchesRole = userRole.equals(selectedRole);
                    }

                    // Check status filter
                    boolean matchesStatus = true;
                    if (!"All".equals(selectedStatus)) {
                        String userStatus = model.getValueAt(row, 6).toString(); // Status is column 6
                        String expectedStatus = "Active".equals(selectedStatus) ? "Yes" : "No";
                        matchesStatus = userStatus.equals(expectedStatus);
                    }

                    return matchesSearch && matchesRole && matchesStatus;
                }
            };
        }

        sorter.setRowFilter(rf);
    }

    private void clearFilters() {
        searchField.setText("");
        roleFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        sorter.setRowFilter(null);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // In a real app, you might want to save state or show login screen
            JOptionPane.showMessageDialog(this, "Logged out successfully!");
            // For now, just exit
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard adminDashboard = new AdminDashboard();
            adminDashboard.setVisible(true);
        });
    }
}