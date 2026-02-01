package service;

import model.Book;
import repository.BookRepository;
import util.Validator;
import java.util.List;

public class BookService {
    private BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    // Add new book
    public boolean addBook(Book book) {
        if (book == null) {
            System.out.println("❌ Book cannot be null");
            return false;
        }

        // Validate ISBN
        if (!Validator.isValidISBN(book.getIsbn())) {
            System.out.println("❌ Invalid ISBN: " + book.getIsbn());
            return false;
        }

        // Check if book already exists
        Book existing = repository.findById(book.getIsbn());
        if (existing != null) {
            System.out.println("❌ Book with ISBN " + book.getIsbn() + " already exists");
            return false;
        }

        // Save book
        repository.save(book);
        System.out.println("✅ Book added: " + book.getTitle());
        return true;
    }

    // Get all books
    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    // Find book by ISBN
    public Book findBook(String isbn) {
        return repository.findById(isbn);
    }

    // Search books by title
    public List<Book> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return List.of();
        }
        return repository.findByTitle(title.trim());
    }

    // Search books by author
    public List<Book> searchByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return List.of();
        }
        return repository.findByAuthor(author.trim());
    }

    // Search books (title or author)
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBooks();
        }

        List<Book> results = searchByTitle(query);
        results.addAll(searchByAuthor(query));

        // Remove duplicates
        return results.stream().distinct().toList();
    }

    // Update book
    public boolean updateBook(Book book) {
        if (book == null) {
            return false;
        }
        return repository.update(book);
    }

    // Delete book
    public boolean deleteBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        return repository.delete(isbn.trim());
    }

    // Get available books only
    public List<Book> getAvailableBooks() {
        return getAllBooks().stream()
                .filter(Book::isAvailable)
                .toList();
    }

    // Borrow a book
    public boolean borrowBook(String isbn) {
        Book book = findBook(isbn);
        if (book != null && book.isAvailable()) {
            book.borrowCopy();
            updateBook(book);
            System.out.println("✅ Book borrowed: " + book.getTitle());
            return true;
        }
        return false;
    }

    // Return a book
    public boolean returnBook(String isbn) {
        Book book = findBook(isbn);
        if (book != null) {
            book.returnCopy();
            updateBook(book);
            System.out.println("✅ Book returned: " + book.getTitle());
            return true;
        }
        return false;
    }
}