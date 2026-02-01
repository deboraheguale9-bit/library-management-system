package model;

import java.util.ArrayList;
import java.util.List;

public class Librarian extends User {
    private String employeeId;
    private String shift;

    public Librarian(String id, String name, String email, String mobile,
                     String username, String plainPassword,
                     String employeeId, String shift) {
        super(id, name, email, mobile, username, plainPassword, UserRole.LIBRARIAN);
        this.employeeId = employeeId;
        this.shift = shift;
    }



    public boolean addBook(Book book) {
        System.out.println("[LIBRARIAN] " + getName() + " added book: " + book.getTitle());
        return true;
    }


    public boolean deleteBook(String isbn) {
        System.out.println("[LIBRARIAN] " + getName() + " deleted book ISBN: " + isbn);
        return true;
    }

    public boolean updateBook(Book book) {
        System.out.println("[LIBRARIAN] " + getName() + " updated book: " + book.getTitle());
        // TODO: Connect to BookRepository: bookRepository.update(book);
        return true;
    }

    public boolean processLoan(Member member, Book book) {
        System.out.println("[LIBRARIAN] " + getName() +
                " processed loan: " + member.getName() +
                " → " + book.getTitle());
        // TODO: Create Loan object, check availability, update status
        return true;
    }


    public boolean processReturn(Loan loan) {
        System.out.println("[LIBRARIAN] " + getName() +
                " processed return for loan ID: " + loan.getLoanId());
        // TODO: Update loan status, update book availability, check for fines
        return true;
    }

    public List<Book> searchBooks(String query) {
        System.out.println("[LIBRARIAN] " + getName() + " searched for: " + query);
        // TODO: Connect to BookRepository: return bookRepository.search(query);
        return new ArrayList<>();
    }

    public boolean checkBookAvailability(Book book) {
        System.out.println("[LIBRARIAN] Checking availability of: " + book.getTitle());
        // TODO: Check book status in repository
        return true;
    }

    public List<Loan> viewOverdueLoans() {
        System.out.println("[LIBRARIAN] " + getName() + " viewing overdue loans");
        // TODO: Connect to LoanRepository
        return new ArrayList<>();
    }

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


    @Override
    public String toString() {
        return getName() + " (Librarian ID: " + employeeId + ", Shift: " + shift + ")";
    }


    @Override
    public java.util.Map<String, Object> getProfile() {
        java.util.Map<String, Object> profile = super.getProfile();
        profile.put("employeeId", employeeId);
        profile.put("shift", shift);
        profile.put("roleDescription", "Manages books, loans, and member services");
        return profile;
    }
}