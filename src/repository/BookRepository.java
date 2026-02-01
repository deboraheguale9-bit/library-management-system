package repository;

import model.Book;
import java.util.List;

public interface BookRepository {
    void save(Book book);
    Book findById(String isbn);
    List<Book> findAll();
    boolean delete(String isbn);
    boolean update(Book book);

    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
}
