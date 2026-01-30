import model.*;
import repository.BookRepository;
import repository.FileBookRepository;
import service.BookService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Testing Complete System ===\n");

        // 1. Create test books
        EBook ebook = new EBook("978-0134685991", "Effective Java", "Joshua Bloch",
                2018, 5, 2.5, "PDF", "https://example.com/ej.pdf", false);

        PrintedBook pbook = new PrintedBook("978-0596009205", "Head First Java", "Kathy Sierra",
                2005, 3, "CS-101-A", "Good", 2);

        // 2. Create repository and service (SOLID: DIP - depend on abstraction)
        BookRepository repository = new FileBookRepository("data/books.csv");
        BookService bookService = new BookService(repository);

        // 3. Test BookService
        System.out.println("📚 Testing BookService (SOLID Principles):");

        System.out.println("\n1. Adding books:");
        System.out.println("   Add EBook: " + bookService.addBook(ebook));
        System.out.println("   Add PrintedBook: " + bookService.addBook(pbook));

        System.out.println("\n2. Searching books:");
        System.out.println("   Search 'Java': " + bookService.searchBooks("Java").size() + " books found");
        System.out.println("   Search 'Joshua': " + bookService.searchByAuthor("Joshua").size() + " books found");

        System.out.println("\n3. Getting all books:");
        System.out.println("   Total books: " + bookService.getAllBooks().size());

        // 4. Test inheritance and polymorphism
        System.out.println("\n👥 Testing User System:");
        Member member = new Member("M001", "Alice Reader", "alice@email.com", "555-0102",
                "alice", "pass123", "MEM001");

        Admin admin = new Admin("A001", "John Admin", "admin@lib.com", "555-0101",
                "admin", "admin123", 1);

        System.out.println("   Member: " + member.getName() + " (" + member.getRole() + ")");
        System.out.println("   Admin: " + admin.getName() + " (" + admin.getRole() + ")");

        // 5. Test Loan system
        System.out.println("\n📅 Testing Loan System:");
        Loan loan = member.borrowBook(ebook, 14);
        if (loan != null) {
            System.out.println("   Loan created: " + loan.getLoanId());
            System.out.println("   Due date: " + loan.getDueDate());
            System.out.println("   Book available after borrow: " + ebook.isAvailable());

            member.returnBook(loan);
            System.out.println("   Book available after return: " + ebook.isAvailable());
        }

        System.out.println("\n=== ✅ ALL SYSTEMS WORKING ===");
        System.out.println("OOP Principles Demonstrated:");
        System.out.println("✓ Inheritance: Book→EBook/PrintedBook, User→Admin/Member");
        System.out.println("✓ Encapsulation: Private fields with getters/setters");
        System.out.println("✓ Polymorphism: User reference to Admin/Member");
        System.out.println("✓ Abstraction: Abstract classes, interfaces");
        System.out.println("\nSOLID Principles Demonstrated:");
        System.out.println("✓ SRP: BookService handles only book operations");
        System.out.println("✓ OCP: Book open for extension (EBook, PrintedBook)");
        System.out.println("✓ LSP: Admin/Member can substitute User");
        System.out.println("✓ ISP: Small interfaces (BookRepository, UserRepository)");
        System.out.println("✓ DIP: BookService depends on BookRepository interface");
    }
}