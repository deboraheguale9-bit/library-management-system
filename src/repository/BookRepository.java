package repository;

import model.Book;
import java.util.List;

public interface BookRepository {
    // CRUD operations
    void save(Book book);
    Book findById(String isbn);
    List<Book> findAll();
    boolean delete(String isbn);
    boolean update(Book book);

    // Search operations
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
}
