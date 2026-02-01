package model;

public interface BookBorrowable {
    boolean borrow(Member member);
    boolean returnItem();
    boolean getAvailability();

    default boolean canBeBorrowed() {
        return getAvailability();
    }

    default String getBorrowStatus() {
        return getAvailability() ? "Available" : "Not Available";
    }
}

