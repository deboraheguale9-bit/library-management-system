package model;

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

    public boolean pickupReservedBook() {
        if (isReserved && super.isAvailable()) {
            isReserved = false;
            // Actually borrow the book (reduce copies)
            return borrow(null); // In real app, pass actual member
        }
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
        return String.format("Shelf: %s | Condition: %s | Edition: %d%s",
                shelfLocation, condition, edition,
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

    @Override
    public java.util.Map<String, Object> getProfile() {
        java.util.Map<String, Object> profile = super.getProfile();
        profile.put("shelfLocation", shelfLocation);
        profile.put("condition", condition);
        profile.put("edition", edition);
        profile.put("reserved", isReserved);
        profile.put("needsRepair", needsRepair());
        return profile;
    }
}