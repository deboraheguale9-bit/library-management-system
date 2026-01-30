package service;

import model.Book;
import repository.BookRepository;
import util.Validator;
import java.util.ArrayList;
import java.util.List;

public class BookService {
    private BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    // Add a new book with validation
    public boolean addBook(Book book) {
        if (book == null) {
            System.out.println("❌ Book cannot be null");
            return false;
        }

        // Validate ISBN
        if (!Validator.isValidISBN(book.getIsbn())) {
            System.out.println("❌ Invalid ISBN: " + book.getIsbn());
            System.out.println("   ISBN must be 10 or 13 digits");
            return false;
        }

        // Check if book already exists
        Book existingBook = repository.findById(book.getIsbn());
        if (existingBook != null) {
            System.out.println("❌ Book with ISBN " + book.getIsbn() + " already exists");
            return false;
        }

        // Validate publication year
        if (!Validator.isValidYear(book.getPublicationYear())) {
            System.out.println("❌ Invalid publication year: " + book.getPublicationYear());
            return false;
        }

        // Validate copies count
        if (!Validator.isNonNegative(book.getCopies())) {
            System.out.println("❌ Invalid copies count: " + book.getCopies());
            System.out.println("   Copies cannot be negative");
            return false;
        }

        // Validate title and author
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            System.out.println("❌ Book title cannot be empty");
            return false;
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            System.out.println("❌ Author name cannot be empty");
            return false;
        }

        if (book.getTitle().length() > 200) {
            System.out.println("❌ Book title is too long (max 200 characters)");
            return false;
        }

        if (book.getAuthor().length() > 100) {
            System.out.println("❌ Author name is too long (max 100 characters)");
            return false;
        }

