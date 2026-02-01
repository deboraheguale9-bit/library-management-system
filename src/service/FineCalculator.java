package service;

import model.Loan;
import model.Member;

public class FineCalculator {
    private static final double DEFAULT_RATE_PER_DAY = 0.50;
    private static final double MAX_FINE = 20.00;
    private double ratePerDay;

    public FineCalculator() {
        this.ratePerDay = DEFAULT_RATE_PER_DAY;
    }

    public FineCalculator(double ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public double calculateFine(Loan loan) {
        if (!loan.isOverdue()) {
            return 0.0;
        }

        int overdueDays = loan.getOverdueDays();
        double fine = overdueDays * ratePerDay;

        if (fine > MAX_FINE) {
            fine = MAX_FINE;
        }

        return fine;
    }

    public double applyDiscount(Member member, double fine) {
        if (member.getTotalFine() == 0) {
            return fine * 0.9;
        }
        return fine;
    }

    public double getRate() {
        return ratePerDay;
    }

    public void setRate(double ratePerDay) {
        if (ratePerDay >= 0) {
            this.ratePerDay = ratePerDay;
        }
    }

    public double getMaxFine() {
        return MAX_FINE;
    }
}