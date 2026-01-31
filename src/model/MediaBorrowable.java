package model;

/**
 * EXACTLY matches UML diagram
 * For UML compliance only
 */
public interface MediaBorrowable {
    // EXACT methods from UML
    boolean borrowMember(Member borrower);
    void returnItem(Item item);  // Note: void return type
    void setToFullList();
}
