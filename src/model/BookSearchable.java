package model;

import java.util.List;

public interface BookSearchable {
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);
    Book searchByISBN(String isbn);

    // Optional: Combined search
    default List<Book> search(String query) {
        List<Book> results = searchByTitle(query);
        results.addAll(searchByAuthor(query));

        Book byISBN = searchByISBN(query);
        if (byISBN != null && !results.contains(byISBN)) {
            results.add(byISBN);
        }

        return results;
    }
}
