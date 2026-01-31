package model;

import java.util.HashMap;
import java.util.Map;

/**
 * PrintedBook - physical book with shelf location and condition
 * Correctly does NOT implement MediaSearchable (Interface Segregation Principle)
 */
public class PrintedBook extends Book {
    private String shelfLocation;
    private String condition;
    private int edition;
    private boolean isReserved;

    public PrintedBook(String isbn, String title, String author,
                       int publicationYear, int copies,
                       String shelfLocation, String condition, int edition) {
        super(isbn, title, author, publicationYear, copies);

        // Enhanced validation
        if (shelfLocation == null || shelfLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Shelf location cannot be null or empty");
        }
        if (edition <= 0) {
            throw new IllegalArgumentException("Edition must be positive: " + edition);
        }

        this.shelfLocation = shelfLocation.trim();
        updateCondition(condition); // Use validation method
        this.edition = edition;
        this.isReserved = false;
    }

    // ====================
    // SQLITE-COMPATIBLE getProfile()
    // ====================

    /**
     * Returns printed book data as Map for SQLite database storage
     * Overrides parent method to add printed book-specific fields
     * Column names match database schema
     */
    @Override
    public Map<String, Object> getProfile() {
        Map<String, Object> profile = super.getProfile(); // Get base Book fields

        // Add PrintedBook-specific fields for SQLite
        profile.put("shelf_location", shelfLocation);
        profile.put("condition", condition);
        profile.put("edition", edition);
        profile.put("is_reserved", isReserved ? 1 : 0); // Boolean to int for SQLite

        // Set EBook-specific fields to null (since this is a PrintedBook)
        profile.put("file_size_mb", null);
        profile.put("format", null);
        profile.put("download_link", null);
        profile.put("drm_protected", null);
        profile.put("download_url", null);

        // Additional calculated field for UI purposes
        profile.put("needs_repair", needsRepair() ? 1 : 0);

        return profile;
    }

    /**
     * Static factory method to create PrintedBook from database Map
     * Used by BookRepositorySQLite when loading from database
     */
    public static PrintedBook fromMap(Map<String, Object> data) {
        // Extract base Book fields
        String isbn = (String) data.get("isbn");
        String title = (String) data.get("title");
        String author = (String) data.get("author");
        int publicationYear = (int) data.get("publication_year");
        int copies = (int) data.get("copies");

        // Extract PrintedBook-specific fields
        String shelfLocation = (String) data.get("shelf_location");
        String condition = (String) data.get("condition");
        int edition = (int) data.get("edition");

        return new PrintedBook(isbn, title, author, publicationYear, copies,
                shelfLocation, condition, edition);
    }

    // ====================
    // LOCATION METHODS
    // ====================
    public String getLocation() {
        return shelfLocation;
    }

    public void relocate(String newShelfLocation) {
        if (newShelfLocation == null || newShelfLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("New shelf location cannot be empty");
        }
        System.out.println("Relocating " + getTitle() +
                " from " + shelfLocation + " to " + newShelfLocation);
        this.shelfLocation = newShelfLocation.trim();
    }

    /**
     * Gets the shelf section (first part before dash)
     */
    public String getShelfSection() {
        if (shelfLocation.contains("-")) {
            return shelfLocation.split("-")[0].trim();
        }
        return shelfLocation;
    }

    // ====================
    // CONDITION MANAGEMENT
    // ====================
    public void updateCondition(String newCondition) {
        String[] validConditions = {"New", "Like New", "Good", "Fair", "Poor"};
        for (String valid : validConditions) {
            if (valid.equalsIgnoreCase(newCondition)) {
                this.condition = valid; // Standardize to proper case
                return;
            }
        }
        throw new IllegalArgumentException("Invalid condition: " + newCondition +
                ". Valid: New, Like New, Good, Fair, Poor");
    }

    public boolean needsRepair() {
        return "Poor".equalsIgnoreCase(condition) || "Fair".equalsIgnoreCase(condition);
    }