        // Save the book
        repository.save(book);
        System.out.println("✅ Book added successfully: " + book.getTitle());
        return true;
    }

    // Remove a book
    public boolean removeBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.out.println("❌ ISBN cannot be empty");
            return false;
        }

        if (!Validator.isValidISBN(isbn)) {
            System.out.println("❌ Invalid ISBN format: " + isbn);
            return false;
        }

        boolean deleted = repository.delete(isbn);
        if (deleted) {
            System.out.println("✅ Book with ISBN " + isbn + " removed successfully");
        } else {
            System.out.println("❌ Book with ISBN " + isbn + " not found");
        }
        return deleted;
    }

    // Update a book with validation
    public boolean updateBook(Book book) {
        if (book == null) {
            System.out.println("❌ Book cannot be null");
            return false;
        }

        // Check if book exists
        Book existingBook = repository.findById(book.getIsbn());
        if (existingBook == null) {
            System.out.println("❌ Book with ISBN " + book.getIsbn() + " not found");
            return false;
        }

        // Validate updated data
        if (!Validator.isValidYear(book.getPublicationYear())) {
            System.out.println("❌ Invalid publication year: " + book.getPublicationYear());
            return false;
        }

        if (!Validator.isNonNegative(book.getCopies())) {
            System.out.println("❌ Invalid copies count: " + book.getCopies());
            return false;
        }

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            System.out.println("❌ Book title cannot be empty");
            return false;
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            System.out.println("❌ Author name cannot be empty");
            return false;
        }

        boolean updated = repository.update(book);
        if (updated) {
            System.out.println("✅ Book updated successfully: " + book.getTitle());
        }
        return updated;
    }

    // Find a book by ISBN
    public Book findBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.out.println("⚠️ ISBN cannot be empty for search");
            return null;
        }

        Book book = repository.findById(isbn);
        if (book == null) {
            System.out.println("ℹ️ No book found with ISBN: " + isbn);
        }
        return book;
    }

    // Get all books
    public List<Book> getAllBooks() {
        List<Book> books = repository.findAll();
        System.out.println("📚 Found " + books.size() + " books in the library");
        return books;
    }

    // Search by title
    public List<Book> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            System.out.println("⚠️ Search title cannot be empty");
            return new ArrayList<>();
        }

        if (title.length() < 2) {
            System.out.println("⚠️ Search term must be at least 2 characters");
            return new ArrayList<>();
        }

        List<Book> results = repository.findByTitle(title);
        System.out.println("🔍 Found " + results.size() + " books with title containing: " + title);
        return results;
    }

    // Search by author
    public List<Book> searchByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            System.out.println("⚠️ Author name cannot be empty");
            return new ArrayList<>();
        }

        if (author.length() < 2) {
            System.out.println("⚠️ Author name must be at least 2 characters");
            return new ArrayList<>();
        }

        List<Book> results = repository.findByAuthor(author);
        System.out.println("👤 Found " + results.size() + " books by author: " + author);
        return results;
    }

    // Combined search
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            System.out.println("⚠️ Search query cannot be empty");
            return new ArrayList<>();
        }

        if (query.length() < 2) {
            System.out.println("⚠️ Search term must be at least 2 characters");
            return new ArrayList<>();
        }

        List<Book> results = new ArrayList<>();

        // Try searching by ISBN first (exact match)
        if (Validator.isValidISBN(query)) {
            Book byISBN = repository.findById(query);
            if (byISBN != null) {
                results.add(byISBN);
                System.out.println("🔍 Found book by ISBN: " + query);
                return results;
            }
        }

        // Search by title and author
        List<Book> byTitle = repository.findByTitle(query);
        List<Book> byAuthor = repository.findByAuthor(query);

        // Combine results, avoiding duplicates
        for (Book book : byTitle) {
            if (!results.contains(book)) {
                results.add(book);
            }
        }

        for (Book book : byAuthor) {
            if (!results.contains(book)) {
                results.add(book);
            }
        }

        System.out.println("🔍 Found " + results.size() + " books for query: " + query);
        return results;
    }

    // Get available books only
    public List<Book> getAvailableBooks() {
        List<Book> allBooks = repository.findAll();
        List<Book> availableBooks = new ArrayList<>();

        for (Book book : allBooks) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }

        System.out.println("✅ Found " + availableBooks.size() + " available books out of " + allBooks.size());
        return availableBooks;
    }

    // Get unavailable books
    public List<Book> getUnavailableBooks() {
        List<Book> allBooks = repository.findAll();
        List<Book> unavailableBooks = new ArrayList<>();

        for (Book book : allBooks) {
            if (!book.isAvailable()) {
                unavailableBooks.add(book);
            }
        }

        System.out.println("ℹ️ Found " + unavailableBooks.size() + " unavailable books");
        return unavailableBooks;
    }

    // Get books by type (EBook or PrintedBook)
    public List<Book> getBooksByType(String type) {
        if (type == null || (!type.equalsIgnoreCase("EBOOK") && !type.equalsIgnoreCase("PRINTED"))) {
            System.out.println("❌ Invalid book type. Use 'EBOOK' or 'PRINTED'");
            return new ArrayList<>();
        }

        List<Book> allBooks = repository.findAll();
        List<Book> filteredBooks = new ArrayList<>();

        for (Book book : allBooks) {
            if (type.equalsIgnoreCase("EBOOK") && book.getType().equals("E-Book")) {
                filteredBooks.add(book);
            } else if (type.equalsIgnoreCase("PRINTED") && book.getType().equals("Printed Book")) {
                filteredBooks.add(book);
            }
        }

        System.out.println("📊 Found " + filteredBooks.size() + " " + type.toLowerCase() + " books");
        return filteredBooks;
    }

    // Check if a book is available
    public boolean isBookAvailable(String isbn) {
        if (!Validator.isValidISBN(isbn)) {
            return false;
        }

        Book book = repository.findById(isbn);
        return book != null && book.isAvailable();
    }

    // Get book count statistics
    public void printStatistics() {
        List<Book> allBooks = repository.findAll();
        long totalBooks = allBooks.size();
        long availableBooks = allBooks.stream().filter(Book::isAvailable).count();
        long eBooks = allBooks.stream().filter(b -> b.getType().equals("E-Book")).count();
        long printedBooks = allBooks.stream().filter(b -> b.getType().equals("Printed Book")).count();

        System.out.println("\n=== 📊 Library Statistics ===");
        System.out.println("Total Books: " + totalBooks);
        System.out.println("Available: " + availableBooks);
        System.out.println("Borrowed: " + (totalBooks - availableBooks));
        System.out.println("E-Books: " + eBooks);
        System.out.println("Printed Books: " + printedBooks);

        if (totalBooks > 0) {
            double availabilityRate = (double) availableBooks / totalBooks * 100;
            System.out.println("Availability Rate: " + String.format("%.1f", availabilityRate) + "%");
        }
    }
}