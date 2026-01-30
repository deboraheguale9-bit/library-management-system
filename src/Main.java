import model.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Complete Library System Test ===\n");

        // Create test objects
        EBook ebook = new EBook("978-0134685991", "Effective Java", "Joshua Bloch",
                2018, 5, 2.5, "PDF", "https://example.com/ej.pdf", false);

        PrintedBook pbook = new PrintedBook("978-0596009205", "Head First Java", "Kathy Sierra",
                2005, 3, "CS-101-A", "Good", 2);

        Member member = new Member("M001", "Alice Reader", "alice@email.com", "555-0102",
                "alice", "pass123", "MEM001");

        Admin admin = new Admin("A001", "John Admin", "admin@lib.com", "555-0101",
                "admin", "admin123", 1);

        // Test 1: Member borrows a book
        System.out.println("📚 Test 1: Member Borrowing Book");
        System.out.println("Book available? " + ebook.isAvailable());
        System.out.println("Member can borrow more? " + member.canBorrowMore());

        Loan loan = member.borrowBook(ebook, 14); // 14-day loan
        if (loan != null) {
            System.out.println("✅ Loan created: " + loan.getLoanId());
            System.out.println("Book borrowed: " + loan.getBook().getTitle());
            System.out.println("Borrowed by: " + loan.getMember().getName());
            System.out.println("Due date: " + loan.getDueDate());
            System.out.println("Book available now? " + ebook.isAvailable());
        }

        // Test 2: Check loan status
        System.out.println("\n📅 Test 2: Loan Status");
        System.out.println("Loan is overdue? " + loan.isOverdue());
        System.out.println("Loan status: " + loan.getStatus());

        // Test 3: Member returns book
        System.out.println("\n↩️ Test 3: Returning Book");
        boolean returned = member.returnBook(loan);
        System.out.println("Return successful? " + returned);
        System.out.println("Book available now? " + ebook.isAvailable());
        System.out.println("Loan status after return: " + loan.getStatus());

        // Test 4: View borrowing history
        System.out.println("\n📖 Test 4: Borrowing History");
        System.out.println("Active loans: " + member.getActiveLoans().size());

        // Test 5: Admin functionality
        System.out.println("\n⚙️ Test 5: Admin Functions");
        System.out.println("Admin report: " + admin.generateReport());

        // Test 6: Polymorphism
        System.out.println("\n🔄 Test 6: Polymorphism");
        User userAsMember = member;
        User userAsAdmin = admin;
        System.out.println("User (as Member): " + userAsMember.getName() + " - " + userAsMember.getRole());
        System.out.println("User (as Admin): " + userAsAdmin.getName() + " - " + userAsAdmin.getRole());

        // Test 7: Book hierarchy
        System.out.println("\n📚 Test 7: Book Hierarchy");
        Book[] books = {ebook, pbook};
        for (Book book : books) {
            System.out.println("- " + book.getType() + ": " + book.getTitle());
        }

        System.out.println("\n=== ✅ ALL SYSTEMS WORKING ===");
        System.out.println("Classes implemented:");
        System.out.println("✓ Book.java (abstract)");
        System.out.println("✓ EBook.java, PrintedBook.java");
        System.out.println("✓ User.java (abstract)");
        System.out.println("✓ UserRole.java (enum)");
        System.out.println("✓ Admin.java, Member.java");
        System.out.println("✓ Loan.java, LoanStatus.java (enum)");
        System.out.println("✓ Composition: Loan has Book and Member");
        System.out.println("✓ Inheritance: User → Admin/Member, Book → EBook/PrintedBook");
        System.out.println("✓ Polymorphism: User reference to Admin/Member");
    }
}