    /**
     * Gets condition score (5 = New, 1 = Poor)
     */
    public int getConditionScore() {
        switch (condition.toLowerCase()) {
            case "new": return 5;
            case "like new": return 4;
            case "good": return 3;
            case "fair": return 2;
            case "poor": return 1;
            default: return 0;
        }
    }

    // ====================
    // RESERVATION SYSTEM
    // ====================
    public boolean reserve() {
        if (!isReserved && super.isAvailable()) {
            isReserved = true;
            System.out.println("Reserved: " + getTitle() + " for pickup at " + shelfLocation);
            return true;
        }
        System.out.println("Cannot reserve " + getTitle() +
                " - Already reserved or not available");
        return false;
    }

    public boolean cancelReservation() {
        if (isReserved) {
            isReserved = false;
            System.out.println("Cancelled reservation for: " + getTitle());
            return true;
        }
        return false;
    }

    public boolean pickupReservedBook(Member member) {
        if (isReserved && super.isAvailable() && member.canBorrowMore()) {
            isReserved = false;
            // Borrow the book for the member
            return borrow(member);
        }
        return false;
    }

    /**
     * Checks if book is overdue based on reservation
     */
    public boolean isReservationOverdue(int maxReservationHours) {
        // In real app, would track reservation timestamp
        // For now, return false as placeholder
        return false;
    }

    // ====================
    // BOOK ABSTRACT METHODS
    // ====================
    @Override
    public String getType() {
        return "Printed Book";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Shelf: %s | Condition: %s (%d/5) | Edition: %d%s",
                shelfLocation, condition, getConditionScore(), edition,
                isReserved ? " (Reserved)" : "");
    }

    // ====================
    // OVERRIDDEN AVAILABILITY
    // ====================
    @Override
    public boolean isAvailable() {
        // A printed book is available if:
        // 1. It's not reserved AND
        // 2. The parent Book class says it's available
        return !isReserved && super.isAvailable();
    }

    @Override
    public void setAvailable(boolean available) {
        // If setting to unavailable, also cancel any reservation
        if (!available) {
            isReserved = false;
        }
        super.setAvailable(available);
    }

    @Override
    public boolean borrow(Member member) {
        // Override to handle reservation logic
        if (isReserved) {
            System.out.println("Cannot borrow reserved book: " + getTitle());
            return false;
        }
        return super.borrow(member);
    }

    // ====================
    // GETTERS AND SETTERS
    // ====================
    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        if (shelfLocation == null || shelfLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Shelf location cannot be null or empty");
        }
        this.shelfLocation = shelfLocation.trim();
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        updateCondition(condition); // Use validation method
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        if (edition <= 0) {
            throw new IllegalArgumentException("Edition must be positive: " + edition);
        }
        this.edition = edition;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    // ====================
    // OVERRIDDEN METHODS
    // ====================
    @Override
    public String toString() {
        return super.toString() + " [" + getSpecificDetails() + "]";
    }

    /**
     * Enhanced toString for detailed display
     */
    public String toDetailedString() {
        return String.format("Printed Book: %s\n" +
                        "Author: %s\n" +
                        "Edition: %d\n" +
                        "Shelf: %s\n" +
                        "Condition: %s (%d/5)\n" +
                        "Status: %s%s",
                getTitle(), getAuthor(), edition, shelfLocation, condition,
                getConditionScore(), isAvailable() ? "Available" : "Checked Out",
                isReserved ? " (Reserved)" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PrintedBook)) return false;
        if (!super.equals(obj)) return false;

        PrintedBook that = (PrintedBook) obj;
        return edition == that.edition &&
                isReserved == that.isReserved &&
                shelfLocation.equals(that.shelfLocation) &&
                condition.equals(that.condition);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + shelfLocation.hashCode();
        result = 31 * result + condition.hashCode();
        result = 31 * result + edition;
        result = 31 * result + (isReserved ? 1 : 0);
        return result;
    }
}