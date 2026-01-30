package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private String loanId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private Book book;
    private Member member;  // We'll create Member.java next

    public Loan(String loanId, Book book, Member member, int loanPeriodDays) {
        this.loanId = loanId;
        this.book = book;
        this.member = member;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(loanPeriodDays);
        this.status = LoanStatus.ACTIVE;
        this.returnDate = null;

        // Mark book as unavailable
        book.setAvailable(false);
    }

    // Methods from UML
    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && LocalDate.now().isAfter(dueDate);
    }

    public void closeLoan() {
        if (status == LoanStatus.ACTIVE) {
            status = LoanStatus.RETURNED;
            returnDate = LocalDate.now();
            book.setAvailable(true);
        }
    }

    public boolean renew(int days) {
        if (status == LoanStatus.ACTIVE && !isOverdue()) {
            dueDate = dueDate.plusDays(days);
            return true;
        }
        return false;
    }

    public int getOverdueDays() {
        if (isOverdue()) {
            return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        return 0;
    }

    // Getters and Setters
    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    @Override
    public String toString() {
        return String.format("Loan #%s: %s borrowed by %s | Due: %s | Status: %s",
                loanId, book.getTitle(), member.getName(), dueDate, status);
    }
}
