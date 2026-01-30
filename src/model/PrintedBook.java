package model;

public class PrintedBook extends Book {
    private String shelfLocation;
    private String condition;
    private int edition;
    private boolean isReserved;

    public PrintedBook(String isbn, String title, String author,
                       int publicationYear, int copies,
                       String shelfLocation, String condition, int edition) {
        super(isbn, title, author, publicationYear, copies);
        this.shelfLocation = shelfLocation;
        this.condition = condition;
        this.edition = edition;
        this.isReserved = false;
    }

    public String getLocation() {
        return shelfLocation;
    }

    public void updateCondition(String newCondition) {
        String[] validConditions = {"New", "Like New", "Good", "Fair", "Poor"};
        for (String valid : validConditions) {
            if (valid.equalsIgnoreCase(newCondition)) {
                this.condition = newCondition;
                return;
            }
        }
        throw new IllegalArgumentException("Invalid condition: " + newCondition);
    }

    public boolean reserve() {
        if (!isReserved && super.isAvailable()) {  // Use super.isAvailable() to check parent availability
            isReserved = true;
            return true;
        }
        return false;
    }

    public boolean cancelReservation() {
        if (isReserved) {
            isReserved = false;
            return true;
        }
        return false;
    }

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

    @Override
    public boolean isAvailable() {
        // A printed book is available if:
        // 1. It's not reserved AND
        // 2. The parent Book class says it's available (copies > 0, available = true)
        return !isReserved && super.isAvailable();
    }

    @Override
    public void setAvailable(boolean available) {
        // If setting to unavailable, also cancel reservation
        if (!available) {
            isReserved = false;
        }
        super.setAvailable(available);  // Call parent's setAvailable
    }

    // Getters and Setters
    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        if (edition > 0) {
            this.edition = edition;
        }
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }
}