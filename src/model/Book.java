package model;

import java.time.Year;

public abstract class Book {
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

    // ⭐ ADD THIS METHOD IF MISSING ⭐
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

    // Getters and Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) {
        int currentYear = Year.now().getValue();
        if (publicationYear <= currentYear && publicationYear > 1000) {
            this.publicationYear = publicationYear;
        } else {
            throw new IllegalArgumentException("Invalid publication year");
        }
    }

    public int getCopies() { return copies; }
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