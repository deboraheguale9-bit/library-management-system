package model;

import java.util.ArrayList;
import java.util.List;

public class Member extends User {
    private String memberId;
    private int maxBooksAllowed;
    private double totalFine;
    private List<Loan> activeLoans;

    public Member(String id, String name, String email, String mobile,
                  String username, String passwordHash, String memberId) {
        super(id, name, email, mobile, username, passwordHash, UserRole.MEMBER);
        this.memberId = memberId;
        this.maxBooksAllowed = 5; // Default
        this.totalFine = 0.0;
        this.activeLoans = new ArrayList<>();
    }

    // Member-specific methods
    public Loan borrowBook(Book book, int loanPeriodDays) {
        if (canBorrowMore() && book.isAvailable()) {
            String loanId = "LN" + System.currentTimeMillis();
            Loan loan = new Loan(loanId, book, this, loanPeriodDays);
            activeLoans.add(loan);
            return loan;
        }
        return null;
    }

    public boolean returnBook(Loan loan) {
        if (activeLoans.contains(loan)) {
            loan.closeLoan();
            activeLoans.remove(loan);
            return true;
        }
        return false;
    }

    public List<Loan> viewBorrowingHistory() {
        return new ArrayList<>(activeLoans);
    }

    public boolean canBorrowMore() {
        return activeLoans.size() < maxBooksAllowed && totalFine == 0;
    }

    // Fine management (will be handled by LoanService later)
    public double getTotalFine() { return totalFine; }
    public void setTotalFine(double totalFine) { this.totalFine = totalFine; }

    public void addFine(double amount) { this.totalFine += amount; }
    public void payFine(double amount) {
        this.totalFine = Math.max(0, this.totalFine - amount);
    }

    // Getters and Setters
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public int getMaxBooksAllowed() { return maxBooksAllowed; }
    public void setMaxBooksAllowed(int maxBooksAllowed) {
        this.maxBooksAllowed = maxBooksAllowed;
    }

    public List<Loan> getActiveLoans() { return activeLoans; }

    @Override
    public String toString() {
        return getName() + " (Member ID: " + memberId + ")";
    }
}