import model.*;
import repository.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Complete Library Management System ===\n");

        // Initialize repositories and services
        BookRepository bookRepo = new FileBookRepository("data/books.csv");
        UserRepository userRepo = new FileUserRepository("data/users.csv");

        BookService bookService = new BookService(bookRepo);
        UserService userService = new UserService(userRepo);
        LoanService loanService = new LoanService();

        // 1. Test Authentication
        System.out.println("🔐 Test 1: Authentication System");
        User authenticated = userService.authenticate("admin", "admin123");
        System.out.println("   Admin login: " + (authenticated != null ? "✅ Success" : "❌ Failed"));

        // 2. Create test member
        Member member = new Member("M001", "Alice Reader", "alice@email.com", "555-0102",
                "alice", "pass123", "MEM001");
        userService.registerUser(member);
        System.out.println("   Member registered: " + member.getName());

        // 3. Add books
        System.out.println("\n📚 Test 2: Book Management");
        EBook ebook = new EBook("978-0134685991", "Effective Java", "Joshua Bloch",
                2018, 5, 2.5, "PDF", "https://example.com/ej.pdf", false);

        PrintedBook pbook = new PrintedBook("978-0596009205", "Head First Java", "Kathy Sierra",
                2005, 3, "CS-101-A", "Good", 2);

        bookService.addBook(ebook);
        bookService.addBook(pbook);
        System.out.println("   Books added: " + bookService.getAllBooks().size());

        // 4. Test borrowing
        System.out.println("\n📅 Test 3: Loan System");
        Loan loan = loanService.borrowBook(member, ebook, 14);
        if (loan != null) {
            System.out.println("   Book borrowed: " + loan.getBook().getTitle());
            System.out.println("   Due date: " + loan.getDueDate());
            System.out.println("   Active loans: " + loanService.getActiveLoans(member).size());
        }

        // 5. Test returning with fine simulation
        System.out.println("\n💰 Test 4: Fine Calculation");
        // Simulate overdue (in real app, wait for due date)
        System.out.println("   Loan overdue? " + loan.isOverdue());
        System.out.println("   Fine if overdue: $" + loanService.calculateFine(loan));

        // 6. Return book
        double fine = loanService.returnBook(loan);
        System.out.println("   Book returned, fine: $" + fine);
        System.out.println("   Active loans after return: " + loanService.getActiveLoans(member).size());

        // 7. Test search
        System.out.println("\n🔍 Test 5: Search Functionality");
        System.out.println("   Search 'Java': " + bookService.searchBooks("Java").size() + " books");
        System.out.println("   Search 'Joshua': " + bookService.searchByAuthor("Joshua").size() + " books");

        // 8. Test user management
        System.out.println("\n👥 Test 6: User Management");
        System.out.println("   Total users: " + userService.getAllUsers().size());
        System.out.println("   Username 'alice' available? " + userService.isUsernameAvailable("alice"));

        System.out.println("\n=== ✅ SYSTEM ARCHITECTURE COMPLETE ===");
        System.out.println("\nLAYERS IMPLEMENTED:");
        System.out.println("1. Model Layer (OOP): Book, User, Loan hierarchies");
        System.out.println("2. Repository Layer (DIP): Interfaces + File implementations");
        System.out.println("3. Service Layer (SRP): Business logic separated");
        System.out.println("4. SOLID Principles fully demonstrated");

        System.out.println("\nNEXT: Implement File I/O (CSV/JSON) or start JavaFX GUI");
    }
}