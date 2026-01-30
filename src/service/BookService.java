package service;

import model.Book;
import repository.BookRepository;
import java.util.List;
import java.util.ArrayList;  // ⭐ ADD THIS IMPORT ⭐

public class BookService {
    private BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    // Business logic methods
    public boolean addBook(Book book) {
        if (book == null || !book.validateISBN()) {
            return false;
        }
        repository.save(book);
        return true;
    }

    public boolean removeBook(String isbn) {
        return repository.delete(isbn);
    }

    public boolean updateBook(Book book) {
        if (book == null || !book.validateISBN()) {
            return false;
        }
        return repository.update(book);
    }

    public Book findBook(String isbn) {
        return repository.findById(isbn);
    }

    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    public List<Book> searchByTitle(String title) {
        return repository.findByTitle(title);
    }

    public List<Book> searchByAuthor(String author) {
        return repository.findByAuthor(author);
    }

    public List<Book> searchBooks(String query) {
        List<Book> results = new ArrayList<>();
        results.addAll(searchByTitle(query));
        results.addAll(searchByAuthor(query));
        return results;
    }
}