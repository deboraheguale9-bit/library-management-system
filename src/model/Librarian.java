package model;

public class Librarian extends User {
    private String employeeId;
    private String shift;

    public Librarian(String id, String name, String email, String mobile,
                     String username, String passwordHash, String employeeId, String shift) {
        super(id, name, email, mobile, username, passwordHash, UserRole.LIBRARIAN);
        this.employeeId = employeeId;
        this.shift = shift;
    }

    // Librarian-specific methods
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
}