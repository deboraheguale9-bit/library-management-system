package model;

import java.util.ArrayList;
import java.util.List;

public class Librarian extends User {
    private String employeeId;
    private String shift;

    // FIXED: Renamed parameter from passwordHash to plainPassword
    public Librarian(String id, String name, String email, String mobile,
                     String username, String plainPassword,
                     String employeeId, String shift) {
        super(id, name, email, mobile, username, plainPassword, UserRole.LIBRARIAN);
        this.employeeId = employeeId;
        this.shift = shift;
    }

    // ============ UML-REQUIRED METHODS ============

    /**
     * Adds a new book to the library catalog
     * @param book Book to add
     * @return true if successful
     */
    public boolean addBook(Book book) {
        System.out.println("[LIBRARIAN] " + getName() + " added book: " + book.getTitle());
        // TODO: Connect to BookRepository: bookRepository.save(book);
        return true;
    }

    /**
     * Deletes a book from the catalog by ISBN
     * @param isbn ISBN of book to delete
     * @return true if successful
     */
    public boolean deleteBook(String isbn) {
        System.out.println("[LIBRARIAN] " + getName() + " deleted book ISBN: " + isbn);
        // TODO: Connect to BookRepository: bookRepository.deleteByIsbn(isbn);
        return true;
    }

    /**
     * Updates book information
     * @param book Updated book object
     * @return true if successful
     */
    public boolean updateBook(Book book) {
        System.out.println("[LIBRARIAN] " + getName() + " updated book: " + book.getTitle());
        // TODO: Connect to BookRepository: bookRepository.update(book);
        return true;
    }

    /**
     * Processes a loan for a member (UML: processReviewerMember - likely typo)
     * @param member Member borrowing the book
     * @param book Book being borrowed
     * @return true if loan successful
     */
    public boolean processLoan(Member member, Book book) {
        System.out.println("[LIBRARIAN] " + getName() +
                " processed loan: " + member.getName() +
                " → " + book.getTitle());
        // TODO: Create Loan object, check availability, update status
        return true;
    }

    /**
     * Processes a return of a borrowed book
     * @param loan Loan to return
     * @return true if return successful
     */
    public boolean processReturn(Loan loan) {
        System.out.println("[LIBRARIAN] " + getName() +
                " processed return for loan ID: " + loan.getLoanId());
        // TODO: Update loan status, update book availability, check for fines
        return true;
    }

    /**
     * Searches books by query (title, author, ISBN, etc.)
     * @param query Search query
     * @return List of matching books
     */
    public List<Book> searchBooks(String query) {
        System.out.println("[LIBRARIAN] " + getName() + " searched for: " + query);
        // TODO: Connect to BookRepository: return bookRepository.search(query);
        return new ArrayList<>();  // Return empty list for now
    }

    // ============ ADDITIONAL USEFUL METHODS ============

    /**
     * Checks if a book is available for loan
     * @param book Book to check
     * @return true if available
     */
    public boolean checkBookAvailability(Book book) {
        System.out.println("[LIBRARIAN] Checking availability of: " + book.getTitle());
        // TODO: Check book status in repository
        return true;  // Assuming available
    }

    /**
     * Views all overdue loans
     * @return List of overdue loans
     */
    public List<Loan> viewOverdueLoans() {
        System.out.println("[LIBRARIAN] " + getName() + " viewing overdue loans");
        // TODO: Connect to LoanRepository
        return new ArrayList<>();
    }

    // ============ GETTERS & SETTERS ============

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    // ============ OVERRIDDEN METHODS ============

    @Override
    public String toString() {
        return getName() + " (Librarian ID: " + employeeId + ", Shift: " + shift + ")";
    }

    /**
     * Enhanced profile with librarian-specific info
     */
    @Override
    public java.util.Map<String, Object> getProfile() {
        java.util.Map<String, Object> profile = super.getProfile();
        profile.put("employeeId", employeeId);
        profile.put("shift", shift);
        profile.put("roleDescription", "Manages books, loans, and member services");
        return profile;
    }
}