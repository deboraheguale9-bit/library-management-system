package model;

/**
 * Base class for items that can be borrowed
 * Only needed if you use MediaBorrowable interface
 */
public abstract class Item {
    private String itemId;
    private String title;
    private boolean available;

    public Item(String itemId, String title) {
        this.itemId = itemId;
        this.title = title;
        this.available = true;
    }

    public abstract String getItemType();

    // Getters and setters
    public String getItemId() { return itemId; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}