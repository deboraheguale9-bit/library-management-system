package model;

public abstract class Book {
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private int copies;

    public Book(String isbn, String title, String author, int publicationYear, int copies) {
        validateIsbn(isbn);
        validateYear(publicationYear);
        validateCopies(copies);

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.copies = copies;
    }

    // Validation methods
    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        // Basic ISBN format check (10 or 13 digits with optional hyphens)
        String cleanedIsbn = isbn.replace("-", "");
        if (!cleanedIsbn.matches("\\d{10}|\\d{13}")) {
            throw new IllegalArgumentException("Invalid ISBN format. Must be 10 or 13 digits");
        }
    }

    private void validateYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        if (year < 1000 || year > currentYear + 2) { // Allow books up to 2 years in future
            throw new IllegalArgumentException("Invalid publication year: " + year);
        }
    }

    private void validateCopies(int copies) {
        if (copies < 0) {
            throw new IllegalArgumentException("Copies cannot be negative");
        }
    }

    // Abstract methods that subclasses MUST implement
    public abstract String getType();
    public abstract String getSpecificDetails();

    // Common methods for all books
    public void borrow() {
        if (isAvailable()) {
            copies--;
            System.out.println("Book borrowed: " + title);
        } else {
            System.out.println("No copies available: " + title);
        }
    }

    public void returnBook() {
        copies++;
        System.out.println("Book returned: " + title);
    }

    public boolean isAvailable() {
        return copies > 0;
    }

    public String getBookInfo() {
        return String.format("ISBN: %s | Title: %s | Author: %s | Year: %d | Copies: %d",
                isbn, title, author, publicationYear, copies);
    }

    public String getFullInfo() {
        return getBookInfo() + " | Type: " + getType() + " | Details: " + getSpecificDetails();
    }

    // Getters and Setters with validation
    public String getIsbn() { return isbn; }

    public void setIsbn(String isbn) {
        validateIsbn(isbn);
        this.isbn = isbn;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            this.author = author;
        }
    }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) {
        validateYear(publicationYear);
        this.publicationYear = publicationYear;
    }

    public int getCopies() { return copies; }
    public void setCopies(int copies) {
        validateCopies(copies);
        this.copies = copies;
    }

    @Override
    public String toString() {
        return getFullInfo();
    }
}