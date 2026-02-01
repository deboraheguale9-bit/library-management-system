package model;

public interface MediaBorrowable {
    // EXACT methods from UML
    boolean borrowMember(Member borrower);
    void returnItem(Item item);
    void setToFullList();
}
