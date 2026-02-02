# **LIBRARY MANAGEMENT SYSTEM**

## **📋 Project Overview**
A complete Java desktop application for managing library operations with role-based access control, book management, and loan processing. Built with Java Swing GUI and demonstrating professional OOP/SOLID principles.

## **🎯 Features**
- **Three User Roles**: Admin, Librarian, Member
- **Book Management**: Printed Books & E-Books
- **Loan System**: Borrowing, returning, automatic fine calculation
- **Search Functionality**: Search books by title, author, or ISBN
- **Data Persistence**: H2 Database + CSV File Storage
- **Input Validation**: Email, ISBN, phone number validation
- **Error Handling**: User-friendly error messages

## **🛠️ Technologies Used**
- **Language**: Java 21
- **GUI**: Java Swing
- **Database**: H2 (Embedded)
- **File Storage**: CSV format
- **Architecture**: 4-Layer Design (GUI → Service → Repository → Model)
- **Design Patterns**: Repository, Strategy, Factory

## **🚀 How to Run**

### **Option 1: Run JAR File (Recommended)**
1. Ensure you have **Java 21 or later** installed
2. Download `LibrarySystem.jar`
3. Double-click the JAR file
   OR
4. Open terminal/command prompt and run:
   ```bash
   java -jar LibrarySystem.jar
   ```

### **Option 2: Run from Source Code**
1. **Requirements**:
   - Java Development Kit (JDK) 21+
   - IntelliJ IDEA (recommended) or any Java IDE

2. **Setup**:
   ```bash
   # Clone the repository
   git clone [your-repository-url]
   
   # Open in IntelliJ IDEA
   # Build the project (Ctrl+F9)
   # Run Main.java (Shift+F10)
   ```

3. **Project Structure**:
   ```
   src/
   ├── model/           # Domain entities (User, Book, Loan)
   ├── repository/      # Data access layer
   ├── service/         # Business logic
   ├── util/           # Utilities (Database, Validation, Dates)
   └── swingui/        # GUI components
   ```

## **🔑 Test Credentials**
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Librarian | `librarian` | `lib123` |
| Member | `member` | `mem123` |

## **📊 Database Schema**
The system creates three main tables:
1. **users** - User accounts and profiles
2. **books** - Book information and inventory
3. **loans** - Borrowing records and fines

## **🎨 User Interface**
- **Login Window**: Role-based authentication
- **Admin Dashboard**: User management
- **Librarian Dashboard**: Book and loan management
- **Member Dashboard**: Book search and borrowing

## **⚙️ OOP & SOLID Principles**
This project demonstrates:
- **Encapsulation**: Private fields with getters/setters
- **Inheritance**: User → Admin/Librarian/Member, Book → EBook/PrintedBook
- **Polymorphism**: Interface-based implementations
- **Abstraction**: Abstract classes and interfaces
- **SOLID**: All five principles implemented throughout

## **📁 File I/O Implementation**
- **CSV File Storage**: `users.csv` for user data backup
- **Database**: H2 embedded database for primary storage
- **Fallback Mechanism**: File storage when database unavailable

## **⚠️ Common Issues & Solutions**

### **"Could not find or load main class"**
- Ensure you're running `java -jar LibrarySystem.jar` from correct directory
- Check Java version: `java -version` (should be 21+)

### **"No suitable driver found"**
- The JAR includes H2 driver. If missing, download H2 JAR and add to classpath

### **Database File Location**
- Database files are created in the same directory as the JAR
- `librarydb.mv.db` - H2 database file
- `users.csv` - User data backup file




## **📄 License**
This project is developed for educational purposes as part of Object-Oriented Programming course requirements.



---

**Note**: This application is designed for educational purposes to demonstrate Java OOP and SOLID principles in a real-world application scenario.

---



---


