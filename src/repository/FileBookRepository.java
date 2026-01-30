package repository;

import model.Book;
import java.util.ArrayList;
import java.util.List;

public class FileBookRepository implements BookRepository {
    private List<Book> books;
    private String filePath;

    public FileBookRepository(String filePath) {
        this.filePath = filePath;
        this.books = new ArrayList<>();
        loadFromFile(); // We'll implement this later
    }

    @Override
    public void save(Book book) {
        // Check if book already exists
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(book.getIsbn())) {
                books.set(i, book); // Update existing
                saveToFile();
                return;
            }
        }
        // Add new book
        books.add(book);
        saveToFile();
    }

    @Override
    public Book findById(String isbn) {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    @Override
    public boolean delete(String isbn) {
        boolean removed = books.removeIf(book -> book.getIsbn().equals(isbn));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    @Override
    public boolean update(Book book) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(book.getIsbn())) {
                books.set(i, book);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Book> findByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public List<Book> findByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    // These will be implemented later
    private void loadFromFile() {
        System.out.println("Loading books from: " + filePath);
        // TODO: Read from CSV/JSON file
    }

    private void saveToFile() {
        System.out.println("Saving books to: " + filePath);
        // TODO: Write to CSV/JSON file
    }
}
