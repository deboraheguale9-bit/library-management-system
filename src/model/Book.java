package model;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public abstract class Book implements BookSearchable, BookBorrowable {
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private boolean available;
    private int copies;

    public Book(String isbn, String title, String author, int publicationYear, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.available = true;
        this.copies = copies;
    }

    public String getDetails() {
        return String.format("\"%s\" by %s (%d) - ISBN: %s",
                title, author, publicationYear, isbn);
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available && copies > 0;
    }

    public boolean validateISBN() {
        String cleanISBN = isbn.replaceAll("[\\s-]", "");

        if (cleanISBN.length() != 10 && cleanISBN.length() != 13) {
            return false;
        }

        if (cleanISBN.length() == 10) {
            if (!cleanISBN.matches("[0-9]{9}[0-9X]")) {
                return false;
            }
        } else {
            if (!cleanISBN.matches("[0-9]{13}")) {
                return false;
            }
        }

        return true;
    }

    // Abstract methods for subclasses
    public abstract String getType();
    public abstract String getSpecificDetails();

    // ====================
    // SEARCHABLE INTERFACE
    // ====================
    @Override
    public List<Book> searchByTitle(String title) {
        List<Book> result = new ArrayList<>();
        if (this.title.toLowerCase().contains(title.toLowerCase())) {
            result.add(this);
        }
        return result;
    }

    @Override
    public List<Book> searchByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        if (this.author.toLowerCase().contains(author.toLowerCase())) {
            result.add(this);
        }
        return result;
    }

    @Override
    public Book searchByISBN(String isbn) {
        if (this.isbn.equals(isbn)) {
            return this;
        }
        return null;
    }

    // Default method from BookSearchable interface
    @Override
    public List<Book> search(String query) {
        List<Book> results = searchByTitle(query);
        results.addAll(searchByAuthor(query));

        Book byISBN = searchByISBN(query);
        if (byISBN != null && !results.contains(byISBN)) {
            results.add(byISBN);
        }

        return results;
    }

    // ====================
    // BORROWABLE INTERFACE
    // ====================
    @Override
    public boolean borrow(Member member) {
        if (isAvailable() && member.canBorrowMore()) {
            setAvailable(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean returnItem() {
        if (!isAvailable()) {
            setAvailable(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean getAvailability() {
        return isAvailable();
    }

    // Default methods from Borrowable interface
    @Override
    public boolean canBeBorrowed() {
        return getAvailability();
    }

    @Override
    public String getBorrowStatus() {
        return getAvailability() ? "Available" : "Not Available";
    }

    // ====================
    // GETTERS AND SETTERS
    // ====================
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = Year.now().getValue();
        if (publicationYear <= currentYear && publicationYear > 1000) {
            this.publicationYear = publicationYear;
        } else {
            throw new IllegalArgumentException("Invalid publication year");
        }
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        if (copies >= 0) {
            this.copies = copies;
        } else {
            throw new IllegalArgumentException("Copies cannot be negative");
        }
    }

    @Override
    public String toString() {
        return getDetails();
    }
}