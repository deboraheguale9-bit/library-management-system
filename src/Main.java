import model.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Library Management System ===\n");

        // Test Book Inheritance
        System.out.println("📚 Testing Book Inheritance:");
        Book[] books = {
                new EBook("978-0134685991", "Effective Java", "Joshua Bloch",
                        2018, 5, 2.5, "PDF", "https://example.com/ej.pdf", false),
                new PrintedBook("978-0596009205", "Head First Java", "Kathy Sierra",
                        2005, 3, "CS-101-A", "Good", 2)
        };

        for (Book book : books) {
            System.out.println("- " + book.getType() + ": " + book.getTitle());
        }

        // Test User Inheritance
        System.out.println("\n👥 Testing User Inheritance:");

        Admin admin = new Admin("A001", "John Admin", "admin@lib.com", "555-0101",
                "admin", "admin123", 1);

        System.out.println("Created: " + admin.getName() + " (" + admin.getRole() + ")");
        System.out.println("Login test: " + (admin.login("admin123") ? "✅ Success" : "❌ Failed"));
        System.out.println("Admin level: " + admin.getAdminLevel());

        // Test Polymorphism
        System.out.println("\n🔄 Testing Polymorphism:");
        User userReference = admin; // Admin IS-A User
        System.out.println("User reference name: " + userReference.getName());
        System.out.println("User reference role: " + userReference.getRole());

        // Test Admin functionality
        System.out.println("\n⚙️ Testing Admin Features:");
        System.out.println("Report: " + admin.generateReport());

        System.out.println("\n=== ✅ All OOP Principles Demonstrated ===");
        System.out.println("1. Inheritance: User → Admin");
        System.out.println("2. Encapsulation: Private fields with getters");
        System.out.println("3. Polymorphism: User reference to Admin object");
        System.out.println("4. Abstraction: Abstract User class");
    }
}
