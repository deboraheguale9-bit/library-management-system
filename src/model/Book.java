package model;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Book implements BookSearchable, BookBorrowable {
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private boolean available;
    private int copies;

    public Book(String isbn, String title, String author, int publicationYear, int copies) {
        setIsbn(isbn);
        setTitle(title);
        setAuthor(author);
        setPublicationYear(publicationYear);
        setCopies(copies);
        this.available = (copies > 0);
    }


    public Map<String, Object> getProfile() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("isbn", isbn);
        profile.put("title", title);
        profile.put("author", author);
        profile.put("publication_year", publicationYear);
        profile.put("copies", copies);
        profile.put("available", isAvailable() ? 1 : 0); // SQLite uses integers for booleans
        profile.put("book_type", getType()); // "E-Book" or "Printed Book"


        return profile;
    }


    public static Book fromMap(Map<String, Object> data) {
        String isbn = (String) data.get("isbn");
        String title = (String) data.get("title");
        String author = (String) data.get("author");
        int pubYear = (int) data.get("publication_year");
        int copies = (int) data.get("copies");
        String bookType = (String) data.get("book_type");


        if ("E-Book".equals(bookType)) {
            double fileSize = (double) data.get("file_size_mb");
            String format = (String) data.get("format");
            String downloadLink = (String) data.get("download_link");
            boolean drmProtected = (int) data.get("drm_protected") == 1;

            return new EBook(isbn, title, author, pubYear, copies,
                    fileSize, format, downloadLink, drmProtected);
        } else if ("Printed Book".equals(bookType)) {
            String shelfLocation = (String) data.get("shelf_location");
            String condition = (String) data.get("condition");
            int edition = (int) data.get("edition");

            return new PrintedBook(isbn, title, author, pubYear, copies,
                    shelfLocation, condition, edition);
        }

        throw new IllegalArgumentException("Unknown book type: " + bookType);
    }

    public abstract String getType();
    public abstract String getSpecificDetails();


    public String getDetails() {
        return String.format("\"%s\" by %s (%d) - ISBN: %s [%d copy/copies, %s]",
                title, author, publicationYear, isbn, copies,
                isAvailable() ? "Available" : "Checked Out");
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


    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available && copies > 0;
    }


    public boolean borrowCopy() {
        if (copies > 0) {
            copies--;
            this.available = (copies > 0);
            return true;
        }
        return false;
    }

    public boolean returnCopy() {
        copies++;
        this.available = true;
        return true;
    }


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


    @Override
    public boolean borrow(Member member) {
        if (isAvailable() && member.canBorrowMore()) {
            return borrowCopy();
        }
        return false;
    }

    @Override
    public boolean returnItem() {
        return returnCopy();
    }

    @Override
    public boolean getAvailability() {
        return isAvailable();
    }

    // Default methods from BookBorrowable interface
    @Override
    public boolean canBeBorrowed() {
        return getAvailability();
    }

    @Override
    public String getBorrowStatus() {
        return getAvailability() ? "Available" : "Not Available";
    }


    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        this.isbn = isbn.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }
        this.author = author.trim();
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = Year.now().getValue();
        if (publicationYear <= currentYear && publicationYear > 1000) {
            this.publicationYear = publicationYear;
        } else {
            throw new IllegalArgumentException("Invalid publication year: " + publicationYear);
        }
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        if (copies < 0) {
            throw new IllegalArgumentException("Copies cannot be negative: " + copies);
        }
        this.copies = copies;
        this.available = (copies > 0); // Update availability
    }


    @Override
    public String toString() {
        return getDetails();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
    
}