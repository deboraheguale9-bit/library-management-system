package swingui;

import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchBooksWindow extends JFrame {
    private User currentUser;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public SearchBooksWindow(User user) {
        this.currentUser = user;
        setupUI();
        loadBooks();
    }

    private void setupUI() {
        setTitle("Search Books - Library Management System");
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        String[] columns = {"ISBN", "Title", "Author", "Year", "Type", "Available", "Copies"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton borrowBtn = new JButton("Borrow Selected");
        JButton closeBtn = new JButton("Close");

        buttonPanel.add(borrowBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        searchBtn.addActionListener(e -> searchBooks(searchField.getText()));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadBooks();
        });
        closeBtn.addActionListener(e -> dispose());
        borrowBtn.addActionListener(e -> borrowSelectedBook());
    }

    private void loadBooks() {
        tableModel.setRowCount(0);

        Object[][] demoBooks = {
                {"9780134685991", "Effective Java", "Joshua Bloch", 2018, "E-BOOK", 3, 3},
                {"9780596009205", "Head First Java", "Kathy Sierra", 2005, "PRINTED", 2, 2},
                {"9780201633610", "Design Patterns", "Erich Gamma", 1994, "PRINTED", 1, 1},
                {"9780321356680", "Effective Java 2nd Ed", "Joshua Bloch", 2008, "PRINTED", 1, 1},
                {"9780132350884", "Clean Code", "Robert Martin", 2008, "PRINTED", 2, 2}
        };

        for (Object[] book : demoBooks) {
            tableModel.addRow(book);
        }

        System.out.println("🔍 Showing all books for searching");
    }

    private void searchBooks(String query) {
        tableModel.setRowCount(0);

        Object[][] allBooks = {
                {"9780134685991", "Effective Java", "Joshua Bloch", 2018, "E-BOOK", 3, 3},
                {"9780596009205", "Head First Java", "Kathy Sierra", 2005, "PRINTED", 2, 2},
                {"9780201633610", "Design Patterns", "Erich Gamma", 1994, "PRINTED", 1, 1},
                {"9780321356680", "Effective Java 2nd Ed", "Joshua Bloch", 2008, "PRINTED", 1, 1},
                {"9780132350884", "Clean Code", "Robert Martin", 2008, "PRINTED", 2, 2}
        };

        String queryLower = query.toLowerCase();

        for (Object[] book : allBooks) {
            String title = ((String) book[1]).toLowerCase();
            String author = ((String) book[2]).toLowerCase();
            String isbn = (String) book[0];

            if (query.isEmpty() ||
                    title.contains(queryLower) ||
                    author.contains(queryLower) ||
                    isbn.contains(query)) {
                tableModel.addRow(book);
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No books found matching: " + query);
        }
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
            JOptionPane.showMessageDialog(this, "Sorry, '" + title + "' is not available.");
            return;
        }

        tableModel.setValueAt(available - 1, selectedRow, 5);

        JOptionPane.showMessageDialog(this,
                "DEMO: '" + title + "' borrowed successfully!\n" +
                        "New availability: " + (available - 1) + " copies\n" +
                        "(Would create loan record in database)");
    }
}