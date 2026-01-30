package util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static LocalDate calculateDueDate(LocalDate borrowDate, int loanPeriodDays) {
        return borrowDate.plusDays(loanPeriodDays);
    }

    public static boolean isOverdue(LocalDate dueDate) {
        return LocalDate.now().isAfter(dueDate);
    }

    public static int daysBetween(LocalDate date1, LocalDate date2) {
        return (int) ChronoUnit.DAYS.between(date1, date2);
    }

    public static int getOverdueDays(LocalDate dueDate) {
        if (isOverdue(dueDate)) {
            return daysBetween(dueDate, LocalDate.now());
        }
        return 0;
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.toString();
    }

    public static boolean isValidDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }
}
