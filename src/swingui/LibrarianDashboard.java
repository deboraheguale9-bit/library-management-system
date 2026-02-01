package swingui;

import model.Book;
import model.User;
import repository.SQLiteBookRepository;
import service.BookService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibrarianDashboard extends JFrame {
    private User currentUser;
    private BookService bookService;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public LibrarianDashboard(User loggedInUser) {
        this.currentUser = loggedInUser;

        SQLiteBookRepository bookRepo = new SQLiteBookRepository();
        bookService = new BookService(bookRepo);

        setupUI();
        loadBooks();
    }

    private void setupUI() {
        setTitle("Librarian Dashboard - Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Book Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (Librarian)");
        welcomeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        headerPanel.add(logoutBtn, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] columns = {"ISBN", "Title", "Author", "Year", "Copies", "Available", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addBtn = new JButton("Add Book");
        JButton editBtn = new JButton("Edit Book");
        JButton deleteBtn = new JButton("Delete Book");
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");
        JButton manageLoansBtn = new JButton("Manage Loans");

        addBtn.addActionListener(e -> addBook());
        editBtn.addActionListener(e -> editBook());
        deleteBtn.addActionListener(e -> deleteBook());
        searchBtn.addActionListener(e -> searchBooks());
        refreshBtn.addActionListener(e -> loadBooks());
        manageLoansBtn.addActionListener(e -> manageLoans());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(manageLoansBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookService.getAllBooks();

        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublicationYear(),
                    book.getCopies(),
                    book.isAvailable() ? "Yes" : "No",
                    book.getType()
            });
        }
    }

    private void addBook() {
        JDialog dialog = new JDialog(this, "Add New Book", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField isbnField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField copiesField = new JTextField("1");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"E-Book", "Printed Book"});

        dialog.add(new JLabel("ISBN:"));
        dialog.add(isbnField);
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Author:"));
        dialog.add(authorField);
        dialog.add(new JLabel("Year:"));
        dialog.add(yearField);
        dialog.add(new JLabel("Copies:"));
        dialog.add(copiesField);
        dialog.add(new JLabel("Type:"));
        dialog.add(typeCombo);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Book added!");
            dialog.dispose();
            loadBooks();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(new JLabel());
        dialog.add(buttonPanel);

        dialog.setVisible(true);
    }

    private void editBook() {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit");
            return;
        }

        String isbn = (String) tableModel.getValueAt(row, 0);
        Book book = bookService.findBook(isbn);

        if (book != null) {
            JOptionPane.showMessageDialog(this, "Edit book: " + book.getTitle());
        }
    }

    private void deleteBook() {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete");
            return;
        }

        String isbn = (String) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete book: " + title + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookService.deleteBook(isbn)) {
                JOptionPane.showMessageDialog(this, "Book deleted");
                loadBooks();
            }
        }
    }

    private void searchBooks() {
        String query = JOptionPane.showInputDialog(this, "Enter search term (title or author):");
        if (query != null && !query.trim().isEmpty()) {
            tableModel.setRowCount(0);
            List<Book> results = bookService.searchBooks(query.trim());

            for (Book book : results) {
                tableModel.addRow(new Object[]{
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublicationYear(),
                        book.getCopies(),
                        book.isAvailable() ? "Yes" : "No",
                        book.getType()
                });
            }

            JOptionPane.showMessageDialog(this, "Found " + results.size() + " books");
        }
    }

    private void manageLoans() {
        JOptionPane.showMessageDialog(this, "Loan management - To be implemented");
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Logout?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginWindow().setVisible(true);
        }
    }
}
