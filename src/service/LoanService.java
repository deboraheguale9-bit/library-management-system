package service;

import model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanService {
    private FineCalculator fineCalculator;
    private List<Loan> loans;

    public LoanService() {
        this.fineCalculator = new FineCalculator();
        this.loans = new ArrayList<>();
    }

    // Core loan operations
    public Loan borrowBook(Member member, Book book, int loanPeriodDays) {
        if (!member.canBorrowMore() || !book.isAvailable()) {
            return null;
        }

        String loanId = "LN" + System.currentTimeMillis();
        Loan loan = new Loan(loanId, book, member, loanPeriodDays);
        loans.add(loan);
        return loan;
    }

    public double returnBook(Loan loan) {
        if (!loans.contains(loan)) {
            return 0.0;
        }

        loan.closeLoan();
        loans.remove(loan);

        // Calculate fine if overdue
        if (loan.isOverdue()) {
            double fine = fineCalculator.calculateFine(loan);
            loan.getMember().addFine(fine);
            return fine;
        }
        return 0.0;
    }

    // Fine management
    public double calculateFine(Loan loan) {
        return fineCalculator.calculateFine(loan);
    }

    public double calculateTotalFine(Member member) {
        return member.getTotalFine();
    }

    public boolean processFinePayment(Member member, double amount) {
        if (amount <= 0 || amount > member.getTotalFine()) {
            return false;
        }
        member.payFine(amount);
        return true;
    }

    // Loan queries
    public List<Loan> getActiveLoans(Member member) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.getMember().getId().equals(member.getId()) &&
                    loan.getStatus() == LoanStatus.ACTIVE) {
                result.add(loan);
            }
        }
        return result;
    }

    public List<Loan> getOverdueLoans() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.isOverdue()) {
                result.add(loan);
            }
        }
        return result;
    }

    public boolean renewLoan(Loan loan, int additionalDays) {
        if (loans.contains(loan) && !loan.isOverdue()) {
            return loan.renew(additionalDays);
        }
        return false;
    }
}