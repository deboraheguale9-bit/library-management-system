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

    public boolean addBook(Book book) {
        if (book == null) {
            System.out.println("❌ Book cannot be null");
            return false;
        }

        if (!Validator.isValidISBN(book.getIsbn())) {
            System.out.println("❌ Invalid ISBN: " + book.getIsbn());
            return false;
        }

        Book existing = repository.findById(book.getIsbn());
        if (existing != null) {
            System.out.println("❌ Book with ISBN " + book.getIsbn() + " already exists");
            return false;
        }

        repository.save(book);
        System.out.println("✅ Book added: " + book.getTitle());
        return true;
    }


    public List<Book> getAllBooks() {
        return repository.findAll();
    }


    public Book findBook(String isbn) {
        return repository.findById(isbn);
    }


    public List<Book> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return List.of();
        }
        return repository.findByTitle(title.trim());
    }


    public List<Book> searchByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return List.of();
        }
        return repository.findByAuthor(author.trim());
    }


    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBooks();
        }

        List<Book> results = searchByTitle(query);
        results.addAll(searchByAuthor(query));

        return results.stream().distinct().toList();
    }

    public boolean updateBook(Book book) {
        if (book == null) {
            return false;
        }
        return repository.update(book);
    }

    public boolean deleteBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        return repository.delete(isbn.trim());
    }

    public List<Book> getAvailableBooks() {
        return getAllBooks().stream()
                .filter(Book::isAvailable)
                .toList();
    }

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